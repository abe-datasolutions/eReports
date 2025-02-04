package com.abclab.abcereports

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abclab.abcereports.ExternalDB.DatabaseAccess
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpConnectionParams
import org.apache.http.params.HttpParams
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

//import com.abclab.abcereports.DBTestInfo.TestDetailsData;
class TestDetailsActivity : AppCompatActivity() {
    private var gc: GlobalClass? = null

    //	private DBTestInfo db;
    private var db: DatabaseAccess? = null
    private var wv: WebView? = null

    //	private TestDetailsData tstDet;
    private var tstDet: DatabaseAccess.TestDetailsData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gc = (this.applicationContext as GlobalClass)
        setContentView(R.layout.test_details)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.drawable.elabslogo)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = title
        wv = findViewById<View>(R.id.tstDetailsView) as WebView

        //		db = new DBTestInfo(getApplicationContext());
        db = DatabaseAccess.getInstance(this)

        GetDetailsAC().execute()
    }

    private fun loadData() {
        if (tstDet != null) {
            var html = if (gc!!.getBranchId() == 2) {
                getString(R.string.testDetailsBodyJKT)
            } else {
                getString(R.string.testDetailsBody)
            }
            html = html.replace("/*code*/", tstDet!!.Code)
            html = html.replace("/*name*/", tstDet!!.Name)
            html = html.replace("/*description*/", tstDet!!.Description)
            html = html.replace("/*specimen*/", tstDet!!.Specimen)
            html = html.replace("/*preparation*/", tstDet!!.Preparation)
            html = html.replace("/*running*/", tstDet!!.Running)
            html = html.replace("/*tat*/", tstDet!!.TAT)
            wv!!.loadData(html, "text/html", "utf-8")
        } else {
            Toast.makeText(applicationContext, getString(R.string.testNotFound), Toast.LENGTH_LONG)
                .show()
            finish()
        }
    }

    private inner class GetDetailsAC : AsyncTask<String?, Void?, Void?>() {
        override fun doInBackground(vararg p0: String?): Void? {
            localData
            if (tstDet == null) {
                fetchOnlineData()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            gc!!.hideProgress()
            loadData()
        }

        override fun onPreExecute() {
            gc!!.showProgress(this@TestDetailsActivity, "Loading Details", "Please Wait")
        }
    }

    private val localData: Unit
        get() {
            try {
                tstDet = db!!.getDetails(gc!!.getBranchId(), gc!!.testCode)
            } catch (e: Exception) {
                Log.d(
                    getString(R.string.tag),
                    "ERROR $e"
                )
            }
        }
    private fun fetchOnlineData() {
            try {
                val httpParam: HttpParams =
                    BasicHttpParams()
                HttpConnectionParams.setConnectionTimeout(httpParam, 30000)
                HttpConnectionParams.setSoTimeout(httpParam, 30000)
                val httpClient: HttpClient =
                    DefaultHttpClient(httpParam)
                val httpPost =
                    HttpPost("https://www.abclab.com/eReportApple/Tests/GetDetails")

                val params: MutableList<NameValuePair> =
                    ArrayList(2)

                params.add(
                    BasicNameValuePair(
                        "branch",
                        gc!!.getBranchId().toString()
                    )
                )
                params.add(BasicNameValuePair("testCode", gc!!.testCode))

                Log.d(
                    "branc -- testcode",
                    gc!!.getBranchId().toString() + " - " + gc!!.testCode
                )
                httpPost.entity = UrlEncodedFormEntity(params)

                val response = httpClient.execute(httpPost)
                val entity = response.entity
                val webs = entity.content
                var result = ""
                try {
                    val reader =
                        BufferedReader(InputStreamReader(webs, "iso-8859-1"), 8)
                    val sb = StringBuilder()
                    var line: String? = null
                    while ((reader.readLine().also { line = it }) != null) {
                        sb.append(line + "\n")
                    }
                    webs.close()
                    result = sb.toString()
                    if (result.length > 0) {
                        Log.d("result", result.toString())
                        val jData = JSONObject(result)
                        //						tstDet = new TestDetailsData();
                        tstDet = DatabaseAccess.TestDetailsData()
                        tstDet!!.Code = jData.getString("Code")
                        tstDet!!.Name = jData.getString("Name")
                        tstDet!!.Description = jData.getString("Description")
                        tstDet!!.Specimen = jData.getString("Specimen")
                        tstDet!!.Preparation = jData.getString("Preparation")
                        tstDet!!.Running = jData.getString("Running")
                        tstDet!!.TAT = jData.getString("TAT")
                        db!!.setDetails(gc!!.getBranchId(), tstDet)
                    }
                } catch (e: Exception) {
                    Log.d(
                        getString(R.string.tag),
                        "ERROR $e"
                    )
                }
            } catch (e: Exception) {
                Log.d(
                    getString(R.string.tag),
                    "ERROR $e"
                )
            }
        }
}
