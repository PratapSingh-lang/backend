package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.EnclosureCplMapping;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface EnclosureCplMappingRepository extends ImsJPATemplate<EnclosureCplMapping> {

	List<EnclosureCplMapping> findByEnclosureEnclosureGroupVenueIdAndDeleted(int venueId, boolean deleted);

	List<EnclosureCplMapping> findByEnclosureId(int id);


	List<EnclosureCplMapping> findByEnclosureIdInAndDeleted(List<Integer> list, boolean b);

	List<EnclosureCplMapping> findByEnclosureIdAndDeleted(int enclosureId, boolean b);
   
}

