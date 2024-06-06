package catering.businesslogic.kitchen;

import java.util.ArrayList;
import java.util.List;

import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.menu.Menu;
import catering.businesslogic.menu.MenuItem;
import catering.businesslogic.menu.Section;
import catering.businesslogic.recipe.AbstractRecipe;
import catering.businesslogic.recipe.RecipeManager;
import catering.persistence.PersistenceManager;

public class ServiceResume {
    private ServiceInfo referredService;
    private List<AbstractRecipe> toBePrepared = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();
    private List<Availability> availabilities = new ArrayList<>();
    private int id;
    private Task currentTask;

    public ServiceResume(ServiceInfo service) {
        if (service == null)
            throw new NullPointerException();

        referredService = service;
        // toBePrepared.addAll(loadToBePrepared());
        loadToBePrepared();
    }

    private ServiceResume(ServiceInfo service, int id) {
        if (service == null)
            throw new NullPointerException();

        referredService = service;
        this.id = id;
        toBePrepared.addAll(loadToBePreparedFromDB());
        tasks.addAll(Task.loadForResume(this));
        availabilities.addAll(Availability.loadForResume(this));

    }

    /**
     * 
     * @return
     */
    List<AbstractRecipe> loadToBePrepared() {
        List<AbstractRecipe> toBePrepared = new ArrayList<>();

        Menu menu = referredService.getMenu();
        List<Section> sections = menu.getSections();
        List<MenuItem> freeItems = menu.getFreeItems();

        for (Section s : sections) {
            List<MenuItem> sectionItems = s.getSectionItems();
            for (MenuItem mi : sectionItems)
                toBePrepared.add(mi.getItemRecipe());

            // toBePrepared.add(mi.getItemRecipe());
        }

        for (MenuItem mi : freeItems)
            toBePrepared.add(mi.getItemRecipe());

        this.toBePrepared.addAll(toBePrepared);
        return toBePrepared;
    }

    public ServiceInfo getReferredService() {
        return referredService;
    }

    private List<AbstractRecipe> loadToBePreparedFromDB() {
        RecipeManager recipeManager = RecipeManager.getInstance();
        String stm = "SELECT * FROM tobeprepared WHERE resume_id =" + id + " ORDER BY position";
        List<AbstractRecipe> recipes = new ArrayList<>();

        PersistenceManager.executeQuery(stm, rs -> {
            int recipeId = rs.getInt("recipe_id");
            recipes.add(recipeManager.getRecipe(recipeId));
        });

        return recipes;
    }

}
