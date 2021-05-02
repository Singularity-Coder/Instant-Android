package com.singularitycoder.kotlinactivitylifecycle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

object AppUtils {

    fun addFragment(
        activity: Activity,
        bundle: Bundle?,
        parentLayout: Int,
        fragment: Fragment,
        addOrReplace: String
    ) {
        fragment.arguments = bundle
        (activity as AppCompatActivity).supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            if ("ADD" == addOrReplace) add(parentLayout, fragment) else replace(parentLayout, fragment)
            addToBackStack(null)
            commit()
        }
    }

    fun launchActivity(context: Context?, activity: Activity) {
        val intent = Intent(context, activity::class.java)
        context?.startActivity(intent)
    }
}