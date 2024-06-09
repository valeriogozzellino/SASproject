package catering.businesslogic.shift;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import catering.businesslogic.kitchen.Task;
import catering.persistence.PersistenceManager;

public class Shift {

    private LocalDateTime start;
    private LocalDateTime end;
    public boolean complete;
    private List<Task> tasks = new ArrayList<>();
    private int id;

    public Shift() {
    }

    public Shift(LocalDateTime start, LocalDateTime end, boolean complete, int id) {
        this.start = start;
        this.end = end;
        this.complete = complete;
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return la durata del turno in minuti
     */
    public int getDuration() {
        long durationInMinutes = ChronoUnit.MINUTES.between(start, end);
        return (int) durationInMinutes;
    }

    public boolean checkOverTimeTask(int time) {
        int totalTime = 0;
        for (Task t : tasks) {
            totalTime += t.getTime();
        }
        totalTime = time;
        if (getDuration() >= totalTime) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd MMMM");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        return dayFormat.format(start.toLocalDate())
                + "  " + timeFormat.format(start.toLocalTime())
                + " - " + timeFormat.format(end.toLocalTime())
                + ", completo: " + complete;
    }

    public static List<Shift> loadShifts() {
        String stm = "SELECT * FROM shifts"; // TODO: test sulla tabella
        List<Shift> result = new ArrayList<>();
        PersistenceManager.executeQuery(stm, rs -> {
            Shift t = new Shift();
            t.start = rs.getTimestamp("start").toLocalDateTime();
            t.end = rs.getTimestamp("end").toLocalDateTime();
            t.id = rs.getInt("id");
            t.complete = rs.getBoolean("complete");
            result.add(t);
        });

        return result;
    }
}
