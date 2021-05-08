package com.singularitycoder.factorypattern1.notifications;

import com.singularitycoder.factorypattern1.Notification;

// concrete sub class
public class EmailNotification implements Notification {

    @Override
    public void notifyUser() {
        System.out.println("Sending an e-mail notification");
    }
}
