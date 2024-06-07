package catering.businesslogic.recipe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

public class Preparation extends AbstractRecipe {
    public Preparation(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return super.toString() + "{ PREPARATION }";
    }

    public static ArrayList<Preparation> loadAllPreparations() {
        ArrayList<Preparation> result = new ArrayList<>();
        String query = "SELECT * FROM Preparations";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Preparation pr = new Preparation(rs.getString("name"));
                pr.id = rs.getInt("id");
                result.add(pr);
            }
        });
        return result;
    }
}
