package hackaton.intuit.uk.smart_rooms_estimote.network;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by andulrv on 24/02/18.
 */

public class RemoveUserFromBookingTask extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... strings) {
        String bookingId = strings[0];
        String userId = strings[1];

        String url = "https://calm-ridge-51167.herokuapp.com/v1/booking/" + bookingId + "/user/" + userId;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        try {
            restTemplate.delete(url);
            Log.i("app", "Removed user " + userId + " from booking " + bookingId);
        } catch (HttpClientErrorException e) {
            Log.e("app", e.getMessage());
        }
        return "Removed";
    }

}
