package calendar.table;

import calendar.booking.Booking;
import calendar.booking.InfoBookingEnum;
import calendar.booking.StatusBooking;
import calendar.range.OpeningRange;
import restaurant.Client;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

/**
 * Creazione classe Tavolo
 */
public class Table {

    /**
     * Inserite tre variabili di istanza private
     */
    private int numeroPostiTavolo;
    private int numeroTavolo;

    private OpeningRange<RangeTimeOfTable> openingRange;


    /**
     * Inserito metodo costruttore Parametrizzato con :
     * @param numeroPostiTavolo
     * @param numeroTavolo
     *
     */
    public Table(int numeroPostiTavolo, int numeroTavolo){
        this.numeroPostiTavolo = numeroPostiTavolo;
        this.numeroTavolo = numeroTavolo;
    }

    /**
     * Inserito metodi Get and Set per ogni variabile di istanza privata
     * (Al momento trascurate ma che serviranno per ulteriori implementazioni del codice)
     */
    public int getNumeroPostiTavolo() {
        return numeroPostiTavolo;
    }

    public void setNumeroPostiTavolo(int numeroPostoTavolo) {
        this.numeroPostiTavolo = numeroPostoTavolo;
    }

    public int getNumeroTavolo() {
        return numeroTavolo;
    }

    public void setNumeroTavolo(int numeroTavolo) {
        this.numeroTavolo = numeroTavolo;
    }

    public OpeningRange<RangeTimeOfTable> getOpeningRange() {
        return openingRange;
    }

    public OpeningRange<RangeTimeOfTable> setAndGetOpeningRange(OpeningRange<RangeTimeOfTable> openingRange) {
        this.openingRange = openingRange;
        return openingRange;
    }


    public StatusBooking bookTable (List<Client> clientsList, LocalDate date, LocalTime time, long bookingRange) throws Exception {
        Optional <RangeTimeOfTable> targetRangeOpt = openingRange.getRangeByTime(time);
        if(targetRangeOpt.isEmpty()) return new StatusBooking(false, InfoBookingEnum.TIME_OUT_OF_OPENING_RANGE);
        RangeTimeOfTable targetRange = targetRangeOpt.get();
     //   if(targetRange.getStatusTable() == StatusTableEnum.BOOKED) return new StatusBooking(false,InfoBookingEnum.IMPOSSIBLE);
        if(clientsList.size()>numeroPostiTavolo) return new StatusBooking(false,InfoBookingEnum.PEOPLE_OUT_OF_MAX_TABLE_SIZE);

        Optional<StatusBooking> statusBookingOpt;

        switch(targetRange.getStatusTable()){

            case TO_CHECK, BOOKED_NEXT -> {
                statusBookingOpt = checkCompatibilityWithNextBookings(targetRange,time,bookingRange);
                if(statusBookingOpt.isPresent()) return statusBookingOpt.get();
            }

            case BOOKED -> {

                statusBookingOpt = checkCompatibilityWithNextBookings(openingRange.higher(targetRange),time,bookingRange);
                if(statusBookingOpt.isPresent()) return statusBookingOpt.get();
                return new StatusBooking(false,InfoBookingEnum.IMPOSSIBLE);

                /*
                LocalTime nextBookingStartTime = getHigerRangeTimeWithBooking(targetRange).get().getStartBookingTime().get();
                LocalTime targetBookingEndTime = targetRange.getStartBookingTime().get();

                long shiftTime = ChronoUnit.MINUTES.between(time,targetBookingEndTime);

                if(time.plusMinutes(shiftTime).isBefore(nextBookingStartTime))
                    return new StatusBooking(false,InfoBookingEnum.PROPOSE_NEW_TIME,targetBookingEndTime) ;
                else return new StatusBooking(false,InfoBookingEnum.IMPOSSIBLE);
                */
            }

        }


        Booking newBooking = new Booking(clientsList, LocalDateTime.of(date,time),bookingRange,this);
        RangeTimeOfTable newRange_BOOKED = new RangeTimeOfTable(newBooking);
        RangeTimeOfTable newRange_BOOKED_NEXT =
                new RangeTimeOfTable(newBooking.getTime().minusMinutes(60),newBooking.getTime(),newRange_BOOKED.getInfoTable().clone());
        newRange_BOOKED_NEXT.setStatusTable(StatusTableEnum.BOOKED_NEXT);
        openingRange.addRangeTime(newRange_BOOKED_NEXT);
        openingRange.addRangeTime(newRange_BOOKED);
        setPreviusRanges_TO_CHECK(newRange_BOOKED_NEXT);
        return new StatusBooking(true,InfoBookingEnum.NO_INFO,newBooking);
    }

    private Optional<RangeTimeOfTable> getCeilingRangeTimeWithBooking(RangeTimeOfTable startRange){
        if(startRange.getBooking().isPresent()) return Optional.of(startRange);
        return getHigerRangeTimeWithBooking(startRange);
    }

