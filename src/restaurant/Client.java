package restaurant;

import calendar.booking.Booking;

import utility.Comparators;

import java.util.TreeSet;

/**
 * Creazione classe Cliente
 */

public class Client {
    /**
     * Inserite quattro variabili di istanza private
     */
    private String nome;
    private String cognome;
    private int numeroDiCellulare;

    private TreeSet<Booking> myBookingsSet;

    /**
     * Inserito metodo costruttore Parametrizzato con :
     * @param nome
     * @param cognome
     * @param numeroDiCellulare
     */
    public Client(String nome, String cognome, int numeroDiCellulare) {
        this.nome = nome;
        this.cognome = cognome;
        this.numeroDiCellulare = numeroDiCellulare;
        this.myBookingsSet = new TreeSet<>(Comparators.getCompareBookingsByDateTime());
    }


    /**
     * Inserito metodi Get and Set per ogni variabile di istanza privata
     * (Al momento trascurate ma che serviranno per ulteriori implementazioni del codice)
     */
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public int getNumeroDiCellulare() {
        return numeroDiCellulare;
    }

    public void setNumeroDiCellulare(int numeroDiCellulare) {
        this.numeroDiCellulare = numeroDiCellulare;
    }


    /**
     * Inserito metodo info che ci aiuterà a distinguere i clienti in base alla categoria
     */
    public String datiCliente(){
        return "Cliente: " + this.nome + " - " + this.cognome;
    }

    /**
     * Inserito metodo prenota che permetterà alla classe Cliente di richiamare la classe Tavolo
     */
    //TODO dobbiamo memorizzare da qualche parte le prenotazione

    private String prenotazione;
    /*
    public StatusBookingEnum bookTable (CalendarBookings calendar, List<Cliente> clientsList, LocalDate date, LocalTime time, long rangeTime) {
        StatusBookingEnum statusBooking =  calendar.bookTable(clientsList,date,time,rangeTime);
        if(statusBooking == StatusBookingEnum.SUCCESS){
            myBookingsSet.add(statusBooking.getSuccesfulBooking());
        }
        else{
            // get info e agire di conseguenza
        }
        return statusBooking;
    }

    /**
     * Inserito metodo prenota che permetterà alla classe Cliente di richiamare la classe Portata e tutte le sotto classi
     */
    /*public void ordina(Cliente cliente, Portata portata){
        System.out.println("Il "+ cliente.datiCliente()+ "\n"+"ha ordinato la "+ portata.printPortataDetails()+"\n");
    }*/
}