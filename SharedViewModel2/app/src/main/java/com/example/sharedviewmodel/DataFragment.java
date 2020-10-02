package com.example.sharedviewmodel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sharedviewmodel.databinding.FragmentDataBinding;

public final class DataFragment extends Fragment {

    @Nullable
    private final String TAG = "DataFragment";

    @Nullable
    private SharedViewModel sharedViewModel;

    @Nullable
    private FragmentDataBinding binding;

    @NonNull
    private AppUtils appUtils = AppUtils.getInstance();

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
        initialise();
        setUpListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        setDataFromSharedViewModel();
    }

    private void initialise() {
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
    }

    private void setDataFromSharedViewModel() {
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        sharedViewModel.getName().observe(getViewLifecycleOwner(), string -> binding.tvName.setText("Name: " + string));
        sharedViewModel.getEmail().observe(getViewLifecycleOwner(), string -> binding.tvEmail.setText("Email: " + string));
        sharedViewModel.getProfession().observe(getViewLifecycleOwner(), string -> binding.tvProfession.setText("Profession: " + string));
        sharedViewModel.getPassword().observe(getViewLifecycleOwner(), string -> binding.tvPassword.setText("Password: " + string));
        sharedViewModel.getBuilding().observe(getViewLifecycleOwner(), string -> binding.tvBuilding.setText("Building: " + string));
        sharedViewModel.getStreet().observe(getViewLifecycleOwner(), string -> binding.tvStreet.setText("Street: " + string));
        sharedViewModel.getCity().observe(getViewLifecycleOwner(), string -> binding.tvCity.setText("City: " + string));
        sharedViewModel.getPin().observe(getViewLifecycleOwner(), string -> binding.tvPinCode.setText("Pincode: " + string));
    }

    private void setUpListeners() {
        binding.conLayDataRoot.setOnClickListener(v -> {
        });
        binding.btnHomeAddress.setOnClickListener(v -> appUtils.replaceFragment(getActivity(), null, R.id.con_lay_root, new HomeAddressFragment()));
        binding.btnOfficeAddress.setOnClickListener(v -> appUtils.replaceFragment(getActivity(), null, R.id.con_lay_root, new OfficeAddressFragment()));
    }
}