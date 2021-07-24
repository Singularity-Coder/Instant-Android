package com.singularitycoder.bottomsheetdialogfragment

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// https://androidwave.com/bottom-sheet-dialog-fragment-in-android/
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MODAL_BOTTOM_SHEET"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.btn_show_modal_bottom_sheet).setOnClickListener {
            ModalBottomSheetDialogFragment(onBottomSheetItemClickListener = { it: String ->
                findViewById<TextView>(R.id.tv_selected_item).text = it.plus(" Selected")
            }).show(supportFragmentManager, TAG)
        }
    }
}