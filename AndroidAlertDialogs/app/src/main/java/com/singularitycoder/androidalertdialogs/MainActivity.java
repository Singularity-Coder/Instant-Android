package com.singularitycoder.androidalertdialogs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void simpleAlertDialog(View view) {
        // Use the Builder class for constructing dialog
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        // Set Optional Title
        alertBuilder.setTitle("Delete Message");

        // Set Optional Message
        alertBuilder.setMessage("Are you sure you want to delete this message?");

        // Optional Icon for dialog
        alertBuilder.setIcon(android.R.drawable.ic_dialog_alert);

        // If you want to dismiss dialog on touch of dialog bounds
        alertBuilder.setCancelable(true);

        // Set positive message
        alertBuilder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        makeText(MainActivity.this, "Yes clicked", LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

        // Set Negative message
        alertBuilder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        makeText(MainActivity.this, "No clicked", LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

        // Set neutral if user unable to decide
        alertBuilder.setNeutralButton("Remind Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                makeText(MainActivity.this, "No clicked", LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        alertBuilder.show();
    }

    public void listDialog(View view) {
        // Use the Builder class for constructing dialog
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("What do you want to do?");

        // If you don't want to dismiss dialog on touch of dialog bounds
        alertBuilder.setCancelable(false);

        // Create a list
        String[] selectArray = {"Option 1", "Option 2", "Option 3", "Close Dialog"};

        // Add the list to builder
        alertBuilder.setItems(selectArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position of the selected item
                switch (which) {
                    case 0:
                        // Do something
                        makeText(MainActivity.this, "Option 1 clicked", LENGTH_SHORT).show();
                        break;
                    case 1:
                        // Do something
                        makeText(MainActivity.this, "Option 2 clicked", LENGTH_SHORT).show();
                        break;
                    case 2:
                        // Do something
                        makeText(MainActivity.this, "Option 3 clicked", LENGTH_SHORT).show();
                        break;
                    case 3:
                        // Do something
                        dialog.dismiss();
                        makeText(MainActivity.this, "Dialog Closed", LENGTH_SHORT).show();
                        break;
                }
            }
        });

        alertBuilder.show();
    }

    public void customDialog(View view) {
        // Instantiate Dialog class
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        // Set custom Layout
        dialog.setContentView(R.layout.dialog_custom);

        // Get the ideal size of a dialog
        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        dialog.getWindow().setLayout((int) (displayRectangle.width() * 0.8f), dialog.getWindow().getAttributes().height);

        // Instantiate custom dialog views
        final EditText etDateDialog = dialog.findViewById(R.id.et_date_setter);
        final String etString = etDateDialog.getText().toString();

        Button btnApply = dialog.findViewById(R.id.btn_ok);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), etString, LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public AlertDialog multitpleChoiceListDialog(View view) {

        // Track the selected items
        final ArrayList<Integer> selectedItems = new ArrayList();
        String[] dialogList = {"Option 1", "Option 2", "Option 3", "Option 4"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set dialog title
        builder.setTitle("Select Options")
                // Specify list array, items to be selected by default (null for none), and listener through which to receive callbacks when items selected
                .setMultiChoiceItems(dialogList, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If user checked the item, add it to selected items
                                    selectedItems.add(which);

                                    for (int i = 0; i < selectedItems.size(); i++) {
                                        System.out.println("Print Selected Items: " + selectedItems.get(i));
                                    }
                                } else if (selectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedItems.remove(which);

                                    for (int i = 0; i < selectedItems.size(); i++) {
                                        System.out.println("Print Deselected Items: " + selectedItems.get(i));
                                    }
                                }
                            }
                        })
                // Set action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Save selectedItems results somewhere or return them to the component that opened the dialog
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.show();
    }

    public AlertDialog singleChoiceListDialog(View view) {
        String[] dialogList = {"Option 1", "Option 2", "Option 3", "Option 4"};

        // Must have an item checked by default in singlezchoiceListDialog
        final int checkedItem = 0;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Select an Option")
                // Specify the list array, the items to be selected by default (null for none), and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(dialogList, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("Checked Item: " + i);
                    }
                })
                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Save selectedItems results somewhere or return them to the component that opened the dialog
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.show();
    }


    /*----------------------------------------------------*/


    public class OtherActivity extends FragmentActivity implements NoticeDialogFragment.NoticeDialogListener {

        public void showNoticeDialog() {
            // Create an instance of the dialog fragment and show it
            DialogFragment dialog = new NoticeDialogFragment();
            dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
        }

        // The dialog fragment receives a reference to this Activity through the Fragment.onAttach() callback, which it uses to call the following methods defined by the NoticeDialogFragment.NoticeDialogListener interface
        @Override
        public void onDialogPositiveClick(DialogFragment dialog) {
            // User touched the dialog's positive button
        }

        @Override
        public void onDialogNegativeClick(DialogFragment dialog) {
            // User touched the dialog's negative button
        }
    }

    public void confirmFireMissiles() {
        DialogFragment newFragment = new NoticeDialogFragment();
        newFragment.show(getSupportFragmentManager(), "missiles");
    }










    public class CustomDialogFragment extends DialogFragment {
         // The system calls this to get the DialogFragment's layout, regardless of whether it's being displayed as a dialog or an embedded fragment.
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout to use as dialog or embedded fragment
            return inflater.inflate(R.layout.dialog_custom, container, false);
        }

         // The system calls this only when creating the layout in a dialog.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // The only reason you might override this method when using onCreateView() is to modify any dialog characteristics. For example, the dialog includes a title by default, but your custom layout might not need it. So here you can remove the dialog title, but you must call the superclass to get the Dialog.
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // Get the ideal size of a dialog
            Rect displayRectangle = new Rect();
            Window window = MainActivity.this.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            dialog.getWindow().setLayout((int) (displayRectangle.width() * 0.8f), dialog.getWindow().getAttributes().height);

            return dialog;
        }
    }

    public void showDialog() {
        boolean isLargeLayout = true;

        FragmentManager fragmentManager = getSupportFragmentManager();
        CustomDialogFragment newFragment = new CustomDialogFragment();

        if (isLargeLayout) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container for the fragment, which is always the root view for the activity
            transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        }
    }


}
