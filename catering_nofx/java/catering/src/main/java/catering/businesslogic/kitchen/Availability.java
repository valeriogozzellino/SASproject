package catering.businesslogic.kitchen;

import catering.businesslogic.recipe.AbstractRecipe;

public class Availability {

    private AbstractRecipe tbp;
    private String quantity;

    public Availability(AbstractRecipe tbp, String quantity) {
        this.tbp = tbp;
        this.quantity = quantity;
    }

}