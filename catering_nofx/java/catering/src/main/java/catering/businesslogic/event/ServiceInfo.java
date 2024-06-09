package catering.businesslogic.event;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

import catering.businesslogic.menu.Menu;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

public class ServiceInfo {
    private int id;
    private String name;
    private Date date;
    private Time timeStart;
    private Time timeEnd;
    private int participants;
    private int id_menu;
    private Menu menu; // Valutare se inserirlo qui

    public ServiceInfo(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return name + ": " + date + " (" + timeStart + "-" + timeEnd + "), " + participants + " pp. "+" menù Id :"+ id_menu ;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ArrayList<ServiceInfo> loadServiceInfoForEvent(int event_id) {
        ArrayList<ServiceInfo> result = new ArrayList<>();
        
        String query = "SELECT id, name, service_date, time_start, time_end, expected_participants, approved_menu_id " +
                "FROM services WHERE event_id = " + event_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String s = rs.getString("name");
                ServiceInfo serv = new ServiceInfo(s);
                serv.id = rs.getInt("id");
                serv.date = rs.getDate("service_date");
                serv.timeStart = rs.getTime("time_start");
                serv.timeEnd = rs.getTime("time_end");
                serv.participants = rs.getInt("expected_participants");
                serv.id_menu = rs.getInt("approved_menu_id");
                System.out.println("MENU ID" +serv.id_menu +"\n");
                result.add(serv);

            }
           

        });
        
        Menu m = new Menu();  // Assumiamo che Menu sia una classe che può essere assegnata ad ogni ServiceInfo
        for (ServiceInfo si : result) {
            si.menu = m; // Assumiamo che ServiceInfo abbia una proprietà 'menu'
            System.out.println("ESEGUITA ASSEGNAMENTO MENU");    
        }

        return result;
    }

    public Menu getMenu() {
        return menu;
    }
}
