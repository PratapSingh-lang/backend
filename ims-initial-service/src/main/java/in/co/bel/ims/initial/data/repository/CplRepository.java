package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.Cpl;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface CplRepository extends ImsJPATemplate<Cpl> {

	List<Cpl> findByEnclosureGroupIdAndDeleted(int id, boolean deleted);

	Cpl findByIdAndDeleted(int cplId, boolean b);

	List<Cpl> findByEnclosureGroupIdInAndDeleted(List<Integer> enclosureGrpIds, boolean b);
   
}

