package com.singularitycoder.androidalertdialogs;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jakewharton.rxbinding3.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements CustomDialogFragment.CustomDialogListener, CustomDialogFragment.DialogEditTextListener {

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
        compositeDisposable.add(RxView.clicks(btnDialogEmbed).map(o -> btnDialogEmbed).subscribe(button -> MainActivity.this.embedDialog(), throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()));
        compositeDisposable.add(RxView.clicks(btnDialogFragment).map(o -> btnDialogFragment).subscribe(button -> MainActivity.this.customDialog(), throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()));
        compositeDisposable.add(RxView.clicks(btnDialogFragmentFullScreen).map(o -> btnDialogFragmentFullScreen).subscribe(button -> MainActivity.this.fullScreenDialogType1(), throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    @UiThread
    public void simpleAlertDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "simpleAlert");

        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    @UiThread
    public void listDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");

        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    @UiThread
    public void multipleChoiceListDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "multipleSelection");

        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    @UiThread
    public void singleChoiceListDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "singleSelection");

        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    @UiThread
    private void embedDialog() {
        findViewById(R.id.frameLayout).setVisibility(View.VISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "embed");

        DialogFragment dialogFragment = new CustomDialogFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.frameLayout, dialogFragment);
        fragmentTransaction.commit();
    }

    @UiThread
    private void customDialog() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("notAlertDialog", true);
        bundle.putString("DIALOG_TYPE", "custom");
        
        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    @UiThread
    private void fullScreenDialogType1() {
        Bundle bundle = new Bundle();
        bundle.putString("email", "abc@email.com");
        bundle.putBoolean("fullScreen", true);
        bundle.putBoolean("notAlertDialog", true);
        bundle.putString("DIALOG_TYPE", "fullScreen");
        
        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    public void fullScreenDialogType2() {
        Bundle bundle = new Bundle();
        bundle.putString("email", "abc@email.com");
        bundle.putBoolean("fullScreen", true);
        bundle.putBoolean("notAlertDialog", true);
        bundle.putString("DIALOG_TYPE", "fullScreen");

        boolean isLargeLayout = true;

        // Create an instance of the dialog fragment and show it
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);

        if (isLargeLayout) {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container for the fragment, which is always the root view for the activity
            transaction.add(android.R.id.content, dialogFragment).addToBackStack(null).commit();
        } else {
            // The device is using a large layout, so show the fragment as a dialog
            dialogFragment.show(fragmentManager, "TAG_CustomDialogFragment");
        }
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
    public void onEditingFinishedDialog(String inputText) {
        if (TextUtils.isEmpty(inputText)) textView.setText("Email field is empty");
        else textView.setText("Email: " + inputText);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
