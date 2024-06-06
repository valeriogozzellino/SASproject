package catering.businesslogic.event;

import java.util.ArrayList;

public class EventManager {

    private ArrayList<EventInfo> eventList = new ArrayList<>();

    public ArrayList<EventInfo> getEventInfo() {

        eventList = EventInfo.loadAllEventInfo();
        return eventList;
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

}
