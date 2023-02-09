package in.co.bel.ims.initial.data.repository;

import in.co.bel.ims.initial.entity.PayLevel;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface PayLevelRepository extends ImsJPATemplate<PayLevel> {

	PayLevel findByName(String name);
   
}

