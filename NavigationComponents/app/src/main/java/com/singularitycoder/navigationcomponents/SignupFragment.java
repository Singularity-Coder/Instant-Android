package com.singularitycoder.navigationcomponents;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.singularitycoder.navigationcomponents.databinding.FragmentSignupBinding;

import java.util.ArrayList;

import static java.lang.String.valueOf;

public final class SignupFragment extends Fragment {

    @NonNull
    private final String TAG = "SignupFragment";

    @NonNull
    private final ArrayList<String> skillList = new ArrayList<>();

    @NonNull
    private final ArrayList<String> interestList = new ArrayList<>();

    @Nullable
    private String gender = "";

    @Nullable
    private SignupViewStateViewModel signupViewStateViewModel;

    @Nullable
    private FragmentSignupBinding binding;

    public SignupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialise();
        setUpInterestChips();
        setUpAgeSlider();
        setUpListeners(view);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Called after onStart()
        if (null == binding) return;
        try {
            signupViewStateViewModel.getName().observe(getViewLifecycleOwner(), string -> binding.etName.getEditText().setText(string));
            signupViewStateViewModel.getEmail().observe(getViewLifecycleOwner(), string -> binding.etEmail.getEditText().setText(string));
            signupViewStateViewModel.getPassword().observe(getViewLifecycleOwner(), string -> binding.etPassword.getEditText().setText(string));
            signupViewStateViewModel.getAge().observe(getViewLifecycleOwner(), string -> {
                try {
                    binding.sliderAge.setValue(Float.parseFloat(string.substring(string.length() - 6, string.length() - 1).trim()));
                } catch (NumberFormatException ignored) {
                }
            });
            restoreGenderViewState();
            restoreInterestsViewState();
            restoreSkillsViewState();
        } catch (Resources.NotFoundException ignored) {
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Called after onStop()
        if (null == binding) return;
        signupViewStateViewModel.setName(valueOf(binding.etName.getEditText().getText()));
        signupViewStateViewModel.setEmail(valueOf(binding.etEmail.getEditText().getText()));
        signupViewStateViewModel.setPassword(valueOf(binding.etPassword.getEditText().getText()));
        signupViewStateViewModel.setGender(gender);
        signupViewStateViewModel.setAge(valueOf(binding.tvAge.getText()));
        signupViewStateViewModel.setInterestList(interestList);
        signupViewStateViewModel.setSkillList(skillList);
    }

    private void restoreGenderViewState() {
        signupViewStateViewModel.getGender().observe(getViewLifecycleOwner(), string -> {
            for (byte i = 0; i < binding.chipGroupGender.getChildCount(); i++) {
                if (string.equals(((Chip) binding.chipGroupGender.getChildAt(i)).getText())) {
                    final Chip chip = (Chip) binding.chipGroupGender.getChildAt(i);
                    chip.setTextColor(getResources().getColor(R.color.purple_100));
                    chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                    chip.setChecked(true);
                }
            }
        });
    }

