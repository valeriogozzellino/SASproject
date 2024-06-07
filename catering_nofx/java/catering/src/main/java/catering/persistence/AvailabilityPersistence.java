package catering.persistence;

import catering.businesslogic.kitchen.Availability;
import catering.businesslogic.kitchen.KitchenEventReceiver;
import catering.businesslogic.kitchen.ServiceResume;

public class AvailabilityPersistence implements KitchenEventReceiver {

    @Override
    public void updateAvailabilityAdded(ServiceResume resume, Availability done) {
        Availability.save(resume, done);
    }

    @Override
    public void updateAvailabilityDeleted(Availability done, ServiceResume resume) {
        Availability.delete(done, resume);
    }
}
