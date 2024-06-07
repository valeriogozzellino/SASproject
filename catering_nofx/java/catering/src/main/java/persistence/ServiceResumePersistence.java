package persistence;

import catering.businesslogic.kitchen.KitchenEventReceiver;
import catering.businesslogic.kitchen.ServiceResume;

public class ServiceResumePersistence implements KitchenEventReceiver {
    @Override
    public void updateResumeCreated(ServiceResume resume) {
        ServiceResume.save(resume);
    }

}