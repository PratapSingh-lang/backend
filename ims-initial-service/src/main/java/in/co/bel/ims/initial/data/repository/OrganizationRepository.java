package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.Organization;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface OrganizationRepository extends ImsJPATemplate<Organization> {

	List<Organization> findByOrganizationGroupIdAndDeleted(int id, boolean deleted);

	Organization findByName(String name);
   
}

