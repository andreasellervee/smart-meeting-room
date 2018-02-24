package hackaton.intuit.uk.smart_rooms_estimote;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.estimote.cloud_plugin.common.EstimoteCloudCredentials;
import com.estimote.internal_plugins_api.cloud.proximity.ProximityAttachment;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import hackaton.intuit.uk.smart_rooms_estimote.entities.Booking;
import hackaton.intuit.uk.smart_rooms_estimote.network.CreateBookingTask;
import hackaton.intuit.uk.smart_rooms_estimote.network.RemoveUserFromBookingTask;
import hackaton.intuit.uk.smart_rooms_estimote.repository.BookingInMemoryRepo;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private ProximityObserver proximityObserver;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initProximityObserver();

        // Layout stuff
        textView = findViewById(R.id.textView);

        createGeneralProximityObserver();
        createNotificationChannel();
        requirements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Booking booking = BookingInMemoryRepo.getBooking();
        if (booking != null) {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            StringBuilder inAMeeting = new StringBuilder();
            inAMeeting.append("Currently in\n")
                    .append(booking.getTitle())
                    .append("\nuntil\n")
                    .append(df.format(booking.getEndDate()));
            textView.setText(inAMeeting.toString());
        }
    }

    private void createGeneralProximityObserver() {
        ProximityZone generalProximityObserver = this.proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("my_company", "meeting_room")
                .inCustomRange(0.1)
                .withOnEnterAction(new OnEntry())
                .withOnExitAction(new OnExit())
                .create();
        Log.i("app", "Added ENTRY proximity zone");
        this.proximityObserver.addProximityZone(generalProximityObserver);
    }

    private void createNotificationChannel(){
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = getApplicationContext().getString(R.string.channel_name);
            String description = getApplicationContext().getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel("homersimpson", name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
        }
    }

    private class OnEntry implements Function1<ProximityAttachment, Unit> {

        @Override
        public Unit invoke(ProximityAttachment proximityAttachment) {
            String meetingRoomId = proximityAttachment.getPayload().get("meeting_room_id");
            String meetingRoomName = proximityAttachment.getPayload().get("meeting_room_name");
            Log.i("app", "Welcome to meeting room " + meetingRoomName);
            CreateBookingTask createBookingTask = new CreateBookingTask(getApplicationContext(), textView);
            createBookingTask.execute(meetingRoomId, meetingRoomName);
            return null;
        }
    }

    private class OnExit implements Function1<ProximityAttachment, Unit> {

        @Override
        public Unit invoke(ProximityAttachment proximityAttachment) {
            String meetingRoomName = proximityAttachment.getPayload().get("meeting_room_name");
            Log.i("app", "Leaving " + meetingRoomName);
            textView.setText("Leaving meeting room: " + meetingRoomName);
            Booking currentBooking = BookingInMemoryRepo.getBooking();
            new RemoveUserFromBookingTask().execute(currentBooking.getId(), getString(R.string.user_id));
            BookingInMemoryRepo.setBooking(null);
            textView.setText("Welcome to Smart World!");
            return null;
        }
    }

    private void initProximityObserver() {
        this.proximityObserver =
                new ProximityObserverBuilder(getApplicationContext(), getEstimoteCredentials())
                        .withOnErrorAction(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "proximity observer error: " + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .build();
    }

    @NonNull
    private EstimoteCloudCredentials getEstimoteCredentials() {
        return new EstimoteCloudCredentials(getString(R.string.app_id), getString(R.string.app_token));
    }

    private void requirements() {
        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override public Unit invoke() {
                                Log.d("app", "requirements fulfilled");
                                proximityObserver.start();
                                return null;
                            }
                        },
                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override public Unit invoke(Throwable throwable) {
                                Log.e("app", "requirements error: " + throwable);
                                return null;
                            }
                        });
    }

}
