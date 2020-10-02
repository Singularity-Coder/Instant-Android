package com.example.sharedviewmodel;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.sharedviewmodel.databinding.FragmentPersonalInfoBinding;

import static java.lang.String.valueOf;

public final class PersonalInfoFragment extends Fragment {

    @NonNull
    private final String[] professionArray = new String[]{"Android Developer", "Data Scientist", "Astronaut", "Athlete"};

    @Nullable
    private SharedViewModel sharedViewModel;

    @Nullable
    private FragmentPersonalInfoBinding binding;

    public PersonalInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPersonalInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialise();
        setUpProfessionDropDown();
        setUpListeners();
    }

    private void initialise() {
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
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
        sharedViewModel.setName(valueOf(binding.etName.getEditText().getText()));
        sharedViewModel.setEmail(valueOf(binding.etEmail.getEditText().getText()));
        sharedViewModel.setProfession(valueOf(((AutoCompleteTextView) binding.etProfession.getEditText()).getText()));
        sharedViewModel.setPassword(valueOf(binding.etPassword.getEditText().getText()));

        // Pops 2 immediate fragments from backstack
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(
                fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 2).getId(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}