package catering.businesslogic.event;

import java.util.ArrayList;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import catering.businesslogic.user.User;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

public class EventInfo {

    private int id;
    private String name;
    private Date dateStart;
    private Date dateEnd;
    private int participants;
    private User organizer;
    private User chef;

    private ArrayList<ServiceInfo> services;

    public EventInfo(String name) {
        this.name = name;
        id = 0;
    }

    public ArrayList<ServiceInfo> getServices() {
        return this.services;
    }

    public String toString() {
        return name + ": " + dateStart + "-" + dateEnd + ", " + participants + " pp. (" + organizer.getUserName() + ")";
    }

    public ServiceInfo getService(int id) {
        for (ServiceInfo s : services) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    // STATIC METHODS FOR PERSISTENCE
    public static ArrayList<EventInfo> loadAllEventInfo() {
        ArrayList<EventInfo> all = new ArrayList<>();
        String query = "SELECT * FROM events WHERE true";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                EventInfo e = new EventInfo(n);
                e.id = rs.getInt("id");
                e.dateStart = rs.getDate("date_start");
                e.dateEnd = rs.getDate("date_end");
                e.participants = rs.getInt("expected_participants");
                int org = rs.getInt("organizer_id");
                e.organizer = User.loadUserById(org);
                all.add(e);
            }
        });

        for (EventInfo e : all) {
            e.services = ServiceInfo.loadServiceInfoForEvent(e.id);
        }

        return all;
    }

    public String getChef() {
        return chef.getUserName();
    }

    public String getOrganizer() {
        return organizer.getUserName();
    }

    public boolean isChefInCharge(User user) {
        return user.equals(chef);
    }

    public boolean isOrgInCharge(User user) {
        return user.getId() == organizer.getId();
    }
}
