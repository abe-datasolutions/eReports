package com.abclab.abcereports

import android.content.Intent
import android.os.AsyncTask
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
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Date

class ReportFindResultActivity : AppCompatActivity() {
    private val listData = ArrayList<ResultData?>()
    private var lastIdx = 0
    private var rowAdded = 0
    private var lv: ListView? = null
    private var aa: ReportArrayAdapter? = null
    private var gc: GlobalClass? = null


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
                    gc!!.findReportFilters.PatientName = d!!.patientName

                    val intent = Intent(applicationContext, ReportFindActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("PATNAME", d.patientName)
                    setResult(RESULT_OK, intent)
                    finish()
                    return true
                }

                R.id.findRptResultDownload -> {
                    gc!!.reportNo = d!!.reportNo
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
        setContentView(R.layout.report_find_result)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.ic_launcher_foreground)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = " $title"
        lv = findViewById<View>(R.id.reportFindLV) as ListView
        gc = (applicationContext as GlobalClass)
        registerForContextMenu(lv)
        lv!!.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                val threshold = 1
                val count = lv!!.count

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (lv!!.lastVisiblePosition >= count - threshold) {
                        AsyncCallWS().execute()
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
        lv!!.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                if (position < listData.size - 1) {
                    val d = listData[position]
                    gc!!.reportNo = d!!.reportNo
                    DownloadFile(this@ReportFindResultActivity)
                }
            }

        AsyncCallWS().execute()
    }

    fun populateData() {
        try {
            val httpClient: HttpClient = DefaultHttpClient()
            val httpPost = HttpPost("https://www.abclab.com/eReportApple/Report/FindReport")

            val params: MutableList<NameValuePair> = ArrayList(2)
            params.add(BasicNameValuePair("branch", gc!!.getBranchId().toString()))
            params.add(BasicNameValuePair("siteid", gc!!.siteId))
            params.add(BasicNameValuePair("username", gc!!.userId))
            params.add(BasicNameValuePair("hash", gc!!.hashCode))
            params.add(BasicNameValuePair("startrow", lastIdx.toString()))
            val filter = gc!!.findReportFilters
            if (filter.ReportNo.length > 0) {
                params.add(BasicNameValuePair("sampid", filter.ReportNo))
            }
            if (filter.PatientName.length > 0) {
                params.add(BasicNameValuePair("patname", filter.PatientName))
            }
            if (filter.Gender.length > 0) {
                params.add(BasicNameValuePair("patsex", filter.Gender))
            }
            if ((filter.DateFrom + filter.DateTo) > 0) {
                //SimpleDateFormat sf = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.US);
                if (filter.DateFrom > 0) {
                    params.add(
                        BasicNameValuePair(
                            "rptdtfr",
                            DateFormat.format("MM/dd/yyyy", Date(filter.DateFrom)) as String
                        )
                    )
                }
                if (filter.DateTo > 0) {
                    params.add(
                        BasicNameValuePair(
                            "rptdtto",
                            DateFormat.format("MM/dd/yyyy", Date(filter.DateTo)) as String
                        )
                    )
                }
            }
            httpPost.entity = UrlEncodedFormEntity(params)

            val response = httpClient.execute(httpPost)
            val entity = response.entity
            val webs = entity.content
            var result = ""
            try {
                val reader = BufferedReader(InputStreamReader(webs, "iso-8859-1"), 8)
                val sb = StringBuilder()
                var line: String? = null
                while ((reader.readLine().also { line = it }) != null) {
                    sb.append(line + "\n")
                }
                webs.close()
                result = sb.toString()
                if (result.length > 0) {
                    gc!!.PopulateData(result, listData)
                }
                rowAdded = listData.size - lastIdx
                lastIdx += rowAdded
            } catch (e: Exception) {
                Log.d(getString(R.string.tag), "ERROR $e")
            }
        } catch (e: Exception) {
            Log.d(getString(R.string.tag), "ERROR $e")
        }
    }


    private inner class AsyncCallWS : AsyncTask<String?, Void?, Void?>() {
        override fun doInBackground(vararg p0: String?): Void? {
            populateData()
            return null
        }

        override fun onPostExecute(result: Void?) {
            aa = ReportArrayAdapter(applicationContext, listData)
            lv!!.adapter = aa
            gc!!.hideProgress()
            if (listData.size > 0) {
                if (rowAdded > 0) {
                    lv!!.post { lv!!.setSelection(lastIdx - rowAdded - 1) }
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

        override fun onPreExecute() {
            gc!!.showProgress(this@ReportFindResultActivity, "Loading Data", "Please Wait")
        }

        override fun onProgressUpdate(vararg values: Void?) {
        }
    }
}
