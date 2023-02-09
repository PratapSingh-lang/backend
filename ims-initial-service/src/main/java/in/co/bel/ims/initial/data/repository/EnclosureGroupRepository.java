package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.EnclosureGroup;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface EnclosureGroupRepository extends ImsJPATemplate<EnclosureGroup> {

	List<EnclosureGroup> findByVenueIdAndDeleted(int id, boolean deleted);

	EnclosureGroup findByName(String name);

	EnclosureGroup findByIdAndDeleted(int id, boolean deleted);
   
}

