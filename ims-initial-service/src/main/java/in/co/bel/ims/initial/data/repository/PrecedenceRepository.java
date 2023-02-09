package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.Precedence;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface PrecedenceRepository extends ImsJPATemplate<Precedence> {

	List<Precedence> findByArticleIdAndDeleted(int id, boolean deleted);
   
}

