package catering.businesslogic;

import catering.businesslogic.event.EventManager;
import catering.businesslogic.kitchen.KitchenManager;
import catering.businesslogic.menu.MenuManager;
import catering.businesslogic.recipe.RecipeManager;
import catering.businesslogic.user.UserManager;
import catering.persistence.AvailabilityPersistence;
import catering.persistence.MenuPersistence;
import catering.persistence.ToBePreparedPersistence;
import persistence.ServiceResumePersistence;

public class CatERing {
    private static CatERing singleInstance;

    public static CatERing getInstance() {
        if (singleInstance == null) {
            singleInstance = new CatERing();
            singleInstance.setPersistence();
        }
        return singleInstance;
    }

    public static CatERing getInstanceNotPersistent() {
        if (singleInstance == null) {
            singleInstance = new CatERing();
        }
        return singleInstance;
    }

    private MenuManager menuMgr;
    private RecipeManager recipeMgr;
    private UserManager userMgr;
    private EventManager eventMgr;
    private KitchenManager kitchenMgr;

    private CatERing() {
        userMgr = UserManager.getInstance();
        recipeMgr = RecipeManager.getInstance();
        menuMgr = new MenuManager();
        eventMgr = EventManager.getInstance();
        kitchenMgr = KitchenManager.getInstance();
    }

    private void setPersistence() {
        MenuPersistence menuPersistence = new MenuPersistence();
        menuMgr.addEventReceiver(menuPersistence);

        ServiceResumePersistence serviceResumePersistence = new ServiceResumePersistence();
        kitchenMgr.addReceiver(serviceResumePersistence);

        ToBePreparedPersistence toBePreparedPersistence = new ToBePreparedPersistence();
        kitchenMgr.addReceiver(toBePreparedPersistence);

        AvailabilityPersistence availabilityPersistence = new AvailabilityPersistence();
        kitchenMgr.addReceiver(availabilityPersistence);

        /*
        TaskPersistence taskPersistence = new TaskPersistence();
        kitchenMgr.addReceiver(taskPersistence);

        TurnPersistence turnPersistence = new TurnPersistence();
        kitchenMgr.addReceiver(turnPersistence);
         */
    }

    public MenuManager getMenuManager() {
        return menuMgr;
    }

    public RecipeManager getRecipeManager() {
        return recipeMgr;
    }

    public UserManager getUserManager() {
        return userMgr;
    }

    public EventManager getEventManager() {
        return eventMgr;
    }

    public KitchenManager getKitchenMgr() {
        return kitchenMgr;
    }

}
