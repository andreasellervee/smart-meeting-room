package hackaton.intuit.uk.smart_rooms_estimote.entities;

import org.springframework.http.HttpStatus;

/**
 * Created by andulrv on 24/02/18.
 */

public class BookingResponseWrapper {

    Booking booking;
    HttpStatus httpStatus;

    public BookingResponseWrapper(HttpStatus statusCode, Booking existingBooking) {
        this.booking = existingBooking;
        this.httpStatus = statusCode;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return "BookingResponseWrapper{" +
                "booking=" + booking +
                ", httpStatus=" + httpStatus +
                '}';
    }
}
