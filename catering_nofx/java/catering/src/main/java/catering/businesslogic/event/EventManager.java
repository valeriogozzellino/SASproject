package catering.businesslogic.event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventManager {
    private static EventManager instance;
    private ArrayList<EventInfo> eventList = new ArrayList<>();

    private EventManager() {
        eventList = EventInfo.loadAllEventInfo();
    }

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
        List<EventInfo> suitableEvents = eventList.stream()
                .filter(e -> e.getServices().contains(serviceInfo))
                .collect(Collectors.toList());
        return suitableEvents.isEmpty() ? null : suitableEvents.get(0);
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
