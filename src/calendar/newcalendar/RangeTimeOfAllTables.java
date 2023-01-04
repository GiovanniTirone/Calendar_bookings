package calendar.newcalendar;


import calendar.booking.InfoBookingEnum;
import calendar.booking.StatusBooking;
import calendar.range.RangeTime;
import calendar.table.RangeTimeOfTable;
import calendar.table.Table;

import java.time.LocalDate;
import java.time.LocalTime;

import restaurant.Client;
import utility.Comparators;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class RangeTimeOfAllTables extends RangeTime<TreeMap<Table, RangeTimeOfTable>> {

    protected TreeMap<Table, RangeTimeOfTable> tablesOriginalRangesMap;


    public RangeTimeOfAllTables(LocalTime startTime, LocalTime endTime) {
        super(startTime, endTime);
        this.tablesOriginalRangesMap = new TreeMap<>(Comparators.getCompareTablesBySeating());
    }

    @Override
    public void overlapDatas(TreeMap<Table, RangeTimeOfTable> newOriginalRangesMap) {
        for(Table table : newOriginalRangesMap.keySet()){
            tablesOriginalRangesMap.put(table,newOriginalRangesMap.get(table));
        }
    }

    @Override
    public TreeMap<Table, RangeTimeOfTable> getDatas() {
        return tablesOriginalRangesMap;
    }

    @Override
    public void setDatas(TreeMap<Table, RangeTimeOfTable> newMap) {
        this.tablesOriginalRangesMap = newMap;
    }

    @Override
    public TreeMap<Table, RangeTimeOfTable> copyDatas(TreeMap<Table, RangeTimeOfTable> dataToCopy) {
        TreeMap<Table, RangeTimeOfTable> copy = new TreeMap<>(Comparators.getCompareTablesBySeating());
        dataToCopy.keySet().forEach(table -> copy.put(table,dataToCopy.get(table)));
        return copy;
    }


    protected StatusBooking bookTable (List<Client> clientsList, LocalDate date, LocalTime time, long bookingRange) throws Exception {
        StatusBooking status = new StatusBooking(false, InfoBookingEnum.IMPOSSIBLE);
        for(Table table : tablesOriginalRangesMap.keySet()){
            if(table.getNumeroPostiTavolo() < clientsList.size()) continue;
            status = table.bookTable(clientsList,  date,  time,  bookingRange);
            if(status.isSuccess()) return status;
        }
        return status;
    }

    public TreeSet<RangeTimeOfAllTables> divide (LocalTime middleTime) throws Exception {
        return (TreeSet<RangeTimeOfAllTables>) super.divide(this.getClass(),middleTime);
    }

    @Override
    public String getDetailsString () {
        String str = getStartTime() + " - " +getEndTime();
        for(Table table: tablesOriginalRangesMap.keySet()){
            str += "\nTable " + table.getNumeroTavolo() + " : " + tablesOriginalRangesMap.get(table).getStatusTable();
        }
        return str;
    }


}
