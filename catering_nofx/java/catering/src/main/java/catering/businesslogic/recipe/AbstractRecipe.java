package catering.businesslogic.recipe;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import catering.businesslogic.kitchen.ServiceResume;
import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;

public abstract class AbstractRecipe {
    protected int id;
    protected String name;

    private List<Preparation> requiredPreparations = new ArrayList<>();

    public List<Preparation> getRequiredPreparations() {
        return requiredPreparations;
    }

    public AbstractRecipe(String name) {
        id = 0;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static void saveToBePrepared(ServiceResume resume, AbstractRecipe tbp) {
        String stm = "INSERT INTO tobeprepared (recipe_id, resume_id, position) VALUES (?, ?, ?)";
        int[] result = PersistenceManager.executeBatchUpdate(stm, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, tbp.getId());
                ps.setInt(2, resume.getId());
                ps.setInt(3, resume.getToBePrepared().indexOf(tbp));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });
    }

    public static void deleteToBePrepared(ServiceResume resume, AbstractRecipe tbp) {
        String stm = "DELETE FROM tobeprepared WHERE recipe_id = ? AND resume_id = ?";
        int[] result = PersistenceManager.executeBatchUpdate(stm, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, tbp.getId());
                ps.setInt(2, resume.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });
    }

    public static void updatePosition(ServiceResume resume) {
        String update = "UPDATE tobeprepared SET position = ? WHERE recipe_id = ? AND resume_id = ?";
        List<AbstractRecipe> tbps = resume.getToBePrepared();
        PersistenceManager.executeBatchUpdate(update, tbps.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                AbstractRecipe ar = tbps.get(batchCount);
                ps.setInt(1, tbps.indexOf(ar));
                ps.setInt(2, ar.id);
                ps.setInt(3, resume.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass())
            return false;

        AbstractRecipe that = (AbstractRecipe) o;
        return Objects.equals(id, that.id);
    }
}
