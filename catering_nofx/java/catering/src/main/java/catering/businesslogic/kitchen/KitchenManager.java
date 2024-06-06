package catering.businesslogic.kitchen;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.EventManager;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.kitchen.*;
import catering.businesslogic.recipe.AbstractRecipe;
import catering.businesslogic.shift.Shift;
import catering.businesslogic.shift.ShiftManager;
import catering.businesslogic.user.User;
import catering.businesslogic.user.UserManager;

public class KitchenManager {
    private static KitchenManager instance;
    private List<KitchenEventReceiver> eventReceivers = new ArrayList<>();
    private List<ServiceResume> currentResumes = new ArrayList<>();
    private List<ServiceResume> resumes = new ArrayList<>();

    private KitchenManager() {
        resumes = ServiceResume.loadAllServiceResumes();
    }

    public static KitchenManager getInstance() {
        if (instance == null)
            instance = new KitchenManager();
        return instance;
    }

    public void addReceiver(KitchenEventReceiver kr) {
        eventReceivers.add(kr);
    }

    public void removeReceiver(KitchenEventReceiver kr) {
        eventReceivers.remove(kr);
    }

    private void notifyToBePreparedAdded(ServiceResume resume, AbstractRecipe tbp) {
        eventReceivers.forEach(er -> er.updateToBePreparedAdded(resume, tbp));
    }

    private void notifyToBePreparedRemoved(ServiceResume resume, AbstractRecipe tbp) {
        eventReceivers.forEach(er -> er.updateToBePreparedRemoved(resume, tbp));
    }

    private void notifyToBePreparedArranged(ServiceResume resume) {
        eventReceivers.forEach(er -> er.updateToBePreparedSorted(resume));
    }

    private void notifyResumeCreated(ServiceResume resume) {
        List<AbstractRecipe> tbps = resume.getToBePrepared();

        eventReceivers.forEach(er -> {
            er.updateResumeCreated(resume);
            tbps.forEach(ar -> er.updateToBePreparedAdded(resume, ar));
        });
    }

    private void notifyNewTask(ServiceResume resume, Task task) {
        eventReceivers.forEach(er -> er.updateTaskChanged(resume, task));
    }

    private void notifyTaskChanged(ServiceResume resume, Task task) {
        eventReceivers.forEach(er -> er.updateTaskChanged(resume, task));
    }

    private void notifyTaskRemoved(ServiceResume resume, Task task) {
        eventReceivers.forEach(er -> er.updateTaskRemoved(resume, task));
    }

    private void notifyTurnComplete(Shift turn, boolean complete) {
        eventReceivers.forEach(er -> er.updateShiftState(turn, complete));
    }

    private void notifyAvailabilityAdded(ServiceResume resume, Availability done) {
        eventReceivers.forEach(er -> er.updateAvailabilityAdded(resume, done));
    }

    // private void notifyAvailabilityDeleted(ServiceResume resume, Availability
    // done) {
    // eventReceivers.forEach(er -> er.updateAvailabilityDeleted(done, resume));
    // }

    /**
     * ------------------------------
     * Method
     * ------------------------------
     */

    /**
     * generate new Resume if it doesn't exist
     * DSD 1
     * 
     * @param service
     * @return
     * @throws UseCaseLogicException
     */
    public ServiceResume generateResume(ServiceInfo service) throws UseCaseLogicException {
        if (service == null)
            throw new NullPointerException("Servizio non valorizzato");

        UserManager userMgr = CatERing.getInstance().getUserManager();
        User user = userMgr.getCurrentUser();

        if (user == null || !user.isChef())
            throw new UseCaseLogicException("L'utente non è autenticato come chef");

        EventManager eventMgr = CatERing.getInstance().getEventManager();
        EventInfo event = eventMgr.getEvent(service);

        if (event == null || !event.isChefInCharge(user) || service.getMenu() == null)
            throw new UseCaseLogicException("L'evento di appartenenza non è valido.");

        if (exists(service))
            throw new UseCaseLogicException("Esiste già una scheda per l'evento.");

        ServiceResume resume = new ServiceResume(service);
        currentResumes.add(resume);
        resumes.add(resume);
        notifyResumeCreated(resume);

        return resume;
    }

