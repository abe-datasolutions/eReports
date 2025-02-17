package com.abclab.abcereports

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.abclab.abcereports.MyLocation.LocationResult
import java.io.IOException


class MainActivity : Activity() {
    //private LocationManager mLocationManager;
    private var gc: GlobalClass? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        gc = (this.applicationContext as GlobalClass)

        if (checkPermissions()){
            toLogin()
        }
    }

    private fun toLogin(){
        val i = Intent(this@MainActivity, UserLoginActivity::class.java)
        startActivityForResult(i, 1)
        finish()
    }

    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in gc!!.permissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray<String>(),
                GlobalClass.MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            GlobalClass.MULTIPLE_PERMISSIONS -> {
                run {
                    if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                        toLogin()
//                        accessCurrentLocation()
                    } else {
//                    Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
//                            .show();
                    }
                }
                return
            }
        }
    }
}


