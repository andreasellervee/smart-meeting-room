package hackaton.intuit.uk.smart_rooms_estimote.services;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import hackaton.intuit.uk.smart_rooms_estimote.R;

/**
 * Created by alex on 24/02/2018.
 */

public class NotificationService {

    private Context applicationContext;

    public NotificationService(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void publishNotification(String title, String description) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(applicationContext, "homersimpson")
                .setSmallIcon(R.drawable.ic_honeybee_logo)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(1, mBuilder.build());
    }
}
