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

public class OpeningCalendar {


    private TreeSet<LocalDate> activateDates;
    private TreeMap<LocalDate, TreeSet<Booking>> bookingsMap;
    private TreeMap<LocalDate, TreeSet<OpeningRange<RangeTimeOfAllTables>>> openingRangesMap;
    // todo sostituire TreeSet con un OpeningRangeDay

    private Restaurant restaurant;

    private static OpeningCalendar calendar = new OpeningCalendar();

    private OpeningCalendar () {
        this.activateDates = new TreeSet<>();
        this.bookingsMap = new TreeMap<>();
        this.openingRangesMap = new TreeMap<>();
        this.restaurant = Restaurant.getInstance();
    }

    public static OpeningCalendar getInstance () {
        return calendar;
    }

    public void activateIntervalFromDate (LocalDate startDate, int numberOfDays) {
        for(int i=0; i<numberOfDays; i++) {
            LocalDate nextDay = startDate.plusDays(i);
            if(activateDates.contains(nextDay)) continue;
            activateDates.add(nextDay);
            bookingsMap.put(nextDay, new TreeSet<>(Comparators.getCompareBookingsByDateTime()));
            openingRangesMap.put(nextDay,new TreeSet<>());
        }
    }

    public void activateOpeningRange (LocalDate date, LocalTime openingTime, LocalTime closureTime) throws Exception {
        if(!activateDates.contains(date)) {
            throw  new Exception("Date out of calendar");
        }
        OpeningRange<RangeTimeOfAllTables> newOpeningRange = new OpeningRange<>(RangeTimeOfAllTables.class,openingTime,closureTime);
        RangeTimeOfAllTables totalRange = newOpeningRange.getRangeByTime(openingTime).get();
        for(Table table : restaurant.getTavoli().values()) {
            OpeningRange<RangeTimeOfTable> tableOpeningRange = table.setAndGetOpeningRange(new OpeningRange<>(RangeTimeOfTable.class,openingTime,closureTime));
            totalRange.tablesOriginalRangesMap.put(table,tableOpeningRange.getRangeByTime(openingTime).get());
        }
        openingRangesMap.get(date).add(newOpeningRange);

        //TODO VERIFICARE LA SOVRAPPOSIZIONE CON ALTRI OPENING RANGES
    }


    private Optional<OpeningRange<RangeTimeOfAllTables>> getOpeningRangeByDateTime (LocalDate date, LocalTime time) {
        for(OpeningRange<RangeTimeOfAllTables> openingRange : openingRangesMap.get(date)){
            if(openingRange.checkTimeInOpeningRange(time)){
                return Optional.of(openingRange);
            }
        }
        return Optional.empty();
    }


    public StatusBooking bookTable (List<Client> clientsList, LocalDate date, LocalTime time, long bookingRange) throws Exception {
        //a seconda del peoplenumber mi sposto nel rangeTimeSetCorrispondente

        //todo check sulla date

        int tableSize = clientsList.size();
        /*if(tableSize > ristorante.getTableSizes().last()){
            return new StatusBooking(false, InfoBookingEnum.PEOPLE_OUT_OF_MAX_TABLE_SIZE);
        }*/
        StatusBooking status; //= new StatusBooking(false,InfoBookingEnum.NO_INFO);

        Optional<OpeningRange<RangeTimeOfAllTables>> targetOpeningRangeOpt = getOpeningRangeByDateTime(date,time);
        if(targetOpeningRangeOpt.isEmpty())
            return new StatusBooking(false, InfoBookingEnum.TIME_OUT_OF_OPENING_RANGE);


        OpeningRange<RangeTimeOfAllTables> targetOpeningRange = targetOpeningRangeOpt.get();

        RangeTimeOfAllTables targetRange = targetOpeningRange.getRangeByTime(time).get();

        status = targetRange.bookTable(clientsList,  date,  time,  bookingRange);

        if(!status.isSuccess()) {
            switch(status.getInfo()){
                case IMPOSSIBLE -> {}
                case PROPOSE_NEW_TIME -> {}
                case NO_INFO -> {}
            }
        }

        Booking successfulBooking = status.getSuccessfulBooking().get();
        bookingsMap.get(date).add(successfulBooking);
        for(RangeTimeOfTable range : successfulBooking.getTable().getOpeningRange().getSubRangesSet())
            targetOpeningRange.addRangeTime(createRangeTimeFromRangeTimeOfTable(
                    range,status.getSuccessfulBooking().get().getTable()));
        /*targetOpeningRange.addRangeTime(createRangeTimeFromRangeTimeOfTable(
                status.getRangeTimeOfBooking().get(),status.getSuccessfulBooking().get().getTable()));
        */
        return status;

    }

