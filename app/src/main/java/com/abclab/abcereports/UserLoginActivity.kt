package com.abclab.abcereports

import android.app.AlertDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class UserLoginActivity : AppCompatActivity() {
    var txtUsername: EditText? = null
    var txtPassword: EditText? = null
    var btnLogIn: Button? = null
    var btnReports: ImageButton? = null
    var btnAboutUs: ImageButton? = null
    var btnContactUs: ImageButton? = null
    var btnTests: ImageButton? = null
    var imgLogo: ImageView? = null
    private var gc: GlobalClass? = null

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

        gc = (this.applicationContext as GlobalClass)

        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.user_login)

        if (intent.getBooleanExtra("EXIT", false)) {
            Log.d(getString(R.string.tag), "Application Exit")
            finish()
        }
        imgLogo = findViewById<View>(R.id.usrLogInLogo) as ImageView
        if (gc!!.getBranchId() == 2) {
            imgLogo!!.setImageResource(R.drawable.logo_jkt)
        }

        txtUsername = findViewById<View>(R.id.usrLogInTxtUsername) as EditText
        txtPassword = findViewById<View>(R.id.usrLogInTxtPassword) as EditText
        btnLogIn = findViewById<View>(R.id.usrLogInBtnLogIn) as Button
        btnLogIn!!.setOnClickListener {
            if (txtUsername!!.text.length != 0 && txtPassword!!.text.length != 0) {
                val task: AsyncCallWS =
                    AsyncCallWS()
                task.execute()
            } else {
                gc!!.alert(
                    this@UserLoginActivity,
                    "Log-In",
                    "Username / Password\ncannot be empty"
                )
            }
        }
        btnReports = findViewById<View>(R.id.usrLogInBtnReports) as ImageButton
        btnReports!!.setOnClickListener {
            startActivity(
                Intent(
                    this@UserLoginActivity,
                    CacheListActivity::class.java
                )
            )
        }
        btnAboutUs = findViewById<View>(R.id.usrLogInBtnAboutUs) as ImageButton
        btnAboutUs!!.setOnClickListener {
            startActivity(
                Intent(
                    this@UserLoginActivity,
                    AboutUsActivity::class.java
                )
            )
        }
        btnContactUs = findViewById<View>(R.id.usrLogInBtnContactUs) as ImageButton
        btnContactUs!!.setOnClickListener {
            startActivity(
                Intent(
                    this@UserLoginActivity,
                    ContactUsActivity::class.java
                )
            )
        }
        btnTests = findViewById<View>(R.id.usrLogInBtnTests) as ImageButton
        btnTests!!.setOnClickListener {
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

    private inner class AsyncCallWS : AsyncTask<String?, Void?, Void?>() {
        override fun doInBackground(vararg p0: String?): Void? {
            tryLogin(txtUsername!!.text.toString(), txtPassword!!.text.toString())
            return null
        }

        override fun onPostExecute(result: Void?) {
            gc!!.hideProgress()

            if (hashId!!.length == 0) {
                gc!!.alert(this@UserLoginActivity, "Access Denied", "Invalid Username/Password")
                Toast.makeText(
                    this@UserLoginActivity,
                    "Please make sure you have internet connection",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                txtUsername!!.requestFocus()
                gc!!.setBranchId(branchId!!)
                gc!!.siteId = siteId
                gc!!.hashCode = hashId

                startActivity(Intent(this@UserLoginActivity, TabHostActivity::class.java))
                txtUsername!!.setText("")
                txtPassword!!.setText("")
            }
        }

        override fun onPreExecute() {
            siteId = ""
            branchId = 0
            hashId = ""
            gc!!.userId = txtUsername!!.text.toString()
            gc!!.hashCode = "test"
            gc!!.siteId = ""
            gc!!.setBranchId(0)

            gc!!.showProgress(this@UserLoginActivity, "Authenticating", "Please wait...")
        }

        override fun onProgressUpdate(vararg values: Void?) {
        }
    }

    fun tryLogin(username: String?, password: String?) {
        //Create request
        try {
            val httpClient: HttpClient = DefaultHttpClient()
            val httpPost = HttpPost("https://www.abclab.com/eReportApple/Account/Validate")

            val params: MutableList<NameValuePair> = ArrayList(2)
            params.add(BasicNameValuePair("userId", username))
            params.add(BasicNameValuePair("password", password))
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
                    val jData = JSONObject(result)
                    siteId = jData.getString("SiteId")
                    hashId = jData.getString("Hash")
                    branchId = jData.getInt("Branch")
                    Log.d(
                        "branchID2",
                        jData.getInt("Branch").toString() + " " + jData.getString("SiteId")
                    )
                }
            } catch (e: Exception) {
                Log.d(getString(R.string.tag), "ERROR $e")
            }
        } catch (e: Exception) {
            Log.d(getString(R.string.tag), "ERROR $e")
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
