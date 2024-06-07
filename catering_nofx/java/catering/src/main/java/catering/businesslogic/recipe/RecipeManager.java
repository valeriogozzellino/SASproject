package catering.businesslogic.recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager {
    private static RecipeManager instance;
    private ArrayList<Recipe> recipes;
    private ArrayList<Preparation> preparations;

    public RecipeManager() {
        Recipe.loadAllRecipes();
    }

    public List<Recipe> getRecipes() {
        return Recipe.loadAllRecipes();
    }

    public static RecipeManager getInstance() {
        if (instance == null)
            instance = new RecipeManager();
        return instance;
    }

    public AbstractRecipe getRecipe(int id) {
        for (AbstractRecipe ar : recipes)
            if (ar.getId() == id)
                return ar;
        return null;
    }

    public List<AbstractRecipe> getRecipesAndPreparations() {
        List<AbstractRecipe> abs = new ArrayList<>();
        abs.addAll(recipes);
        abs.addAll(preparations);

        return abs;
    }

}
