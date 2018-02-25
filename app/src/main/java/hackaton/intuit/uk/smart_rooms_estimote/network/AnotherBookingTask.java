package hackaton.intuit.uk.smart_rooms_estimote.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import hackaton.intuit.uk.smart_rooms_estimote.R;
import hackaton.intuit.uk.smart_rooms_estimote.entities.Booking;
import hackaton.intuit.uk.smart_rooms_estimote.entities.BookingResponseWrapper;
import hackaton.intuit.uk.smart_rooms_estimote.entities.CreateBookingDto;
import hackaton.intuit.uk.smart_rooms_estimote.entities.User;
import hackaton.intuit.uk.smart_rooms_estimote.repository.BookingInMemoryRepo;
import hackaton.intuit.uk.smart_rooms_estimote.repository.CurrentRoomTracker;

/**
 * Created by andulrv on 25/02/18.
 */

public class AnotherBookingTask extends AsyncTask<String, String, BookingResponseWrapper> {
    private Context applicationContext;

    public AnotherBookingTask(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected BookingResponseWrapper doInBackground(String... strings) {
        Log.i("app", "Trying to create a booking in another meeting room");

        String url = "https://calm-ridge-51167.herokuapp.com/v1/booking";
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
        restTemplate.getMessageConverters().add(mapper);

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(new Date());
        endTime.add(Calendar.MINUTE, 30);

        CreateBookingDto booking = new CreateBookingDto();
        booking.setStartDate(new Date());
        booking.setEndDate(endTime.getTime());
        booking.setOwnerId(applicationContext.getString(R.string.user_id));

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
                    CurrentRoomTracker.setRoomId(null);
                }
                return new BookingResponseWrapper(e.getStatusCode(), existingBooking);
            }
            CurrentRoomTracker.setRoomId(null);
        } catch (Exception e) {
            CurrentRoomTracker.setRoomId(null);
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
        Set<String> attendeeIds = new HashSet<>();
        for (User user : booking.getAttendees()) {
            attendeeIds.add(user.getId());
        }
        BookingInMemoryRepo.setBooking(booking);
        HttpStatus bookingResponse = bookingWrapper.getHttpStatus();

        if (bookingResponse.equals(HttpStatus.CREATED)) {
            // new booking created
            Log.i("app", "Created a new booking " + booking);
        }
    }
}
