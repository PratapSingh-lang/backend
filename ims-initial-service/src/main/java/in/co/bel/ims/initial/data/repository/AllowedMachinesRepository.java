package in.co.bel.ims.initial.data.repository;

import in.co.bel.ims.initial.entity.AllowedMachines;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface AllowedMachinesRepository extends ImsJPATemplate<AllowedMachines> {

	AllowedMachines findByImsUserId(int id);
   
}

