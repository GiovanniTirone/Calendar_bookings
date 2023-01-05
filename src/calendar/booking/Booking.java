package calendar.booking;

import calendar.table.Table;
import restaurant.Client;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Booking {

    private List<Client> clientsList;

    private LocalDateTime dateTime;

    private Table table;

    private long rangeTime;

    private int id;

    private static int idsCounter = 0;

    public Booking (List<Client> clientsList, LocalDateTime dateTime, long rangeTime, Table table){
        this.clientsList = clientsList;
        this.dateTime = dateTime;
        this.table = table;
        this.rangeTime = rangeTime;
        this.id = ++idsCounter;
    }



    //costruttore per i test
    public Booking (LocalTime time, long rangeTime){
        this.dateTime = LocalDateTime.of(LocalDate.now(),time);
        this.rangeTime = rangeTime;
    }

    public int getId() {
        return id;
    }

    public List<Client> getClientsList() {
        return clientsList;
    }

    public void setClientsList(List<Client> clientsList) {
        this.clientsList = clientsList;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public LocalTime getTime () {
        return this.dateTime.toLocalTime();
    }

    public LocalDate getDate () {
        return this.dateTime.toLocalDate();
    }

    public long getRangeTime() {
        return rangeTime;
    }

    public void setRangeTime(long rangeTime) {
        this.rangeTime = rangeTime;
    }

    public LocalTime getEndTime () {
        return this.dateTime.toLocalTime().plusMinutes(this.rangeTime);
    }


    public String getDetails () {
        return  "Booking details: " +
                "\n DateTime: " + dateTime +
                "\n Range: " +rangeTime +
                "\n People number: " + clientsList.size() +
                "\n Table: " + table.getNumeroTavolo() ;
    }



}
