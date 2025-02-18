package com.abclab.abcereports

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abclab.abcereports.databinding.ReportFindResultBinding
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class ReportFindResultActivity : AppCompatActivity() {
    private val binding by lazy { 
        ReportFindResultBinding.inflate(layoutInflater)
    }
    private val gc by lazy { 
        applicationContext as GlobalClass
    }
    private val listData = ArrayList<ResultData?>()
    private var lastIdx = 0
    private var rowAdded = 0
    private var aa: ReportArrayAdapter? = null


    override fun onCreateContextMenu(
        menu: ContextMenu, v: View,
        menuInfo: ContextMenuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.find_report_result_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo

        if (info.position < listData.size - 1) {
            val d = listData[info.position]
            when (item.itemId) {
                R.id.findRptResultFindRelated -> {
                    gc.findReportFilters.PatientName = d!!.patientName

                    val intent = Intent(applicationContext, ReportFindActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("PATNAME", d.patientName)
                    setResult(RESULT_OK, intent)
                    finish()
                    return true
                }

                R.id.findRptResultDownload -> {
                    gc.reportNo = d!!.reportNo
                    DownloadFile(this@ReportFindResultActivity)
                    return true
                }

                else -> return super.onContextItemSelected(item)
            }
        } else {
            return super.onContextItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.ic_launcher_foreground)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = " $title"
        registerForContextMenu(binding.reportFindLV)
        binding.reportFindLV.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                val threshold = 1
                val count = binding.reportFindLV.count

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (binding.reportFindLV.lastVisiblePosition >= count - threshold) {
                        populateData()
                    }
                }
            }

            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
            }
        })
        binding.reportFindLV.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                if (position < listData.size - 1) {
                    val d = listData[position]
                    gc.reportNo = d!!.reportNo
                    DownloadFile(this@ReportFindResultActivity)
                }
            }

        populateData()
    }

    fun populateData() {
        lifecycleScope.launch {
            gc.showProgress(this@ReportFindResultActivity, "Loading Data", "Please Wait")
            try {
                try {
                    val result = withContext(Dispatchers.IO){
                        HttpClient(Android).use {
                            it.submitForm(
                                url = "https://www.abclab.com/eReportApple/Report/FindReport",
                                formParameters = Parameters.build {
                                    append("branch", gc.getBranchId().toString())
                                    append("siteid", gc.siteId!!)
                                    append("username", gc.userId!!)
                                    append("hash", gc.hashCode!!)
                                    append("startrow", lastIdx.toString())
                                    val filter = gc.findReportFilters
                                    if (filter.ReportNo.isNotEmpty())
                                        append("sampid", filter.ReportNo)
                                    if (filter.PatientName.isNotEmpty())
                                        append("patname", filter.PatientName)
                                    if (filter.Gender.isNotEmpty())
                                        append("patsex", filter.Gender)
                                    if ((filter.DateFrom + filter.DateTo) > 0) {
                                        //SimpleDateFormat sf = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.US);
                                        if (filter.DateFrom > 0) {
                                            append(
                                                name = "rptdtfr",
                                                value = DateFormat.format("MM/dd/yyyy", Date(filter.DateFrom)) as String
                                            )
                                        }
                                        if (filter.DateTo > 0) {
                                            append(
                                                name = "rptdtto",
                                                value = DateFormat.format("MM/dd/yyyy", Date(filter.DateTo)) as String
                                            )
                                        }
                                    }
                                }
                            ).bodyAsText()
                        }
                    }
                    if (result.isNotEmpty()) {
                        gc.PopulateData(result, listData)
                    }
                    rowAdded = listData.size - lastIdx
                    lastIdx += rowAdded
                } catch (e: Exception) {
                    Log.d(getString(R.string.tag), "ERROR $e")
                }
            } catch (e: Exception) {
                Log.d(getString(R.string.tag), "ERROR $e")
            }
            updateList()
        }
    }

    private fun updateList(){
        aa = ReportArrayAdapter(applicationContext, listData)
        binding.reportFindLV.adapter = aa
        gc.hideProgress()
        if (listData.size > 0) {
            if (rowAdded > 0) {
                binding.reportFindLV.post { binding.reportFindLV.setSelection(lastIdx - rowAdded - 1) }
                Toast.makeText(
                    applicationContext,
                    getString(R.string.menuLongPressOption),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.noAddRecordFound),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.noRecordFoundFilter),
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }
}