    /**
     * DSD 1a.1
     * 
     * @param resume
     * @return
     * @throws UseCaseLogicException
     */
    public ServiceResume openResume(ServiceResume resume) throws UseCaseLogicException {
        UserManager userMgr = CatERing.getInstance().getUserManager();
        User user = userMgr.getCurrentUser();

        if (user == null || !user.isChef())
            throw new UseCaseLogicException("L'utente non è autenticato come chef");

        ServiceInfo service = resume.getReferredService();
        EventManager eventMgr = CatERing.getInstance().getEventManager();
        EventInfo event = eventMgr.getEvent(service);

        if (event == null || !event.isChefInCharge(user))
            throw new UseCaseLogicException("L'evento a cui appartiene il servizio non è in carico ad un altro chef.");

        currentResumes.add(resume);

        return resume;
    }

    /**
     * DSD 1b.1
     * 
     * @param resume
     * @return
     * @throws UseCaseLogicException
     */
    public ServiceResume resetResume(ServiceResume resume) throws UseCaseLogicException {
        openResume(resume);
        UserManager userMgr = CatERing.getInstance().getUserManager();
        User user = userMgr.getCurrentUser();

        if (user == null || !user.isChef())
            throw new UseCaseLogicException("L'utente non è autenticato come chef");

        // from now on currentResume is set to resume
        ServiceInfo service = resume.getReferredService();
        if (service.getMenu() == null)
            throw new UseCaseLogicException("Il menù per il servizio non è definito.");

        List<Task> deletedTasks = resume.deleteAllTask();
        for (Task t : deletedTasks)
            notifyTaskRemoved(resume, t);

        List<AbstractRecipe> deletedTBP = resume.deleteAllToBePrepared();
        for (AbstractRecipe ar : deletedTBP)
            notifyToBePreparedRemoved(resume, ar);

        List<AbstractRecipe> loadedTBP = resume.loadToBePrepared();
        for (AbstractRecipe ar : loadedTBP)
            notifyToBePreparedAdded(resume, ar);

        return resume;
    }

    /**
     * DSD 2
     * 
     * @param resume
     * @param tbp
     * @throws UseCaseLogicException
     */
    public void addToBePrepared(ServiceResume resume, AbstractRecipe tbp) throws UseCaseLogicException {
        UserManager userMgr = CatERing.getInstance().getUserManager();
        User user = userMgr.getCurrentUser();

        if (user == null || !user.isChef() || resume == null)
            throw new UseCaseLogicException("L'utente non è uno chef.");

        if (!resume.isRequired(tbp)) {
            resume.addToBePrepared(tbp);
            notifyToBePreparedAdded(resume, tbp);
        }
    }

    /**
     * DSD 2a.1
     * 
     * @param resume
     * @param tbp
     * @throws UseCaseLogicException
     */
    public void removeToBePrepared(ServiceResume resume, AbstractRecipe tbp)
            throws UseCaseLogicException {
        UserManager userMgr = CatERing.getInstance().getUserManager();
        User user = userMgr.getCurrentUser();

        if (user == null || !user.isChef() || resume == null ||
                !resume.isRequired(tbp))
            throw new UseCaseLogicException();

        AbstractRecipe removed = resume.removeToBePrepared(tbp);
        notifyToBePreparedRemoved(resume, removed);
    }

    /**
     * DSD 3
     * 
     * @param resume
     * @param tbp
     * @param position
     */
    public void sortToBePrepared(ServiceResume resume, AbstractRecipe tbp, int position) {
        if (position < 0 || position >= resume.toBePreparedSize())
            return;

        resume.sortToBePrepared(tbp, position);
        notifyToBePreparedArranged(resume);
    }

    /**
     * DSD 4
     * 
     * @return
     * @throws UseCaseLogicException
     */
    public List<Shift> showShiftsBoard() throws UseCaseLogicException {
        UserManager userMgr = CatERing.getInstance().getUserManager();
        User user = userMgr.getCurrentUser();

        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }

        ShiftManager shiftMgr = ShiftManager.getInstance();
        List<Shift> shifts = shiftMgr.getShifts();

