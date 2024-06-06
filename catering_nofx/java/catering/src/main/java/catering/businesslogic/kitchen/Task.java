package catering.businesslogic.kitchen;

import catering.businesslogic.recipe.AbstractRecipe;
import catering.businesslogic.shift.Shift;
import catering.businesslogic.user.User;

public class Task {
    private AbstractRecipe tbp;
    private User cook;
    private Shift shift;
    private int time;
    private String quantity;
    private Task previousTask;
    private int id;

    private Task() {
    }

    public Task(AbstractRecipe tbp, User cook, Shift shift, int time, String quantity) {
        if (tbp == null)
            throw new NullPointerException();
        this.tbp = tbp;
        this.cook = cook;
        this.shift = shift;
        this.time = time;
        this.quantity = quantity;
    }

    public AbstractRecipe getTbp() {
        return tbp;
    }

    public void setTbp(AbstractRecipe tbp) {
        this.tbp = tbp;
    }

    public User getCook() {
        return cook;
    }

    public void setCook(User cook) {
        this.cook = cook;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setPreviousStep(Task previousTask) {
        this.previousTask = previousTask;
    }

    public Task getPreviousStep() {
        return previousTask;
    }

    public int getId() {
        return id;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }

}
