package com.singularitycoder.bottomsheetdialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.singularitycoder.bottomsheetdialogfragment.databinding.FragmentModalBottomSheetDialogBinding

class ModalBottomSheetDialogFragment(val onBottomSheetItemClickListener: (item: String) -> Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentModalBottomSheetDialogBinding

    // Fragments must have public no-arg constructor
    constructor() : this({})

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentModalBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvShare.setOnClickListener { setTextAndDismiss(binding.tvShare) }
            tvGetLink.setOnClickListener { setTextAndDismiss(binding.tvGetLink) }
            tvEditName.setOnClickListener { setTextAndDismiss(binding.tvEditName) }
            tvDeleteCollection.setOnClickListener { setTextAndDismiss(binding.tvDeleteCollection) }
        }
    }

    private fun setTextAndDismiss(textView: TextView) {
        onBottomSheetItemClickListener.invoke(textView.text.toString())
        dismiss()
    }
}