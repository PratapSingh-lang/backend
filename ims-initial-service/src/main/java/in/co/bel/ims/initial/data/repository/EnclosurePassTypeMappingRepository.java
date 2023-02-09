package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.EnclosurePassTypeMapping;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface EnclosurePassTypeMappingRepository extends ImsJPATemplate<EnclosurePassTypeMapping> {

	List<EnclosurePassTypeMapping> findByEventIdAndDeleted(int eventId, boolean deleted);

	List<EnclosurePassTypeMapping> findByPassSubcategoryId(int subCategoryId);

	List<EnclosurePassTypeMapping> findByEventIdAndEnclosureIdAndDeleted(int eventId, int enclosureId, boolean b);

	List<EnclosurePassTypeMapping> findByEventIdAndPassCategoryId(int eventId, int type);

	List<EnclosurePassTypeMapping> findByEventIdAndPassSubcategoryIdAndEnclosureEnclosureGroupId(int eventId, int type,
			int enclosureGroupId);

	List<EnclosurePassTypeMapping> findByEventIdAndPassSubcategoryIdAndEnclosureEnclosureGroupIdAndDeleted(int eventId,
			int type, int enclosureGroupId, boolean b);
	List<EnclosurePassTypeMapping> findByEnclosureIdAndPassSubcategoryIdAndDeleted(int id, int type, boolean b);

	List<EnclosurePassTypeMapping> findByPassSubcategoryIdAndEnclosureEnclosureGroupIdAndDeleted(int passSubCatId, int enclGrpId,
			boolean deleted);

	List<EnclosurePassTypeMapping> findByEnclosureIdAndPassSubcategoryIdAndDeletedAndEventId(int id, int type,
			boolean b, int eventId);

	List<EnclosurePassTypeMapping> findAllByEventIdAndPassSubcategoryIdAndDeleted(int eventId, int passSubcategoryId,
			boolean b);

	List<EnclosurePassTypeMapping> findByPassSubcategoryIdAndDeleted(int id, boolean b);

	List<EnclosurePassTypeMapping> findByEventIdAndPassCategoryIdAndDeleted(int eventid, int passCategoryId, boolean b);

	List<EnclosurePassTypeMapping> findByEventIdAndPassCategoryIdAndEnclosureIdAndDeleted(int eventId,
			int passCategoryId, int id, boolean b);
   
}