        return shifts;
    }

    public void assignTask(ServiceResume resume, AbstractRecipe tbp, User cook,
            Shift shift, String time, String quantity) throws UseCaseLogicException {
        UserManager userMgr = CatERing.getInstance().getUserManager();
        User user = userMgr.getCurrentUser();

        if (user == null || !user.isChef())
            throw new UseCaseLogicException("L'utente non è uno chef");

        if (resume == null)
            throw new UseCaseLogicException("Non è aperta alcuna scheda.");

        if (tbp == null)
            throw new UseCaseLogicException("Il compito deve avere una mansione.");

        if (!resume.isRequired(tbp))
            throw new UseCaseLogicException("La scheda non prevede la ricetta/preparazione.");

        if (shift != null && shift.getStart().isBefore(LocalDateTime.now()))
            throw new UseCaseLogicException("Il turno si svolge nel passato.");

        if (cook != null && !cook.isCook())
            throw new UseCaseLogicException("Il cuoco non è valido");

        if (shift != null && shift.getDuration(shift.getId()) < time)
            throw new UseCaseLogicException("Il tempo della preparazione supera la durata del turno");

        if (shift != null && cook != null && !cook.isAvaible(shift))
            throw new UseCaseLogicException("Il cuoco non è disponibile nel turno");

        Task newTask = new Task(tbp, cook, shift, time, quantity);
        resume.addTask(newTask);
        resume.setCurrentTask(newTask);
        notifyNewTask(resume, newTask);
    }

    // public void modifyTask(ServiceResume resume, Task task, AbstractRecipe tbp,
    // User cook, Turn turn, String time, String quantity) throws
    // UseCaseLogicException {
    // if(task == null)
    // throw new NullPointerException();
    // UserManager userMgr = CatERing.getInstance().getUserManager();
    // User user = userMgr.getCurrentUser();

    // if(user == null || !user.isChef())
    // throw new UseCaseLogicException("L'utente non è uno chef");

    // if(resume == null || !resume.isSummarized(task))
    // throw new UseCaseLogicException("La scheda non è aperta o non contiene il
    // compito.");

    // if(tbp == null)
    // throw new UseCaseLogicException("Il compito deve avere una mansione.");

    // if(!resume.isRequired(tbp))
    // throw new UseCaseLogicException("La scheda non prevede la
    // ricetta/preparazione.");

    // if(turn != null && turn.getStart().isBefore(LocalDateTime.now()))
    // throw new UseCaseLogicException("Il turno si svolge nel passato.");

    // if(cook != null && !cook.isCook())
    // throw new UseCaseLogicException("Il cuoco non è valido");

    // if(turn != null && cook != null && !cook.isAvaible(turn))
    // throw new UseCaseLogicException("Il cuoco non è disponibile nel turno.");

    // Task succ = resume.whichAmIPrecedent(task);
    // if(turn != null && succ != null && succ.getTurn() != null &&
    // succ.getTurn().getStart().isBefore(turn.getEnd()))
    // throw new UseCaseLogicException("Il compito è impostato come precedente di un
    // altro, ma il turno non consente questa successione.");

    // task.setCook(cook);
    // task.setTurn(turn);
    // task.setTbp(tbp);
    // task.setTime(time);
    // task.setQuantity(quantity);

    // resume.setCurrentTask(task);
    // notifyTaskChanged(resume, task);
    // }

    // public void deleteTask(ServiceResume resume, Task task) throws
    // UseCaseLogicException {
    // if(task == null)
    // throw new NullPointerException();

    // UserManager userMgr = CatERing.getInstance().getUserManager();
    // User user = userMgr.getCurrentUser();

    // if(user == null || !user.isChef()
    // || resume == null
    // || !resume.isSummarized(task)
    // )
    // throw new UseCaseLogicException("Il compito non appartiene al foglio in
    // uso.");

    // resume.removeTask(task);
    // notifyTaskRemoved(resume, task);
    // }

    // public void signalTurnComplete(ServiceResume resume, Turn turn, boolean
    // complete) throws UseCaseLogicException {
    // if(turn == null)
    // throw new NullPointerException();

    // UserManager userMgr = CatERing.getInstance().getUserManager();
    // User user = userMgr.getCurrentUser();

    // if(user == null || !user.isChef())
    // throw new UseCaseLogicException("L'utente deve essere uno chef.");

    // if(resume == null || !resume.validTurn(turn))
    // throw new UseCaseLogicException("Non è possibile operare su turni nel
    // passato.");

    // List<Turn> suitableTurns = resume.getTasks().stream()
    // .map(Task::getTurn)
    // .collect(Collectors.toList());
    // if(!suitableTurns.contains(turn))
    // throw new UseCaseLogicException("Non hai assegnato compiti in questo
    // turno.");

    // TurnManager turnMgr = TurnManager.getInstance();
    // turnMgr.setTurnComplete(turn, complete);
    // notifyTurnComplete(turn, complete);
    // }

    // public void signalReady(ServiceResume resume, AbstractRecipe tbp, String
    // quantity) throws UseCaseLogicException {
    // if (tbp == null)
    // throw new NullPointerException();

    // UserManager userMgr = CatERing.getInstance().getUserManager();
    // User user = userMgr.getCurrentUser();

    // if (user == null || !user.isChef())
    // throw new UseCaseLogicException("Solo uno chef può effettuare
    // l'operazione.");

    // if (quantity == null || quantity.isEmpty())
    // throw new UseCaseLogicException("Devi indicare una quantità.");

    // Availability done = new Availability(tbp, quantity);
    // if (!resume.getAvailabilities().contains(done)){
    // resume.addAvailability(done);
    // notifyAvailabilityAdded(resume, done);
    // }
    // }

    // public void deleteReady(ServiceResume resume, Availability av) throws
    // UseCaseLogicException {
    // if(av == null)
    // throw new NullPointerException();

    // UserManager userMgr = CatERing.getInstance().getUserManager();
    // User user = userMgr.getCurrentUser();

    // if(user == null || !user.isChef())
    // throw new UseCaseLogicException("Solo uno chef può effettuare
    // l'operazione.");

    // if(!resume.getAvailabilities().contains(av))
    // throw new UseCaseLogicException("La voce non appartiene alla foglio.");

    // resume.deleteAvailability(av);
    // notifyAvailabilityDeleted(resume, av);
    // }

    // public void setPreviousStep(ServiceResume resume, Task previousTask) throws
    // UseCaseLogicException {
    // if(previousTask == null)
    // throw new NullPointerException();

    // UserManager userMgr = CatERing.getInstance().getUserManager();
    // User user = userMgr.getCurrentUser();

    // if(user == null || !user.isChef()
    // || resume == null
    // || !resume.isSummarized(previousTask)
    // || resume.getCurrentTask() == null
    // || !resume.isSummarized(resume.getCurrentTask())
    // )
    // throw new UseCaseLogicException("Precedente non valido");

    // if(resume.getCurrentTask().getTurn() != null
    // && previousTask.getTurn() != null
    // &&
    // resume.getCurrentTask().getTurn().getStart().isBefore(previousTask.getTurn().getEnd()))
    // throw new UseCaseLogicException("Precedente non valido");

    // resume.getCurrentTask().setPreviousStep(previousTask);
    // notifyTaskChanged(resume, resume.getCurrentTask());
    // }

    // public void removePreviousStep(ServiceResume resume) throws
    // UseCaseLogicException {

    // UserManager userMgr = CatERing.getInstance().getUserManager();
    // User user = userMgr.getCurrentUser();

    // if(user == null || !user.isChef()
    // || resume == null
    // || resume.getCurrentTask() == null
    // || !resume.isSummarized(resume.getCurrentTask())
    // )
    // throw new UseCaseLogicException("Non è in corso la defizione di un
    // compito.");
    // resume.getCurrentTask().setPreviousStep(null);
    // notifyTaskChanged(resume, resume.getCurrentTask());
    // }

    // public List<Task> getTasks(ServiceResume resume) {
    // return resume.getTasks();
    // }

    /**
     * 
     * @return List<ServiceResume>
     */
    public List<ServiceResume> getResumes() {
        return resumes;
    }

    /**
     * check if the serviceResume exist for service referred
     * 
     * @param resume
     * @return boolean
     */
    public boolean exists(ServiceInfo resume) {
        for (ServiceResume r : resumes)
            if (r.getReferredService().equals(resume))
                return true;
        return false;
    }

    // public List<ServiceResume> getCurrentResumes() {
    // return currentResumes;
    // }

    // public void closeResume(ServiceResume resume) {
    // currentResumes.remove(resume);
    // }
}
