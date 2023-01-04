package calendar.newcalendar;

import calendar.booking.Booking;
import calendar.booking.InfoBookingEnum;
import calendar.booking.StatusBooking;
import calendar.range.OpeningRange;
import calendar.table.RangeTimeOfTable;
import calendar.table.StatusTableEnum;
import calendar.table.Table;
import restaurant.Client;
import restaurant.Restaurant;
import utility.Comparators;
import utility.Format;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class OpeningCalendarDay {

    Day day;

    private TreeSet<Booking> bookingsSet;

    private TreeSet<OpeningRange<RangeTimeOfAllTables>> openingRangesSet;
    // todo sostituire TreeSet con un OpeningRangeDay

    private Restaurant restaurant;


    protected OpeningCalendarDay(Day day) {
        this.day = day;
        this.bookingsSet = new TreeSet<>(Comparators.getCompareBookingsByDateTime());
        this.openingRangesSet = new TreeSet<>();
        this.restaurant = Restaurant.getInstance();
    }


    public void activateOpeningRange (LocalTime openingTime, LocalTime closureTime) throws Exception {
        OpeningRange<RangeTimeOfAllTables> newOpeningRange = new OpeningRange<>(RangeTimeOfAllTables.class,openingTime,closureTime);
        RangeTimeOfAllTables totalRange = newOpeningRange.getRangeByTime(openingTime).get();
        for(Table table : restaurant.getTavoli().values()) {
            OpeningRange<RangeTimeOfTable> tableOpeningRange = table.setAndGetOpeningRange(new OpeningRange<>(RangeTimeOfTable.class,openingTime,closureTime));
            totalRange.tablesOriginalRangesMap.put(table,tableOpeningRange.getRangeByTime(openingTime).get());
        }
        openingRangesSet.add(newOpeningRange);

        //TODO VERIFICARE LA SOVRAPPOSIZIONE CON ALTRI OPENING RANGES - creare un metodo per unire gli opening ranges
    }


    protected Optional<OpeningRange<RangeTimeOfAllTables>> getOpeningRangeByTime (LocalTime time) {
        for(OpeningRange<RangeTimeOfAllTables> openingRange : openingRangesSet){
            if(openingRange.checkTimeInOpeningRange(time)){
                return Optional.of(openingRange);
            }
        }
        return Optional.empty();
    }


    public StatusBooking bookTable (List<Client> clientsList, LocalDate date, LocalTime time, long bookingRange) throws Exception {
        //a seconda del peoplenumber mi sposto nel rangeTimeSetCorrispondente

        Optional<OpeningRange<RangeTimeOfAllTables>> targetOpeningRangeOpt = getOpeningRangeByTime(time);
        if(targetOpeningRangeOpt.isEmpty())
            return new StatusBooking(false, InfoBookingEnum.TIME_OUT_OF_OPENING_RANGE);
        OpeningRange<RangeTimeOfAllTables> targetOpeningRange = targetOpeningRangeOpt.get();

        RangeTimeOfAllTables targetRange = targetOpeningRange.getRangeByTime(time).get();

        StatusBooking status = targetRange.bookTable(clientsList, date,  time,  bookingRange);

        if(!status.isSuccess()) return status;

        Booking successfulBooking = status.getSuccessfulBooking().get();
        bookingsSet.add(successfulBooking);
        for(RangeTimeOfTable range : successfulBooking.getTable().getOpeningRange().getSubRangesSet())
            targetOpeningRange.addRangeTime(createRangeTimeFromRangeTimeOfTable(
                    range,status.getSuccessfulBooking().get().getTable()));

        return status;
    }

    private RangeTimeOfAllTables createRangeTimeFromRangeTimeOfTable(RangeTimeOfTable rangeTimeOfTable, Table table) {
        RangeTimeOfAllTables rangeTimeOfAllTables = new RangeTimeOfAllTables(rangeTimeOfTable.getStartTime(),rangeTimeOfTable.getEndTime());
        rangeTimeOfAllTables.tablesOriginalRangesMap.put(table, rangeTimeOfTable);
        return rangeTimeOfAllTables;
    }


    // ----------------- PRINT and TOSTRING METHODS ------------------------


    public String getDetailsOfBookings () {
        String str = "";
        if(bookingsSet.isEmpty())
            str += " No bookings for this date\n";
        else
            for(Booking booking : bookingsSet)
                str += booking.getDetails() .indent(-1)+ "\n";
        return str;
    }

    public String getAllRangesFormatted (){
        String str = "";
        for(OpeningRange<RangeTimeOfAllTables> openingRange : openingRangesSet){
            str += getRangesOfOpeningRangeFormatted(openingRange) + "\n\n";
        }
        return str;
    }


    public String getRangesOfTimeFormatted (LocalTime time ){
         OpeningRange<RangeTimeOfAllTables> targetOpeningRange = getOpeningRangeByTime(time).get();
         return getRangesOfOpeningRangeFormatted(targetOpeningRange);
    }

    public String getRangesOfOpeningRangeFormatted(OpeningRange<RangeTimeOfAllTables> targetOpeningRange) {
        TreeSet<Table> tables = new TreeSet<>(Comparators.getCompareTablesBySeating());
        tables.addAll(restaurant.getTavoli().values());
        String mat [][] = new String[tables.size()+1][targetOpeningRange.getSubRangesSet().size()+1];
        mat[0][0] = "RangeTime: ";
        int i=1;
        for(Table table : tables){
            mat[i][0] = "Table: " + table.getNumeroTavolo() + " : ";
            i++;
        }
        int j=1;
        for(RangeTimeOfAllTables range : targetOpeningRange.getSubRangesSet()){
            mat[0][j] = range.getStartTime() + " - " + range.getEndTime();
            i=1;
            for(Table table : tables){
                StatusTableEnum statusTable =  range.tablesOriginalRangesMap.get(table).getStatusTable();
                mat[i][j] = statusTable.name();
                if(statusTable == StatusTableEnum.BOOKED){
                    mat[i][j] = mat[i][j].substring(0,4) + "-"  + range.tablesOriginalRangesMap.get(table).getInfoTable().getBooking().get().getId();
                }
                i++;
            }
            j++;
        }
        return Format.formatStringMatrix(mat);
    }

}
