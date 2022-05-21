package com.example.androidstoragemadness

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class FileDownloader(
    downloadItemsList: ArrayList<DownloadItem>,
    private val context: Context,
    private val fileDirectory: String,
    private val downloadTitle: String,
    private val downloadDesc: String,
    private val isOAuth: Boolean = false,
    private val oAuthHeader: String? = "",
    private val oAuthValue: String? = "",
    private val onSuccess: () -> Unit = {},
    private val onFailure: () -> Unit = {},
) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private var expectedFilesCount = 0
    private var downloadedFilesCount = 0
    private var retryCount = 2
    private var reDownloadItemsList = ArrayList<DownloadItem>()

    private val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val query = DownloadManager.Query().also { it: DownloadManager.Query ->
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
                it.setFilterById(downloadId)
            }
            val cursor = downloadManager.query(query)
            var isStatusSuccessful = false
            var fileName = ""
            var uriString = ""
            var localUriString = ""

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    isStatusSuccessful = cursor.isStatusSuccessful()
                    fileName = cursor.fileName()
                    uriString = cursor.uriString()
                    localUriString = cursor.localUriString()
                    cursor.close()
                }
            } catch (e: Exception) {
                println(e.message)
            }

            println("""
                    isStatusSuccessful: $isStatusSuccessful
                    fileName: $fileName
                    uriString: $uriString
                    localUriString: $localUriString
                 """.trimIndent())

//          if (localUriString?.contains("biiiigfile") == true) {
//              val file = File(Uri.parse(downloadFileLocalUri).path)
//              val path = file.absolutePath
//              moveFile(
//                  context = context,
//                  inputPath = getDirectory(path),
//                  path = downloadFileLocalUri.substringBeforeLast("/").substringAfterLast("/"),
//                  fileName = downloadFileLocalUri.substringAfterLast("/")
//              )
//          }

            if (isStatusSuccessful) {
                downloadedFilesCount++
                context.getExternalStoragePathOrFile(subDir = fileDirectory, fileName = fileName).setLastModified(System.currentTimeMillis())
                if (downloadedFilesCount == expectedFilesCount) {
                    unregisterReceiver(context)
                    onSuccess.invoke()
                }
            } else {
                reDownloadItemsList.add(DownloadItem(url = uriString, fileName = fileName))
                if (retryCount > 0) {
                    startDownloading(reDownloadItemsList)
                    retryCount--
                    reDownloadItemsList.clear()
                } else {
                    unregisterReceiver(context)
                    onFailure.invoke()
                }
            }
        }
    }

    init {
        registerReceiver()
        startDownloading(downloadItemsList)
    }

    private fun startDownloading(downloadItemsList: ArrayList<DownloadItem>) = CoroutineScope(IO).launch {
        if (downloadItemsList.isEmpty()) return@launch
        downloadedFilesCount = 0
        expectedFilesCount = downloadItemsList.size
        downloadItemsList.forEach { downloadItem: DownloadItem ->
            val downloadRequest = DownloadManager.Request(Uri.parse(downloadItem.url)).apply {
                if (isOAuth) addRequestHeader(oAuthHeader, oAuthValue)
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                setTitle(downloadTitle)
                setDescription(downloadDesc)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                setDestinationInExternalFilesDir(context, fileDirectory, downloadItem.fileName)
            }
            downloadManager.enqueue(downloadRequest).also { downloadId: Long ->
                println("downloadId: $downloadId")
            }
        }
    }

    private fun registerReceiver() {
        try {
            context.registerReceiver(downloadCompleteReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) // Register broadcast receiver to get download status
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun unregisterReceiver(context: Context) {
        try {
            context.unregisterReceiver(downloadCompleteReceiver)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    data class DownloadItem(
        var url: String,
        var fileName: String,
    )
}


