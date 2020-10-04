package com.example.interfacefragmentcommunication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.interfacefragmentcommunication.databinding.ActivityMainBinding;

public final class MainActivity
        extends AppCompatActivity
        implements HomeAddressFragment.HomeAddressListener,
        OfficeAddressFragment.OfficeAddressListener,
        PersonalInfoFragment.PersonalInfoListener{

    @Nullable
    private final String TAG = "MainActivity";

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @Nullable
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtils.setStatusBarColor(this, R.color.colorPrimary);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appUtils.addFragmentNoBackStack(this, null, R.id.fl_main, new DataFragment(), "data_fragment");
    }

    @Override
    public void onHomeAddressSendClick(String building, String street, String city, String pin) {
        final DataFragment dataFragment = (DataFragment) getSupportFragmentManager().findFragmentByTag("data_fragment");     // or u can maintain global variable for DataFragment and use the same instance to get the method
        dataFragment.getHomeAddress(building, street, city, pin);
    }

    @Override
    public void onOfficeAddressSendClick(String building, String street, String city, String pin) {
        final DataFragment dataFragment = (DataFragment) getSupportFragmentManager().findFragmentByTag("data_fragment");
        dataFragment.getOfficeAddress(building, street, city, pin);
    }

    @Override
    public void onPersonalInfoSendClick(String name, String email, String profession, String password) {
        final DataFragment dataFragment = (DataFragment) getSupportFragmentManager().findFragmentByTag("data_fragment");
        dataFragment.getPersonalInfo(name, email, profession, password);
    }
}