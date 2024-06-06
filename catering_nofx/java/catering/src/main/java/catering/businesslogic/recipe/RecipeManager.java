package catering.businesslogic.recipe;

import java.util.ArrayList;

public class RecipeManager {
    private static RecipeManager instance;
    private ArrayList<Recipe> recipes;
    private ArrayList<Preparation> preparations;

    public RecipeManager() {
        Recipe.loadAllRecipes();
    }

    public ArrayList<Recipe> getRecipes() {
        return Recipe.getAllRecipes();
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

}