    private RangeTimeOfAllTables createRangeTimeFromRangeTimeOfTable(RangeTimeOfTable rangeTimeOfTable, Table table) {
        RangeTimeOfAllTables rangeTimeOfAllTables = new RangeTimeOfAllTables(rangeTimeOfTable.getStartTime(),rangeTimeOfTable.getEndTime());
        rangeTimeOfAllTables.tablesOriginalRangesMap.put(table, rangeTimeOfTable);
        return rangeTimeOfAllTables;
    }


    // ----------------- PRINT METHODS ------------------------

    public void printActivateDays () {
        activateDates.forEach(System.out::println);
    }

    public String getDetailsOfDate (LocalDate date) {
        String str = "";
        str += "\nBookings of " +  date + " : \n" ;
        if(bookingsMap.get(date).isEmpty())
            str += " No bookings for this date\n";
        else
            for(Booking booking : bookingsMap.get(date))
                str += booking.getDetails() .indent(-1)+ "\n";
        return str;
    }

    public String getDetailsBetweenTwoDates (LocalDate startDate, LocalDate endDate) {
        SortedMap<LocalDate,TreeSet<Booking>> subMap =  bookingsMap.subMap(startDate,true,endDate,true);
        String str = "";
        for(LocalDate date : subMap.keySet()){
            str += "\nBookings of " +  date + " : \n" ;
            if(bookingsMap.get(date).isEmpty())
                str += " No bookings for this date\n";
            else
                str += getDetailsOfDate(date);
            str += "--------------------\n";
        }
        return str;
    }

    public String getDetailsFromDate (LocalDate startDate, int numberOfDays) {
        LocalDate endDate = startDate.plusDays(numberOfDays);
        return getDetailsBetweenTwoDates(startDate,endDate);
    }

    public String getDetailsOfAllDays () {
        return getDetailsBetweenTwoDates(bookingsMap.firstKey(),bookingsMap.lastKey());
    }

    public void printDetailsOfDate (LocalDate date) {
        System.out.println(getDetailsOfDate(date));
    }

    public void printDetailsBetweenTwoDates (LocalDate startDate, LocalDate endDate){
        System.out.println(getDetailsBetweenTwoDates(startDate,endDate));
    }

    public void printDetailsFromDate (LocalDate startDate, int numberOfDays){
        System.out.println(getDetailsFromDate(startDate,numberOfDays));
    }

    public void printDetailsOfAllDays ( ) {
        System.out.println(getDetailsOfAllDays());
    }

    public void printRangesOfDate (LocalDate date) {
        String str = "Opening ranges of " + date  + " :\n ";
        for(OpeningRange openingRange : openingRangesMap.get(date)){
           // str += "\nOpening range: " ;
            str += openingRange.getDetailsOfRanges();
        }
        System.out.println(str);
    }

    public void printRangesOfDateTimeFormatted (LocalDate date, LocalTime time) {
        OpeningRange<RangeTimeOfAllTables> targetOpeningRange = getOpeningRangeByDateTime(date,time).get();
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
        System.out.println(Format.formatStringMatrix(mat));
    }

    public void printAllRanges () {
        for(LocalDate date : activateDates){
            printRangesOfDate(date);
        }
    }
}
