package com.example.androidstoragemadness

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

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
    private fun doOnDownloadComplete(context: Context, intent: Intent) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
        val query = DownloadManager.Query().also { it: DownloadManager.Query ->
            it.setFilterById(downloadId)
        }
        val cursor = downloadManager.query(query)
        val downloadIntent = Intent(DownloadManager.ACTION_DOWNLOAD_COMPLETE)

        try {
            if (cursor != null && cursor.moveToFirst()) {
                val columnStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val success = cursor.getInt(columnStatus) == DownloadManager.STATUS_SUCCESSFUL
                if (success) {
                    val downloadUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI))
                    val downloadFileLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    if (downloadFileLocalUri != null) {
                        val uri = Uri.parse(downloadFileLocalUri) ?: Uri.EMPTY
                        val file = File(uri.path ?: "")
                        downloadIntent.putExtra("FILE_CHECKSUM_PASSED", file.absolutePath)
                    }
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
        cursor.close()
        context.sendBroadcast(downloadIntent)
    }
}
