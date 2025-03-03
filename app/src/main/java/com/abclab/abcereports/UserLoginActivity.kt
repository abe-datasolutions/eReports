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
import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.data.network.auth.AuthApi
import com.abedatasolutions.ereports.core.models.auth.LoginData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class UserLoginActivity : AppCompatActivity() {
    private val binding by lazy { 
        UserLoginBinding.inflate(layoutInflater)
    }
    private val gc: GlobalClass by lazy {
        applicationContext as GlobalClass
    }
    private val api by inject<AuthApi>()

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

    private fun tryLogin(username: String, password: String) {
        //Create request
        lifecycleScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Logger.recordException(throwable)
                gc.hideProgress()
                gc.alert(this@UserLoginActivity, "Access Denied", "Invalid Username/Password")
                Toast.makeText(
                    this@UserLoginActivity,
                    "Please make sure you have internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            gc.showProgress(this@UserLoginActivity, "Authenticating", "Please wait...")

            val loggedIn = withContext(Dispatchers.IO){
                val loginData = LoginData(
                    userId = username,
                    password = password
                )
                runCatching {
                    api.login(loginData)
                }.onFailure {
                    throw it
                }.isSuccess
            }

            gc.hideProgress()

            if (loggedIn) {
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
