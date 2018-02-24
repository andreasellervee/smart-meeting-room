package hackaton.intuit.uk.smart_rooms_estimote.repository;

import hackaton.intuit.uk.smart_rooms_estimote.entities.Booking;

/**
 * Created by andulrv on 24/02/18.
 */

public class BookingInMemoryRepo {

    private static Booking booking;

    public static Booking getBooking() {
        return booking;
    }

    public static void setBooking(Booking booking) {
        BookingInMemoryRepo.booking = booking;
    }
}