    private Optional<RangeTimeOfTable> getHigerRangeTimeWithBooking(RangeTimeOfTable startRange){
        Optional<RangeTimeOfTable> nextRange = Optional.ofNullable(openingRange.higher(startRange));
        while (nextRange.isPresent()) {
            if (nextRange.get().getStartBookingTime().isPresent()) return nextRange;
            nextRange = Optional.ofNullable(openingRange.higher(nextRange.get()));
        }
        return Optional.empty();
    }

    private Optional<RangeTimeOfTable> getLowerRangeTimeWithBooking(RangeTimeOfTable startRange){
        Optional<RangeTimeOfTable> previusRange = Optional.ofNullable(openingRange.lower(startRange));
        while (previusRange.isPresent()) {
            if (previusRange.get().getStartBookingTime().isPresent()) return previusRange;
            previusRange = Optional.ofNullable(openingRange.lower(previusRange.get()));
        }
        return Optional.empty();
    }

    private Optional<StatusBooking> checkCompatibilityWithNextBookings (RangeTimeOfTable targetRange,LocalTime time,long bookingRange) {
        Optional<RangeTimeOfTable> nextRangeWithBookings = getCeilingRangeTimeWithBooking(targetRange);
        LocalTime newBookingEndTime = time.plusMinutes(bookingRange);
        if(nextRangeWithBookings.isPresent()){
            LocalTime nextBookingStartTime = nextRangeWithBookings.get().getStartBookingTime().get();
            if(newBookingEndTime.isBefore(nextBookingStartTime) || newBookingEndTime.equals(nextBookingStartTime)) return Optional.empty();
            LocalTime timeToPropose = time.minusMinutes(ChronoUnit.MINUTES.between(nextBookingStartTime,newBookingEndTime));
            Optional<RangeTimeOfTable> previusRangeWithBookings = getLowerRangeTimeWithBooking(targetRange);
            if(previusRangeWithBookings.isPresent()){
                LocalTime prevBookingEndTime = previusRangeWithBookings.get().getBooking().get().getEndTime();
                if(timeToPropose.isBefore(prevBookingEndTime))
                    return Optional.of(new StatusBooking(false,InfoBookingEnum.IMPOSSIBLE));
                else
                    return Optional.of(new StatusBooking(false,InfoBookingEnum.PROPOSE_NEW_TIME,timeToPropose));
            }
            else{
                if(timeToPropose.isBefore(openingRange.getOpeningTime()))
                    return Optional.of(new StatusBooking(false, InfoBookingEnum.TIME_OUT_OF_OPENING_RANGE));
                else
                    return Optional.of(new StatusBooking(false,InfoBookingEnum.PROPOSE_NEW_TIME,timeToPropose));
            }
        }

        if (newBookingEndTime.isAfter(openingRange.getClosureTime()))
            return Optional.of(new StatusBooking(false, InfoBookingEnum.TIME_OUT_OF_OPENING_RANGE));

        return Optional.empty();
    }

    private void setPreviusRanges_TO_CHECK (RangeTimeOfTable newRange) throws Exception {
        Optional<RangeTimeOfTable> previusRangeOpt = Optional.ofNullable(openingRange.getSubRangesSet().lower(newRange));
        LocalTime timetoCheck = newRange.getStartTime().minusMinutes(60); // todo mettere max time of booking
        while(previusRangeOpt.isPresent()){
            RangeTimeOfTable previusRange = previusRangeOpt.get();
            if(previusRange.getStartTime().isAfter(timetoCheck)){
                if(previusRange.getStatusTable() == StatusTableEnum.FREE){
                    previusRange.setStatusTable(StatusTableEnum.TO_CHECK);
                   // continue;
                }
                previusRangeOpt = Optional.ofNullable(openingRange.getSubRangesSet().lower(previusRange));
            }
            else if(previusRange.getStartTime().equals(timetoCheck)){
                if(previusRange.getStatusTable() == StatusTableEnum.FREE){
                    previusRange.setStatusTable(StatusTableEnum.TO_CHECK);
                    break;
                }
            }
            else {
                if(previusRange.getStatusTable() == StatusTableEnum.FREE) {
                    TreeSet<RangeTimeOfTable> dividedRanges = openingRange.divideRangeAndSubstitute(previusRange, timetoCheck);
                    dividedRanges.last().setStatusTable(StatusTableEnum.TO_CHECK);
                /*
                TreeSet<RangeTimeOfTable> dividedRanges = previusRange.divide(timetoCheck);
                dividedRanges.last().setStatusTable(StatusTableEnum.TO_CHECK);
                */
                }
                break;
            }
        }
    }



    /**
     * Inserimento metodo infoTavolo() che ci aiuterà a vedere il numero del tavolo la capienza e la disponibilità
     * @return
     */
    public void printDetails() {
        System.out.println("Numero tavolo: "+ numeroTavolo + " - " +  "Numero posti tavolo: " + numeroPostiTavolo + " - " + "Dispinibilita del tavolo: " );
    }

    public void printRanges () {
        openingRange.printRanges();
    }

}
