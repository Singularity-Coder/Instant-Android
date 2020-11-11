package com.singularitycoder.navigationcomponents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.singularitycoder.navigationcomponents.databinding.FragmentLoginBinding;

import static java.lang.String.valueOf;

public final class LoginFragment extends Fragment {

    @Nullable
    private LoginViewStateViewModel loginViewStateViewModel;

    @Nullable
    private FragmentLoginBinding binding;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialise();
        setUpListeners(view);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Called after onStart()
        if (null == binding) return;
        loginViewStateViewModel.getEmail().observe(getViewLifecycleOwner(), string -> binding.etEmail.getEditText().setText(string));
        loginViewStateViewModel.getPassword().observe(getViewLifecycleOwner(), string -> binding.etPassword.getEditText().setText(string));

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Called after onStop()
        if (null == binding) return;
        loginViewStateViewModel.setEmail(valueOf(binding.etEmail.getEditText().getText()));
        loginViewStateViewModel.setPassword(valueOf(binding.etPassword.getEditText().getText()));
    }

    private void initialise() {
        loginViewStateViewModel = new ViewModelProvider(this).get(LoginViewStateViewModel.class);
    }

    private boolean hasValidInput() {
        binding.etEmail.setError(null);
        binding.etPassword.setError(null);

        if (("").equals(valueOf(binding.etEmail.getEditText().getText()))) {
            binding.etEmail.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etPassword.getEditText().getText()))) {
            binding.etPassword.setError("Field must not be empty.");
            return false;
        }

        return true;
    }

    private void setUpListeners(@NonNull View view) {
        final NavController navController = Navigation.findNavController(view);

        binding.btnLogin.setOnClickListener(v -> {
            if (!hasValidInput()) return;

            final Bundle bundle = new Bundle();
            bundle.putString("KEY_SCREEN", "login");
            bundle.putString("KEY_EMAIL", valueOf(binding.etEmail.getEditText().getText()));
            bundle.putString("KEY_PASSWORD", valueOf(binding.etPassword.getEditText().getText()));
            navController.navigate(R.id.action_login_fragment_to_home_fragment, bundle);
        });

        binding.btnSignup.setOnClickListener(v -> {
            // onBackPress avoid going to home and jump to login pops all added fragments including the destination we provided. which means when u land on the login frag n click back since ther r no other frags u will quit the app
            final NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.signup_fragment, true).build();
            navController.navigate(R.id.action_login_fragment_to_signup_fragment, null, navOptions);
        });
    }
}