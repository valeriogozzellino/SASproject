package catering.businesslogic.shift;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.temporal.ChronoUnit;

import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;

public class Shift {
    private LocalDateTime start;
    private LocalDateTime end;
    public boolean complete;
    private int id;

    private Shift() {
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

    public int getDuration() {
        // @TODO
        long ore = ChronoUnit.HOURS.between(start, end);
        long minuti = ChronoUnit.MINUTES.between(start, end);
        long secondi = ChronoUnit.SECONDS.between(start, end);

    }

    @Override
    public String toString() {
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd MMMM");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        return dayFormat.format(start.toLocalDate()) +
                "  " + timeFormat.format(start.toLocalTime()) +
                " - " + timeFormat.format(end.toLocalTime()) +
                ", completo: " + complete;
    }

    public static List<Shift> loadTurns() {
        String stm = "SELECT * FROM shifts"; //// testareeeee
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

    // public static void update(Shift shift, boolean complete) {
    // String stm = "UPDATE turns SET complete = ? WHERE id = ?";
    // PersistenceManager.executeUpdate(stm, 1, new BatchUpdateHandler() {
    // @Override
    // public void handleBatchItem(PreparedStatement ps, int batchCount) throws
    // SQLException {
    // ps.setBoolean(1, complete);
    // ps.setInt(2, shift.id);
    // }

    // @Override
    // public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

    // }
    // });
    // }

}
