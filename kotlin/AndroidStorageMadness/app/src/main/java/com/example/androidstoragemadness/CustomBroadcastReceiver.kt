package com.example.androidstoragemadness

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

const val INTENT_DOWNLOAD_STATUS = "INTENT_DOWNLOAD_STATUS"

class CustomBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                clearDownloadsAfterReboot(context)
            }
            DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                doOnDownloadComplete(context, intent)
            }
        }
    }

    @SuppressLint("Range")
    private fun clearDownloadsAfterReboot(context: Context) = CoroutineScope(IO).launch {
        println("Cancelling all queued downloads of download manager since device rebooted!")
        delay(3000) // Added delay to make sure download has started
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = (DownloadManager.STATUS_FAILED or DownloadManager.STATUS_PENDING or DownloadManager.STATUS_RUNNING or DownloadManager.STATUS_PAUSED).toLong()
        val query = DownloadManager.Query().also { it: DownloadManager.Query ->
            it.setFilterById(downloadId)
        }
        val cursor = downloadManager.query(query) ?: return@launch
        try {
            while (cursor.moveToNext()) {
                downloadManager.remove(cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID)))
            }
        } catch (e: Exception) {
            println(e)
        }
        cursor.close()
    }

    @SuppressLint("Range")
    private fun doOnDownloadComplete(context: Context, intent: Intent) = CoroutineScope(IO).launch {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().also { it: DownloadManager.Query ->
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
            it.setFilterById(downloadId)
        }
        val downloadStatusIntent = Intent(BROADCAST_DOWNLOAD_COMPLETE)
        val cursor = downloadManager.query(query) ?: return@launch

        try {
            if (!cursor.moveToFirst()) return@launch

            val fileName = cursor.fileName()
            val uriString = cursor.uriString()
            val localUriString = cursor.localUriString()
            val columnStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

            println("""
                    fileName: $fileName
                    uriString: $uriString
                    localUriString: $localUriString
                """.trimIndent())

            when (cursor.getInt(columnStatus)) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    println("$fileName download successful")
                    val downloadUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI))
                    val downloadFileLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    if (downloadFileLocalUri != null) {
                        val uri = Uri.parse(downloadFileLocalUri) ?: Uri.EMPTY
                        val file = File(uri.path ?: "")
                        if (localUriString.contains(DIRECTORY_DOWNLOAD_MANAGER_VIDEOS)) {
//                            context.internalFilesDir(directory = DIRECTORY_DOWNLOAD_MANAGER_VIDEOS, fileName = fileName).also {
//                                if (!it.exists()) it.createNewFile()
//                            }
//                            context.copyFileToInternalStorage(
//                                inputFileUri = Uri.parse(downloadFileLocalUri),
//                                inputCustomPath = DIRECTORY_DOWNLOAD_MANAGER_VIDEOS,
//                                inputFileName = fileName
//                            )
                            // context.externalFilesDir(subDir = fileDirectory, fileName = fileName).setLastModified(System.currentTimeMillis())
                            // TODO copy file to internal not working with custom path
                            // TODO delete file from external storage after copying to internal storage
                        }
                        val downloadItem = FileDownloader.DownloadItem(url = "", fileName = "", isDownloaded = true)
                        downloadStatusIntent.putParcelableArrayListExtra(INTENT_DOWNLOAD_STATUS, ArrayList<FileDownloader.DownloadItem>().apply { add(downloadItem) })
                        context.sendBroadcast(downloadStatusIntent)
                    }
                }
                DownloadManager.STATUS_PAUSED -> println("$fileName download paused")
                DownloadManager.STATUS_PENDING -> println("$fileName download pending")
                DownloadManager.STATUS_RUNNING -> println("$fileName download running")
                DownloadManager.STATUS_FAILED -> println("$fileName download failed")
                else -> println("Unknown error ${cursor.getInt(columnStatus)} for $fileName")
            }

            cursor.close()
        } catch (e: Exception) {
            println(e.message)
        }
    }
}