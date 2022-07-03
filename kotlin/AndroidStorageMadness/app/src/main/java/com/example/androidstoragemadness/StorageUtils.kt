package com.example.androidstoragemadness

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.min

const val KB = 1024.0
const val MB = 1024.0 * KB
const val GB = 1024.0 * MB
const val TB = 1024.0 * GB

const val FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider"

const val DIRECTORY_DOWNLOAD_MANAGER_VIDEOS = "DIRECTORY_DOWNLOAD_MANAGER_VIDEOS"
const val DIRECTORY_PR_DOWNLOADER_VIDEOS = "DIRECTORY_PR_DOWNLOADER_VIDEOS"

const val BROADCAST_DOWNLOAD_COMPLETE = "BROADCAST_DOWNLOAD_COMPLETE"

val videoUrlList = listOf(
    "https://pixabay.com/videos/download/video-2119_medium.mp4",
    "https://pixabay.com/videos/download/video-13704_medium.mp4",
    "https://pixabay.com/videos/download/video-3998_medium.mp4",
    "https://pixabay.com/videos/download/video-4006_medium.mp4",
    "https://pixabay.com/videos/download/video-22183_medium.mp4",
    "https://pixabay.com/videos/download/video-1890_medium.mp4",
    "https://pixabay.com/videos/download/video-110790_medium.mp4",
    "https://pixabay.com/videos/download/video-113004_medium.mp4"
)

val allowedImageFormats = arrayOf(
    MimeType.IMAGE_JPG.value,
    MimeType.IMAGE_JPEG.value,
    MimeType.IMAGE_JPG.value
)

val allowedVideoFormats = arrayOf(
    MimeType.VIDEO_MP4.value,
    MimeType.VIDEO_MOV.value,
    MimeType.VIDEO_WMV.value,
    MimeType.VIDEO_AVI.value,
    MimeType.VIDEO_MKV.value,
)

