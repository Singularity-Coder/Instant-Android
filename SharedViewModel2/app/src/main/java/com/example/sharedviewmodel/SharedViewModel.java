package com.example.sharedviewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public final class SharedViewModel extends ViewModel {

    // Personal Info
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> profession = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();

    // Home or Office Address
    private MutableLiveData<String> building = new MutableLiveData<>();
    private MutableLiveData<String> street = new MutableLiveData<>();
    private MutableLiveData<String> city = new MutableLiveData<>();
    private MutableLiveData<String> pin = new MutableLiveData<>();

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

    public LiveData<String> getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession.setValue(profession);
    }

    public LiveData<String> getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public LiveData<String> getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building.setValue(building);
    }

    public LiveData<String> getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street.setValue(street);
    }

    public LiveData<String> getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city.setValue(city);
    }

    public LiveData<String> getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin.setValue(pin);
    }
}