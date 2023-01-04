package calendar.table;

import calendar.booking.Booking;
import calendar.booking.InfoBookingEnum;
import calendar.booking.StatusBooking;
import calendar.range.OpeningRange;
import restaurant.Client;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
     * @param isFree
     */
    public Table(int numeroPostiTavolo, int numeroTavolo, boolean isFree){
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
        if(targetRange.getStatusTable() == StatusTableEnum.BOOKED) return new StatusBooking(false,InfoBookingEnum.IMPOSSIBLE);
        if(clientsList.size()>numeroPostiTavolo) return new StatusBooking(false,InfoBookingEnum.PEOPLE_OUT_OF_MAX_TABLE_SIZE);

        Optional<StatusBooking> statusBookingOpt;

        if(targetRange.getStatusTable() == StatusTableEnum.TO_CHECK) {
          statusBookingOpt = checkCompatibilityWithNextBookings(targetRange,time,bookingRange);
          if(statusBookingOpt.isPresent())
              return statusBookingOpt.get();
        }

        Booking newBooking = new Booking(clientsList, LocalDateTime.of(date,time),bookingRange,this);
        RangeTimeOfTable newRange = new RangeTimeOfTable(newBooking);
        openingRange.addRangeTime(newRange);
        setPreviusRanges_TO_CHECK(newRange);
        // todo passare TUTTO l'opening range
        return new StatusBooking(true,InfoBookingEnum.NO_INFO,newBooking,newRange);
    }

    private Optional<RangeTimeOfTable> getNextRangeTimeWithBooking ( )

    private Optional<StatusBooking> checkCompatibilityWithNextBookings (RangeTimeOfTable targetRange,LocalTime time,long bookingRange) {
        Optional<RangeTimeOfTable> nextRange = Optional.ofNullable(openingRange.higher(targetRange));
        while (nextRange.isPresent()) {
            if(nextRange.get().getStartBookingTime().isPresent()) {
                LocalTime nextBookingTime = nextRange.get().getStartBookingTime().get();
                if (time.plusMinutes(bookingRange).isAfter(nextBookingTime)) {
                    return Optional.of(new StatusBooking(false, InfoBookingEnum.PROPOSE_NEW_TIME));
                    // todo add time to propose
                }
            }
            nextRange = Optional.ofNullable(openingRange.higher(nextRange.get()));
        }

        if (time.plusMinutes(bookingRange).isAfter(openingRange.getClosureTime())) {
            return Optional.of(new StatusBooking(false, InfoBookingEnum.TIME_OUT_OF_OPENING_RANGE));
        }
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
