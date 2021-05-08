package com.singularitycoder.factorypattern1.notifications;

import com.singularitycoder.factorypattern1.Notification;

// concrete sub class
public class PushNotification implements Notification {

    @Override
    public void notifyUser() {
        System.out.println("Sending a push notification");
    }
}
