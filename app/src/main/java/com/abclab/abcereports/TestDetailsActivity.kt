package com.abclab.abcereports

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abclab.abcereports.ExternalDB.DatabaseAccess
import com.abclab.abcereports.databinding.TestDetailsBinding
import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.data.network.test.TestApi
import com.abedatasolutions.ereports.core.models.test.TestDetailsQuery
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.koin.android.ext.android.inject

class TestDetailsActivity : AppCompatActivity() {
    private val binding by lazy { 
        TestDetailsBinding.inflate(layoutInflater)
    }
    private val gc: GlobalClass by lazy {
        applicationContext as GlobalClass
    }
    private val db: DatabaseAccess by lazy { 
        DatabaseAccess.getInstance(applicationContext)
    }
    private val api by inject<TestApi>()
    private var tstDet: DatabaseAccess.TestDetailsData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.drawable.elabslogo)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = title

        //		db = new DBTestInfo(getApplicationContext());
        Log.d("TestDetails", "Activity Created")
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun renderData() {
        Log.d("Test Details", "Data Loaded: $tstDet")
        if (tstDet != null) {
            var html = if (gc.getBranchId() == 2) {
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
            Log.d("Test Details", "HTML Setup: $html")
            binding.tstDetailsView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        } else {
            Toast.makeText(applicationContext, getString(R.string.testNotFound), Toast.LENGTH_LONG)
                .show()
            finish()
        }
    }
    
    private fun loadData(){
        lifecycleScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Logger.recordException(throwable)
                gc.hideProgress()
            }
        ) {
            gc.showProgress(this@TestDetailsActivity, "Loading Details", "Please Wait")
            loadLocalData()
            if (tstDet == null) {
                fetchOnlineData()
            }
            gc.hideProgress()
            renderData()
        }
    }

    private suspend fun loadLocalData() {
        Log.d("TestDetails", "Loading Local Data")
        Log.d("TestDetails", "TestCode: ${gc.testCode}")
        try {
            tstDet = withContext(Dispatchers.IO){
                db.getDetails(gc.getBranchId(), gc.testCode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(
                getString(R.string.tag),
                "ERROR $e"
            )
        }
    }

    private suspend fun fetchOnlineData() {
        try {
            Log.d("TestDetails", "Fetching Data")
            Log.d("TestDetails", "TestCode: ${gc.testCode}")
            try {
                val details = withContext(Dispatchers.IO){
                    api.getDetails(
                        TestDetailsQuery(
                            BuildConfig.BRANCH_ID,
                            gc.testCode!!
                        )
                    )
                } ?: return

                tstDet = DatabaseAccess.TestDetailsData()
                tstDet!!.Code = details.code
                tstDet!!.Name = details.name
                tstDet!!.Description = details.description
                tstDet!!.Specimen = details.specimen
                tstDet!!.Preparation = details.preparation
                tstDet!!.Running = details.running
                tstDet!!.TAT = details.tAT
                db.setDetails(gc.getBranchId(), tstDet)
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
