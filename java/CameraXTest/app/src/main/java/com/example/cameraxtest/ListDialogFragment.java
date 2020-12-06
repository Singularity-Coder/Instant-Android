package com.example.cameraxtest;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public final class ListDialogFragment extends DialogFragment {

    @NonNull
    private final String TAG = "ListDialogFragment";

    @Nullable
    private ListDialogListener listDialogListener;

    public ListDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (null == getArguments()) return;
        if (!("list").equals(getArguments().getString("DIALOG_TYPE"))) return;
        if (!("activity").equals(getArguments().getString("KEY_CONTEXT_TYPE"))) return;

        try {
            listDialogListener = (ListDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement ListDialogViewListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (null == getArguments()) return builder.create();
        if (!("list").equals(getArguments().getString("DIALOG_TYPE"))) return builder.create();
        if (("").equals(getArguments().getStringArray("KEY_LIST"))) return builder.create();
        if (("").equals(getArguments().getString("KEY_TITLE"))) return builder.create();

        final String[] list = getArguments().getStringArray("KEY_LIST");
        final String title = getArguments().getString("KEY_TITLE");
        final String contextType = getArguments().getString("KEY_CONTEXT_TYPE");
        final String contextObject = getArguments().getString("KEY_CONTEXT_OBJECT");
        listDialog(builder, list, title, contextType, contextObject);

        return builder.create();
    }

    @UiThread
    public void listDialog(
            @NonNull final AlertDialog.Builder builder,
            @NonNull final String[] list,
            @NonNull final String title,
            @NonNull final String contextType,
            @NonNull final String contextObject) {

        if (("fragment").equals(contextType) && ("PhotoCameraFragment").equals(contextObject)) {
            listDialogListener = (PhotoCameraFragment) getTargetFragment();
        }

        if (("fragment").equals(contextType) && ("VideoCameraFragment").equals(contextObject)) {
            listDialogListener = (VideoCameraFragment) getTargetFragment();
        }

        builder.setTitle(title);
        builder.setCancelable(false);
        String[] selectArray = list;
        builder.setItems(selectArray, (dialog, which) -> {
            for (int i = 0; i < list.length; i++) {
                if (which == i) {
                    if (null == listDialogListener) return;
                    listDialogListener.onListDialogItemClick(ListDialogFragment.this, selectArray[i], title);
                }
            }
        });
    }

    public interface ListDialogListener {
        void onListDialogItemClick(DialogFragment dialog, String listItemText, String listTitle);
    }
}
