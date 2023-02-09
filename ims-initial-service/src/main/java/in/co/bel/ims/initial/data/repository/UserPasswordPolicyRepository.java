package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.UserPasswordPolicy;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface UserPasswordPolicyRepository extends ImsJPATemplate<UserPasswordPolicy> {

	List<UserPasswordPolicy> findAllByImsUserId(int id);
   
}

