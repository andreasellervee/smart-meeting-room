package hackaton.intuit.uk.smart_rooms_estimote.entities;

import java.util.Date;

/**
 * Created by andulrv on 24/02/18.
 */

public class CreateBookingDto {

    private Date startDate;
    private Date endDate;
    private String ownerId;
    private String roomId;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
