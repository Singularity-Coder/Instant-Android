package com.example.interfacefragmentcommunication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.interfacefragmentcommunication.databinding.FragmentDataBinding;

public final class DataFragment extends Fragment {

    @Nullable
    private final String TAG = "DataFragment";

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @Nullable
    private FragmentDataBinding binding;

    public DataFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDataBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpListeners();
    }

    private void setUpListeners() {
        binding.conLayDataRoot.setOnClickListener(v -> {
        });
        binding.btnHomeAddress.setOnClickListener(v -> appUtils.addFragment(getActivity(), null, R.id.fl_main, new HomeAddressFragment()));
        binding.btnOfficeAddress.setOnClickListener(v -> appUtils.addFragment(getActivity(), null, R.id.fl_main, new OfficeAddressFragment()));
    }

    public void getHomeAddress(
            @NonNull final String building,
            @NonNull final String street,
            @NonNull final String city,
            @NonNull final String pin) {
        binding.tvBuilding.setText("Building: " + building);
        binding.tvStreet.setText("Street: " + street);
        binding.tvCity.setText("City: " + city);
        binding.tvPinCode.setText("Pincode: " + pin);
    }

    public void getOfficeAddress(
            @NonNull final String building,
            @NonNull final String street,
            @NonNull final String city,
            @NonNull final String pin) {
        binding.tvBuilding.setText("Building: " + building);
        binding.tvStreet.setText("Street: " + street);
        binding.tvCity.setText("City: " + city);
        binding.tvPinCode.setText("Pincode: " + pin);
    }

    public void getPersonalInfo(
            @NonNull final String name,
            @NonNull final String email,
            @NonNull final String profession,
            @NonNull final String password) {
        binding.tvName.setText("Name: " + name);
        binding.tvEmail.setText("Email: " + email);
        binding.tvProfession.setText("Profession: " + profession);
        binding.tvPassword.setText("Password: " + password);
    }
}