package com.example.androidstoragemadness

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.androidstoragemadness.databinding.ActivityMainBinding
import java.io.File

// Internal storage
// External storage
// Assets

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permissionsResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, @JvmSuppressWildcards Boolean>? ->
        permissions?.entries?.forEach { it: Map.Entry<String, @JvmSuppressWildcards Boolean> ->
            val permission = it.key
            val isGranted = it.value
            when {
                isGranted -> {
                }
                ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
                    // Permission denied but not permanently, tell user why you need it. Ideally provide a button to request it again and another to dismiss
                }
                else -> {
                    // permission permanently denied. Show settings dialog
                }
            }
        }
    }

    private val imageSelectionResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it: ActivityResult? ->
        if (it?.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult
        val uri = if (null != data.clipData) {
            try {
                data.clipData?.getItemAt(0)?.uri
            } catch (e: Exception) {
                Uri.EMPTY
            }
        } else {
            data.data
        }
        val file = File(getFilePathFromUri(this, uri, null, null) ?: "")
        val fileName = getFileName(file.absolutePath)
        println(
            """
            Uri: ${data.data}
            SELECT_IMAGE path: ${file.absolutePath}
        """.trimIndent()
        )
        showFile(file = file)
    }

    private val pdfSelectionResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it: ActivityResult? ->
        if (it?.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult
        //Permission needed if you want to retain access even after reboot
        val uri = data.data ?: Uri.EMPTY
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // Perform operations on the document using its URI.

        val path = makeFileCopyInCacheDir(uri)
        val file = File(path)
        println(
            """
            Path: ${path.toString()}
            Uri: ${data.data}
            SELECT_PDF path: ${file.absolutePath}
        """.trimIndent()
        )
        showFile(file = file)
    }

    private val takePhotoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it: ActivityResult? ->
        if (it?.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult
        val bitmap = (data.extras?.get("data") as? Bitmap) ?: return@registerForActivityResult
//        val fileName = "note_pic_${System.currentTimeMillis()}.jpg"
//        val result = MediaStore.Images.Media.insertImage(contentResolver, bitmap, fileName, null)
//        val file = File(getFilePathFromUri(this, Uri.parse(result), null, null) ?: "")
        binding.ivResultImage.setImageBitmap(bitmap)
        val file = bitmap.toFile(fileName = "note_pic_${System.currentTimeMillis()}.jpg", context = this)
        println(
            """
            Uri: ${data.extras}
            Uri: ${data.extras?.get("data")}
            TAKE_PHOTO path: ${file.absolutePath}
        """.trimIndent()
        )
        showFile(file = file)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permissionsResult.launch(oldStorageAndCameraPermissions)
        setUpUserActionListeners()
    }

    private fun setUpUserActionListeners() {
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        //     if (!Environment.isExternalStorageManager()) return
        // }


        binding.btnSelectImage.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
//                type = "image/*"
//                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//            imageSelectionResult.launch(intent)

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
//                type = "*/*"
//                val mimetypes = arrayOf("image/png", "image/jpg", "image/jpeg")
//                putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
            }
            imageSelectionResult.launch(intent)
//            imageSelectionResult.launch(Intent.createChooser(intent, "Select Image"))

        }
        binding.btnSelectPdf.setOnClickListener {
            // https://stackoverflow.com/questions/34664915/no-persistable-permission-grants-found-for-uri
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            pdfSelectionResult.launch(intent)
        }
        binding.btnTakeImage.setOnClickListener {
            // https://developer.android.com/reference/android/provider/MediaStore#ACTION_IMAGE_CAPTURE
            // The caller may pass an extra EXTRA_OUTPUT to control where this image will be written. If the EXTRA_OUTPUT is not present, then a small sized image is returned as a Bitmap object in the extra field.
            // val file = File(Environment.getExternalStorageDirectory(), "take_photo.jpg")
//            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val imagePath = File(filesDir, "take_images")
            val outputFile = File(imagePath, "take_photo.jpg")
            val imageUri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                outputFile
            )
            grantUriPermission(BuildConfig.APPLICATION_ID, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile))
//                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
//                data = imageUri
//                clipData?.addItem(ClipData.Item(imageUri))
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            takePhotoResult.launch(intent)
        }
    }

    private fun showFile(file: File) {
        val fileName = getFileName(file.absolutePath)
        val fileExtension = getFileName(path = file.absolutePath).substringAfterLast(delimiter = ".").lowercase()
        val fileSizeInBytes = getFileSizeInBytes(file.absolutePath)
        val fileSizeInMb = fileSizeInBytes.div(1024.0 * 1024.0)
        val string = "Size in mb: ${String.format("%.2f", fileSizeInMb)}"

        println(
            """
            fileName: $fileName
            fileExtension: $fileExtension
            fileSizeInBytes: $fileSizeInBytes
            fileSizeInMb: $fileSizeInMb
        """.trimIndent()
        )

        Glide.with(this)
            .setDefaultRequestOptions(
                RequestOptions().placeholder(R.color.purple_200).error(android.R.color.holo_red_dark)
            )
            .load(file)
            .into(binding.ivResultImage)
    }

    fun getFileExtension(uri: Uri?): String? {
        val contentResolver = contentResolver
        val mimeType = MimeTypeMap.getSingleton()
        return mimeType.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}