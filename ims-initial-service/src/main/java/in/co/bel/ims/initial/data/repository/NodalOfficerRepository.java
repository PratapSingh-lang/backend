package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.NodalOfficer;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface NodalOfficerRepository extends ImsJPATemplate<NodalOfficer> {

	List<NodalOfficer> findByImsUserId(int userId);

	List<NodalOfficer> findByDepartmentIdAndDeleted(int departmentId, boolean deleted);
	
	List<NodalOfficer> findByEventIdAndDeleted(int eventId, boolean deleted);
   
}

