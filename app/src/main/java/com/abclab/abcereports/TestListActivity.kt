package com.abclab.abcereports

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
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
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

//import com.abclab.abcereports.DBTestInfo.TestListData;
class TestListActivity : AppCompatActivity() {
    //	private ArrayList<TestListData> listData = new ArrayList<TestListData>();
    private var listData = ArrayList<DatabaseAccess.TestListData>()
    private var lv: ListView? = null
    private var aa: TestListArrayAdapter? = null
    var mapIndex: MutableMap<String, Int?> = mutableMapOf()

    private var gc: GlobalClass? = null

    //	private DBTestInfo db;
    private var db: DatabaseAccess? = null
    private var dbOnlineVersion = 0
    private var loadLocal = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gc = (this.applicationContext as GlobalClass)
        setContentView(R.layout.test_list)
        //		db = new DBTestInfo(getApplicationContext());
        db = DatabaseAccess.getInstance(this)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.ic_launcher_foreground)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = title
        lv = findViewById<View>(R.id.testListLV) as ListView
        lv!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> //				TestListData d = listData.get(position);
                val d = listData[position]

                gc!!.testCode = d.Code
                startActivity(
                    Intent(
                        this@TestListActivity,
                        TestDetailsActivity::class.java
                    )
                )
            }
        if (gc!!.getBranchId() >= 0) {
            GetVerAC().execute()
        } else {
            Toast.makeText(
                this,
                "Please enable your location services to be able to show the test list",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }


    private fun fetchOnlineVersion() {
        try {
            val httpParam: HttpParams =
                BasicHttpParams()
            HttpConnectionParams.setConnectionTimeout(httpParam, 2000)
            HttpConnectionParams.setSoTimeout(httpParam, 1000)
            val httpClient: HttpClient =
                DefaultHttpClient(httpParam)
            val httpPost =
                HttpPost("https://www.abclab.com/eReportApple/Tests/GetVersion")

            val params: MutableList<NameValuePair> =
                ArrayList(2)
            params.add(
                BasicNameValuePair(
                    "branch",
                    gc!!.getBranchId().toString()
                )
            )
            httpPost.entity = UrlEncodedFormEntity(params)

            val response = httpClient.execute(httpPost)
            val entity = response.entity
            val webs = entity.content
            try {
                //FIXME: StandardCharsets.ISO_8859_1
                val reader =
                    BufferedReader(InputStreamReader(webs, "iso-8859-1"), 8)
                var result = reader.readLine()
                if (result != null) {
                    result = result.replace("\"", "")
                    try {
                        dbOnlineVersion = result.toInt()
                    } catch (e: Exception) {
                    }
                }
                webs.close()
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

    private fun fetchOnlineList() {
            try {
                val httpClient: HttpClient =
                    DefaultHttpClient()
                val httpPost =
                    HttpPost("https://www.abclab.com/eReportApple/Tests/GetList")

                val params: MutableList<NameValuePair> =
                    ArrayList(2)
                params.add(
                    BasicNameValuePair(
                        "branch",
                        gc!!.getBranchId().toString()
                    )
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
                        db!!.clearList(gc!!.getBranchId())
                        val jArray = JSONArray(result)
                        for (i in 0 until jArray.length()) {
                            val jData = jArray.getJSONObject(i)
                            //	    				TestListData nData = new TestListData();
                            val nData = DatabaseAccess.TestListData()
                            nData.Code = jData.getString("Code")
                            nData.Name = jData.getString("Name")
                            listData.add(nData)
                            db!!.addTestList(gc!!.getBranchId(), nData.Code, nData.Name)
                        }
                        db!!.setVersion(gc!!.getBranchId(), dbOnlineVersion)
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
    private val localList: Unit
        get() {
            try {
                listData = db!!.getList(gc!!.getBranchId())
                Log.d("listDataSize", listData.size.toString())
            } catch (e: Exception) {
                Log.d(
                    getString(R.string.tag),
                    "ERROR $e"
                )
            }
        }

    private fun compareVersions() {
        val dbLocalVersion = db!!.getVersion(gc!!.getBranchId())
        loadLocal = if (dbOnlineVersion > dbLocalVersion) {
            false
        } else {
            true
        }
        GetListAC().execute()
    }

    private inner class GetVerAC : AsyncTask<String?, Void?, Void?>() {
        override fun doInBackground(vararg p0: String?): Void? {
            fetchOnlineVersion()
            return null
        }

        override fun onPostExecute(result: Void?) {
            gc!!.hideProgress()
            compareVersions()
        }

        override fun onPreExecute() {
            gc!!.showProgress(this@TestListActivity, "Checking Online Version", "Please Wait")
        }
    }

    private fun setIndexList() {
        mapIndex = LinkedHashMap()
        for (i in listData.indices) {
            val d = listData[i].Name
            val index = d.substring(0, 1)

            if (mapIndex[index] == null) mapIndex[index] = i
        }
        displayIndex()
    }

    @SuppressLint("InflateParams")
    private fun displayIndex() {
        /*
	        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.testListIndex);
	        indexLayout.setVisibility(0);
	        TextView textView;
	        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
	        for (String index : indexList) {
	            textView = (TextView) getLayoutInflater().inflate(
	                    R.layout.side_index_row, null);
	            textView.setText(index);
	            textView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						TextView idx = (TextView) v;
				        lv.setSelection(mapIndex.get(idx.getText()));
					}
				});
	            indexLayout.addView(textView);
	        }
	        */
    }

    private inner class GetListAC : AsyncTask<String?, Void?, Void?>() {
        override fun doInBackground(vararg p0: String?): Void? {
            if (loadLocal) {
                localList
                if (listData.size == 0) {
                    fetchOnlineList()
                }
            } else {
                fetchOnlineList()
                if (listData.size == 0) {
                    localList
                }
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (listData.size > 0) {
                aa = TestListArrayAdapter(this@TestListActivity, listData)
                lv!!.adapter = aa
                setIndexList()
                gc!!.hideProgress()
            } else {
                gc!!.hideProgress()
                Toast.makeText(
                    this@TestListActivity,
                    "No data retrieved from server. Please make sure you have internet connection.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }

        override fun onPreExecute() {
            gc!!.showProgress(this@TestListActivity, "Loading Data", "Please Wait")
        }
    }
}
