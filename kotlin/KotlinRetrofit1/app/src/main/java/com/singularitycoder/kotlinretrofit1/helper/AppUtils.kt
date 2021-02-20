package com.singularitycoder.kotlinretrofit1.helper

import android.R
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.singularitycoder.kotlinretrofit1.model.RepoError
import com.singularitycoder.kotlinretrofit1.model.RepoResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

object AppUtils {

    /* https://futurestud.io/tutorials/retrofit-2-simple-error-handling */
    fun parseError(response: Response<RepoResponse>): RepoError {
        val converter: Converter<ResponseBody?, RepoError> = Retrofit.Builder().build()
            .responseBodyConverter(RepoError::class.java, arrayOfNulls<Annotation>(0))
        val error: RepoError
        error = try {
            converter.convert(response.errorBody())!!
        } catch (e: IOException) {
            return RepoError()
        }
        return error
    }

    fun setStatusBarColor(activity: Activity, statusBarColor: Int) {
        val window: Window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, statusBarColor)
    }

    fun hasInternet(context: Context): Boolean {
        val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun glideImage(context: Context?, imgUrl: String?, imageView: ImageView?) {
        Glide.with(context!!).load(imgUrl)
            .placeholder(R.color.holo_purple)
            .error(R.color.holo_red_light)
            .encodeQuality(40)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView!!)
    }

    fun showSnack(
        view: View,
        message: String,
        actionButtonText: String,
        voidFunction: (Unit) -> Unit,
    ) = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(actionButtonText) { voidFunction }
        .show()

    fun dummy() {

    }
}