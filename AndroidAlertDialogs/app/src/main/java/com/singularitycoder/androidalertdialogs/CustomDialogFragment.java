package com.singularitycoder.androidalertdialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Map;

public class CustomDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    CustomDialogFragment.NoticeDialogListener listener;

    public CustomDialogFragment() {
    }

    public CustomDialogFragment(Map<String, String> dialogDetails) {
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (CustomDialogFragment.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString() + " must implement NoticeDialogListener");
        }
    }

    // The system calls this only when creating the layout in a dialog.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            if (getArguments().getBoolean("notAlertDialog")) {
                return super.onCreateDialog(savedInstanceState);
            }
        }

        // The only reason you might override this method when using onCreateView() is to modify any dialog characteristics. For example, the dialog includes a title by default, but your custom layout might not need it. So here you can remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Get the ideal size of a dialog
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        dialog.getWindow().setLayout((int) (displayRectangle.width() * 0.8f), dialog.getWindow().getAttributes().height);

        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Message");
        builder.setMessage("Fire Missiles")
                .setPositiveButton("OK", (dialog1, id) -> {
                    // Send the positive button event back to the host activity
                    listener.onDialogPositiveClick(CustomDialogFragment.this);
                })
                .setNegativeButton("Cancel", (dialog12, id) -> {
                    // Send the negative button event back to the host activity
                    listener.onDialogNegativeClick(CustomDialogFragment.this);
                })
                .setNeutralButton("Later", (dialogInterface, id) -> {
                    // Send the neutral button event back to the host activity
                    listener.onDialogNeutralClick(CustomDialogFragment.this);
                });
        return builder.create();
    }

    // The system calls this to get the DialogFragment's layout, regardless of whether it's being displayed as a dialog or an embedded fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.fragment_custom_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText editText = view.findViewById(R.id.et_email);

        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString("email")))
            editText.setText(getArguments().getString("email"));

        Button btnDone = view.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(view1 -> {
            CustomDialogFragment.DialogListener dialogListener = (CustomDialogFragment.DialogListener) getActivity();
            dialogListener.onFinishEditDialog(editText.getText().toString());
            dismiss();
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean setFullScreen = false;
        if (getArguments() != null)
            setFullScreen = getArguments().getBoolean("fullScreen");

        if (setFullScreen)
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
    }

    @UiThread
    public void customDialog() {
        // Instantiate Dialog class
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        // Set custom Layout
        dialog.setContentView(R.layout.fragment_custom_dialog);

        // Get the recommended size of a dialog
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        dialog.getWindow().setLayout((int) (displayRectangle.width() * 0.8f), dialog.getWindow().getAttributes().height);

        // Instantiate custom dialog views
//        final EditText etDateDialog = dialog.findViewById(R.id.et_date_setter);
//        final String etString = etDateDialog.getText().toString();
//
//        Button btnApply = dialog.findViewById(R.id.btn_ok);
//        btnApply.setOnClickListener(view -> {
//            Toast.makeText(getContext(), etString, LENGTH_LONG).show();
//            dialog.dismiss();
//        });

        dialog.show();
    }

    /* The activity that creates an instance of this dialog fragment must implement this interface in order to receive event callbacks. Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onDialogNeutralClick(DialogFragment dialog);
    }

    public interface DialogListener {
        void onFinishEditDialog(String inputText);
    }
}