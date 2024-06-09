package catering.businesslogic.kitchen;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import catering.businesslogic.recipe.AbstractRecipe;
import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;

public class Availability {

    private AbstractRecipe tbp;
    private String quantity;

    public Availability() {

    }

    public Availability(AbstractRecipe tbp, String quantity) {
        this.tbp = tbp;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return tbp + ": " + quantity;
    }

    public static void save(ServiceResume resume, Availability done) {
        String stm = "INSERT INTO availabilities (resume_id, recipe_id, quantity) VALUES (?,?,?)";
        PersistenceManager.executeBatchUpdate(stm, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, resume.getId());
                ps.setInt(2, done.tbp.getId());
                ps.setString(3, done.quantity);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
            }
        });
    }

    public static void delete(Availability done, ServiceResume resume) {
        String stm = "DELETE FROM availabilities WHERE resume_id = ? AND recipe_id = ?";
        PersistenceManager.executeBatchUpdate(stm, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, resume.getId());
                ps.setInt(2, done.tbp.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
            }
        });
    }

}