// https://stackoverflow.com/questions/60360368/android-11-r-file-path-access
fun Context.makeFileCopyInCacheDir(contentUri: Uri): String? {
    try {
        val filePathColumn = arrayOf(
            // Base File
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            // Normal File
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        // val contentUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", File(mediaUrl))
        val returnCursor = contentUri.let { contentResolver.query(it, filePathColumn, null, null, null) }
        if (returnCursor != null) {
            returnCursor.moveToFirst()
            val nameIndex = returnCursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
            val name = returnCursor.getString(nameIndex)
            val file = File(cacheDir, name)
            val inputStream = contentResolver.openInputStream(contentUri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable = inputStream!!.available()

            // int bufferSize = 1024;
            val bufferSize = min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
            println("File Path: Path " + file.path)
            println("File Size: Size " + file.length())
            return file.absolutePath
        }
    } catch (ex: Exception) {
        println("Exception: ${ex.message}")
    }
    return getFilePathFromUriApi19AndAbove(contentUri).toString()
}

/**
 * Get a file path from a Uri. This will get the the path for Storage Access
 * Framework Documents, as well as the _data field for the MediaStore and
 * other file-based ContentProviders.
 * @author paulburke - https://gist.github.com/tatocaster/32aad15f6e0c50311626
 */
@SuppressLint("NewApi")
private fun Context.getFilePathFromUriApi19AndAbove(fileUri: Uri): String? {
    // DocumentProvider
    when {
        DocumentsContract.isDocumentUri(this, fileUri) -> {
            // ExternalStorageProvider
            when (fileUri.authority) {
                UriAuthority.EXTERNAL_STORAGE_DOC.value -> {
                    val docId = DocumentsContract.getDocumentId(fileUri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                    // TODO handle non-primary volumes
                }
                UriAuthority.DOWNLOADS_DOC.value -> {
                    val id = DocumentsContract.getDocumentId(fileUri)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    return getFilePathFromUri(uri = contentUri, selection = null, selectionArgs = null)
                }
                UriAuthority.MEDIA_DOC.value -> {
                    val docId = DocumentsContract.getDocumentId(fileUri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    val contentUri = when (type) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else -> Uri.EMPTY
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getFilePathFromUri(uri = contentUri, selection = selection, selectionArgs = selectionArgs)
                }
            }
        }
        "content".equals(fileUri.scheme, ignoreCase = true) -> {
            // Return the remote address
            return when (fileUri.authority) {
                UriAuthority.GOOGLE_PHOTOS.value -> fileUri.lastPathSegment
                else -> getFilePathFromUri(uri = fileUri, selection = null, selectionArgs = null)
            }
        }
        "file".equals(fileUri.scheme, ignoreCase = true) -> return fileUri.path
    }
    return null
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 *
 * @param uri           The Uri to query.
 * @param selection     (Optional) Filter used in the query.
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * @return The value of the _data column, which is typically a file path.
 */
fun Context.getFilePathFromUri(
    uri: Uri,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
): String? {
    // Get Column Data
    if (uri == Uri.EMPTY) return null
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(columnIndex)
        }
    } finally {
        cursor?.close()
        println("Cursor closed after  ")
    }
    return null
}

/**
 * Check whether the image is whatsapp image
 * @return true if whatsapp image, else false
 */
fun isWhatsappImage(uriAuthority: String): Boolean {
    return UriAuthority.WHATSAPP.value == uriAuthority
}

fun createFile() {
    val file = File("new_file.txt")

    // u need write permission for this
    file.createNewFile()
    println("File path: ${file.absolutePath}")
}

// https://stackoverflow.com/questions/7769806/convert-bitmap-to-file
fun Bitmap.toFile(
    fileName: String,
    context: Context,
): File {
    // create a file to write bitmap data
    val file = context.internalFilesDir(fileName = fileName).also {
        it.createNewFile() // This doesnt work on subdirectories for some reason even after granting storage read permission
    }

    // Convert bitmap to byte array
    val bos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos)
    val bitmapByteArray: ByteArray = bos.toByteArray()

    // write the bytes in file
    FileOutputStream(file).run {
        write(bitmapByteArray)
        flush()
        close()
    }

    return file
}

fun getFileName(path: String): String {
    val uriParts = path.split(File.separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    if (uriParts.isEmpty()) return ""
    return uriParts[uriParts.lastIndex]
}

fun Context.getFileExtension(uri: Uri?): String? {
    val contentResolver = contentResolver
    val mimeType = MimeTypeMap.getSingleton()
    return mimeType.getExtensionFromMimeType(contentResolver.getType(uri!!))
}

fun Context.isCameraPermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}

fun Context.isOldStorageWritePermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}

val oldStorageAndCameraPermissions = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.CAMERA
)

val cameraPermission = arrayOf(Manifest.permission.CAMERA)

// Get path from Uri
// content resolver instance used for firing a query inside the internal sqlite database that contains all file info from android os
// projection is the set of columns u want to fetch from sqlite db
// query returns Cursor instance which is an interface which holds the data returned by the query
// So cursor holds the data and in this case it holds a single file
// cursor.moveToFirst() moves the cursor on first row, in this case only 1 row. with the cursor u can get each column data
// The 2 columns here are OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
// filesDir is the internal storage path
/** Copy file from external to internal storage */
fun Context.readFileFromExternalDbAndWriteFileToInternalDb(inputFileUri: Uri): File? {
    // Get file name and size
    val projection = arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)
    val cursor = contentResolver?.query(inputFileUri, projection, null, null, null)?.also {
        it.moveToFirst() // We are in first row of the table now
    }
    val inputFileNamePositionInRow = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    val inputFileSizePositionInRow = cursor?.getColumnIndex(OpenableColumns.SIZE)
    val inputFileName = cursor?.getString(inputFileNamePositionInRow ?: 0)
    val inputFileSize = cursor?.getLong(inputFileSizePositionInRow ?: 0)

    println(
        """
            Input File name: $inputFileName
            Input File size: $inputFileSize
        """.trimIndent()
    )

    // Copy file to internal storage
    return copyFileToInternalStorage(inputFileUri = inputFileUri, inputFileName = inputFileName ?: "")
}

