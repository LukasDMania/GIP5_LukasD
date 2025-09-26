package be.ucll.util;

import com.vaadin.flow.component.notification.Notification;

public class NotificationUtil {
    public static void showNotification(String message, int duration) {
        Notification notification = new Notification();
        notification.setText(message);
        notification.setDuration(duration);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();
    }
}
