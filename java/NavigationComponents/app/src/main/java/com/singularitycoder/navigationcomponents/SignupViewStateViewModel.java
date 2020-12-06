package com.singularitycoder.navigationcomponents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public final class SignupViewStateViewModel extends ViewModel {

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<String> gender = new MutableLiveData<>();
    private MutableLiveData<String> age = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> interestList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> skillList = new MutableLiveData<>();

    public LiveData<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public LiveData<String> getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public LiveData<String> getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.setValue(gender);
    }

    public LiveData<String> getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age.setValue(age);
    }

    public LiveData<ArrayList<String>> getInterestList() {
        return interestList;
    }

    public void setInterestList(ArrayList<String> interestList) {
        this.interestList.setValue(interestList);
    }

    public LiveData<ArrayList<String>> getSkillList() {
        return skillList;
    }

    public void setSkillList(ArrayList<String> skillList) {
        this.skillList.setValue(skillList);
    }
}
