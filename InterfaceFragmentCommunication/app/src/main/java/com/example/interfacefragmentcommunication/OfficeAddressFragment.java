package com.example.interfacefragmentcommunication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.interfacefragmentcommunication.databinding.FragmentOfficeAddressBinding;

import static java.lang.String.valueOf;

public final class OfficeAddressFragment extends Fragment {

    @Nullable
    private OfficeAddressListener officeAddressListener;

    @Nullable
    private FragmentOfficeAddressBinding binding;

    public OfficeAddressFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OfficeAddressListener) {
            officeAddressListener = (OfficeAddressListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OfficeAddressListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOfficeAddressBinding.inflate(inflater, container, false);
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
        officeAddressListener = null;
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
        officeAddressListener.onOfficeAddressSendClick(
                valueOf(binding.etOfficeBuilding.getEditText().getText()),
                valueOf(binding.etOfficeStreet.getEditText().getText()),
                valueOf(binding.etOfficeCity.getEditText().getText()),
                valueOf(binding.etOfficePin.getEditText().getText())
        );
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public interface OfficeAddressListener {
        void onOfficeAddressSendClick(String building, String street, String city, String pin);
    }
}