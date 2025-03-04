package com.abclab.abcereports

import android.Manifest
import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.abedatasolutions.ereports.core.common.platform.initCommon
import com.abedatasolutions.ereports.core.data.network.apiModule
import com.abedatasolutions.ereports.core.data.network.platform.initBaseUrl
import org.json.JSONArray
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.Locale

class GlobalClass : Application() {

    override fun onCreate() {
        super.onCreate()
        initBaseUrl()
        initCommon()
        startKoin {
            androidContext(this@GlobalClass)
            modules(
                apiModule,
                module {
                    single {
                        PdfDownloader(
                            gc = get<Context>() as GlobalClass,
                            api = get()
                        )
                    }
                }
            )
        }
    }

    /**
     * Authentication Token
     */
    @JvmField
    @Deprecated("Moved to Cookie Authentication")
	var hashCode: String? = null
    /**
     * Authentication Token
     */
    @JvmField
    @Deprecated("Moved to Cookie Authentication")
	var siteId: String? = null

    /**
     * UserName
     */
    @JvmField
    @Deprecated("Moved to Cookie Authentication")
	var userId: String? = null
    @JvmField
	var reportNo: String? = null
    @JvmField
	var testCode: String? = null
    @Deprecated("Moved to Cookie Authentication")
    var branchIdentifier: Int = 0

    @JvmField
	var forceFindResult: Boolean = false
    @JvmField
	var findReportFilters: FindReportFilters = FindReportFilters()

    @JvmField
	var permissions: Array<String> = buildList {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S)
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        add(Manifest.permission.REORDER_TASKS)
    }.toTypedArray()

    val rptStorage: String get() = getExternalFilesDir(
        Environment.DIRECTORY_DOCUMENTS
    ).toString() + "/Reports"

    fun getBranchId(): Int = BuildConfig.BRANCH_ID

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
//            val res = when(BuildConfig.PREF_COUNTRY){
//                "PH" -> R.string.aboutUsMLA
//                "ID" -> R.string.aboutUsJKT
//                else -> R.string.aboutUsUS
//            }
            return getString(R.string.aboutUs)
        }
    val contact: String
        get() {
//            val res = when(BuildConfig.PREF_COUNTRY){
//                "PH" -> R.string.contactUsMLA
//                "ID" -> R.string.contactUsJKT
//                else -> R.string.contactUsUS
//            }
            return getString(R.string.contactUs)
        }

    val dateFormat: String
        get() = when(BuildConfig.PREF_COUNTRY){
            "ID" -> "dd/MM/yyyy"
            else -> "MM/dd/yyyy"
        }

    val dateTimeFormat: String
        get() = when(BuildConfig.PREF_COUNTRY){
            "ID" -> "dd/MM/yyyy kk:mm:ss"
            else -> "MM/dd/yyyy kk:mm:ss"
        }

    val locale: Locale
        get() = when(BuildConfig.PREF_COUNTRY){
            "ID" -> Locale.UK
            else -> Locale.US
        }

    companion object {
        const val MULTIPLE_PERMISSIONS: Int = 10 // code you want.
    }
}
