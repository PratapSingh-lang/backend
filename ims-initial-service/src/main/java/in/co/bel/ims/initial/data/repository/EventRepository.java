package in.co.bel.ims.initial.data.repository;

import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface EventRepository extends ImsJPATemplate<Event> {

	Event findByIdAndDeleted(int eventId, boolean b);
   
}