    private void restoreInterestsViewState() {
        final ArrayList<String> savedInterestList = new ArrayList<>();
        signupViewStateViewModel.getInterestList().observe(getViewLifecycleOwner(), strings -> {
            if (null == strings || 0 == strings.size()) return;
            savedInterestList.clear();
            savedInterestList.addAll(strings);

            interestList.clear();
            interestList.addAll(savedInterestList);
        });
        setUpInterestChips();
        for (byte i = 0; i < binding.chipGroupInterests.getChildCount(); i++) {
            for (byte j = 0; j < savedInterestList.size(); j++) {
                if (savedInterestList.get(j).equals(((Chip) binding.chipGroupInterests.getChildAt(i)).getText())) {
                    final Chip chip = (Chip) binding.chipGroupInterests.getChildAt(i);
                    chip.setText(savedInterestList.get(j));
                    chip.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                    chip.setTextColor(getResources().getColor(R.color.purple_100));
                    chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                    chip.setCheckedIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_100)));
                    chip.setChecked(true);
                }
            }
        }
    }

    private void restoreSkillsViewState() {
        final ChipDrawable chipDrawableCustom = ChipDrawable.createFromResource(getContext(), R.xml.chip_entry_skill);
        final ArrayList<String> savedSkillList = new ArrayList<>();
        signupViewStateViewModel.getSkillList().observe(getViewLifecycleOwner(), strings -> {
            if (null == strings || 0 == strings.size()) return;
            savedSkillList.clear();
            savedSkillList.addAll(strings);

            skillList.clear();
            skillList.addAll(skillList);
        });
        for (byte i = 0; i < savedSkillList.size(); i++) {
            final Chip chip = (Chip) binding.chipGroupSkills.getChildAt(i);
            chip.setText(savedSkillList.get(i));
            chip.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
            chip.setTextColor(getResources().getColor(R.color.purple_100));
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
            chip.setCheckedIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_100)));
            chip.setChipDrawable(chipDrawableCustom);
            chip.setChipIconResource(R.drawable.ic_twotone_handyman_24);
            chip.setIconStartPadding(0f);
            chip.setIconEndPadding(0f);
            chip.setCheckable(false);
            chip.setClickable(false);
            chip.setChecked(true);

            chip.setOnCloseIconClickListener(v -> binding.chipGroupSkills.removeView(chip));

            binding.chipGroupSkills.addView(chip);
        }
    }

    private void initialise() {
        // RTL-friendly chip layout
        binding.chipChoiceMale.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        binding.chipChoiceFemale.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        binding.chipChoiceOther.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);

        signupViewStateViewModel = new ViewModelProvider(this).get(SignupViewStateViewModel.class);
    }

    private void setUpInterestChips() {
        final String[] chipTextArray = {"Biking", "Running", "Research", "Self Improvement", "Tennis", "E-Sports"};

        for (byte i = 0; i < chipTextArray.length; i++) {
            final Chip chipInterest = (Chip) getLayoutInflater().inflate(R.layout.chip_filter_interest, binding.chipGroupInterests, false);
            chipInterest.setText(chipTextArray[i]);
            chipInterest.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
            chipInterest.setChipBackgroundColorResource(R.color.purple_100);
            chipInterest.setCheckedIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_100)));

            chipInterest.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    interestList.add(valueOf(chipInterest.getText()));
                    chipInterest.setTextColor(getResources().getColor(R.color.purple_100));
                    chipInterest.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                } else {
                    for (byte j = 0; j < interestList.size(); j++) {
                        if (valueOf(chipInterest.getText()).equals(interestList.get(j))) {
                            interestList.remove(valueOf(chipInterest.getText()));
                        }
                    }
                    chipInterest.setTextColor(getResources().getColor(android.R.color.black));
                    chipInterest.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_100)));
                }
            });

            binding.chipGroupInterests.addView(chipInterest);
        }
    }

    private void setUpAgeSlider() {
        // Responds to when slider's value is changed
        binding.sliderAge.addOnChangeListener((slider, value, fromUser) -> binding.tvAge.setText(valueOf(Math.round(value)) + " Years"));

        // Sets "Age" mark on the slider preview value
        binding.sliderAge.setLabelFormatter(value -> Math.round(value) + " Years");
    }

    private void setUpSkillChips() {
        if (("").equals(valueOf(binding.etSkills.getEditText().getText()))) return;

        skillList.add(valueOf(binding.etSkills.getEditText().getText()));

        final ChipDrawable chipDrawableDefault = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Entry);
        final ChipDrawable chipDrawableCustom = ChipDrawable.createFromResource(getContext(), R.xml.chip_entry_skill);

        final Chip chip = new Chip(getContext());
        chip.setChipDrawable(chipDrawableCustom);
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setChipIconResource(R.drawable.ic_twotone_handyman_24);
        chip.setText(valueOf(binding.etSkills.getEditText().getText()));
        chip.setTextColor(getResources().getColor(R.color.purple_100));
        chip.setIconStartPadding(0f);
        chip.setIconEndPadding(0f);
