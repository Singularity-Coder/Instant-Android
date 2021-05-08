package com.singularitycoder.androiddialogs1

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.singularitycoder.androiddialogs1.databinding.ActivityMainBinding
import com.singularitycoder.androiddialogs1.databinding.LayoutLoginBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG_CUSTOM_DIALOG_FRAGMENT = "TAG_CUSTOM_DIALOG_FRAGMENT"
        val DEFAULT_ARRAY = arrayOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5", "Option 6", "Option 7", "Option 8")
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        binding.apply {
            btnInfoDialog.setOnClickListener { showInfoDialog() }
            btnDecisionDialog.setOnClickListener { showDecisionDialog() }
            btnProgrammaticViewDialog.setOnClickListener { showProgrammaticViewDialog() }
            btnCustomViewDialog.setOnClickListener { showCustomViewDialog() }
            btnCustomLayoutDialog.setOnClickListener { showCustomLayoutDialog() }
            btnListSelectionDialog.setOnClickListener { showListSelectionDialog() }
            btnSingleChoiceDialog.setOnClickListener { showSingleChoiceListDialog() }
            btnMultiChoiceDialog.setOnClickListener { showMultiChoiceListDialog() }

            btnDfInfoDialog.setOnClickListener { showDfInfoDialog() }
            btnDfDecisionDialog.setOnClickListener { showDialogFragmentDecisionDialog() }
            btnDfProgrammaticViewDialog.setOnClickListener { showDfProgrammaticViewDialog() }
            btnDfCustomViewDialog.setOnClickListener { showDfCustomViewDialog() }
            btnDfCustomLayoutDialog.setOnClickListener { showDfCustomLayoutDialog() }
            btnDfListSelectionDialog.setOnClickListener { showDfListSelectionDialog() }
            btnDfSingleChoiceDialog.setOnClickListener { showDfSingleChoiceListDialog() }
            btnDfMultiChoiceDialog.setOnClickListener { showDfMultiChoiceListDialog() }
        }
    }

    private fun showDfMultiChoiceListDialog() {
        // Bug: If u add message then u wont see list
        val previousFragment = supportFragmentManager.findFragmentByTag(TAG_CUSTOM_DIALOG_FRAGMENT)
        val fragmentTransaction = supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            if (null != previousFragment) remove(previousFragment)
            addToBackStack(null)
        }
        CustomDialogFragment.getInstance(
            title = "Select Options",
            multiSelectArray = DEFAULT_ARRAY,
            positiveBtnText = "DONE",
            negativeBtnText = "CANCEL",
            positiveAction = { it: List<Any>? -> binding.tvResult.text = "${it} got selected!" }
        ).show(fragmentTransaction, TAG_CUSTOM_DIALOG_FRAGMENT)
    }

    private fun showDfSingleChoiceListDialog() {
        // Bug: If u add message then u wont see list
        val previousFragment = supportFragmentManager.findFragmentByTag(TAG_CUSTOM_DIALOG_FRAGMENT)
        val fragmentTransaction = supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            if (null != previousFragment) remove(previousFragment)
            addToBackStack(null)
        }
        CustomDialogFragment.getInstance(
            title = "Select an option",
            singleSelectArray = DEFAULT_ARRAY,
            positiveBtnText = "DONE",
            negativeBtnText = "CANCEL",
            positiveAction = { it: List<Any>? -> binding.tvResult.text = "${it?.get(0)} got selected!" }
        ).show(fragmentTransaction, TAG_CUSTOM_DIALOG_FRAGMENT)
    }

    private fun showDfListSelectionDialog() {
        // Bug: If u add message then u wont see list
        val previousFragment = supportFragmentManager.findFragmentByTag(TAG_CUSTOM_DIALOG_FRAGMENT)
        val fragmentTransaction = supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            if (null != previousFragment) remove(previousFragment)
            addToBackStack(null)
        }
        CustomDialogFragment.getInstance(
            isCancelable = true,
            title = "Select an option",
            listItemsArray = DEFAULT_ARRAY,
            itemAction = { it: String -> binding.tvResult.text = "$it got selected!" }
        ).show(fragmentTransaction, TAG_CUSTOM_DIALOG_FRAGMENT)
    }

    private fun showDfCustomLayoutDialog() {
        val previousFragment = supportFragmentManager.findFragmentByTag(TAG_CUSTOM_DIALOG_FRAGMENT)
        val fragmentTransaction = supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            if (null != previousFragment) remove(previousFragment)
            addToBackStack(null)
        }
        CustomDialogFragment.getInstance(
            layout = R.layout.layout_image,
            title = "See It",
            message = "See the lion!",
            positiveBtnText = "OK",
            positiveAction = { binding.tvResult.text = "Saw the Lion" }
        ).show(fragmentTransaction, TAG_CUSTOM_DIALOG_FRAGMENT)
    }

    private fun showDfCustomViewDialog() {
        val loginBinding: LayoutLoginBinding = LayoutLoginBinding.inflate(LayoutInflater.from(this)).apply {
            etEmail.hint = "Type Email"
            etPassword.hint = "Type Password"
        }

        val previousFragment = supportFragmentManager.findFragmentByTag(TAG_CUSTOM_DIALOG_FRAGMENT)
        val fragmentTransaction = supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            if (null != previousFragment) remove(previousFragment)
            addToBackStack(null)
        }
        CustomDialogFragment.getInstance(
            icon = R.drawable.ic_baseline_info_24,
            view = loginBinding.root,
            title = "Login",
            message = "Please login to access content!",
            positiveBtnText = "Login",
            negativeBtnText = "Cancel",
            neutralBtnText = "Forgot Password",
            positiveAction = { binding.tvResult.text = "Logged In successfully with name ${loginBinding.etEmail.editText?.text.toString()} and email ${loginBinding.etPassword.editText?.text.toString()}" },
            negativeAction = { binding.tvResult.text = "Ignored Login!" },
            neutralAction = { binding.tvResult.text = "Forgot Password!" }
        ).show(fragmentTransaction, TAG_CUSTOM_DIALOG_FRAGMENT)
    }

    private fun showDfProgrammaticViewDialog() {
        val etEmailParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setMargins(48, 16, 48, 0)
        }
        val etEmail = EditText(this@MainActivity).apply {
            hint = "Type Email"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            layoutParams = etEmailParams
        }

        val etPasswordParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setMargins(48, 16, 48, 0)
        }
        val etPassword = EditText(this@MainActivity).apply {
            hint = "Type Password"
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            layoutParams = etPasswordParams
        }

        val linearParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = linearParams
            addView(etEmail)
            addView(etPassword)
        }

        val previousFragment = supportFragmentManager.findFragmentByTag(TAG_CUSTOM_DIALOG_FRAGMENT)
        val fragmentTransaction = supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            if (null != previousFragment) remove(previousFragment)
            addToBackStack(null)
        }
        CustomDialogFragment.getInstance(
            icon = R.drawable.ic_baseline_info_24,
            view = linearLayout,
            title = "Login",
            message = "Please login to access content!",
            positiveBtnText = "Login",
            negativeBtnText = "Cancel",
            neutralBtnText = "Forgot Password",
            positiveAction = { binding.tvResult.text = "Logged In successfully with name ${etEmail.text.toString()} and email ${etPassword.text.toString()}" },
            negativeAction = { binding.tvResult.text = "Ignored Login!" },
            neutralAction = { binding.tvResult.text = "Forgot Password!" }
        ).show(fragmentTransaction, TAG_CUSTOM_DIALOG_FRAGMENT)
    }

    private fun showDialogFragmentDecisionDialog() {
        val previousFragment = supportFragmentManager.findFragmentByTag(TAG_CUSTOM_DIALOG_FRAGMENT)
        val fragmentTransaction = supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            if (null != previousFragment) remove(previousFragment)
            addToBackStack(null)
        }
        CustomDialogFragment.getInstance(
            icon = R.drawable.ic_baseline_info_24,
            title = "Decisions",
            message = "Basic Decision Dialog!",
            positiveBtnText = "Decision 1",
            negativeBtnText = "Decision 2",
            neutralBtnText = "Decision 3",
            positiveAction = { binding.tvResult.text = "Decision 1 Clicked" },
            negativeAction = { binding.tvResult.text = "Decision 2 Clicked" },
            neutralAction = { binding.tvResult.text = "Decision 3 Clicked" }
        ).show(fragmentTransaction, TAG_CUSTOM_DIALOG_FRAGMENT)
    }

    private fun showDfInfoDialog() {
        val previousFragment = supportFragmentManager.findFragmentByTag(TAG_CUSTOM_DIALOG_FRAGMENT)
        val fragmentTransaction = supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            if (null != previousFragment) remove(previousFragment)
            addToBackStack(null)
        }
        CustomDialogFragment.getInstance(
            isCancelable = true,
            icon = R.drawable.ic_baseline_info_24,
            title = "Info",
            message = "Basic Info Dialog!",
            positiveBtnText = "OK",
            positiveAction = { binding.tvResult.text = "I read it!" }
        ).show(fragmentTransaction, TAG_CUSTOM_DIALOG_FRAGMENT)
    }


    private fun showMultiChoiceListDialog() {
        // Bug: If u add message then u wont see list
        showMultiPurposeDialog(
            context = this,
            title = "Select Options",
            multiSelectArray = DEFAULT_ARRAY,
            positiveBtnText = "DONE",
            negativeBtnText = "CANCEL",
            positiveAction = { it: List<Any>? -> binding.tvResult.text = "${it} got selected!" }
        )
    }

    private fun showSingleChoiceListDialog() {
        // Bug: If u add message then u wont see list
        showMultiPurposeDialog(
            context = this,
            title = "Select an option",
            singleSelectArray = DEFAULT_ARRAY,
            positiveBtnText = "DONE",
            negativeBtnText = "CANCEL",
            positiveAction = { it: List<Any>? -> binding.tvResult.text = "${it?.get(0)} got selected!" }
        )
    }

    private fun showListSelectionDialog() {
        // Bug: If u add message then u wont see list
        showMultiPurposeDialog(
            context = this,
            isCancelable = true,
            title = "Select an option",
            listItemsArray = DEFAULT_ARRAY,
            itemAction = { it: String -> binding.tvResult.text = "$it got selected!" }
        )
    }

    private fun showCustomLayoutDialog() {
        showMultiPurposeDialog(
            context = this,
            layout = R.layout.layout_image,
            title = "See It",
            message = "See the lion!",
            positiveBtnText = "OK",
            positiveAction = { binding.tvResult.text = "Saw the Lion" }
        )
    }

    private fun showCustomViewDialog() {
        val loginBinding: LayoutLoginBinding = LayoutLoginBinding.inflate(LayoutInflater.from(this)).apply {
            etEmail.hint = "Type Email"
            etPassword.hint = "Type Password"
        }

        showMultiPurposeDialog(
            context = this,
            icon = R.drawable.ic_baseline_info_24,
            view = loginBinding.root,
            title = "Login",
            message = "Please login to access content!",
            positiveBtnText = "Login",
            negativeBtnText = "Cancel",
            neutralBtnText = "Forgot Password",
            positiveAction = { binding.tvResult.text = "Logged In successfully with name ${loginBinding.etEmail.editText?.text.toString()} and email ${loginBinding.etPassword.editText?.text.toString()}" },
            negativeAction = { binding.tvResult.text = "Ignored Login!" },
            neutralAction = { binding.tvResult.text = "Forgot Password!" }
        )
    }

    private fun showProgrammaticViewDialog() {
        val etEmailParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setMargins(48, 16, 48, 0)
        }
        val etEmail = EditText(this@MainActivity).apply {
            hint = "Type Email"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            layoutParams = etEmailParams
        }

        val etPasswordParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setMargins(48, 16, 48, 0)
        }
        val etPassword = EditText(this@MainActivity).apply {
            hint = "Type Password"
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            layoutParams = etPasswordParams
        }

        val linearParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = linearParams
            addView(etEmail)
            addView(etPassword)
        }

        showMultiPurposeDialog(
            context = this,
            icon = R.drawable.ic_baseline_info_24,
            view = linearLayout,
            title = "Login",
            message = "Please login to access content!",
            positiveBtnText = "Login",
            negativeBtnText = "Cancel",
            neutralBtnText = "Forgot Password",
            positiveAction = { binding.tvResult.text = "Logged In successfully with name ${etEmail.text.toString()} and email ${etPassword.text.toString()}" },
            negativeAction = { binding.tvResult.text = "Ignored Login!" },
            neutralAction = { binding.tvResult.text = "Forgot Password!" }
        )
    }

    private fun showDecisionDialog() {
        showMultiPurposeDialog(
            context = this,
            icon = R.drawable.ic_baseline_info_24,
            title = "Decisions",
            message = "Basic Decision Dialog!",
            positiveBtnText = "Decision 1",
            negativeBtnText = "Decision 2",
            neutralBtnText = "Decision 3",
            positiveAction = { binding.tvResult.text = "Decision 1 Clicked" },
            negativeAction = { binding.tvResult.text = "Decision 2 Clicked" },
            neutralAction = { binding.tvResult.text = "Decision 3 Clicked" }
        )
    }

    private fun showInfoDialog() {
        showMultiPurposeDialog(
            context = this,
            isCancelable = true,
            icon = R.drawable.ic_baseline_info_24,
            title = "Info",
            message = "Basic Info Dialog!",
            positiveBtnText = "OK",
            positiveAction = { binding.tvResult.text = "I read it!" }
        )
    }

    fun showMultiPurposeDialog(
        context: Context?,
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
    ) {
        val list = ArrayList<String>()
        AlertDialog.Builder(context).apply {
            setCancelable(isCancelable)
            if ("NA" != title) setTitle(title)
            if ("NA" != message) setMessage(message)
            if ("NA" != positiveBtnText) setPositiveButton(positiveBtnText) { dialog, id -> if (null == positiveAction) dialog.cancel() else positiveAction.invoke(list) }
            if ("NA" != negativeBtnText) setNegativeButton(negativeBtnText) { dialog, id -> if (null == negativeAction) dialog.cancel() else negativeAction.invoke() }
            if ("NA" != neutralBtnText) setNeutralButton(neutralBtnText) { dialog, id -> if (null == neutralAction) dialog.cancel() else neutralAction.invoke() }
            if (null != icon) setIcon(icon)
            if (null != view) setView(view)
            if (null != layout) setView(layout)
            if (null != listItemsArray) setItems(listItemsArray) { dialog, which ->
                itemAction?.invoke(listItemsArray?.get(which))
            }
            if (null != singleSelectArray) setSingleChoiceItems(singleSelectArray, 0 /* default checked item in list */) { dialog, which ->
                list.clear()
                list.add(singleSelectArray?.get(which))
            }
            if (null != multiSelectArray) setMultiChoiceItems(multiSelectArray, null) { dialog, which, isChecked ->
                if (isChecked) list.add(multiSelectArray?.get(which))
                else list.remove(multiSelectArray?.get(which))
            }
            show()
        }
    }
}