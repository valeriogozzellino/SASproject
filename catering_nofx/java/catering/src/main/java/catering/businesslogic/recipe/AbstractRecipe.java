package catering.businesslogic.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractRecipe {
    protected int id;
    protected String name;

    private List<Preparation> requiredPreparations = new ArrayList<>();

    public List<Preparation> getRequiredPreparations() {
        return requiredPreparations;
    }

    public AbstractRecipe(String name) {
        id = 0;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass())
            return false;

        AbstractRecipe that = (AbstractRecipe) o;
        return Objects.equals(id, that.id);
    }
}
