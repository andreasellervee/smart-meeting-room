package hackaton.intuit.uk.smart_rooms_estimote.repository;

/**
 * Created by andulrv on 24/02/18.
 */

public class CurrentRoomTracker {

    public static String getRoomId() {
        return roomId;
    }

    public static void setRoomId(String roomId) {
        CurrentRoomTracker.roomId = roomId;
    }

    private static String roomId;

}
