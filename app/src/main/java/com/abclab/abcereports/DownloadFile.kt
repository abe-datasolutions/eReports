package com.abclab.abcereports

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Date

@Deprecated("Endpoint Updated. Implementation not applicable")
class DownloadFile internal constructor(private val context: Context) {
    private val gc: GlobalClass = context.applicationContext as GlobalClass
    private var pdfLocURL = ""
    private var ex: Exception? = null

    init {
        var url = ""
        when (gc.getBranchId()) {
            0 -> url = String.format(
                "https://www.abclab.com/ReportSystem/applecreatepdf.aspx?0=%1\$s&1=%2\$s&2=%3\$s&3=%4\$s&4=%5\$s",
                gc.reportNo,
                gc.siteId,
                gc.userId,
                gc.hashCode,
                1
            )

            1 -> url = String.format(
                "https://www.abclab.com/MLAWEB/applecreatepdf.aspx?0=%1\$s&1=%2\$s&2=%3\$s&3=%4\$s&4=%5\$s",
                gc.reportNo,
                gc.siteId,
                gc.userId,
                gc.hashCode,
                1
            )

            2 -> url = String.format(
                "https://www.abclab.com/JKTWEB/applecreatepdf.aspx?0=%1\$s&1=%2\$s&2=%3\$s&3=%4\$s&4=%5\$s",
                gc.reportNo,
                gc.siteId,
                gc.userId,
                gc.hashCode,
                1
            )

            3 -> url = String.format(
                "https://www.abclab.com/CABWEB/applecreatepdf.aspx?0=%1\$s&1=%2\$s&2=%3\$s&3=%4\$s&4=%5\$s",
                gc.reportNo,
                gc.siteId,
                gc.userId,
                gc.hashCode,
                1
            )

            else -> {}
        }
        pdfLocURL = gc.reportNo + ".pdf"
        DownloadFileAsync().execute(url)
    }

    internal inner class DownloadFileAsync : AsyncTask<String?, String?, String?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            gc.showProgress(context, "Downloading File", "Please Wait")
        }

        override fun doInBackground(vararg aurl: String?): String? {
            ex = null
            try {
                val folder = File(gc.rptStorage)
                var success = true
                if (!folder.exists()) {
                    success = folder.mkdirs()
                }
                if (success) {
                    val file = File(folder, pdfLocURL)
                    val dm = Date(file.lastModified())

                    pdfLocURL = file.path // + "/" + pdfLocURL;


                    val c = Calendar.getInstance()
                    c.add(Calendar.MINUTE, -2)
                    Log.d(
                        context.getString(R.string.tag),
                        "File Mod Date A$dm"
                    )
                    Log.d(context.getString(R.string.tag), "File Mod Date B" + c.time.toString())
                    if (dm.before(c.time)) {
                        val fileOutput = FileOutputStream(file)


                        val url = URL(aurl[0])
                        val urlConnection = url.openConnection() as HttpURLConnection


                        urlConnection.requestMethod = "GET"
                        urlConnection.doOutput = true

                        urlConnection.connect()


                        val inputStream = urlConnection.inputStream


                        urlConnection.contentLength
                        val buffer = ByteArray(1024)
                        var bufferLength = 0

                        while ((inputStream.read(buffer).also { bufferLength = it }) > 0) {
                            fileOutput.write(buffer, 0, bufferLength)
                        }
                        fileOutput.close()
                    }
                } else {
                    ex = Exception("Unable to create Temporary Folder")
                }
            } catch (e: Exception) {
                ex = e
            }

            return null
        }

        override fun onPostExecute(unused: String?) {
            gc.hideProgress()
            if (ex == null) {
                try {
                    val targetFile = File(pdfLocURL)
                    val install = Intent(Intent.ACTION_VIEW)
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    //Uri targetUri = Uri.fromFile(targetFile);
                    //Intent intent;
                    //intent = new Intent(Intent.ACTION_VIEW);
                    //intent.setDataAndType(targetUri, "application/pdf");
                    val apkURI = FileProvider.getUriForFile(
                        context,
                        context.applicationContext
                            .packageName + ".provider", targetFile
                    )
                    install.setDataAndType(apkURI, "application/pdf")
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    context.startActivity(install)
                    Toast.makeText(
                        context,
                        "Enter your log in Password to view report",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Log.d("PDFerror", e.toString())
                    gc.alert(
                        context,
                        "Error viewing report",
                        "Please install PDF viewer to view the report."
                    )
                }
            } else {
                gc.alert(context, "Error", ex!!.message)
            }
        }
    }
}