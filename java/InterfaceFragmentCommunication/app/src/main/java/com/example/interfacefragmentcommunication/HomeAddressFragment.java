package com.example.interfacefragmentcommunication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.interfacefragmentcommunication.databinding.FragmentHomeAddressBinding;

import static java.lang.String.valueOf;

public final class HomeAddressFragment extends Fragment {

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @Nullable
    private HomeAddressListener homeAddressListener;

    @Nullable
    private FragmentHomeAddressBinding binding;

    public HomeAddressFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeAddressListener) {
            homeAddressListener = (HomeAddressListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HomeAddressListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeAddressListener = null;
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
        binding.btnPersonalInfo.setOnClickListener(v -> appUtils.addFragment(getActivity(), null, R.id.fl_main, new PersonalInfoFragment()));
        binding.btnCancel.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.btnSend.setOnClickListener(v -> btnSend());
    }

    private void btnSend() {
        if (!hasValidInput()) return;
        homeAddressListener.onHomeAddressSendClick(
                valueOf(binding.etHomeBuilding.getEditText().getText()),
                valueOf(binding.etHomeStreet.getEditText().getText()),
                valueOf(binding.etHomeCity.getEditText().getText()),
                valueOf(binding.etHomePin.getEditText().getText())
        );
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public interface HomeAddressListener {
        void onHomeAddressSendClick(String building, String street, String city, String pin);
    }
}