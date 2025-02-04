package com.abclab.abcereports

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTabHost
import com.abclab.abcereports.UserLoginActivity

class TabHostActivity : AppCompatActivity() {
    private var mTabHost: FragmentTabHost? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val mInflater = menuInflater
        mInflater.inflate(R.menu.report_menu, menu)
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.ic_launcher_foreground)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = title
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setContentView(R.layout.tab_host)
        mTabHost = findViewById<View>(android.R.id.tabhost) as FragmentTabHost
        mTabHost!!.setup(this, supportFragmentManager, R.id.realtabcontent)

        mTabHost!!.addTab(
            mTabHost!!.newTabSpec("final").setIndicator("Final"),
            ReportFinalActivity::class.java, null
        )
        mTabHost!!.addTab(
            mTabHost!!.newTabSpec("pending").setIndicator("Pending"),
            ReportPendingActivity::class.java, null
        )
        mTabHost!!.addTab(
            mTabHost!!.newTabSpec("find").setIndicator("Find"),
            ReportFindActivity::class.java, null
        )
    }

    override fun onBackPressed() {
        logOff()
    }

    private fun logOff() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to Log Off?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, id -> finish() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun exit() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to Exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val intent = Intent(applicationContext, UserLoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("EXIT", true)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.menuLogOff -> {
                logOff()
                return true
            }

            R.id.menuExit -> {
                exit()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}
