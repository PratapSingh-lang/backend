package in.co.bel.ims.initial.data.repository;

import in.co.bel.ims.initial.entity.SessionManagement;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface SessionManagementRepository extends ImsJPATemplate<SessionManagement> {
   
	public SessionManagement findByTokenAndValid(String token, boolean valid);
}

