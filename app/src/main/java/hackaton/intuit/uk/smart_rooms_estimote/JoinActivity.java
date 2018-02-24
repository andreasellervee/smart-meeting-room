package hackaton.intuit.uk.smart_rooms_estimote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import hackaton.intuit.uk.smart_rooms_estimote.network.AddUserToBookingTask;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Intent intent = getIntent();
        final String meetingRoomId = intent.getStringExtra("meetingRoomId");
        String meetingRoomName = intent.getStringExtra("meetingRoomName");

        TextView joinText = (TextView) findViewById(R.id.join_text);
        joinText.setText("Ongoing meeting in " + meetingRoomName + ".");

        Button joinButton = (Button) findViewById(R.id.join_button);
        joinButton.setText("Join " + meetingRoomName + "?");
        Button leaveButton = (Button) findViewById(R.id.leave_button);
        leaveButton.setText("Whoops. I will leave " + meetingRoomName + " now");

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddUserToBookingTask().execute(meetingRoomId, "1");
                JoinActivity.this.finish();
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JoinActivity.this.finish();
            }
        });
    }
}
