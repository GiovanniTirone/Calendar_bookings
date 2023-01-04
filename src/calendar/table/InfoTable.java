package calendar.table;



import calendar.booking.Booking;

import java.time.LocalTime;
import java.util.Optional;

public class InfoTable {

    private Optional<LocalTime> startBookingTime;

    private Optional<Booking> booking;

    private StatusTableEnum statusTable;

    public InfoTable () {
        this.statusTable = StatusTableEnum.FREE;
        this.startBookingTime = Optional.empty();
        this.booking = Optional.empty();
    }

    public Optional<LocalTime> getStartBookingTime() {
        return startBookingTime;
    }

    public void setStartBookingTime(Optional<LocalTime> startBookingTime) {
        this.startBookingTime = startBookingTime;
    }

    public Optional<Booking> getBooking() {
        return booking;
    }

    public void setBooking(Optional<Booking> booking) {
        this.booking = booking;
    }

    public StatusTableEnum getStatusTable() {
        return statusTable;
    }

    public void setStatusTable(StatusTableEnum statusTable) {
        this.statusTable = statusTable;
    }

    public InfoTable clone () {
        InfoTable copy = new InfoTable();
        copy.statusTable = this.statusTable;
        copy.startBookingTime = this.startBookingTime.isPresent() ?
                Optional.of(LocalTime.of(this.startBookingTime.get().getHour(),this.startBookingTime.get().getMinute())) :
                Optional.empty();
        copy.booking = this.booking.isPresent() ? Optional.of(booking.get()) : Optional.empty();
        return copy;
    }

    public String getInfoString () {
        String str = "";
        str += "Status: " + statusTable.name() + " - Booking: " + booking.isPresent() + " - StartBookingTime: " + startBookingTime;
        return str;
    }



}
