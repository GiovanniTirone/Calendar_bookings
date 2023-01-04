package calendar.table;

import calendar.booking.Booking;
import calendar.range.RangeTime;

import java.time.LocalTime;
import java.util.Optional;
import java.util.TreeSet;

public class RangeTimeOfTable extends RangeTime<InfoTable> {

    private InfoTable infoTable ;

    public RangeTimeOfTable(LocalTime startTime, LocalTime endTime){
        super(startTime,endTime);
        this.infoTable = new InfoTable();
    }

    public RangeTimeOfTable(Booking booking){
        super(booking.getTime().minusMinutes(60),booking.getTime().plusMinutes(booking.getRangeTime())); //todo mettere min time bookings ->60
        this.infoTable = new InfoTable();
        this.infoTable.setStartBookingTime(Optional.of(booking.getTime()));
        this.infoTable.setBooking(Optional.of(booking));
        this.infoTable.setStatusTable(StatusTableEnum.BOOKED);
    }


    public StatusTableEnum getStatusTable() {
        return this.infoTable.getStatusTable();
    }

    public void setStatusTable(StatusTableEnum statusTable) {
        this.infoTable.setStatusTable(statusTable);
    }

    public Optional<LocalTime> getStartBookingTime() {
        return this.infoTable.getStartBookingTime();
    }

    public InfoTable getInfoTable() {
        return infoTable;
    }

    public void setInfoTable(InfoTable infoTable) {
        this.infoTable = infoTable;
    }

    public void setStartBookingTime(Optional<LocalTime> startBookingTime) {
        this.infoTable.setStartBookingTime(startBookingTime);
    }

    public Optional<Booking> getBooking() {
        return this.infoTable.getBooking();
    }


    public void  setBooking ( Booking booking){
        this.infoTable.setBooking(Optional.of(booking));
        this.infoTable.setStatusTable(StatusTableEnum.BOOKED);
    }



    @Override
    public void overlapDatas(InfoTable newInfo) {  //todo sistemare
        if(this.infoTable.getStatusTable() == StatusTableEnum.BOOKED){
            LocalTime newEndTime = newInfo.getBooking().get().getTime().plusMinutes(newInfo.getBooking().get().getRangeTime());
            if(this.getStartTime().isBefore(newEndTime) && this.getStartBookingTime().get().isAfter(newEndTime)){
                this.setInfoTable(newInfo);
            }
            else{
                return;
            }
        }
        this.setInfoTable(newInfo);
    }

    @Override
    public InfoTable getDatas() {
        return this.infoTable;
    }

    @Override
    public void setDatas(InfoTable infoTable) {
        this.infoTable = infoTable;
    }

    @Override
    public InfoTable copyDatas(InfoTable dataToCopy) {
        return this.infoTable.clone(); //todo controllare
    }

    @Override
    public String getDetailsString() {
        String str = getStartTime() + " - " +getEndTime();
        str += "\n" + infoTable.getInfoString();
        return str;
    }


    public TreeSet<RangeTimeOfTable> divide (LocalTime middleTime) throws Exception {
        return (TreeSet<RangeTimeOfTable>) super.divide(this.getClass(),middleTime);
    }



}
