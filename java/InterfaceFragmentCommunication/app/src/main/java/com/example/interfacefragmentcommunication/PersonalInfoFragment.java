package com.example.interfacefragmentcommunication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.interfacefragmentcommunication.databinding.FragmentPersonalInfoBinding;

import static java.lang.String.valueOf;

public final class PersonalInfoFragment extends Fragment {

    @Nullable
    private PersonalInfoListener personalInfoListener;

    @NonNull
    private final String[] professionArray = new String[]{"Android Developer", "Data Scientist", "Astronaut", "Athlete"};

    @Nullable
    private FragmentPersonalInfoBinding binding;

    public PersonalInfoFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            personalInfoListener = (PersonalInfoListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement HomeAddressListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPersonalInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpProfessionDropDown();
        setUpListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        personalInfoListener = null;
    }

    private void setUpProfessionDropDown() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, professionArray);
        ((AutoCompleteTextView) binding.etProfession.getEditText()).setAdapter(adapter);
    }

    private boolean hasValidInput() {
        binding.etName.setError(null);
        binding.etEmail.setError(null);
        binding.etProfession.setError(null);
        binding.etPassword.setError(null);

        if (("").equals(valueOf(binding.etName.getEditText().getText()))) {
            binding.etName.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etEmail.getEditText().getText()))) {
            binding.etEmail.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etProfession.getEditText().getText()))) {
            binding.etProfession.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etPassword.getEditText().getText()))) {
            binding.etPassword.setError("Field must not be empty.");
            return false;
        }

        return true;
    }

    private void setUpListeners() {
        binding.conLayPersonalInfoRoot.setOnClickListener(v -> {
        });
        binding.btnCancel.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.btnSend.setOnClickListener(v -> btnSend());
    }

    private void btnSend() {
        if (!hasValidInput()) return;
        personalInfoListener.onPersonalInfoSendClick(
                valueOf(binding.etName.getEditText().getText()),
                valueOf(binding.etEmail.getEditText().getText()),
                valueOf(binding.etProfession.getEditText().getText()),
                valueOf(binding.etPassword.getEditText().getText())
        );

        // Pops 2 immediate fragments from backstack
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(
                fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 2).getId(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public interface PersonalInfoListener {
        void onPersonalInfoSendClick(String name, String email, String profession, String password);
    }
}