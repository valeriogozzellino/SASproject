package catering.businesslogic.recipe;

import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Recipe extends AbstractRecipe {

    public Recipe(String name) {
        super(name);
    }

    public Recipe(String name, int id) {
        super(name);
        this.id = id;
    }

    public static ArrayList<Recipe> loadAllRecipes() {
        ArrayList<Recipe> result = new ArrayList<>();
        String query = "SELECT * FROM recipes";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Recipe rec = new Recipe(rs.getString("name"));
                rec.id = rs.getInt("id");
                result.add(rec);
            }
        });
        return result;
    }

}
