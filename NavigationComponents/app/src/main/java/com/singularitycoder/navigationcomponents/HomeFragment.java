package com.singularitycoder.navigationcomponents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.singularitycoder.navigationcomponents.databinding.FragmentHomeBinding;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.valueOf;

public final class HomeFragment extends Fragment {

    @Nullable
    private FragmentHomeBinding binding;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showWelcomeSnack();
        getBundleDataFromLogin();
        getSafeArgsFromSignUp();
        final NavController navController = Navigation.findNavController(view);
        binding.btnLogout.setOnClickListener(v -> navController.navigate(R.id.action_home_fragment_to_login_fragment));
    }

    private void showWelcomeSnack() {
        Snackbar.make(binding.cordLayHomeRoot, "Welcome Home! Check out your profile details before logging out.", Snackbar.LENGTH_INDEFINITE)
                .setAction("GOT IT", v -> {
                })
//                .setAnchorView(binding.btnLogout)
                .setBackgroundTint(getResources().getColor(R.color.purple_500))
                .setTextColor(getResources().getColor(android.R.color.white))
                .setActionTextColor(getResources().getColor(android.R.color.white))
                .show();
    }

    private void getBundleDataFromLogin() {
        if (null == getArguments()) return;

        if (null != getArguments().getString("KEY_EMAIL") && !("").equals(getArguments().getString("KEY_EMAIL"))) {
            final String email = getArguments().getString("KEY_EMAIL");
            binding.tvEmail.setText("Email: " + email);
        }

        if (null != getArguments().getString("KEY_PASSWORD") && !("").equals(getArguments().getString("KEY_PASSWORD"))) {
            final String password = getArguments().getString("KEY_PASSWORD");
            binding.tvPassword.setText("Password: " + password);
        }

        binding.tvName.setVisibility(View.GONE);
        binding.tvAge.setVisibility(View.GONE);
        binding.tvGender.setVisibility(View.GONE);
        binding.tvInterests.setVisibility(View.GONE);
        binding.tvSkills.setVisibility(View.GONE);
    }

    private void getSafeArgsFromSignUp() {
        if (null == getArguments()) return;
        if (("login").equals(getArguments().getString("KEY_SCREEN"))) return;

        binding.tvName.setVisibility(View.VISIBLE);
        binding.tvAge.setVisibility(View.VISIBLE);
        binding.tvGender.setVisibility(View.VISIBLE);
        binding.tvInterests.setVisibility(View.VISIBLE);
        binding.tvSkills.setVisibility(View.VISIBLE);

        final HomeFragmentArgs args = HomeFragmentArgs.fromBundle(getArguments());

        if (!("").equals(args.getKeyName())) {
            final String name = args.getKeyName();
            binding.tvName.setText("Name: " + name);
        }

        if (!("").equals(args.getKeyEmail())) {
            final String email = args.getKeyEmail();
            binding.tvEmail.setText("Email: " + email);
        }

        if (!("").equals(args.getKeyPassword())) {
            final String password = args.getKeyPassword();
            binding.tvPassword.setText("Password: " + password);
        }

        if (!("").equals(args.getKeyAge())) {
            final String age = args.getKeyAge();
            binding.tvAge.setText("Age: " + age);
        }

        if (!("").equals(args.getKeyGender())) {
            final String gender = args.getKeyGender();
            binding.tvGender.setText("Gender: " + gender);
        }

        if (null != args.getKeySkillsArray()) {
            final List<String> skillList = Arrays.asList(args.getKeySkillsArray());
            binding.tvSkills.setText("Skills: " + valueOf(skillList).substring(1, valueOf(skillList).length() - 1));
        }

        if (null != args.getKeyInterestsArray()) {
            final List<String> interestList = Arrays.asList(args.getKeyInterestsArray());
            binding.tvInterests.setText("Interests: " + valueOf(interestList).substring(1, valueOf(interestList).length() - 1));
        }
    }
}