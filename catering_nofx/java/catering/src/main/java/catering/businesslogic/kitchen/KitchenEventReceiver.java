package catering.businesslogic.kitchen;

public interface KitchenEventReceiver {
    default void updateResumeCreated(ServiceResume resume) {}
    default void updateTaskRemoved(ServiceResume resume, Task task) {}
    default void updateTaskChanged(ServiceResume resume, Task task) {}
    default void updateNewTask(ServiceResume resume, Task task) {}
    default void updateToBePreparedSorted(ServiceResume resume) {}
    default void updateToBePreparedRemoved(ServiceResume resume, AbstractRecipe tbp) {}
    default void updateToBePreparedAdded(ServiceResume resume, AbstractRecipe tbp) {}
    default void updateAvailabilityAdded(ServiceResume resume, Availability done) {}
    default void updateShiftState(Shift turn, boolean complete) {}
}
