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
import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.data.network.test.TestApi
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class TestListActivity : AppCompatActivity() {
    private val binding by lazy {
        TestListBinding.inflate(layoutInflater)
    }
    private val gc by lazy {
        applicationContext as GlobalClass
    }
    private val api by inject<TestApi>()
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
        lifecycleScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Logger.recordException(throwable)
                gc.hideProgress()
            }
        ) {
            gc.showProgress(this@TestListActivity, "Checking Online Version", "Please Wait")
            withContext(Dispatchers.IO){
                dbOnlineVersion = api.getVersion(BuildConfig.BRANCH_ID)
            }
            gc.hideProgress()
            compareVersions()
        }
    }

    private suspend fun fetchOnlineList() {
        gc.showProgress(this@TestListActivity, "Loading Data", "Please Wait")
        try {
            try {
                val reports = withContext(Dispatchers.IO){
                    api.getList(BuildConfig.BRANCH_ID)
                }
                if (reports.isEmpty()) return
                reports.forEach {
                    val nData = DatabaseAccess.TestListData()
                    nData.Code = it.code
                    nData.Name = it.name
                    listData.add(nData)
                    db.addTestList(gc.getBranchId(), nData.Code, nData.Name)
                }
                db.setVersion(gc.getBranchId(), dbOnlineVersion)
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
            listData = db.getList(BuildConfig.BRANCH_ID)
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
            db.getVersion(BuildConfig.BRANCH_ID)
        }
        loadLocal = dbOnlineVersion <= dbLocalVersion
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
