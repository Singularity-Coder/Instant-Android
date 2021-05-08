package com.singularitycoder.factorypattern1;

import com.singularitycoder.factorypattern1.notifications.EmailNotification;
import com.singularitycoder.factorypattern1.notifications.PushNotification;
import com.singularitycoder.factorypattern1.notifications.SMSNotification;

public class NotificationFactory {

    // This constructs Notification object and gives it to the caller
    public Notification createNotification(NotificationType channel) {
        if (channel == null) return null;
        if (NotificationType.SMS.equals(channel)) return new SMSNotification();
        if (NotificationType.EMAIL.equals(channel)) return new EmailNotification();
        if (NotificationType.PUSH.equals(channel)) return new PushNotification();
        return null;
    }
}