//        chip.setPadding(0, 0, 0, 0);

        chip.setOnCloseIconClickListener(v -> binding.chipGroupSkills.removeView(chip));

        binding.chipGroupSkills.addView(chip);
        binding.etSkills.getEditText().setText("");
    }

    private boolean hasValidInput() {
        binding.etName.setError(null);
        binding.etEmail.setError(null);
        binding.etPassword.setError(null);

        if (("").equals(valueOf(binding.etName.getEditText().getText()))) {
            binding.etName.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etEmail.getEditText().getText()))) {
            binding.etEmail.setError("Field must not be empty.");
            return false;
        }

        if (("").equals(valueOf(binding.etPassword.getEditText().getText()))) {
            binding.etPassword.setError("Field must not be empty.");
            return false;
        }

        if (0 == skillList.size()) {
            binding.etSkills.setError("Please add some skills!");
            return false;
        }

        if (("").equals(gender)) {
            Snackbar.make(binding.conLaySignupRoot, "Please choose a gender!", BaseTransientBottomBar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.error))
                    .setTextColor(getResources().getColor(android.R.color.white))
                    .show();
            return false;
        }

        if (0 == interestList.size()) {
            Snackbar.make(binding.conLaySignupRoot, "Please choose some interests!", BaseTransientBottomBar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.error))
                    .setTextColor(getResources().getColor(android.R.color.white))
                    .show();
            return false;
        }

        return true;
    }

    private void setUpListeners(@NonNull View view) {
        final NavController navController = Navigation.findNavController(view);

        binding.btnSignup.setOnClickListener(v -> {
            if (!hasValidInput()) return;

            final SignupFragmentDirections.ActionSignupFragmentToHomeFragment actionSignupFragmentToHomeFragment = SignupFragmentDirections.actionSignupFragmentToHomeFragment(
                    valueOf(binding.etName.getEditText().getText()),
                    valueOf(binding.etEmail.getEditText().getText()),
                    valueOf(binding.etPassword.getEditText().getText()),
                    gender,
                    valueOf(binding.tvAge.getText()),
                    interestList.toArray(new String[interestList.size()]),
                    skillList.toArray(new String[skillList.size()])
            );

            navController.navigate(actionSignupFragmentToHomeFragment);
        });

        binding.btnLogin.setOnClickListener(v -> {
            // onBackPress avoid going to home and jump to login pops all added fragments including the destination we provided. which means when u land on the login frag n click back since ther r no other frags u will quit the app
            final NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.login_fragment, true).build();
            navController.navigate(R.id.action_signup_fragment_to_login_fragment, null, navOptions);
        });

        binding.chipGroupGender.setOnCheckedChangeListener((chipGroup, checkedId) -> {
            // Responds to child chip checked/unchecked
            final Chip chip = chipGroup.findViewById(checkedId);
            if (null != chip) gender = valueOf(chip.getText());
        });

        changeGenderChipBackgroundColorsOnClick();

        binding.etSkills.setEndIconOnClickListener(v -> setUpSkillChips());

        binding.etSkills.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setUpSkillChips();
                return true;
            }
            return false;
        });
    }

    private void changeGenderChipBackgroundColorsOnClick() {
        final Chip[] genderChipViewsArray = {binding.chipChoiceMale, binding.chipChoiceFemale, binding.chipChoiceOther};
        for (byte i = 0; i < genderChipViewsArray.length; i++) {
            byte finalI = i;
            genderChipViewsArray[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    genderChipViewsArray[finalI].setTextColor(getResources().getColor(R.color.purple_100));
                    genderChipViewsArray[finalI].setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                } else {
                    genderChipViewsArray[finalI].setTextColor(getResources().getColor(android.R.color.black));
                    genderChipViewsArray[finalI].setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_100)));
                }
            });
        }
    }
}