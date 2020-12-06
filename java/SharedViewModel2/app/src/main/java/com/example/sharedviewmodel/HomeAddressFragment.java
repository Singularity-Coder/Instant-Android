package com.example.sharedviewmodel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sharedviewmodel.databinding.FragmentHomeAddressBinding;

import static java.lang.String.valueOf;

public final class HomeAddressFragment extends Fragment {

    @Nullable
    private SharedViewModel sharedViewModel;

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @Nullable
    private FragmentHomeAddressBinding binding;

    public HomeAddressFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeAddressBinding.inflate(inflater, container, false);
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
        binding.etHomeBuilding.setError(null);
        binding.etHomeStreet.setError(null);
        binding.etHomeCity.setError(null);
        binding.etHomePin.setError(null);

        if (("").equals(valueOf(binding.etHomeBuilding.getEditText().getText()))) {
            binding.etHomeBuilding.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etHomeStreet.getEditText().getText()))) {
            binding.etHomeStreet.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etHomeCity.getEditText().getText()))) {
            binding.etHomeCity.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etHomePin.getEditText().getText()))) {
            binding.etHomePin.setError("Field must not be empty.");
            return false;
        }

        return true;
    }

    private void setUpListeners() {
        binding.conLayHomeAddressRoot.setOnClickListener(v -> {
        });
        binding.btnPersonalInfo.setOnClickListener(v -> appUtils.addFragment(getActivity(), null, R.id.con_lay_root, new PersonalInfoFragment()));
        binding.btnCancel.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.btnSend.setOnClickListener(v -> btnSend());
    }

    private void btnSend() {
        if (!hasValidInput()) return;
        sharedViewModel.setBuilding(valueOf(binding.etHomeBuilding.getEditText().getText()));
        sharedViewModel.setStreet(valueOf(binding.etHomeStreet.getEditText().getText()));
        sharedViewModel.setCity(valueOf(binding.etHomeCity.getEditText().getText()));
        sharedViewModel.setPin(valueOf(binding.etHomePin.getEditText().getText()));
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }
}