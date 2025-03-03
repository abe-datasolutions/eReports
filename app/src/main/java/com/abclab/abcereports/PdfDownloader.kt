package com.abclab.abcereports

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.data.network.reports.ReportsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Date

class PdfDownloader(
    private val gc: GlobalClass,
    private val api: ReportsApi
) {
    suspend fun downloadPdf(accession: String): File? = withContext(Dispatchers.IO) {
        val folder = File(gc.rptStorage)
        var success = true
        if (!folder.exists()) {
            success = folder.mkdirs()
        }
        if(!success) error("Unable to create Temporary Folder")
        val file = File(folder, "$accession.pdf")
        val dm = Date(file.lastModified())

        val c = Calendar.getInstance()
        c.add(Calendar.MINUTE, -2)


        if (dm.before(c.time)) {
            val byteArrayFile = api.createPdf(accession) ?: return@withContext null
            FileOutputStream(file).use {
                it.write(byteArrayFile.byteArray)
            }
        }
        file
    }

    companion object {
        fun Activity.viewPdf(pdfFile: File){
            val gc = applicationContext as GlobalClass
            try {
                val targetUri = FileProvider.getUriForFile(
                    gc,
                    "${gc.packageName}.fileprovider",
                    pdfFile
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    setDataAndType(targetUri, "application/pdf")
                }

                startActivity(intent)
                Toast.makeText(
                    gc,
                    "Enter your log in Password to view report",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Logger.recordException(e)
                runCatching {
                    gc.alert(
                        gc,
                        "Error viewing report",
                        "Please install PDF viewer to view the report."
                    )
                }.onFailure {
                    Toast.makeText(
                        gc,
                        "Error viewing report!. Please install PDF viewer to view the report.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}