package hackaton.intuit.uk.smart_rooms_estimote.network;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import hackaton.intuit.uk.smart_rooms_estimote.R;
import hackaton.intuit.uk.smart_rooms_estimote.services.NotificationService;

import static android.provider.Settings.Global.getString;

/**
 * Created by andulrv on 24/02/18.
 */

public class AddUserToBookingTask extends AsyncTask<String, String, String> {
    private static final String CHANNEL_ID = "bartsimpson";


    private Context applicationContext;
    private NotificationService notificationService;

    public AddUserToBookingTask(Context applicationContext) {
        this.applicationContext = applicationContext;
        notificationService = new NotificationService(applicationContext);
    }

    @Override
    protected String doInBackground(String... strings) {

        String bookingId = strings[0];
        String userId = strings[1];
        String meetingRoomName = strings[2];
        String meetingTitle = strings[3];

        String url = "https://calm-ridge-51167.herokuapp.com/v1/booking/" + bookingId + "/user/" + userId;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        try {
            restTemplate.postForEntity(url, null, Void.class);
            Log.i("app", "Added user " + userId + " to booking " + bookingId);
        } catch (HttpClientErrorException e) {
            Log.e("app", e.getMessage());
        }

        notificationService.publishNotification("You entered room " + meetingRoomName, "You just joined " + meetingTitle);

        return "Added";
    }



}
