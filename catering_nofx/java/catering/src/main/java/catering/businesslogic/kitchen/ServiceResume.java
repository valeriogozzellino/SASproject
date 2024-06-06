package catering.businesslogic.kitchen;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import catering.businesslogic.event.EventManager;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.menu.Menu;
import catering.businesslogic.menu.MenuItem;
import catering.businesslogic.menu.Section;
import catering.businesslogic.recipe.AbstractRecipe;
import catering.businesslogic.recipe.RecipeManager;
import catering.businesslogic.shift.Shift;
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

    /**
     * 
     * @param service
     * @param id
     */
    private ServiceResume(ServiceInfo service, int id) {
        if (service == null)
            throw new NullPointerException();

        referredService = service;
        this.id = id;
        toBePrepared.addAll(loadToBePreparedFromDB());
        // tasks.addAll(Task.loadForResume(this));
        // availabilities.addAll(Availability.loadForResume(this));

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

    /**
     * 
     * @return
     */
    public ServiceInfo getReferredService() {
        return referredService;
    }

    /**
     * 
     * @param tbp
     */
    public void addToBePrepared(AbstractRecipe tbp) {
        toBePrepared.add(tbp);
    }

    /**
     * 
     * @param tbp
     * @param position
     */
    public void sortToBePrepared(AbstractRecipe tbp, int position) {
        toBePrepared.remove(tbp);
        toBePrepared.add(position, tbp);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * 
     * @return size of toBePrepared list
     */
    public int toBePreparedSize() {
        return toBePrepared.size();
    }

    /**
     * 
     * @param recipe
     * @return
     */
    public boolean isRequired(AbstractRecipe recipe) {
        return toBePrepared.contains(recipe);
    }

    public AbstractRecipe removeToBePrepared(AbstractRecipe tbp) {
        for (AbstractRecipe x : toBePrepared) {
            if (tbp.equals(x)) {
                toBePrepared.remove(x);
            }
            return x;
        }
        return null;
    }

    /**
     * 
     * @return
     */
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

    /**
     * 
     * @return
     */
    public static List<ServiceResume> loadAllServiceResumes() {
        String stm = "SELECT * FROM serviceresumes";
        List<ServiceResume> resumes = new ArrayList<>();
        PersistenceManager.executeQuery(stm, rs -> {
            int id = rs.getInt("id");
            int serviceId = rs.getInt("service_id");
            ServiceInfo service = EventManager.getInstance().getService(serviceId);
            if (service != null)
                resumes.add(new ServiceResume(service, id));
        });

        return resumes;
    }

    /**
     * remote all tasks from list in service resume
     * 
     * @return
     */
    public List<Task> deleteAllTask() {
        List<Task> deletedTask = tasks;
        tasks.clear();

        return deletedTask;
    }

    /**
     * 
     * @return
     */
    public List<AbstractRecipe> deleteAllToBePrepared() {
        List<AbstractRecipe> deletedTBP = new ArrayList<>();

        for (AbstractRecipe ar : toBePrepared)
            deletedTBP.add(ar);
        toBePrepared.clear();

        return deletedTBP;
    }

    /**
     * 
     * @return
     */
    public List<AbstractRecipe> getToBePrepared() {
        return toBePrepared;
    }

    /**
     * 
     * @return
     */
    public Task getCurrentTask() {
        return currentTask;
    }

    /**
     * 
     * @param
     */
    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }

    /**
     * 
     * @param task
     * @return
     */
    public boolean isSummarized(Task task) {
        return tasks.contains(task);
    }

    /**
     * 
     * @param shift
     * @return
     */
    public boolean validTurn(Shift shift) {
        if (shift == null)
            throw new NullPointerException();
        return shift.getStart().isAfter(LocalDateTime.now());
    }

    /**
     * 
     * @return
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * 
     * @param task
     * @return
     */
    public Task whichAmIPrecedent(Task task) {
        for (Task t : tasks)
            if (t.getPreviousStep() != null && t.getPreviousStep() == task)
                return t;
        return null;
    }

    /**
     * 
     * @param task
     */
    public void removeTask(Task task) {
        if (task == null)
            throw new NullPointerException();
        tasks.remove(task);
        Task t = whichAmIPrecedent(task);
        if (t != null)
            t.setPreviousStep(null);
    }

    /**
     * 
     * @param done
     */
    public void addAvailability(Availability done) {
        availabilities.add(done);
    }

    /**
     * 
     * @param done
     */
    public void deleteAvailability(Availability done) {
        availabilities.remove(done);
    }

    /**
     * 
     * @return
     */
    public List<Availability> getAvailabilities() {
        return availabilities;
    }
}
