package calendar.booking;

import calendar.table.RangeTimeOfTable;
import java.time.LocalTime;
import java.util.Optional;

public class StatusBooking {

    private boolean success;

    private InfoBookingEnum info;

    private Optional<LocalTime> newTimeToPropose;

    private Optional<Booking> successfulBooking;

   // private Optional<RangeTimeOfTable> rangeTimeOfBooking;

  //  private Optional<RangeTimeOfTable> range_BOOKED_NEXT;

    public StatusBooking (boolean success, InfoBookingEnum info){
        this.success = success;
        this.info = info;
        this.successfulBooking = Optional.empty();
        this.newTimeToPropose = Optional.empty();
    }

    public StatusBooking (boolean success, InfoBookingEnum info,LocalTime newTimeToPropose){
        this.success = success;
        this.info = info;
        this.successfulBooking = Optional.empty();
        this.newTimeToPropose = Optional.of(newTimeToPropose);
     //   this.rangeTimeOfBooking = Optional.empty();
    }


    public StatusBooking (boolean success, InfoBookingEnum info,Booking booking){
        this.success = success;
        this.info = info;
        this.successfulBooking = Optional.of(booking);
        this.newTimeToPropose = Optional.empty();
   //     this.rangeTimeOfBooking = Optional.of(rangeTimeOfTable);
    //    this.range_BOOKED_NEXT = Optional.of(range_BOOKED_NEXT);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public InfoBookingEnum getInfo() {
        return info;
    }

    public void setInfo(InfoBookingEnum info) {
        this.info = info;
    }

    public Optional<LocalTime> getNewTimeToPropose() {
        return newTimeToPropose;
    }

    public void setNewTimeToPropose(Optional<LocalTime> newTimeToPropose) {
        this.newTimeToPropose = newTimeToPropose;
    }

    public Optional<Booking> getSuccessfulBooking() {
        return successfulBooking;
    }

    public void setSuccessfulBooking(Optional<Booking> successfulBooking) {
        this.successfulBooking = successfulBooking;
    }

    /*
    public Optional<RangeTimeOfTable> getRangeTimeOfBooking() {
        return rangeTimeOfBooking;
    }



    public void setRangeTimeOfBooking(Optional<RangeTimeOfTable> rangeTimeOfBooking) {
        this.rangeTimeOfBooking = rangeTimeOfBooking;
    }

    public Optional<RangeTimeOfTable> getRange_BOOKED_NEXT() {
        return range_BOOKED_NEXT;
    }

     */

    public void printDetails () {
        if(success) {
            System.out.println("Success: " + success + " - Booking: " + successfulBooking.get());
        }else{
            System.out.println("Success: " + success + " - Info: " + info);
        }
    }
}