fun Context.copyFileToInternalStorage(
    inputFileUri: Uri,
    inputCustomPath: String = "",
    inputFileName: String,
): File? {
    return try {
        val outputFile = if (inputCustomPath.isNotBlank()) {
            File(filesDir?.absolutePath + File.separator + inputCustomPath + File.separator + inputFileName) // Place where our input file is copied
        } else {
            File(filesDir?.absolutePath + File.separator + inputFileName) // Place where our input file is copied
        }
        val fileOutputStream = FileOutputStream(outputFile)
        val fileInputStream = contentResolver?.openInputStream(inputFileUri)
        fileOutputStream.write(fileInputStream?.readBytes())
        fileInputStream?.close()
        fileOutputStream.flush()
        fileOutputStream.close()
        outputFile
    } catch (e: IOException) {
        println(e.message)
        null
    }
}

/** Checks if a volume containing external storage is available for read and write. */
fun isExternalStorageWritable(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}

/** Checks if a volume containing external storage is available to at least read. */
fun isExternalStorageReadable(): Boolean {
    return Environment.getExternalStorageState() in setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Context.availableStorageSpace(
    storageType: StorageType = StorageType.INTERNAL,
): Long {
    val internalStorage = filesDir
    val externalStorage = getExternalFilesDir("") ?: File("")
    val storageManager = applicationContext.getSystemService<StorageManager>() ?: return 0L
    val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(if (storageType == StorageType.INTERNAL) internalStorage else externalStorage)
    return storageManager.getAllocatableBytes(appSpecificInternalDirUuid) // Available Bytes
}

fun File.sizeInBytes(): Int {
    if (!this.exists()) return 0
    return this.length().toInt()
}

fun File.sizeInMB(): Double {
    if (!this.exists()) return 0.0
    return this.sizeInBytes().div(1024.0 * 1024.0)
}

fun File.extension(): String {
    if (!this.exists()) return ""
    return this.absolutePath.substringAfterLast(delimiter = ".").lowercase().trim()
}

fun File.nameWithExtension(): String {
    if (!this.exists()) return ""
    return this.absolutePath.substringAfterLast(delimiter = "/")
}

fun File.name(): String {
    if (!this.exists()) return ""
    return this.nameWithExtension().substringBeforeLast(".")
}

fun File.customName(prefix: String = "my_file"): String {
    if (!this.exists()) return ""
    return prefix.sanitize() + "_" + this.name().sanitize()
}

fun File?.customPath(directory: String?, fileName: String?): String {
    var path = this?.absolutePath

    if (directory != null) {
        path += File.separator + directory
    }

    if (fileName != null) {
        path += File.separator + fileName
    }

    return path ?: ""
}

/** /data/user/0/com.example.androidstoragemadness/files */
fun Context.internalFilesDir(
    directory: String? = null,
    fileName: String? = null,
): File = File(filesDir.customPath(directory, fileName))

/** /storage/emulated/0/Android/data/com.example.androidstoragemadness/files */
fun Context.externalFilesDir(
    rootDir: String = "",
    subDir: String? = null,
    fileName: String? = null,
): File = File(getExternalFilesDir(rootDir).customPath(subDir, fileName))

// https://stackoverflow.com/questions/3425906/creating-temporary-files-in-android
fun Context.createTempFile() {

}

inline fun deleteAllFilesFrom(
    directory: File?,
    withName: String,
    crossinline onDone: () -> Unit = {},
) {
    CoroutineScope(Default).launch {
        directory?.listFiles()?.forEach files@{ it: File? ->
            it ?: return@files
            if (it.name.contains(withName)) {
                if (it.exists()) it.delete()
            }
        }

        withContext(Main) { onDone.invoke() }
    }
}

/**
 * The idea is to replace all special characters with underscores
 * 48 to 57 are ASCII characters of numbers from 0 to 1
 * 97 to 122 are ASCII characters of lowercase alphabets from a to z
 * https://www.w3schools.com/charsets/ref_html_ascii.asp
 * */
fun String?.sanitize(): String {
    if (this.isNullOrBlank()) return ""
    var sanitizedString = ""
    val range0to9 = '0'.code..'9'.code
    val rangeLowerCaseAtoZ = 'a'.code..'z'.code
    this.forEachIndexed { index: Int, char: Char ->
        if (char.code !in range0to9 && char.code !in rangeLowerCaseAtoZ) {
            if (sanitizedString.lastOrNull() != '_' && this.lastIndex != index) {
                sanitizedString += "_"
            }
        } else {
            sanitizedString += char
        }
    }
    return sanitizedString
}

fun Cursor.isStatusSuccessful(): Boolean {
    val columnStatus = this.getColumnIndex(DownloadManager.COLUMN_STATUS)
    return this.getInt(columnStatus) == DownloadManager.STATUS_SUCCESSFUL
}

fun Cursor.fileName(): String {
    val columnTitle = this.getColumnIndex(DownloadManager.COLUMN_TITLE)
    return this.getString(columnTitle)
}

fun Cursor.uriString(): String {
    val columnUri = this.getColumnIndex(DownloadManager.COLUMN_URI)
    return this.getString(columnUri)
}

fun Cursor.localUriString(): String {
    val columnLocalUri = this.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
    return this.getString(columnLocalUri)
}

fun prepareCustomName(
    url: String,
    prefix: String,
): String {
    if (url.isBlank() || prefix.isBlank()) return "file_${UUID.randomUUID()}".sanitize()
    return prefix.sanitize() + "_" +
            url.substringAfterLast(delimiter = "/")
                .substringBeforeLast(delimiter = ".")
                .lowercase(Locale.ROOT)
                .sanitize()
}

private enum class UriAuthority(val value: String) {
    EXTERNAL_STORAGE_DOC("com.android.externalstorage.documents"), // ExternalStorageProvider
    DOWNLOADS_DOC("com.android.providers.downloads.documents"), // Downloads Provider
    MEDIA_DOC("com.android.providers.media.documents"), // Media Provider
    GOOGLE_PHOTOS("com.google.android.apps.photos.content"),
    WHATSAPP("com.whatsapp.provider.media"),
    GOOGLE_DRIVE("com.google.android.apps.docs.storage"),
    GOOGLE_DRIVE_LEGACY("com.google.android.apps.docs.storage.legacy"),
}

enum class FileType(val value: String) {
    OTHER(value = "OTHER"),
    IMAGE(value = "IMAGE"),
    PDF(value = "PDF"),
    VIDEO(value = "VIDEO")
}

// https://www.adobe.com/creativecloud/video/discover/best-video-format.html
enum class MimeType(val value: String) {
    ALL("*/*"),

    FILE_ALL("file/*"),
    FILE_PDF("application/pdf"),

    TEXT_ALL("text/*"),
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),

    IMAGE_ALL("image/*"),
    IMAGE_PNG("image/png"),
    IMAGE_JPG("image/jpg"),
    IMAGE_JPEG("image/jpeg"),

    VIDEO_ALL("video/*"),
    VIDEO_MP4("video/mp4"),
    VIDEO_MOV("video/mov"),
    VIDEO_WMV("video/wmv"),
    VIDEO_AVI("video/avi"),
    VIDEO_MKV("video/mkv"),
}

enum class StorageType {
    INTERNAL, EXTERNAL
}

val extDir = File(Environment.getExternalStorageDirectory(), "take_photo.jpg")
val extPublicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
val dataDir = File(Environment.getDataDirectory(), "take_images")
