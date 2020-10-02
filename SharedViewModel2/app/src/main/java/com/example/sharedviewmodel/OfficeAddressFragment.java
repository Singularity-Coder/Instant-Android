package com.example.sharedviewmodel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sharedviewmodel.databinding.FragmentOfficeAddressBinding;

import static java.lang.String.valueOf;

public final class OfficeAddressFragment extends Fragment {

    @Nullable
    private SharedViewModel sharedViewModel;

    @Nullable
    private FragmentOfficeAddressBinding binding;

    public OfficeAddressFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOfficeAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialise();
        setUpListeners();
    }

    private void initialise() {
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
    }

    private boolean hasValidInput() {
        binding.etOfficeBuilding.setError(null);
        binding.etOfficeStreet.setError(null);
        binding.etOfficeCity.setError(null);
        binding.etOfficePin.setError(null);

        if (("").equals(valueOf(binding.etOfficeBuilding.getEditText().getText()))) {
            binding.etOfficeBuilding.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etOfficeStreet.getEditText().getText()))) {
            binding.etOfficeStreet.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etOfficeCity.getEditText().getText()))) {
            binding.etOfficeCity.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etOfficePin.getEditText().getText()))) {
            binding.etOfficePin.setError("Field must not be empty.");
            return false;
        }

        return true;
    }


    private void setUpListeners() {
        binding.conLayOfficeAddressRoot.setOnClickListener(v -> {
        });
        binding.btnCancel.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.btnSend.setOnClickListener(v -> btnSend());
    }

    private void btnSend() {
        if (!hasValidInput()) return;
        sharedViewModel.setBuilding(valueOf(binding.etOfficeBuilding.getEditText().getText()));
        sharedViewModel.setStreet(valueOf(binding.etOfficeStreet.getEditText().getText()));
        sharedViewModel.setCity(valueOf(binding.etOfficeCity.getEditText().getText()));
        sharedViewModel.setPin(valueOf(binding.etOfficePin.getEditText().getText()));
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }
}