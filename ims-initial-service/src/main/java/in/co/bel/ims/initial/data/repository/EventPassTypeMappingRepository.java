package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.EventPassTypeMapping;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface EventPassTypeMappingRepository extends ImsJPATemplate<EventPassTypeMapping> {

	List<EventPassTypeMapping> findByEventIdAndDeleted(int eventId, boolean deleted);

	List<EventPassTypeMapping> findByEventIdAndPassCategoryIdAndDeleted(int eventId, int passCategoryId, boolean b);
   
}

