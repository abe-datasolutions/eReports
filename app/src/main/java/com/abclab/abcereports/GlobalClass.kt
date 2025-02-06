package com.abclab.abcereports

import android.Manifest
import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.os.Environment
import android.util.Log
import org.json.JSONArray
import java.util.Locale

class GlobalClass : Application() {
    /**
     * Authentication Token
     */
    @JvmField
	var hashCode: String? = null
    /**
     * Authentication Token
     */
    @JvmField
	var siteId: String? = null

    /**
     * UserName
     */
    @JvmField
	var userId: String? = null
    @JvmField
	var reportNo: String? = null
    @JvmField
	var testCode: String? = null
    var branchIdentifier: Int = 0

    @JvmField
	var forceFindResult: Boolean = false
    @JvmField
	var findReportFilters: FindReportFilters = FindReportFilters()

    @JvmField
	var permissions: Array<String> = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.REORDER_TASKS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @JvmField
	val rptStorage: String = Environment.getExternalStorageDirectory().toString() + "/ABC/eReport"

    fun getSharedString(pref: String?): String {
        val settings = getSharedPreferences(PREFS_NAME, 0)
        return settings.getString(pref, "").trim { it <= ' ' }
    }

    fun setSharedString(pref: String?, value: String?) {
        val settings = getSharedPreferences(PREFS_NAME, 0)
        val editor = settings.edit()
        editor.putString(pref, value)
        editor.commit()
    }

    fun getBranchId(): Int {
        val rVal: Int
        val c = getSharedString(PREF_COUNTRY)
        rVal = if (c.equals("PH", ignoreCase = true)) {
            1
        } else if (c.equals("ID", ignoreCase = true)) {
            2
        } else if (c.equals("US", ignoreCase = true)) {
            0
        } else {
            -1
        }
        return rVal
    }

    fun setBranchId(value: Int) {
        branchIdentifier = value
    }


    private var progDlg: ProgressDialog? = null

    fun showProgress(context: Context?, title: String?, message: String?) {
        progDlg = ProgressDialog(context)
        progDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progDlg!!.setTitle(title)
        progDlg!!.setMessage(message)
        progDlg!!.setCancelable(false)
        progDlg!!.isIndeterminate = true
        progDlg!!.show()
    }

    fun hideProgress() {
        progDlg!!.dismiss()
    }

    private var alertDlg: AlertDialog? = null
    fun alert(context: Context?, title: String?, msg: String?) {
        alertDlg = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setNegativeButton(
                android.R.string.yes
            ) { dialog, which -> alertDlg!!.dismiss() }
            .show()
    }

    fun PopulateData(result: String?, listData: ArrayList<ResultData?>) {
        try {
            if (listData.size > 0) {
                listData.removeAt(listData.size - 1)
            }
            val jArray = JSONArray(result)
            for (i in 0 until jArray.length()) {
                val jData = jArray.getJSONObject(i)
                val nData = ResultData()
                nData.reportNo = jData.getString("ReportNo")
                nData.patientName = jData.getString("PatientName")
                nData.gender = jData.getString("Gender")
                nData.reportDate = jData.getString("ReportDate")
                nData.status = jData.getString("Status")
                listData.add(nData)
            }
            val lData = ResultData()
            lData.reportNo = "LAST"
            lData.patientName = ""
            lData.gender = ""
            lData.reportDate = ""
            lData.status = ""
            listData.add(lData)
        } catch (e: Exception) {
            Log.d(getString(R.string.tag), "ERROR $e")
        }
    }

    val about: String
        get() {
            val c = getSharedString(PREF_COUNTRY)
            var d = ""

            d = if (c.equals("PH", ignoreCase = true)) {
                getString(R.string.aboutUsMLA)
            } else if (c.equals("ID", ignoreCase = true)) {
                getString(R.string.aboutUsJKT)
            } else {
                getString(R.string.aboutUsUS)
            }
            return d
        }
    val contact: String
        get() {
            val c = getSharedString(PREF_COUNTRY)
            var d = ""

            d = if (c.equals("PH", ignoreCase = true)) {
                getString(R.string.contactUsMLA)
            } else if (c.equals("ID", ignoreCase = true)) {
                getString(R.string.contactUsJKT)
            } else if (c.equals("US", ignoreCase = true)) {
                getString(R.string.contactUsUS)
            } else {
                getString(R.string.contactUsDefault)
            }
            return d
        }

    val dateFormat: String
        get() {
            val c = getSharedString(PREF_COUNTRY)
            var d = ""

            d = if (c.equals("ID", ignoreCase = true)) {
                "dd/MM/yyyy"
            } else {
                "MM/dd/yyyy"
            }

            return d
        }
    val dateTimeFormat: String
        get() {
            val c = getSharedString(PREF_COUNTRY)
            var d = ""

            d = if (c.equals("ID", ignoreCase = true)) {
                "dd/MM/yyyy kk:mm:ss"
            } else {
                "MM/dd/yyyy kk:mm:ss"
            }
            return d
        }
    val locale: Locale
        get() {
            val c = getSharedString(PREF_COUNTRY)
            var d = Locale.US

            if (c.equals("ID", ignoreCase = true)) {
                d = Locale.UK
            }
            return d
        }


    companion object {
        private const val PREFS_NAME = "eLabsPref"
        const val PREF_COUNTRY: String = "country"
        const val MULTIPLE_PERMISSIONS: Int = 10 // code you want.
    }
}
