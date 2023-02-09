package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.HigherOfficer;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface HigherOfficerRepository extends ImsJPATemplate<HigherOfficer> {

	List<HigherOfficer> findByEventIdAndDeleted(int eventId, boolean b);
   
}

