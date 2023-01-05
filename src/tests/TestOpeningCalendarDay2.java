package tests;

import calendar.newcalendar.OpeningCalendarAllDays;
import restaurant.Client;
import restaurant.Restaurant;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestOpeningCalendarDay2 {

    public static void main(String[] args) throws Exception {

        OpeningCalendarAllDays calendar = OpeningCalendarAllDays.getInstance();
        Restaurant restaurant = Restaurant.getInstance();

        calendar.activateIntervalFromDate(LocalDate.now(),2);

        calendar.activateOpeningRange(getDateFromNow(1), LocalTime.of(11,00),LocalTime.of(14,30));

        calendar.bookTable(createClientsList(4),getDateFromNow(1),LocalTime.of(12,00),70);

        calendar.bookTable(createClientsList(4),getDateFromNow(1),LocalTime.of(13,05),80);
        calendar.bookTable(createClientsList(4),getDateFromNow(1),LocalTime.of(13,20),65);


        calendar.printDetailsOfAllDays();
        calendar.printRangesOfDate(getDateFromNow(1));
      //  calendar.printAllRanges();
        restaurant.getTables().get(2).printRanges();
    }

    private static String [] names = {"Liam" ,	"Olivia" , "Noah" ,	"Emma", "Oliver",	"Charlotte","Elijah"	,"Amelia"
            , "James",	"Ava","William",	"Sophia" ,"Benjamin",	"Isabella", "Lucas",	"Mia",
            "Henry",	"Evelyn" ,"Theodore",	"Harper" } ;



    private static LocalDate getDateFromNow (int numDays) {
        return LocalDate.now().plusDays(numDays);
    }


    private static List<Client> createClientsList (int number) {
        Random random = new Random();
        List<Client> clientsList = new ArrayList<>();
        for(int i=0; i<number; i++){
            String randomName = names[random.nextInt(names.length)];
            String randomSurname = names[random.nextInt(names.length)];
            int randomPhoneNumber = random.nextInt(9999);
            clientsList.add(new Client(randomName,randomSurname,randomPhoneNumber));
        }
        return clientsList;
    }



}
