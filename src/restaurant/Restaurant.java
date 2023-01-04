
package restaurant;

import calendar.table.Table;
import utility.Comparators;


import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Creazione classe Ristorante
 */
public class Restaurant {

    /**
     * Inserite quattro variabili di istanza private
     */
    private String name;
    private String address;
    private String phoneNumber;
    private String email;

    //al posto di integer potremmo mettere una lista di clienti, con tavolo per chiave ("questo tavolo è occupato da questo gruppo di clienti")
    private HashMap<Integer, Table> tavoli = new HashMap<>();

    private static Restaurant restaurant = new Restaurant("name","address","545454","mail");

    /**
     * Inserito metodo costruttore Parametrizzato con :
     * @param name
     * @param address
     * @param phoneNumber
     * @param email
     */
    public Restaurant(String name, String address, String phoneNumber, String email){
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.tavoli = new HashMap<>() {{
            put(1, new Table(5, 1, true));
            put(2, new Table(4, 2, true));
            put(3, new Table(2, 3, true));
            put(4, new Table(3, 4, true));
            put(5, new Table(3, 5, true));
            put(6, new Table(7, 6, true));
        }};
    }

    public static Restaurant getInstance() {
        return restaurant;
    }
    /**
     * Inserito metodi Get and Set per ogni variabile di istanza privata
     * (Al momento trascurate ma che serviranno per ulteriori implementazioni del codice)
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<Integer, Table> getTavoli() {
        return tavoli;
    }

    public void setTavoli(HashMap<Integer, Table> tavoli) {
        this.tavoli = tavoli;
    }

    /**
     * Inserito metodo info che ci aiuterà a vedere le varie info del ristorante
     */
    public void printRistoranteInfo(){
        System.out.println("Dati ristorante: " + name + " - " + address + " - " + phoneNumber + " - " + email);
    }

    //todo quando aggiungo / rimuovo un tavolo devo modificare anche i possibleFreetables nei rangetimes
    public void aggiungiTavolo(Integer numero, Table table){
        tavoli.put(numero, table);
    }

    public void rimuoviTavolo(Integer numero, Table table) {
        tavoli.put(numero, table);
    }

    public Table getTavoloByNumber(int numero){
        return tavoli.get(numero);
    }

    public TreeSet<Integer> getTableSizes () {
        return tavoli.values().stream().map(Table::getNumeroPostiTavolo).collect(Collectors.toCollection(TreeSet::new));
    }

    public TreeSet<Table> getFreeTableFromTakenTables (Set<Table>takenTables, int peopleNumber){
        Set<Table> freeTables = tavoli.values().stream()
                .filter(tavolo -> !takenTables.contains(tavolo)).collect(Collectors.toSet());
        return getPossibleTablesFromFreeTables(freeTables,peopleNumber);
    }

    private TreeSet<Table> getPossibleTablesFromFreeTables (Set<Table>freeTables, int peopleNumber){
        TreeSet<Table> possibleTables = new TreeSet<>(Comparators.getCompareTablesBySeating());
        Set<Table> targetTables = freeTables.stream().filter(tavolo -> tavolo.getNumeroPostiTavolo()>=peopleNumber).collect(Collectors.toSet());
        possibleTables.addAll(targetTables);
        return possibleTables;
    }



}




