package calendar.newcalendar;

import java.time.LocalDate;
import java.util.Objects;

public class Day implements Comparable<Day>{

    private LocalDate date;

    public Day (LocalDate date){
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public int compareTo(Day day) {
        return this.date.compareTo(day.getDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return Objects.equals(date, day.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
