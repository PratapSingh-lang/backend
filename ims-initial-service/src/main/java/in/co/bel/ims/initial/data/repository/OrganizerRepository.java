package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.Organizer;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface OrganizerRepository extends ImsJPATemplate<Organizer> {

	List<Organizer> findByEventIdAndDeleted(int eventId, boolean b);

	//List<Organizer> findAllByImsUserByImsUserIdIdAndEventIdAndDeleted(int id, int eventId, boolean b);

	List<Organizer> findByImsUserIdAndEventIdAndDeleted(int id, int eventId, boolean b);
   
}

