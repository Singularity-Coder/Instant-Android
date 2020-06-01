package com.singularitycoder.androidalertdialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jakewharton.rxbinding3.view.RxView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements CustomDialogFragment.NoticeDialogListener, CustomDialogFragment.DialogListener {

    @Nullable
    @BindView(R.id.btn_dialog_simple)
    Button btnDialogSimple;
    @Nullable
    @BindView(R.id.btn_dialog_list)
    Button btnDialogList;
    @Nullable
    @BindView(R.id.btn_dialog_single_choice_list)
    Button btnDialogSingleChoiceList;
    @Nullable
    @BindView(R.id.btn_dialog_multi_choice_list)
    Button btnDialogMultiChoiceList;
    @Nullable
    @BindView(R.id.btnEmbedDialogFragment)
    Button btnDialogEmbed;
    @Nullable
    @BindView(R.id.btnDialogFragment)
    Button btnDialogFragment;
    @Nullable
    @BindView(R.id.btnDialogFragmentFullScreen)
    Button btnDialogFragmentFullScreen;
    @Nullable
    @BindView(R.id.btnAlertDialogFragment)
    Button btnDialogAlert;
    @Nullable
    @BindView(R.id.textView)
    TextView textView;

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        unbinder = ButterKnife.bind(this);
        setClickListeners();
    }

    private void setClickListeners() {
        compositeDisposable.add(RxView.clicks(btnDialogSimple).map(o -> btnDialogSimple).subscribe(button -> MainActivity.this.simpleAlertDialog(), throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()));
        compositeDisposable.add(RxView.clicks(btnDialogList).map(o -> btnDialogList).subscribe(button -> MainActivity.this.listDialog(), throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()));
        compositeDisposable.add(RxView.clicks(btnDialogSingleChoiceList).map(o -> btnDialogSingleChoiceList).subscribe(button -> MainActivity.this.singleChoiceListDialog(), throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()));
        compositeDisposable.add(RxView.clicks(btnDialogMultiChoiceList).map(o -> btnDialogMultiChoiceList).subscribe(button -> MainActivity.this.multipleChoiceListDialog(), throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()));

        compositeDisposable.add(RxView.clicks(btnDialogEmbed).map(o -> btnDialogEmbed).subscribe(button -> MainActivity.this.embedDialogFragment(), throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()));
        compositeDisposable.add(RxView.clicks(btnDialogFragment).map(o -> btnDialogFragment).subscribe(button -> MainActivity.this.normalDialogFragment(), throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()));
        compositeDisposable.add(RxView.clicks(btnDialogFragmentFullScreen).map(o -> btnDialogFragmentFullScreen).subscribe(button -> MainActivity.this.dialogFragmentFullSCreen(), throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()));
        compositeDisposable.add(RxView.clicks(btnDialogAlert).map(o -> btnDialogAlert).subscribe(button -> MainActivity.this.alertDialogFragment(), throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    @UiThread
    public void simpleAlertDialog() {
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

    @UiThread
    public void listDialog() {
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

    @UiThread
    public AlertDialog multipleChoiceListDialog() {

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

    @UiThread
    public AlertDialog singleChoiceListDialog() {
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

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CustomDialogFragment();
        dialog.show(getSupportFragmentManager(), "TAG_NoticeDialogFragment");
    }

    public void showDialog() {
        boolean isLargeLayout = true;

        FragmentManager fragmentManager = getSupportFragmentManager();
        CustomDialogFragment newFragment = new CustomDialogFragment();

        if (isLargeLayout) {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container for the fragment, which is always the root view for the activity
            transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        } else {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        }
    }

    private void embedDialogFragment() {
        findViewById(R.id.frameLayout).setVisibility(View.VISIBLE);
        DialogFragment dialogFragment = new CustomDialogFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.frameLayout, dialogFragment);
        fragmentTransaction.commit();
    }

    private void normalDialogFragment() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("notAlertDialog", true);
        
        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("dialog");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "dialog");
    }

    private void dialogFragmentFullSCreen() {
        Bundle bundle = new Bundle();
        bundle.putString("email", "abc@email.com");
        bundle.putBoolean("fullScreen", true);
        bundle.putBoolean("notAlertDialog", true);
        
        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("dialog");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "dialog");
    }

    private void alertDialogFragment() {
        DialogFragment dialogFragment = new CustomDialogFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("dialog");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "dialog");
    }

    // The dialog fragment receives a reference to this Activity through the Fragment.onAttach() callback, which it uses to call the following methods defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        makeText(this, "+", LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        makeText(this, "-", LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
        makeText(this, "N", LENGTH_SHORT).show();
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        if (TextUtils.isEmpty(inputText)) textView.setText("Email field is empty");
        else textView.setText("Email: " + inputText);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
