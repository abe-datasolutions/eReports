package com.abclab.abcereports

import android.app.AlertDialog
import android.content.ContentUris
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ActionMode
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView.MultiChoiceModeListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File
import java.util.Calendar
import java.util.Date

class CacheListActivity : AppCompatActivity() {
    private var listData = ArrayList<CacheData>()
    private var lv: ListView? = null
    private var aa: CacheArrayAdapter? = null
    private var gc: GlobalClass? = null
    private var actionMode: ActionMode? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cache_list)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.ic_launcher_foreground)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = title
        lv = findViewById<View>(R.id.cacheListLV) as ListView
        lv!!.visibility = View.VISIBLE

        //registerForContextMenu(lv);
        lv!!.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        lv!!.setMultiChoiceModeListener(object : MultiChoiceModeListener {
            override fun onItemCheckedStateChanged(
                mode: ActionMode, position: Int,
                id: Long, checked: Boolean
            ) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                // Respond to clicks on the actions in the CAB
                actionMode = mode
                when (item.itemId) {
                    R.id.cacheMenuDeleteSel -> {
                        if (lv!!.checkedItemCount > 0) {
                            AlertDialog.Builder(this@CacheListActivity)
                                .setMessage("Remove selected items?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { dialog: DialogInterface?, id: Int ->
                                    val checked = lv!!.checkedItemPositions
                                    val nList = ArrayList<CacheData>()
                                    var i = 0
                                    while (i < listData.size) {
                                        if (checked[i]) {
                                            val fn = gc!!.rptStorage + "/" + listData[i].fileName
                                            val f = File(fn)
                                            if (f.exists()) {
                                                f.delete()
                                            }
                                        } else {
                                            val d = listData[i]
                                            val nd = CacheData()
                                            nd.fileName = d.fileName
                                            nd.fileDate = d.fileDate
                                            nList.add(nd)
                                        }
                                        i++
                                    }
                                    actionMode!!.finish()
                                    listData.clear()
                                    listData = nList
                                    aa = CacheArrayAdapter(
                                        this@CacheListActivity,
                                        listData
                                    )
                                    lv!!.adapter = aa
                                }
                                .setNegativeButton("No", null)
                                .show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Select item to delete",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        return true
                    }

                    R.id.cacheMenuDeleteAll -> {
                        if (lv!!.checkedItemCount > 0) {
                            AlertDialog.Builder(this@CacheListActivity)
                                .setMessage("Remove all items?")
                                .setCancelable(false)
                                .setPositiveButton(
                                    "Yes"
                                ) { dialog, id ->
                                    var i = 0
                                    while (i < listData.size) {
                                        val fn = gc!!.rptStorage + "/" + listData[i].fileName
                                        val f = File(fn)
                                        if (f.exists()) {
                                            f.delete()
                                        }

                                        i++
                                    }
                                    listData.clear()
                                    aa = CacheArrayAdapter(
                                        this@CacheListActivity,
                                        listData
                                    )
                                    lv!!.adapter = aa
                                    actionMode!!.finish()
                                }
                                .setNegativeButton("No", null)
                                .show()
                        }
                        // Action picked, so close the CAB
                        return true
                    }

                    else -> return false
                }
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                // Inflate the menu for the CAB
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.cache_menu, menu)
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false
            }
        })

        lv!!.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                val d =
                    listData[position]
                val pdfFile = File(File(gc!!.rptStorage), d.fileName)
                Log.d("CacheList", "File Clicked: $pdfFile")
                if (pdfFile.exists()) {
                    try {
                        val targetUri = FileProvider.getUriForFile(
                            this,
                            "${applicationContext.packageName}.fileprovider",
                            pdfFile
                        )
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            setDataAndType(targetUri, "application/pdf")
                        }

                        startActivity(intent)
                        Toast.makeText(
                            applicationContext,
                            "Enter your log in Password to view report",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runCatching {
                            gc!!.alert(
                                applicationContext,
                                "Error viewing report",
                                "Please install PDF viewer to view the report."
                            )
                        }.onFailure {
                            Toast.makeText(
                                applicationContext,
                                "Error viewing report!. Please install PDF viewer to view the report.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        gc = (applicationContext as GlobalClass)
        AsyncCallWS().execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.report_menu, menu)
        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu, v: View,
        menuInfo: ContextMenuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.report_menu, menu)
    }

    fun populateData() {
        try {
            try {
                val folder = File(gc!!.rptStorage)
                if (folder.exists()) {
                    var files: Array<File>? = null
                    files = folder.listFiles { dir: File?, filename: String ->
                        Log.d("CacheList", "Files Check: $dir, $filename")
                        filename.endsWith(".pdf")
                    }
                    Log.d("CacheList", "Files Found: ${files.size}")
                    for (file in files) {
                        val nData = CacheData()
                        nData.fileName = file.name
                        nData.fileDate = file.lastModified()
                        val dm = Date(file.lastModified())
                        val c = Calendar.getInstance()
                        c.add(Calendar.HOUR, -24)
                        if (dm.before(c.time)) {
                            Log.d("CacheList", "File Deleted: $nData")
                            file.delete()
                        } else {
                            Log.d("CacheList", "File Added: $nData")
                            listData.add(nData)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private inner class AsyncCallWS : AsyncTask<String?, Void?, Void?>() {
        override fun doInBackground(vararg p0: String?): Void? {
            populateData()
            return null
        }

        override fun onPostExecute(result: Void?) {
            aa = CacheArrayAdapter(this@CacheListActivity, listData)
            lv!!.adapter = aa
            gc!!.hideProgress()
            if (listData.size > 0) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.menuLongPressOption),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.noRecordFoundCache),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun onPreExecute() {
            gc!!.showProgress(this@CacheListActivity, "Loading Data", "Please Wait")
        }

        override fun onProgressUpdate(vararg values: Void?) {
        }
    }
}
