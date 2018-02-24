package hackaton.intuit.uk.smart_rooms_estimote.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import hackaton.intuit.uk.smart_rooms_estimote.services.NotificationService;

/**
 * Created by andulrv on 24/02/18.
 */

public class RemoveUserFromBookingTask extends AsyncTask<String, String, String> {

    private Context applicationContext;
    private NotificationService notificationService;

    public RemoveUserFromBookingTask(Context applicationContext) {
        this.notificationService = new NotificationService(applicationContext);
        this.applicationContext = applicationContext;
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
            restTemplate.delete(url);
            Log.i("app", "Removed user " + userId + " from booking " + bookingId);
        } catch (HttpClientErrorException e) {
            Log.e("app", e.getMessage());
        }

        notificationService.publishNotification("You left room " + meetingRoomName, "You were removed from " + meetingTitle);

        return "Removed";
    }

}
