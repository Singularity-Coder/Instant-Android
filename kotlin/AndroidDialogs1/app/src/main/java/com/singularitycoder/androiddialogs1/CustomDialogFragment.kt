package com.singularitycoder.androiddialogs1

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class CustomDialogFragment : DialogFragment() {

    companion object {
        @JvmStatic
        fun getInstance(
            isCancelable: Boolean = false,
            icon: Int? = null,
            view: View? = null,
            layout: Int? = null,
            title: String? = "NA",
            message: String? = "NA",
            listItemsArray: Array<String>? = null,
            singleSelectArray: Array<String>? = null,
            multiSelectArray: Array<String>? = null,
            itemAction: ((selectedItem: String) -> Unit)? = null,
            positiveBtnText: String? = "NA",
            negativeBtnText: String? = "NA",
            neutralBtnText: String? = "NA",
            positiveAction: ((selectedList: List<Any>?) -> Unit)? = null,
            negativeAction: (() -> Unit)? = null,
            neutralAction: (() -> Unit)? = null
        ) = CustomDialogFragment().apply {
            dialogIsCancelable = isCancelable
            dialogIcon = icon
            dialogView = view
            dialogLayout = layout
            dialogTitle = title
            dialogMessage = message
            dialogListItemsArray = listItemsArray
            dialogSingleSelectArray = singleSelectArray
            dialogMultiSelectArray = multiSelectArray
            dialogItemAction = itemAction
            dialogPositiveBtnText = positiveBtnText
            dialogNegativeBtnText = negativeBtnText
            dialogNeutralBtnText = neutralBtnText
            dialogPositiveAction = positiveAction
            dialogNegativeAction = negativeAction
            dialogNeutralAction = neutralAction
        }
    }

    var dialogIsCancelable: Boolean = false
    var dialogIcon: Int? = null
    var dialogView: View? = null
    var dialogLayout: Int? = null
    var dialogTitle: String? = "NA"
    var dialogMessage: String? = "NA"
    var dialogListItemsArray: Array<String>? = null
    var dialogSingleSelectArray: Array<String>? = null
    var dialogMultiSelectArray: Array<String>? = null
    var dialogItemAction: ((selectedItem: String) -> Unit)? = null
    var dialogPositiveBtnText: String? = "NA"
    var dialogNegativeBtnText: String? = "NA"
    var dialogNeutralBtnText: String? = "NA"
    var dialogPositiveAction: ((selectedList: List<Any>?) -> Unit)? = null
    var dialogNegativeAction: (() -> Unit)? = null
    var dialogNeutralAction: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val list = ArrayList<String>()
        val builder = AlertDialog.Builder(requireContext()).apply {
            setCancelable(dialogIsCancelable)
            if ("NA" != dialogTitle) setTitle(dialogTitle)
            if ("NA" != dialogMessage) setMessage(dialogMessage)
            if ("NA" != dialogPositiveBtnText) setPositiveButton(dialogPositiveBtnText) { dialog, id -> if (null == dialogPositiveAction) dialog.cancel() else dialogPositiveAction!!.invoke(list) }
            if ("NA" != dialogNegativeBtnText) setNegativeButton(dialogNegativeBtnText) { dialog, id -> if (null == dialogNegativeAction) dialog.cancel() else dialogNegativeAction!!.invoke() }
            if ("NA" != dialogNeutralBtnText) setNeutralButton(dialogNeutralBtnText) { dialog, id -> if (null == dialogNeutralAction) dialog.cancel() else dialogNeutralAction!!.invoke() }
            if (null != dialogIcon) setIcon(dialogIcon!!)
            if (null != dialogView) setView(dialogView)
            if (null != dialogLayout) setView(dialogLayout!!)
            if (null != dialogListItemsArray) setItems(dialogListItemsArray) { dialog, which ->
                dialogItemAction?.invoke(dialogListItemsArray?.get(which).toString())
            }
            if (null != dialogSingleSelectArray) setSingleChoiceItems(dialogSingleSelectArray, 0 /* default checked item in list */) { dialog, which ->
                list.clear()
                list.add(dialogSingleSelectArray?.get(which).toString())
            }
            if (null != dialogMultiSelectArray) setMultiChoiceItems(dialogMultiSelectArray, null) { dialog, which, isChecked ->
                if (isChecked) list.add(dialogMultiSelectArray?.get(which).toString())
                else list.remove(dialogMultiSelectArray?.get(which).toString())
            }
        }
        return builder.create()
    }
}