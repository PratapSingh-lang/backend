package in.co.bel.ims.initial.data.repository;

import in.co.bel.ims.initial.entity.IdentityProof;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface IdentityProofRepository extends ImsJPATemplate<IdentityProof> {
	IdentityProof findByName(String name);
}

