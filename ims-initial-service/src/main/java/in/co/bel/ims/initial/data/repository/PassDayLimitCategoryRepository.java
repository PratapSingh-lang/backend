package in.co.bel.ims.initial.data.repository;

import java.time.LocalDate;
import java.util.List;

import in.co.bel.ims.initial.entity.PassDayLimitCategory;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface PassDayLimitCategoryRepository extends ImsJPATemplate<PassDayLimitCategory> {


	PassDayLimitCategory findByPassDayLimitEventIdAndPassDayLimitDateAndRoleIdAndPassSubcategoryIdAndEnclosureGroupIdAndDeleted(
			int eventId, LocalDate localDate, int roleId, int passSubCatId, int enclGrpId, boolean deleted);

	List<PassDayLimitCategory> findAllByPassDayLimitEventIdAndPassDayLimitDateAndRoleIdAndDeleted(int eventId, LocalDate date, int roleId, boolean deleted);

	List<PassDayLimitCategory> findByDeletedAndRoleIdAndEnclosureGroupIdAndPassSubcategoryIdAndPassDayLimitIdIn(
			boolean b, int roleId, int enclosureGroupId, int passSubcategoryId, List<Integer> passDayLimitIds);

	List<PassDayLimitCategory> findByPassDayLimitEventIdAndPassDayLimitDateAndRoleIdAndPassSubcategoryIdAndDeleted(
			int eventId, LocalDate now, int roleId, int passSubcategoryId, boolean b);

	List<PassDayLimitCategory> findAllByPassDayLimitEventIdAndRoleIdAndPassDayLimitDateAndDeleted(int id, int role,
			LocalDate now, boolean b);

	PassDayLimitCategory findByPassDayLimitEventIdAndPassDayLimitDateAndRoleIdAndPassSubcategoryIdAndEnclosureGroupId(
			int id, LocalDate localDate, int id2, int id3, int id4);

	List<PassDayLimitCategory> findAllByRoleIdAndPassDayLimitDate(int role, LocalDate now);

}

