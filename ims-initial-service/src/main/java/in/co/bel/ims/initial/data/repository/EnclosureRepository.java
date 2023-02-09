package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.Enclosure;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface EnclosureRepository extends ImsJPATemplate<Enclosure> {

	List<Enclosure> findByEnclosureGroupIdAndDeleted(int id, boolean deleted);

	Enclosure findByIdAndDeleted(int id, boolean b);
   
}

