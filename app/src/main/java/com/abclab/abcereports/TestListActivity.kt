package com.abclab.abcereports

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abclab.abcereports.ExternalDB.DatabaseAccess
import com.abclab.abcereports.databinding.TestListBinding
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class TestListActivity : AppCompatActivity() {
    private val binding by lazy {
        TestListBinding.inflate(layoutInflater)
    }
    private val gc by lazy {
        applicationContext as GlobalClass
    }
    private var listData = ArrayList<DatabaseAccess.TestListData>()
    private var aa: TestListArrayAdapter? = null
    private var mapIndex: MutableMap<String, Int?> = mutableMapOf()

    //	private DBTestInfo db;
    private val db: DatabaseAccess by lazy { 
        DatabaseAccess.getInstance(applicationContext)
    }
    private var dbOnlineVersion = 0
    private var loadLocal = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.ic_launcher_foreground)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = title
        binding.testListLV.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> //				TestListData d = listData.get(position);
                val d = listData[position]

                gc.testCode = d.Code
                startActivity(
                    Intent(
                        this@TestListActivity,
                        TestDetailsActivity::class.java
                    )
                )
            }
        if (gc.getBranchId() >= 0) {
            fetchOnlineVersion()
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
        lifecycleScope.launch {
            gc.showProgress(this@TestListActivity, "Checking Online Version", "Please Wait")
            try {
                try {
                    val result = withContext(Dispatchers.IO){
                        HttpClient(Android).use {
                            it.submitForm(
                                url = "https://www.abclab.com/eReportApple/Tests/GetVersion",
                                formParameters = Parameters.build {
                                    append("branch", gc.getBranchId().toString())
                                }
                            ).bodyAsText().replace("\"", "")
                        }
                    }
                    runCatching {
                        dbOnlineVersion = result.toInt()
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
            gc.hideProgress()
            compareVersions()
        }
    }

    private suspend fun fetchOnlineList() {
        gc.showProgress(this@TestListActivity, "Loading Data", "Please Wait")
        try {
            try {
                val result = withContext(Dispatchers.IO){
                    HttpClient(Android).use {
                        it.submitForm(
                            url = "https://www.abclab.com/eReportApple/Tests/GetList",
                            formParameters = Parameters.build {
                                append("branch", gc.getBranchId().toString())
                            }
                        ).bodyAsText()
                    }
                }
                if (result.isNotEmpty()) {
                    db.clearList(gc.getBranchId())
                    val jArray = JSONArray(result)
                    for (i in 0 until jArray.length()) {
                        val jData = jArray.getJSONObject(i)
                        //	    				TestListData nData = new TestListData();
                        val nData = DatabaseAccess.TestListData()
                        nData.Code = jData.getString("Code")
                        nData.Name = jData.getString("Name")
                        listData.add(nData)
                        db.addTestList(gc.getBranchId(), nData.Code, nData.Name)
                    }
                    db.setVersion(gc.getBranchId(), dbOnlineVersion)
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

    private suspend fun loadLocalList() = withContext(Dispatchers.IO) {
        try {
            listData = db.getList(gc.getBranchId())
            Log.d("listDataSize", listData.size.toString())
        } catch (e: Exception) {
            Log.d(
                getString(R.string.tag),
                "ERROR $e"
            )
        }
    }

    private suspend fun compareVersions() {
        val dbLocalVersion = withContext(Dispatchers.IO){
            db.getVersion(gc.getBranchId())
        }
        loadLocal = if (dbOnlineVersion > dbLocalVersion) {
            false
        } else {
            true
        }
        loadList()
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
    
    private fun updateList(){
        if (listData.size > 0) {
            aa = TestListArrayAdapter(this@TestListActivity, listData)
            binding.testListLV.adapter = aa
            setIndexList()
            gc.hideProgress()
        } else {
            gc.hideProgress()
            Toast.makeText(
                this@TestListActivity,
                "No data retrieved from server. Please make sure you have internet connection.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }
    
    private suspend fun loadList(){
        if (loadLocal) {
            loadLocalList()
            if (listData.size == 0) {
                fetchOnlineList()
            }
        } else {
            fetchOnlineList()
            if (listData.size == 0) {
                loadLocalList()
            }
        }
        updateList()
    }
}
