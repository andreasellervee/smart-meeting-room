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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import hackaton.intuit.uk.smart_rooms_estimote.JoinActivity;
import hackaton.intuit.uk.smart_rooms_estimote.R;
import hackaton.intuit.uk.smart_rooms_estimote.entities.Booking;
import hackaton.intuit.uk.smart_rooms_estimote.entities.BookingResponseWrapper;
import hackaton.intuit.uk.smart_rooms_estimote.entities.CreateBookingDto;
import hackaton.intuit.uk.smart_rooms_estimote.entities.User;
import hackaton.intuit.uk.smart_rooms_estimote.repository.BookingInMemoryRepo;
import hackaton.intuit.uk.smart_rooms_estimote.repository.CurrentRoomTracker;

/**
 * Created by andulrv on 24/02/18.
 */

public class CreateBookingTask extends AsyncTask<String, String, BookingResponseWrapper> {

    private final Context context;
    private final TextView title;
    private final TextView subtitle;

    public CreateBookingTask(Context applicationContext, TextView title, TextView subtitle) {
        this.context = applicationContext;
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    protected BookingResponseWrapper doInBackground(String... strings) {
        String meetingRoomId = strings[0];
        String meetingRoomName = strings[1];
        Log.i("app", "Trying to create a booking for meeting room: " + meetingRoomId + ", " + meetingRoomName);

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
        booking.setRoomId(meetingRoomId);
        booking.setOwnerId(context.getString(R.string.user_id));

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
            title.setText(createTitleMessage(booking).toString());
            subtitle.setText(createSubtitleMessage(booking).toString());
        } else if (bookingResponse.equals(HttpStatus.BAD_REQUEST)) {
            if (attendeeIds.contains(context.getString(R.string.user_id))) {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                StringBuilder titleBuilder = new StringBuilder();
                titleBuilder.append("Currently in ").append(booking.getTitle());
                title.setText(titleBuilder.toString());

                String subtitleBuilder = "until " + df.format(booking.getEndDate());
                subtitle.setText(subtitleBuilder);
                return;
            }
            Intent intent = new Intent(context, JoinActivity.class);
            intent.putExtra("bookingId", booking.getId());
            intent.putExtra("meetingRoomName", booking.getRoom() != null ? booking.getRoom().getName() : "noName");
            context.startActivity(intent);
        }
    }

    private StringBuilder createTitleMessage(Booking booking) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        StringBuilder meetingCreated = new StringBuilder();
        meetingCreated.append("Meeting\n");
        meetingCreated.append("'").append(booking.getTitle()).append("'");
        meetingCreated.append("\n created in \n");
        meetingCreated.append(booking.getRoom().getName());

        return meetingCreated;
    }

    private StringBuilder createSubtitleMessage(Booking booking) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        StringBuilder meetingCreated = new StringBuilder();
        meetingCreated.append("\nFrom\n");
        meetingCreated.append(df.format(booking.getStartDate()));
        meetingCreated.append("\nTo\n");
        meetingCreated.append(df.format(booking.getEndDate()));
        return meetingCreated;
    }

}
