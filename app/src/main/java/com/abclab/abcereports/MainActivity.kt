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

        if (checkPermissions()) {
            Log.d("checkPermissions", checkPermissions().toString() + " ")
            accessCurrentLocation()
        } else {
            Log.d("checkPermissions", checkPermissions().toString() + " ")
            accessCurrentLocation()
        }

        Toast.makeText(
            this.applicationContext,
            "Please make sure that your Location Services it Turned On",
            Toast.LENGTH_LONG
        ).show()


        //Location location = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER))

        //LocationClient locationClient = new LocationClient(get\, this, this);
        /*
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        String c = gc.getSharedString(GlobalClass.PREF_COUNTRY);
        if (c.length()==0) {
	        if (isNetworkEnabled) {
	        	//location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, mLocationListener);
	    		Toast.makeText(getApplicationContext(), "Getting current location using Network", Toast.LENGTH_LONG).show();
	    	} else if (isGPSEnabled) {
	    		//location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, mLocationListener);
	    		Toast.makeText(getApplicationContext(), "Getting current location using GPS", Toast.LENGTH_LONG).show();
	    	}	
        } else {
        	Intent i = new Intent(MainActivity.this,  UserLoginActivity.class);
    		startActivityForResult(i, 1);
    		finish();
        }
        
        
        if (location == null) {
    		Toast.makeText(getApplicationContext(), "Cannot get current location because Location Services is turn off", Toast.LENGTH_LONG).show();
    		Intent i = new Intent(MainActivity.this,  UserLoginActivity.class);
    		startActivityForResult(i, 1);
    		finish();
    	} else {
    		Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
            //mLocationManager.removeUpdates(this);
        	List<Address> addresses=null;
        	try {
				addresses = new Geocoder(getApplicationContext()).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				if (addresses.size()>0) {
					String c =addresses.get(0).getCountryCode();
					gc.setSharedString(GlobalClass.PREF_COUNTRY, c);
		    		Toast.makeText(getApplicationContext(), "Country set to " + c, Toast.LENGTH_SHORT).show();
				}
			} catch (IOException e) {
			}

    		Intent i = new Intent(MainActivity.this,  UserLoginActivity.class);
    		startActivityForResult(i, 1);
    		finish();
    	}
    	*/
    }

    /*
        private final LocationListener mLocationListener = new LocationListener() {
            @Override

            public void onLocationChanged(final Location location) {
                if (location != null) {
                    Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
                    mLocationManager.removeUpdates(this);
                    List<Address> addresses=null;
                    try {
                        addresses = new Geocoder(getApplicationContext()).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses.size()>0) {
                            String c =addresses.get(0).getCountryCode();
                            gc.setSharedString(GlobalClass.PREF_COUNTRY, c);
                            Toast.makeText(getApplicationContext(), "Country set to " + c, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                    }
                }

                Intent i = new Intent(MainActivity.this,  UserLoginActivity.class);
                startActivityForResult(i, 1);
                finish();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

                Toast.makeText(getApplicationContext(), "Status " + String.valueOf(status), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
    */
    private fun accessCurrentLocation() {
        val locationResult: LocationResult = object : LocationResult() {
            override fun gotLocation(location: Location?) {
                if (location != null) {
                    var addresses: List<Address>? = null
                    try {
                        addresses = Geocoder(applicationContext).getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )
                        if (addresses.size > 0) {
                            val c = addresses[0].countryCode
                            gc!!.setSharedString(GlobalClass.PREF_COUNTRY, c)
                            Toast.makeText(
                                applicationContext,
                                "Country set to $c",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                val i =
                    Intent(this@MainActivity, UserLoginActivity::class.java)
                startActivityForResult(i, 1)
                finish()
            }
        }

        val myLocation = MyLocation()
        myLocation.getLocation(this, locationResult)
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
                        accessCurrentLocation()
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


