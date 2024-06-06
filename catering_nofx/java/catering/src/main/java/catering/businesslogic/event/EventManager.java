package catering.businesslogic.event;

import java.util.ArrayList;

public class EventManager {
    private static EventManager instance;
    private ArrayList<EventInfo> eventList = new ArrayList<>();

    public ArrayList<EventInfo> getEventInfo() {

        eventList = EventInfo.loadAllEventInfo();
        return eventList;
    }

    public static EventManager getInstance() {
        if (instance == null)
            instance = new EventManager();
        return instance;
    }

    /**
     * getEvent
     * 
     * @param serviceInfo
     * @return EventInfo
     */
    public EventInfo getEvent(ServiceInfo serviceInfo) {

        for (EventInfo event : eventList) {
            for (ServiceInfo s : event.getServices()) {
                if (s.equals(serviceInfo)) {
                    return event;
                }
            }
        }
        return null;
    }

    public ServiceInfo getService(int id) {
        for (EventInfo e : eventList) {
            ServiceInfo s = e.getService(id);
            if (s != null)
                return s;
        }
        return null;
    }

}
