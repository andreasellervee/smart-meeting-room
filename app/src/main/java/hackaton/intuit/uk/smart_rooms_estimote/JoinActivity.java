package hackaton.intuit.uk.smart_rooms_estimote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import hackaton.intuit.uk.smart_rooms_estimote.network.AddUserToBookingTask;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Intent intent = getIntent();
        final String meetingRoomId = intent.getStringExtra("meetingRoomId");
        String meetingRoomName = intent.getStringExtra("meetingRoomName");

        Button button = (Button) findViewById(R.id.join_button);
        button.setText("Join " + meetingRoomName + "?");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddUserToBookingTask().execute(meetingRoomId, "1");
                JoinActivity.this.finish();
            }
        });
    }
}
