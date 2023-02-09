package in.co.bel.ims.initial.data.repository;

import in.co.bel.ims.initial.entity.MaritalStatus;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface MaritalStatusRepository extends ImsJPATemplate<MaritalStatus> {

	MaritalStatus findByStatus(String status);
   
}

