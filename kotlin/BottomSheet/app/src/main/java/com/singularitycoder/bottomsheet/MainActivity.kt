package com.singularitycoder.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.singularitycoder.bottomsheet.databinding.ActivityMainBinding
import com.singularitycoder.bottomsheet.databinding.ItemBottomSheetBinding

// https://androidwave.com/bottom-sheet-behavior-in-android/
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        for (i in 1..20) {
            val itemBinding = ItemBottomSheetBinding.inflate(LayoutInflater.from(this), binding.layoutBottomSheet.llSongs, false)
            itemBinding.root.text = "My Song $i"
            binding.layoutBottomSheet.llSongs.addView(itemBinding.root)
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutBottomSheet.root).apply {
            addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    println("onStateChanged: $newState")
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> binding.tvBottomSheetState.text = "STATE HIDDEN"
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            binding.tvBottomSheetState.text = "STATE EXPANDED"
                            binding.btnShowBottomSheet.text = "Hide Bottom Sheet"
                            binding.layoutBottomSheet.llBottomSheetHeader.findViewById<TextView>(R.id.tv_song).showHideIcon(
                                context = this@MainActivity,
                                showTick = true,
                                icon1 = R.drawable.ic_baseline_play_arrow_24,
                                icon3 = R.drawable.ic_baseline_keyboard_arrow_down_24,
                                iconColor1 = R.color.purple_500,
                                iconColor3 = R.color.purple_500,
                                direction = 5
                            )
                        }
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> binding.tvBottomSheetState.text = "STATE HALF EXPANDED"
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            binding.tvBottomSheetState.text = "STATE COLLAPSED"
                            binding.btnShowBottomSheet.text = "Show Bottom Sheet"
                            binding.layoutBottomSheet.llBottomSheetHeader.findViewById<TextView>(R.id.tv_song).showHideIcon(
                                context = this@MainActivity,
                                showTick = true,
                                icon1 = R.drawable.ic_baseline_pause_24,
                                icon3 = R.drawable.ic_baseline_keyboard_arrow_up_24,
                                iconColor1 = R.color.purple_500,
                                iconColor3 = R.color.purple_500,
                                direction = 5
                            )
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> binding.tvBottomSheetState.text = "STATE DRAGGING"
                        BottomSheetBehavior.STATE_SETTLING -> binding.tvBottomSheetState.text = "STATE SETTLING"
                        else -> binding.tvBottomSheetState.text = "Bottom Sheet State"
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            })
        }

        binding.btnShowBottomSheet.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }
}