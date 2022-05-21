package com.example.androidstoragemadness

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.androidstoragemadness.databinding.FragmentFileDetailBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.snackbar.Snackbar
import java.io.File

private const val ARG_TYPE = "ARG_TYPE"
private const val ARG_PATH = "ARG_PATH"
private const val ARG_URL = "ARG_URL"

class FileDetailFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(
            type: String,
            path: String,
            url: String? = null
        ) = FileDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TYPE, type)
                putString(ARG_PATH, path)
                putString(ARG_URL, url)
            }
        }
    }

    private var type: String = ""
    private var path: String = ""
    private var url: String = ""

    private lateinit var nnContext: Context
    private lateinit var nnActivity: MainActivity
    private lateinit var binding: FragmentFileDetailBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        nnContext = context
        nnActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            path = it.getString(ARG_PATH) ?: ""
            url = it.getString(ARG_URL) ?: ""
            type = it.getString(ARG_TYPE) ?: ""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFileDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val file = File(path)
        val fileExtension = file.extension()
        val fileName = file.name()
        val fileSizeInBytes = file.sizeInBytes()
        val fileSizeInMb = file.sizeInMB()

        println(
            """
                file path: ${file.absolutePath}
                fileName: $fileName
                fileExtension: $fileExtension
                fileSizeInBytes: $fileSizeInBytes
                fileSizeInMb: $fileSizeInMb
            """.trimIndent()
        )

        when (type) {
            FileType.IMAGE.value -> {
                binding.imageView.visibility = View.VISIBLE
                Glide.with(this)
                    .setDefaultRequestOptions(RequestOptions().placeholder(R.color.purple_200).error(android.R.color.holo_red_dark))
                    .load(Uri.fromFile(file))
                    .into(binding.imageView)
            }
            FileType.PDF.value -> {
                binding.pdfView.visibility = View.VISIBLE
                binding.pdfView.fromFile(file)
                    .password(null)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .onPageError { page, _ ->
                        Snackbar.make(binding.root, "Could not load page number $page", Snackbar.LENGTH_SHORT).show()
                    }
                    .load()
            }
            FileType.VIDEO.value -> {
                binding.videoView.visibility = View.VISIBLE
                val mediaItem = MediaItem.fromUri(Uri.fromFile(file))
                ExoPlayer.Builder(nnContext).build().apply {
                    binding.videoView.player = this
                    addMediaItem(mediaItem)
                    prepare()
                    play()
                }
            }
            FileType.OTHER.value -> {
                // show in chrome browser
                val intent = Intent(Intent.ACTION_VIEW, File(path).toUri()).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    setPackage("com.android.chrome")
                }
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    // If Chrome not installed
                    intent.setPackage(null)
                    startActivity(intent)
                }
            }
        }
    }
}