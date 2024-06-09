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

    public static void main(String[] args) {
        /*---------- TEST KITCHEN MANAGER -----------*/
        KitchenManagerTest test = new KitchenManagerTest();
        KitchenManagerTest.init();
        try {
            System.out.println("\n GENERATE RESUME \n");
            test.generateResume();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n ADD TO BE PREPARED \n");
            test.addToBePrepared();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n ASSIGN TASK \n");
            test.assignTask();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n DELETE TASK \n");
            test.deleteTask();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n OPEN RESUME \n");
            test.openResume();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n MODIFY TASK \n");
            test.modifyTask();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n SORT TO BE PREPARED \n");
            test.sortToBePrepared();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n SHOW TURN BOARD \n");
            test.showTurnBoard();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n RESET RESUME \n");
            test.resetResume();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n REMOVE TO BE PREPARED  \n");
            test.removeToBePrepared();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n TEST PREVIOUS STEP \n");
            test.testSetPreviousStep();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n TEST ASSIGN TASK TIME \n");
            test.testAssignTaskTime();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n TEST SIGNAL SHIFT COMPLETE \n");
            test.testSignalShiftComplete();
            test.after();
            System.out.println("\n------------------------------\n");
            System.out.println("\n TEST DELETE READY \n");
            test.testDeleteReady();
            test.after();
            System.out.println("\n------------------------------\n");

        } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
    }

    public static void init() {
        CatERing cer = CatERing.getInstanceNotPersistent();
        um = UserManager.getInstance();
        km = KitchenManager.getInstance();
        rm = RecipeManager.getInstance();
        em = EventManager.getInstance();
        sm = ShiftManager.getInstance();
        testService = em.getService(2);
        System.out.println("SERVICE TEST : " + testService + "\n");
        um.fakeLogin("Lidia");
    }

    public void after() throws UseCaseLogicException {
        if (testResume != null) {
            km.getResumes().remove(testResume);
        }
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
        if (!notAlreadyRequired.isEmpty()) {
            testResume.addToBePrepared(notAlreadyRequired.get(0));
        }
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

    public void sortToBePrepared() throws UseCaseLogicException {
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
        System.out.println("Task modified.");
        km.modifyTask(testResume,t,t.getTbp(),null,null,0,null);
    }

    public void deleteTask() throws UseCaseLogicException {
        testResume = km.generateResume(testService);
        testResume.addTask(new Task(testResume.getToBePrepared().get(0), null, null, 0, null));
        km.deleteTask(testResume, testResume.getTasks().get(0));
        System.out.println("Task deleted.");
    }

    private List<AbstractRecipe> getSuitableRecipes(ServiceResume resume) {
        List<AbstractRecipe> required_recipes = resume.getToBePrepared();
        List<AbstractRecipe> allRecipes = (List<AbstractRecipe>) (List<?>) Recipe.loadAllRecipes();
        return allRecipes.stream()
                .filter(r -> !required_recipes.contains(r))
                .collect(Collectors.toList());
    }

    public void testSignalShiftComplete() {
        try {
            Shift invalidShift = null;
            km.signalShiftComplete(testResume, invalidShift, true);
            System.out.println("Error: NullPointerException expected for null shift.");
        } catch (NullPointerException e) {
            System.out.println("Passed: Caught NullPointerException as expected.");
        } catch (Exception e) {
            System.out.println("Error: Unexpected exception type " + e.getClass().getSimpleName());
        }

        try {
            Shift validShift = new Shift();
            um.fakeLogin("NonChefUser");
            km.signalShiftComplete(testResume, validShift, true);
            System.out.println("Error: UseCaseLogicException expected for non-chef user.");
        } catch (UseCaseLogicException e) {
            System.out.println("Passed: Caught UseCaseLogicException as expected with message: " + e.getMessage());
        }

    }

    public void testSignalReady() {
        try {
            AbstractRecipe nullRecipe = null;
            km.signalReady(testResume, nullRecipe, "10");
            System.out.println("Error: NullPointerException expected for null recipe.");
        } catch (NullPointerException e) {
            System.out.println("Passed: Caught NullPointerException as expected.");
        } catch (Exception e) {
            System.out.println("Error: Unexpected exception type " + e.getClass().getSimpleName());
        }

        try {
            AbstractRecipe validRecipe = new Recipe("pasta al sugo");
            um.fakeLogin("NonChefUser");
            km.signalReady(testResume, validRecipe, "10");
            System.out.println("Error: UseCaseLogicException expected for non-chef user.");
        } catch (UseCaseLogicException e) {
            System.out.println("Passed: Caught UseCaseLogicException as expected with message: " + e.getMessage());
        }

    }

    public void testDeleteReady() {
        try {
            km.deleteReady(testResume, null);
            System.out.println("Error: NullPointerException expected for null availability.");
        } catch (NullPointerException e) {
            System.out.println("Passed: Caught NullPointerException as expected.");
        } catch (Exception e) {
            System.out.println("Error: Unexpected exception type " + e.getClass().getSimpleName());
        }

        try {
            Availability invalidAvailability = new Availability();
            um.fakeLogin("NonChefUser");
            km.deleteReady(testResume, invalidAvailability);
            System.out.println("Error: UseCaseLogicException expected for non-chef user.");
        } catch (UseCaseLogicException e) {
            System.out.println("Passed: Caught UseCaseLogicException as expected with message: " + e.getMessage());
        }

        try {
            Availability validAvailability = new Availability();
            testResume.addAvailability(validAvailability);
            um.fakeLogin("Tony");
            km.deleteReady(testResume, validAvailability);
            System.out.println("Passed: Successfully deleted availability.");
        } catch (Exception e) {
            System.out.println("Error: Failed with exception " + e.getMessage());
        }
    }

    public void testAssignTaskTime() {
        try {
            km.assignTaskTime(null, 30);
            System.out.println("Error: NullPointerException expected for null task.");
        } catch (Exception e) {
            System.out.println("Passed: Caught NullPointerException as expected.");
        }

        try {
            um.fakeLogin("Guido");
            AbstractRecipe ar = testResume.getToBePrepared().get(0);
            Task validTask = new Task(ar, um.getCurrentUser(), null, 0, null);
            validTask.setShift(new Shift());
            um.fakeLogin("NonChefUser");
            km.assignTaskTime(validTask, 30);
            System.out.println("Error: UseCaseLogicException expected for non-chef user.");
        } catch (UseCaseLogicException e) {
            System.out.println("Passed: Caught UseCaseLogicException as expected.");
        }
    }

    public void testSetPreviousStep() {
        try {
            km.setPreviousStep(testResume, null);
            System.out.println("Error: NullPointerException expected for null previous task.");
        } catch (Exception e) {
            System.out.println("Passed: Caught NullPointerException as expected.");
        }
        try {
            testResume = km.generateResume(testService);
            Shift tSucc = sm.getShifts().get(2);
            Shift tPrec = sm.getShifts().get(1);
            km.assignTask(testResume, testResume.getToBePrepared().get(0), null, tPrec, 0, null);
            km.assignTask(testResume, testResume.getToBePrepared().get(0), null, tSucc, 0, null);
            km.setPreviousStep(testResume, testResume.getTasks().get(0));
            System.out.println("Passed: Successfully set previous step.");
        } catch (UseCaseLogicException e) {
            System.out.println("Error: UseCaseLogicException expected for invalid task setup: " + e.getMessage());
        }
    }
}
