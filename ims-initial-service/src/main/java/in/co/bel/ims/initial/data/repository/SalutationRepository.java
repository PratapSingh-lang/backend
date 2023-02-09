package in.co.bel.ims.initial.data.repository;

import in.co.bel.ims.initial.entity.Salutation;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface SalutationRepository extends ImsJPATemplate<Salutation> {

	Salutation findByName(String name);
   
}

