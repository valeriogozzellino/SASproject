package catering.businesslogic.shift;

import java.util.ArrayList;
import java.util.List;

public class ShiftManager {
    private static ShiftManager instance;
    private List<Shift> shifts;

    public static ShiftManager getInstance() {
        if (instance == null)
            instance = new ShiftManager();
        return instance;
    }

    private ShiftManager() {
        shifts = new ArrayList<>();
        shifts.addAll(Shift.loadTurns());
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    public Shift getShift(int id) {
        for (Shift t : shifts)
            if (t.getId() == id)
                return t;
        return null;
    }

    public void setTurnComplete(Shift shift, boolean complete) {
        if (shift == null)
            throw new NullPointerException();
        shift.setComplete(complete);
    }
}
