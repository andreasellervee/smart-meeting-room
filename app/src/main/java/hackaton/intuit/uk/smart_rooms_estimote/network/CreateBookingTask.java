package hackaton.intuit.uk.smart_rooms_estimote.network;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import hackaton.intuit.uk.smart_rooms_estimote.JoinActivity;
import hackaton.intuit.uk.smart_rooms_estimote.entities.Booking;
import hackaton.intuit.uk.smart_rooms_estimote.entities.BookingResponseWrapper;
import hackaton.intuit.uk.smart_rooms_estimote.entities.CreateBookingDto;

/**
 * Created by andulrv on 24/02/18.
 */

public class CreateBookingTask extends AsyncTask<String, String, BookingResponseWrapper> {

    private final Context context;
    private final TextView textView;

    public CreateBookingTask(Context applicationContext, TextView textView) {
        this.context = applicationContext;
        this.textView = textView;
    }

    @Override
    protected BookingResponseWrapper doInBackground(String... strings) {
        String meetingRoomId = strings[0];
        String meetingRoomName = strings[1];
        Log.i("app", "Trying to create a booking for meeting room: " + meetingRoomId + ", " + meetingRoomName);

        String url = "https://calm-ridge-51167.herokuapp.com/v1/booking";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(new Date());
        endTime.add(Calendar.MINUTE, 30);

        CreateBookingDto booking = new CreateBookingDto();
        booking.setStartDate(new Date());
        booking.setEndDate(endTime.getTime());
        booking.setRoomId(meetingRoomId);
        booking.setOwnerId("1");

        try {
            ResponseEntity<Booking> stringResponseEntity = restTemplate.postForEntity(url, booking, Booking.class);
            if (stringResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                return new BookingResponseWrapper(HttpStatus.CREATED, stringResponseEntity.getBody());
            }
        } catch (HttpClientErrorException e) {
            Log.e("app", e.getMessage());
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                ObjectMapper objectMapper = new ObjectMapper();
                Log.i("app", e.getResponseBodyAsString());
                Booking existingBooking = null;
                try {
                    existingBooking = objectMapper.readValue(e.getResponseBodyAsString(), Booking.class);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return new BookingResponseWrapper(e.getStatusCode(), existingBooking);
            }
        } catch (Exception e) {
            Log.e("app", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(BookingResponseWrapper bookingWrapper) {
        Log.i("app", "Rest api call response: " + bookingWrapper);
        if (bookingWrapper == null) {
            return;
        }

        Booking booking = bookingWrapper.getBooking();
        HttpStatus bookingResponse = bookingWrapper.getHttpStatus();

        if (bookingResponse.equals(HttpStatus.CREATED)) {
            // new booking created
            textView.setText("Meeting " + booking.getTitle() + " created in " + booking.getRoom().getName());
        } else if (bookingResponse.equals(HttpStatus.BAD_REQUEST)) {
            // booking existing, wanna join?
            Intent intent = new Intent(context, JoinActivity.class);
            intent.putExtra("meetingRoomId", booking.getId());
            intent.putExtra("meetingRoomName", booking.getRoom() != null ? booking.getRoom().getName() : "noName");
            context.startActivity(intent);
        }
    }

}
