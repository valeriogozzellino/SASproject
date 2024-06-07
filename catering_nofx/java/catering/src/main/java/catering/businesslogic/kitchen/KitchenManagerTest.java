package catering.businesslogic.kitchen;

import java.util.List;
import java.util.stream.Collectors;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventManager;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.recipe.AbstractRecipe;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.recipe.RecipeManager;
import catering.businesslogic.shift.Shift;
import catering.businesslogic.shift.ShiftManager;
import catering.businesslogic.user.UserManager;

public class KitchenManagerTest {
    static UserManager um;
    static KitchenManager km;
    static RecipeManager rm;
    static EventManager em;
    static ShiftManager sm;
    static ServiceInfo testService;
    static ServiceResume testResume;

    public static void init() {
        CatERing cer = CatERing.getInstanceNotPersistent();
        um = UserManager.getInstance();
        km = KitchenManager.getInstance();
        rm = RecipeManager.getInstance();
        em = EventManager.getInstance();
        sm = ShiftManager.getInstance();
        testService = em.getService(2);
        um.fakeLogin("Tony");
    }

    public void after() throws UseCaseLogicException {
        if (testResume != null)
            km.getResumes().remove(testResume);
    }

    public void generateResume() throws UseCaseLogicException {
        testResume = km.generateResume(testService);
        try {
            km.generateResume(testService);
            System.out.println("Error: Should not generate resume twice.");
        } catch (UseCaseLogicException e) {
            System.out.println("Passed: UseCaseLogicException thrown as expected.");
        }
    }

    public void openResume() throws UseCaseLogicException {
        testResume = km.generateResume(testService);
        km.openResume(testResume);
        System.out.println("Resume opened.");
    }

    public void resetResume() throws UseCaseLogicException {
        testResume = km.generateResume(testService);
        List<AbstractRecipe> notAlreadyRequired = getSuitableRecipes(testResume);
        int tbp_before = testResume.getToBePrepared().size();
        int tasks_before = testResume.getTasks().size();
        testResume.addTask(new Task(testResume.getToBePrepared().get(0), null, null, 0, null));
        if (!notAlreadyRequired.isEmpty())
            testResume.addToBePrepared(notAlreadyRequired.get(0));
        km.resetResume(testResume);
        if (tbp_before != testResume.getToBePrepared().size() || tasks_before != 0) {
            System.out.println("Error: Resume reset failed.");
        } else {
            System.out.println("Reset completed successfully.");
        }
    }

    public void addToBePrepared() throws UseCaseLogicException {
        testResume = km.generateResume(testService);
        int tbpBefore = testResume.getToBePrepared().size();
        testResume.addToBePrepared(getSuitableRecipes(testResume).get(0));
        if (tbpBefore + 1 != testResume.getToBePrepared().size()) {
            System.out.println("Error: Adding to 'To Be Prepared' failed.");
        } else {
            System.out.println("Addition successful.");
        }
    }

    public void removeToBePrepared() throws UseCaseLogicException {
        testResume = km.generateResume(testService);
        int tbpBefore = testResume.getToBePrepared().size();
        testResume.removeToBePrepared(testResume.getToBePrepared().get(0));
        if (tbpBefore - 1 != testResume.getToBePrepared().size()) {
            System.out.println("Error: Removing from 'To Be Prepared' failed.");
        } else {
            System.out.println("Removal successful.");
        }
    }

    public void moveToBePrepared() throws UseCaseLogicException {
        testResume = km.generateResume(testService);
        AbstractRecipe ar = testResume.getToBePrepared().get(0);
        km.sortToBePrepared(testResume, ar, 1);
        if (1 != testResume.getToBePrepared().indexOf(ar)) {
            System.out.println("Error: Move to index 1 failed.");
        } else {
            System.out.println("Move to index 1 successful.");
        }
        km.sortToBePrepared(testResume, ar, 0);
        if (0 != testResume.getToBePrepared().indexOf(ar)) {
            System.out.println("Error: Move to index 0 failed.");
        } else {
            System.out.println("Move to index 0 successful.");
        }
    }

    public void showTurnBoard() throws UseCaseLogicException {
        List<Shift> shifts = km.showShiftsBoard();
        if (shifts == null) {
            System.out.println("Error: No shifts available.");
        } else {
            System.out.println("Shifts displayed successfully.");
        }
    }

    public void assignTask() throws UseCaseLogicException {
        testResume = km.generateResume(testService);
        km.assignTask(testResume, testResume.getToBePrepared().get(0), null, null, 0, null);
        System.out.println("Task assigned.");
    }

    public void modifyTask() throws UseCaseLogicException {
        testResume = km.generateResume(testService);
        km.assignTask(testResume, testResume.getToBePrepared().get(0), null, null, 0, null);
        Task t = testResume.getTasks().get(0);
        km.modifyTask(testResume, t, t.getTbp(), null, null, 0, null);
        System.out.println("Task modified.");
    }

    public void deleteTask() throws UseCaseLogicException {
        testResume = km.generateResume(testService);
        testResume.addTask(new Task(testResume.getToBePrepared().get(0), null, null, 0, null));
        km.deleteTask(testResume, testResume.getTasks().get(0));
        System.out.println("Task deleted.");
    }

    private List<AbstractRecipe> getSuitableRecipes(ServiceResume resume) {
        List<AbstractRecipe> required_recipes = resume.getToBePrepared();
       List<AbstractRecipe> allRecipes = (List<AbstractRecipe>)(List<?>) Recipe.loadAllRecipes();
        return allRecipes.stream()
            .filter(r -> !required_recipes.contains(r))
            .collect(Collectors.toList());
    }
}
