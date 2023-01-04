package calendar.newcalendar;

import calendar.booking.StatusBooking;
import calendar.range.OpeningRange;
import restaurant.Client;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class OpeningCalendarAllDays {

    private TreeSet<Day> activeDays;

    private TreeMap<Day,OpeningCalendarDay> openingDaysMap;

    private static OpeningCalendarAllDays calendar = new OpeningCalendarAllDays();

    private OpeningCalendarAllDays () {
        this.activeDays = new TreeSet<>();
        this.openingDaysMap = new TreeMap<>();
    }

    public static OpeningCalendarAllDays getInstance () {
        return calendar;
    }

    public void activateIntervalFromDate (LocalDate startDate, int numberOfDays) {
        for(int i=0; i<numberOfDays; i++) {
            Day nextDay = new Day(startDate.plusDays(i));
            if(activeDays.contains(nextDay)) continue;
            activeDays.add(nextDay);
            openingDaysMap.put(nextDay,new OpeningCalendarDay(nextDay));
        }
    }

    public void activateOpeningRange (LocalDate date, LocalTime openingTime, LocalTime closureTime) throws Exception {
        Optional<Day> dayOpt = getDayByDate(date);
        if(dayOpt.isEmpty()) throw  new Exception("Date out of calendar");
        openingDaysMap.get(dayOpt.get()).activateOpeningRange(openingTime,closureTime);
    }

    private Optional<OpeningRange<RangeTimeOfAllTables>> getOpeningRangeByDayTime(Day day, LocalTime time) {
        return openingDaysMap.get(day).getOpeningRangeByTime(time);
    }

    public Optional<Day> getDayByDate (LocalDate date){
        return this.activeDays.stream().filter(day -> day.getDate().equals(date)).findFirst();
    }

    public synchronized StatusBooking bookTable (List<Client> clientsList, LocalDate date, LocalTime time, long bookingRange) throws Exception {
        //a seconda del peoplenumber mi sposto nel rangeTimeSetCorrispondente

        //todo check sulla date
        Optional<Day> dayOpt = getDayByDate(date);
        Day day = dayOpt.get();

        int tableSize = clientsList.size();
        /*if(tableSize > ristorante.getTableSizes().last()){
            return new StatusBooking(false, InfoBookingEnum.PEOPLE_OUT_OF_MAX_TABLE_SIZE);
        }*/

        return openingDaysMap.get(day).bookTable(clientsList,date,time,bookingRange);
    }





    // ------------------- PRINT and TOSTRING METHODS ---------------------------

    public String getDetailsOfBookingsOfDate (LocalDate date) {
        String str = "";
        str += "\nBookings of " +  date + " : \n" ;

        Optional<Day> dayOpt = getDayByDate(date);
        if(dayOpt.isEmpty()) str += "The date isn't active\n";
        else str += openingDaysMap.get(dayOpt.get()).getDetailsOfBookings();
        return str;
    }

    public String getDetailsBetweenTwoDates (LocalDate startDate, LocalDate endDate) {
        Day startDay = getDayByDate(startDate).get();
        Day endDay = getDayByDate(endDate).get();
        NavigableSet<Day> subSet = activeDays.subSet(startDay,true,endDay,true);
        String str = "";
        for(Day day : subSet){
            str += "\nBookings of " +  day.getDate() + " : \n"
                + getDetailsOfBookingsOfDate(day.getDate())
                + "--------------------\n";
        }
        return str;
    }

    public String getDetailsFromDate (LocalDate startDate, int numberOfDays) {
        LocalDate endDate = startDate.plusDays(numberOfDays);
        return getDetailsBetweenTwoDates(startDate,endDate);
    }

    public String getDetailsOfAllDays () {
        return getDetailsBetweenTwoDates(activeDays.first().getDate(), activeDays.last().getDate());
    }

    public void printDetailsOfDate (LocalDate date) {
        System.out.println(getDetailsOfBookingsOfDate(date));
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
        Day day = getDayByDate(date).get();
        String str = "Opening ranges of " + date  + " :\n\n "
                    + openingDaysMap.get(day).getAllRangesFormatted();
        System.out.println(str);
    }


}
