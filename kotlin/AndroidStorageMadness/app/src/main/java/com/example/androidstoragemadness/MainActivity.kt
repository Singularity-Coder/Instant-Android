package com.example.androidstoragemadness

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.example.androidstoragemadness.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File


// https://stackoverflow.com/questions/51565897/saving-files-in-android-for-beginners-internal-external-storage

// MANAGE_EXTERNAL_STORAGE - https://www.youtube.com/watch?v=0313bhp-8uA
// Google Play Store rejects this if u are not one of these apps - https://support.google.com/googleplay/android-developer/answer/10467955?hl=en#zippy=%2Cpermitted-uses-of-the-all-files-access-permission

// CREATE, READ, UPDATE, DELETE, COPY, MOVE, RENAME, ENCRYPT, ZIP, COMPRESS file or directory
// CRUD Internal storage
// CRUD External storage
// CRUD Assets

// File Picker - All files

// View PDF Online - Google url, download file
// View Image Online - use coil
// View Video Online - Exo Player

// Download PDF
// Download Image
// Download Video

class MainActivity : AppCompatActivity() {

    private lateinit var takenPhotoFile: File
    private lateinit var takenVideoFile: File
    private lateinit var binding: ActivityMainBinding

    private val permissionsResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, @JvmSuppressWildcards Boolean>? ->
        permissions?.entries?.forEach { it: Map.Entry<String, @JvmSuppressWildcards Boolean> ->
            val permission = it.key
            val isGranted = it.value
            when {
                isGranted -> Unit
                ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
                    // Permission denied but not permanently, tell user why you need it. Ideally provide a button to request it again and another to dismiss
                }
                else -> {
                    // permission permanently denied. Show settings dialog
                }
            }
        }
    }

    /** Needs READ_EXTERNAL_STORAGE permission */
    private val externalStorageVideoSelectionResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it: ActivityResult? ->
        if (it?.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult
        data.clipData ?: return@registerForActivityResult
        val uriList = ArrayList<Uri>()
        for (i in 0 until data.clipData!!.itemCount) {
            val uri = data.clipData!!.getItemAt(i).uri
            uriList.add(uri)
        }
        val thumbnailVideoUri = data.data
        val originalVideoUri = uriList.first()
        val file = File(getFilePathFromUri(uri = originalVideoUri) ?: "")

        println(
            """
            originalVideoUri: $originalVideoUri
            thumbnailVideoUri: $thumbnailVideoUri
        """.trimIndent()
        )

        showFile(
            type = FileType.VIDEO.value,
            path = file.absolutePath
        )
    }

    /** Needs READ_EXTERNAL_STORAGE permission */
    private val externalStorageImageSelectionResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it: ActivityResult? ->
        if (it?.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult
        data.clipData ?: return@registerForActivityResult
        val uriList = ArrayList<Uri>()
        for (i in 0 until data.clipData!!.itemCount) {
            val uri = data.clipData!!.getItemAt(i).uri
            uriList.add(uri)
        }
        val thumbnailImageUri = data.data
        val originalImageUri = uriList.first()
        val file = File(getFilePathFromUri(uri = originalImageUri) ?: "")

        println(
            """
            originalImageUri: $originalImageUri
            thumbnailImageUri: $thumbnailImageUri
        """.trimIndent()
        )

        showFile(
            type = FileType.IMAGE.value,
            path = file.absolutePath
        )
    }

    private val externalStoragePdfSelectionResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it: ActivityResult? ->
        if (it?.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult
        val uri = data.data ?: Uri.EMPTY
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) // Permission needed if you want to retain access even after reboot
        val path = makeFileCopyInCacheDir(uri) ?: ""
        val file = File(path)

        println("originalPdfUri: ${data.data}")

        showFile(
            type = FileType.PDF.value,
            path = file.absolutePath
        )
    }

    private val imageSelectionResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it: ActivityResult? ->
        if (it?.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult
        val file = readFileFromExternalDbAndWriteFileToInternalDb(data.data ?: Uri.EMPTY) ?: return@registerForActivityResult

        println("originalImageUri: ${data.data}")

        showFile(
            type = FileType.IMAGE.value,
            path = file.absolutePath
        )
    }

    private val videoSelectionResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it: ActivityResult? ->
        if (it?.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult
        val file = readFileFromExternalDbAndWriteFileToInternalDb(data.data ?: Uri.EMPTY) ?: return@registerForActivityResult

        println("originalVideoUri: ${data.data}")

        showFile(
            type = FileType.VIDEO.value,
            path = file.absolutePath
        )
    }

    private val pdfSelectionResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it: ActivityResult? ->
        if (it?.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult
        val file = readFileFromExternalDbAndWriteFileToInternalDb(inputFileUri = data.data ?: Uri.EMPTY) ?: return@registerForActivityResult

        println("originalPdfUri: ${data.data}")

        showFile(
            type = FileType.PDF.value,
            path = file.absolutePath
        )
    }

    // Doesnt work on samsung, oppo, oneplus
    private val takePhotoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it: ActivityResult? ->
        if (it?.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult
        val thumbnailBitmap = (data.extras?.get("data") as? Bitmap)

        println(
            """
            thumbnailBitmap: ${data.extras?.get("data")}
            originalImagePath: ${takenPhotoFile.absolutePath}
        """.trimIndent()
        )

        showFile(
            type = FileType.IMAGE.value,
            path = takenPhotoFile.absolutePath,
        )
    }

    private val takeVideoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it: ActivityResult? ->
        if (it?.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult
        val thumbnailBitmap = (data.extras?.get("data") as? Bitmap)

        println(
            """
            thumbnailBitmap: ${data.extras?.get("data")}
            originalVideoPath: ${takenVideoFile.absolutePath}
        """.trimIndent()
        )

        showFile(
            type = FileType.VIDEO.value,
            path = takenVideoFile.absolutePath,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permissionsResult.launch(oldStorageAndCameraPermissions)
        setUpUserActionListeners()
    }

    private fun setUpUserActionListeners() {
        binding.btnSelectAnyFilesManageStorage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                } else {
                }
            }
        }

        binding.apply {
            btnSelectImageExtStorage.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    type = MimeType.IMAGE_ALL.value
                    putExtra(Intent.EXTRA_MIME_TYPES, allowedImageFormats) // Allow only images in these formats
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // If you dont want multiple selection remove this
                }
                if (intent.resolveActivity(packageManager) == null) return@setOnClickListener
                externalStorageImageSelectionResult.launch(Intent.createChooser(intent, "Select Images"))
            }
            btnSelectVideoExtStorage.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).apply {
                    type = MimeType.VIDEO_ALL.value
                    putExtra(Intent.EXTRA_MIME_TYPES, allowedVideoFormats) // Allow only videos in these formats
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // If you dont want multiple selection remove this
                }
                if (intent.resolveActivity(packageManager) == null) return@setOnClickListener
                externalStorageVideoSelectionResult.launch(Intent.createChooser(intent, "Select Videos"))
            }
            btnSelectPdfExtStorage.setOnClickListener {
                // https://stackoverflow.com/questions/34664915/no-persistable-permission-grants-found-for-uri
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = MimeType.FILE_PDF.value
                }
                if (intent.resolveActivity(packageManager) == null) return@setOnClickListener
                externalStoragePdfSelectionResult.launch(Intent.createChooser(intent, "Select Documents"))
            }
        }

        binding.apply {
            btnSelectImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = MimeType.IMAGE_ALL.value
                }
                if (intent.resolveActivity(packageManager) == null) return@setOnClickListener
                imageSelectionResult.launch(intent)
            }
            btnSelectVideo.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = MimeType.VIDEO_ALL.value
                }
                if (intent.resolveActivity(packageManager) == null) return@setOnClickListener
                videoSelectionResult.launch(intent)
            }
            btnSelectPdf.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = MimeType.FILE_PDF.value
                }
                if (intent.resolveActivity(packageManager) == null) return@setOnClickListener
                pdfSelectionResult.launch(intent)
            }
        }

        binding.apply {
            btnTakePhoto.setOnClickListener {
                if (!isCameraPresent()) {
                    Snackbar.make(binding.root, "You don't have a camera on your device!", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // https://developer.android.com/reference/android/provider/MediaStore#ACTION_IMAGE_CAPTURE
                takenPhotoFile = internalFilesDir(fileName = "camera_photo_${System.currentTimeMillis()}.jpg").also {
                    if (!it.exists()) it.createNewFile()
                }
                /** fileProvider file should be exactly in the "path" attribute that u define in file_paths.xml and declare provider in manifest */
                val fileProvider = FileProvider.getUriForFile(this@MainActivity, FILE_PROVIDER_AUTHORITY, takenPhotoFile)
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
                }
                if (intent.resolveActivity(packageManager) == null) return@setOnClickListener
                takePhotoResult.launch(intent)
            }
            btnTakeVideo.setOnClickListener {
                if (!isCameraPresent()) {
                    Snackbar.make(binding.root, "You don't have a camera on your device!", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                takenVideoFile = internalFilesDir(fileName = "camera_video_${System.currentTimeMillis()}.mp4").also {
                    if (!it.exists()) it.createNewFile()
                }
                /** fileProvider file should be exactly in the "path" attribute that u define in file_paths.xml and declare provider in manifest */
                val fileProvider = FileProvider.getUriForFile(this@MainActivity, FILE_PROVIDER_AUTHORITY, takenVideoFile)
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
                }
                if (intent.resolveActivity(packageManager) == null) return@setOnClickListener
                takeVideoResult.launch(intent)
            }
        }

        binding.apply {
            btnDownloadMultipleFilesDownloadManager.setOnClickListener {
                if (!isValidDownload(isDownloadManager = true)) return@setOnClickListener
                getDownloadableUrlFromWebView(url = videoUrlList.shuffled().first()) { downloadableUrl: String ->
                    val downloadItemList = listOf(downloadableUrl).map { url: String ->
                        FileDownloader.DownloadItem(
                            url = url,
                            fileName = prepareCustomName(url = url, prefix = "download_man")
                        )
                    }
                    FileDownloader(
                        downloadItemsList = downloadItemList,
                        context = this@MainActivity,
                        fileDirectory = DIRECTORY_DOWNLOAD_MANAGER_VIDEOS,
                        downloadTitle = "Download Videos",
                        downloadDesc = "Downloading Pixabay Videos...",
                        onSuccess = { it: ArrayList<FileDownloader.DownloadItem?> ->
                            showFile(
                                type = FileType.VIDEO.value,
                                path = externalFilesDir(subDir = DIRECTORY_DOWNLOAD_MANAGER_VIDEOS).listFiles()?.last()?.absolutePath ?: ""
                            )
                        },
                        onFailure = { it: ArrayList<FileDownloader.DownloadItem?> ->
                            binding.root.showSnackBar("Failed to download files.")
                        }
                    )
                }
            }
            btnDownloadFilePrDownloader.setOnClickListener {
                if (!isValidDownload()) return@setOnClickListener
                getDownloadableUrlFromWebView(url = videoUrlList.first()) { downloadableUrl: String ->
                    val fileName = prepareCustomName(url = downloadableUrl, prefix = "pr_download")
                    val file = internalFilesDir(directory = DIRECTORY_PR_DOWNLOADER_VIDEOS, fileName = fileName)
                    val filePath = internalFilesDir(directory = DIRECTORY_PR_DOWNLOADER_VIDEOS).absolutePath

                    showNotification(fileName)

                    println(
                        """
                        File name: $fileName
                        File path: ${file.absolutePath}
                    """.trimIndent()
                    )

                    if (file.exists()) {
                        showFile(
                            type = FileType.VIDEO.value,
                            path = file.absolutePath
                        )
                        return@getDownloadableUrlFromWebView
                    }

                    PRDownloader
                        .download(downloadableUrl, filePath, fileName)
                        .build()
                        .start(object : OnDownloadListener {
                            override fun onDownloadComplete() {
                                binding.root.showSnackBar("$fileName Download Complete")
                                showFile(
                                    type = FileType.VIDEO.value,
                                    path = file.absolutePath
                                )
                            }

                            override fun onError(error: com.downloader.Error?) {
                                binding.root.showSnackBar("Error downloading $fileName - $error")
                            }
                        })
                }
            }
            btnDownloadMultipleVideosPrDownloader.setOnClickListener {
                if (!isValidDownload()) return@setOnClickListener
                val downloadedFilesSuccessList = ArrayList<File>()
                val downloadedFilesFailedList = ArrayList<File>()
                var urlCount = 0
                val url = videoUrlList.first()

                fun downloadWithPrDownloader(url: String, onDownloadComplete: () -> Unit = {}) {
                    getDownloadableUrlFromWebView(url = url) { downloadableUrl: String ->
                        val fileName = prepareCustomName(url = downloadableUrl, prefix = "pr_download")
                        val file = internalFilesDir(directory = DIRECTORY_PR_DOWNLOADER_VIDEOS, fileName = fileName)
                        val filePath = internalFilesDir(directory = DIRECTORY_PR_DOWNLOADER_VIDEOS).absolutePath

                        showNotification(fileName)

                        println(
                            """
                            File name: $fileName
                            File path: ${file.absolutePath}
                        """.trimIndent()
                        )

                        if (file.exists()) {
                            if (urlCount < videoUrlList.lastIndex) {
                                downloadWithPrDownloader(videoUrlList[urlCount++])
                            } else {
                                onDownloadComplete.invoke()
                            }
                            return@getDownloadableUrlFromWebView
                        }

                        PRDownloader
                            .download(downloadableUrl, filePath, fileName)
                            .build()
                            .start(object : OnDownloadListener {
                                override fun onDownloadComplete() {
                                    binding.root.showSnackBar("$fileName Download Complete")
                                    downloadedFilesSuccessList.add(file)
                                    if (urlCount < videoUrlList.lastIndex) {
                                        downloadWithPrDownloader(videoUrlList[urlCount++])
                                    } else {
                                        onDownloadComplete.invoke()
                                    }
                                }

                                override fun onError(error: com.downloader.Error?) {
                                    binding.root.showSnackBar("Error downloading $fileName - $error")
                                    downloadedFilesFailedList.add(file)
                                }
                            })
                    }
                }

                downloadWithPrDownloader(url) {
                    println("All downloads complete. Show file ${downloadedFilesSuccessList.last().absolutePath}")
                    showFile(
                        type = FileType.VIDEO.value,
                        path = downloadedFilesSuccessList.last().absolutePath
                    )
                }
            }
        }
    }

    private fun isValidDownload(isDownloadManager: Boolean = false): Boolean {
        if (!isOnline()) {
            binding.root.showSnackBar("You are offline. Connect to the Internet and try again.")
            return false
        }

        if (!isExternalStorageReadable()) {
            binding.root.showSnackBar("Cannot read your external storage.")
            return false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (availableStorageSpace() < 15 * MB) {
                binding.root.showSnackBar("Insufficient storage space. Need at least 15 MB free space available.")
                return false
            }
        }

        if (batteryPercent() < 5) {
            binding.root.showSnackBar("Insufficient battery. Need at least 5 percent power.")
            return false
        }

        if (isDownloadManager) {
            if (!isAppEnabled(App.DOWNLOAD_MANAGER.id)) {
                binding.root.showSnackBar("Download Manager system App is disabled. Go to Settings -> Apps -> All Apps -> Show System -> Search for Download Manager -> Enable")
                return false
            }
        }

        return true
    }

    private fun showFile(
        type: String,
        path: String,
        url: String? = null,
    ) {
        supportFragmentManager.beginTransaction().add(
            binding.clContainer.id,
            FileDetailFragment.newInstance(
                type = type,
                path = path,
                url = url
            ),
            "FileDetailFragment"
        ).addToBackStack(null).commit()
    }
}