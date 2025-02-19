package com.abclab.abcereports

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.UUID

class MediaStoreQueryTest {
    @get:Rule
    val permissionsRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            *buildList {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                    add("android.permission.READ_EXTERNAL_STORAGE")
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
                    add("android.permission.WRITE_EXTERNAL_STORAGE")
            }.toTypedArray()
        )
    private lateinit var storagePath: String
    private lateinit var context: Context

    @Before
    fun setup(){
        context = ApplicationProvider.getApplicationContext()
        storagePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/Reports"
//        storagePath = Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_DOCUMENTS
//        ).toString() + "/ABC/eReport"
    }

    @Test
    fun checkExistingFiles(){
        val folder = File(storagePath)
        if (!folder.exists()) folder.mkdirs()
        println("Storage: $${storagePath}")
        println("Folder: $folder")
        println("Folder Exists: ${folder.exists()}")
        println("Folder Can Read: ${folder.canRead()}")
        val folderContents = folder.listFiles()?.takeUnless {
            it.isEmpty()
        } ?: run {
            repeat(10) {
//                context.contentResolver.insert(
//                    MediaStore.Files.getContentUri("external"),
//                    ContentValues().apply {
//                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, UUID.randomUUID().toString() + ".pdf")
//                        put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain")
//                        put(MediaStore.Files.FileColumns.RELATIVE_PATH, "Documents/ABC/eReport/")
//                    }
//                )?.also {
//                    println("File Created: $it")
//                }
                val file = File(folder, UUID.randomUUID().toString() + ".pdf")
                file.createNewFile()
                scanFile(file.path)
            }
            folder.listFiles() ?: emptyArray()
        }
        println("Folder Contents: ${folderContents.size}")
        folderContents.forEachIndexed { index, file ->
            println("File $index: $file")
            scanFile(file.path)
        }

        println("Folder Uri: ${folder.toUri()}")
        val pdfFile = File(folder, "231005003.pdf")
        println("PDF File: $pdfFile")
        println("PDF File Exists: ${pdfFile.exists()}")
        println("PDF File Can Read: ${pdfFile.canRead()}")

        val projection = buildList {
            addAll(
                listOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns.MIME_TYPE,
                    MediaStore.Files.FileColumns.TITLE,
                )
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                add(MediaStore.Files.FileColumns.RELATIVE_PATH)
            else add(MediaStore.Files.FileColumns.DATA)
        }.toTypedArray()
        val selection = buildString {
            val column = when {
                projection.contains(MediaStore.Files.FileColumns.RELATIVE_PATH) -> MediaStore.Files.FileColumns.RELATIVE_PATH
                else -> MediaStore.Files.FileColumns.DATA
            }
            append(column)
            append(" LIKE ?")
        }
        val selectionArgs = arrayOf("%/${Environment.DIRECTORY_DOCUMENTS}/ABC/eReport/%")
        context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (!cursor.moveToFirst()) error("Cursor is empty")
            val idColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Files.FileColumns._ID
            )
            val dataColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val mimeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
            do {
                val id = cursor.getLong(idColumn)
                val data = cursor.getString(dataColumn)
                val name = cursor.getString(nameColumn)
                val mime = cursor.getString(mimeColumn)
                val title = cursor.getString(titleColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
                println("File Uri Found(${cursor.position}): $uri")
                println("File Data: $data")
                println("File Name: $name")
                println("File Title: $title")
                println("File Mime: $mime")
            }while (cursor.moveToNext())
        }
        println("Query Finished")
    }

    fun scanFile(path: String) {
        MediaScannerConnection.scanFile(
            context,
            arrayOf(path),
            null
        ) { _, _ ->
            Log.d("CacheList", "File scanned: $path")
        }
    }
}