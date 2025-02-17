package com.abclab.abcereports

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abclab.abcereports.databinding.UserLoginBinding
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class UserLoginActivity : AppCompatActivity() {
    private val binding by lazy { 
        UserLoginBinding.inflate(layoutInflater)
    }
    private val gc: GlobalClass by lazy {
        applicationContext as GlobalClass
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, id -> finish() }
            .setNegativeButton("No", null)
            .show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(binding.root)

        if (intent.getBooleanExtra("EXIT", false)) {
            Log.d(getString(R.string.tag), "Application Exit")
            finish()
        }
        binding.usrLogInBtnLogIn.setOnClickListener {
            if (binding.usrLogInTxtUsername.text.isNotEmpty() && binding.usrLogInTxtPassword.text.isNotEmpty()) {
                tryLogin(binding.usrLogInTxtUsername.text.toString().trim(), binding.usrLogInTxtPassword.text.toString().trim())
            } else {
                gc.alert(
                    this@UserLoginActivity,
                    "Log-In",
                    "Username / Password\ncannot be empty"
                )
            }
        }
        binding.usrLogInBtnReports.setOnClickListener {
            startActivity(
                Intent(
                    this@UserLoginActivity,
                    CacheListActivity::class.java
                )
            )
        }
        binding.usrLogInBtnAboutUs.setOnClickListener {
            startActivity(
                Intent(
                    this@UserLoginActivity,
                    AboutUsActivity::class.java
                )
            )
        }
        binding.usrLogInBtnContactUs.setOnClickListener {
            startActivity(
                Intent(
                    this@UserLoginActivity,
                    ContactUsActivity::class.java
                )
            )
        }
        binding.usrLogInBtnTests.setOnClickListener {
            startActivity(
                Intent(
                    this@UserLoginActivity,
                    TestListActivity::class.java
                )
            )
        }

        Toast.makeText(
            this,
            "Note: Application require internet connection to view reports.",
            Toast.LENGTH_LONG
        ).show()
    }

    fun tryLogin(username: String, password: String) {
        //Create request
        lifecycleScope.launch {
            siteId = ""
            branchId = 0
            hashId = ""
            gc.userId = binding.usrLogInTxtUsername.text.toString()
            gc.hashCode = "test"
            gc.siteId = ""
            gc.setBranchId(0)

            gc.showProgress(this@UserLoginActivity, "Authenticating", "Please wait...")

            withContext(Dispatchers.IO){
                try {
                    var result = ""
                    try {
                        result = HttpClient(Android).submitForm(
                            url = "https://www.abclab.com/eReportApple/Account/Validate",
                            formParameters = Parameters.build {
                                append("userId", username)
                                append("password", password)
                            }
                        ).bodyAsText()

                        if (result.isNotEmpty()) {
                            val jData = JSONObject(result)
                            siteId = jData.getString("SiteId")
                            hashId = jData.getString("Hash")
                            branchId = jData.getInt("Branch")
                            Log.d(
                                "branchID2",
                                jData.getInt("Branch").toString() + " " + jData.getString("SiteId")
                            )
                        }
                        Unit
                    } catch (e: Exception) {
                        Log.d(getString(R.string.tag), "ERROR $e")
                    }
                } catch (e: Exception) {
                    Log.d(getString(R.string.tag), "ERROR $e")
                }
            }

            gc.hideProgress()

            if (hashId.isNullOrEmpty()) {
                gc.alert(this@UserLoginActivity, "Access Denied", "Invalid Username/Password")
                Toast.makeText(
                    this@UserLoginActivity,
                    "Please make sure you have internet connection",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                binding.usrLogInTxtUsername.requestFocus()
                gc.setBranchId(branchId!!)
                gc.siteId = siteId
                gc.hashCode = hashId

                startActivity(Intent(this@UserLoginActivity, TabHostActivity::class.java))
                binding.usrLogInTxtUsername.setText("")
                binding.usrLogInTxtPassword.setText("")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private var siteId: String? = null
        private var branchId: Int? = null
        private var hashId: String? = null
    }
}
