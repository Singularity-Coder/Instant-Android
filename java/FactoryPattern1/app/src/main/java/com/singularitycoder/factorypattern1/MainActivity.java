package com.singularitycoder.factorypattern1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// https://www.geeksforgeeks.org/factory-method-design-pattern-in-java/
// Creational design pattern
// Deals with object creation
// define an interface. A java interface or an abstract class) and let the subclasses decide which object to instantiate.
// The factory method in the interface lets a class defer the instantiation to one or more concrete subclasses.
// object creation logic is hidden to the client.
// You can also create different factories if the existing one doenst suffice

// Steps:
// 1. Define factory method in interface or abstract class
// 2. Implement them to the types. Every type will be a subclass n they will implement the factory method
// 3. Create a factory class with a method that creates the object based on client's specification

// Uses:
// 1. Object with different types. Ex: BigInteger class accepts both String and Long
// 2. Wrapper classes like Integer, Boolean, String, etc
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Notification notification = new NotificationFactory().createNotification(NotificationType.SMS);
        notification.notifyUser();
    }
}