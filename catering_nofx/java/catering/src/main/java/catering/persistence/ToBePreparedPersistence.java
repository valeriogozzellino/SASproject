package catering.persistence;

import catering.businesslogic.kitchen.KitchenEventReceiver;
import catering.businesslogic.kitchen.ServiceResume;
import catering.businesslogic.recipe.AbstractRecipe;

public class ToBePreparedPersistence implements KitchenEventReceiver {
    @Override
    public void updateToBePreparedAdded(ServiceResume resume, AbstractRecipe tbp) {
        AbstractRecipe.saveToBePrepared(resume, tbp);
    }

    @Override
    public void updateToBePreparedRemoved(ServiceResume resume, AbstractRecipe tbp) {
        AbstractRecipe.deleteToBePrepared(resume, tbp);
        AbstractRecipe.updatePosition(resume);
    }

    @Override
    public void updateToBePreparedSorted(ServiceResume resume) {
        AbstractRecipe.updatePosition(resume);
    }
}
