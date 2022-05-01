package com.example.androidstoragemadness

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

// CREATE, READ, UPDATE, DELETE, COPY, MOVE, RENAME, ENCRYPT, ZIP, COMPRESS file or directory

fun createFile() {
    val file = File("new_file.txt")

    // u need write permission for this
    file.createNewFile()
    println("File path: ${file.absolutePath}")
}

// https://stackoverflow.com/questions/60360368/android-11-r-file-path-access
fun Context.makeFileCopyInCacheDir(contentUri: Uri): String? {
    try {
        val filePathColumn = arrayOf(
            //Base File
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            //Normal File
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        //val contentUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", File(mediaUrl))
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

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
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
    return contentUri.let { RealPathUtil.getRealPathFromURI(this, it).toString() }
}

// https://stackoverflow.com/questions/7769806/convert-bitmap-to-file
fun Bitmap.toFile(fileName: String, context: Context): File {
    //create a file to write bitmap data
    val file = File(context.filesDir, fileName)
    file.createNewFile()

    //Convert bitmap to byte array
    val bitmap: Bitmap = this
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos)
    val bitmapdata: ByteArray = bos.toByteArray()

    //write the bytes in file
    val fos = FileOutputStream(file)
    fos.write(bitmapdata)
    fos.flush()
    fos.close()

    return file
}

fun Context.hasCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasOldStorageWritePermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}

val oldStorageAndCameraPermissions = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.CAMERA
)

val cameraPermission = arrayOf(Manifest.permission.CAMERA)

fun getFilePathFromUri(
    context: Context,
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = uri?.let {
            context.contentResolver.query(
                it,
                projection,
                selection,
                selectionArgs,
                null
            )
        }
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


fun getFileName(path: String): String {
    val uriParts = path.split(File.separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    if (uriParts.isEmpty()) return ""
    return uriParts[uriParts.size - 1]
}

fun getFileSizeInBytes(filePath: String?): Int {
    filePath ?: return 0
    val file = File(filePath)
    if (file.exists()) {
        return file.length().toInt() // in bytes
    }
    return 0
}