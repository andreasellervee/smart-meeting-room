package hackaton.intuit.uk.smart_rooms_estimote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import hackaton.intuit.uk.smart_rooms_estimote.entities.User;
import hackaton.intuit.uk.smart_rooms_estimote.network.AddUserToBookingTask;
import hackaton.intuit.uk.smart_rooms_estimote.repository.BookingInMemoryRepo;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Intent intent = getIntent();
        final String bookingId = intent.getStringExtra("bookingId");
        final String meetingRoomName = intent.getStringExtra("meetingRoomName");

        TextView joinText = (TextView) findViewById(R.id.join_text);
        StringBuilder ongoingMeeting = new StringBuilder();
        ongoingMeeting.append("Ongoing meeting in '")
                .append(meetingRoomName)
                .append("' with \n")
                .append(getConcatenatedAttendees())
                .append(".\n")
                .append("Do you .. ?");
        joinText.setText(ongoingMeeting.toString());

        Button joinButton = (Button) findViewById(R.id.join_button);
        joinButton.setText("Join " + meetingRoomName + "?");
        Button leaveButton = (Button) findViewById(R.id.leave_button);
        leaveButton.setText("Whoops. I will leave " + meetingRoomName + " now");

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddUserToBookingTask(getApplicationContext()).execute(bookingId, getString(R.string.user_id), meetingRoomName, BookingInMemoryRepo.getBooking().getTitle());
                setResult(RESULT_OK);
                JoinActivity.this.finish();
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookingInMemoryRepo.setBooking(null);
                setResult(RESULT_CANCELED);
                JoinActivity.this.finish();
            }
        });
    }

    @NonNull
    private StringBuilder getConcatenatedAttendees() {
        StringBuilder attendeesConcat = new StringBuilder();
        List<User> attendees = BookingInMemoryRepo.getBooking().getAttendees();
        if (!attendees.isEmpty() && attendees.size() == 1) {
            attendeesConcat = new StringBuilder(attendees.get(0).getNickname());
        } else {
            for (int i = 0; i < attendees.size()-1; i++) {
                attendeesConcat.append(attendees.get(i).getNickname()).append(", ");
            }
            attendeesConcat.append(" and ").append(attendees.get(attendees.size() - 1).getNickname());
        }
        return attendeesConcat;
    }
}
