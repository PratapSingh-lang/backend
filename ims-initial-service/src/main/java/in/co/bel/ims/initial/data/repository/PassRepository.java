package in.co.bel.ims.initial.data.repository;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.co.bel.ims.initial.entity.Enclosure;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.entity.PassDayLimitCategory;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface PassRepository extends ImsJPATemplate<Pass> {

	List<Pass> findByEventIdAndDeleted(int eventId, boolean deleted);

	Pass findByControlNoAndDeleted(String controlNo, boolean deleted);

	List<Pass> findByPassCategoryIdAndPassSubcategoryIdAndDeleted(int categoryId, int subcategoryId, boolean deleted);

	List<Pass> findByImsUserByImsUserIdUserTypeIdAndDeleted(int userTypeId, boolean deleted);

	List<Pass> findByImsUserByImsUserIdIdAndPassStatusIdNotAndDeleted(int userId, int passStatus, boolean deleted);

	List<Pass> findByDepartmentOrganizationIdAndDeleted(int organizationId, boolean deleted);

	List<Pass> findByDepartmentIdAndDeleted(int departmentId, boolean deleted);
	
	Pass findByCarPassIdAndDeleted(int carPassId, boolean deleted);

	List<Pass> findByPassCategoryIdAndDeleted(int categoryId, boolean deleted);

	List<Pass> findByDepartmentOrganizationIdAndEventIdAndDeleted(int organizationId, int eventId, boolean deleted);

	List<Pass> findByDepartmentIdAndEventIdAndDeleted(int departmentId, int eventId, boolean deleted);

	List<Pass> findByPassCategoryIdAndEventIdAndDeleted(int categoryId, int eventId, boolean deleted);

	List<Pass> findByPassCategoryIdAndPassSubcategoryIdAndEventIdAndDeleted(int categoryId, int subcategoryId,
			int eventId, boolean deleted);

	List<Pass> findByImsUserByImsUserIdUserTypeIdAndEventIdAndDeleted(int userTypeId, int eventId, boolean deleted);

	@Query(value = "select last_value from public.pass_seq", nativeQuery = true)
	int getLastIdOfPass();

	List<Pass> findByImsUserByInvitationAdminIdIdAndEventIdAndDeleted(int invitationAdminId, int eventId,
			boolean deleted);

	List<Pass> findByImsUserByInvitationAdminIdIdAndEventIdAndCreatedTimestampBetweenAndDeleted(int invitationAdminId,
			int eventId, LocalDateTime localDateTime, LocalDateTime localDateTime2, boolean deleted);
	
	List<Pass> findByEventIdAndDeletedAndDepartmentIdIn(int eventId, boolean b, List<Integer> departments);

	List<Pass> findAllByEventIdAndDeleted(int eventId, boolean deleted);

	@Query("select DISTINCT(p.enclosure) from Pass p where p.department.id = :departmentId")
	List<Enclosure> findAllEnclosureForDepartment(@Param("departmentId") int departmentId);

	List<Pass> findByEventIdAndPassCategoryIdAndEnclosureIdAndDeleted(int eventId, int type, int enclosureId,
			boolean b);

	List<Pass> findAllByEventIdAndDepartmentIdAndDeleted(int eventId, int departmentId, boolean deleted);

	List<Pass> findAllByPassStatusIdAndEventId(int type, int eventId);

	Pass findByPaidPassHoldersIdAndDeleted(int id, boolean b);

	Pass findByImsUserByImsUserIdIdAndEventIdAndDeleted(int imsUserId, int eventId, boolean deleted);
	
	List<Pass> findAllByImsUserByImsUserIdIdAndEventIdAndDeleted(int imsUserId, int eventId, boolean deleted);

	List<Pass> findByPassCategoryIdAndPassSubcategoryIdAndEventIdAndCreatedTimestampBetweenAndEnclosureEnclosureGroupIdAndImsUserByImsUserIdRoleIdAndPassStatusIdNotAndDeleted(
			int type, int type2, int eventId, LocalDateTime fromDateTime, LocalDateTime toDateTime, int enclGroupId,
			int role, int passStatusId, boolean b);

	List<Pass> findAllByImsUserByImsUserIdIdAndEventIdAndPassStatusIdAndDeleted(int imsUserId, int eventId, int type,
			boolean b);

	List<Pass> findByImsUserByImsUserIdMobileNo(String mobileNo);

	List<Pass> findAllByEventIdAndPassCategoryIdAndPassStatusIdNotAndImsUserByImsUserIdRoleIdAndCreatedTimestampBetween(
			int eventId, int type, int type2, int roleId, LocalDateTime atStartOfDay, LocalDateTime atTime);

	List<Pass> findByImsUserByImsUserIdMobileNoAndPassStatusId(String mobileNo, int passStatus);

	List<Pass> findByEventIdAndEnclosureIdAndPassCategoryIdAndPassSubcategoryIdAndDeleted(int eventId, int enclosureId,
			int type, int subcategoryId, boolean b);

	@Query(value = "Select p.id, p.annex_users_id, p.car_pass_id, p.control_salt, p.created_by, p.downloaded, p.created_timestamp, p.deleted, p.department_id, p.ims_user_id, p.invitation_admin_id, p.modified_by,	p.modified_timestamp, p.paid_pass_holders_id, p.pass_subcategory_id,"
			+ "(select \"name\" from tdms.annex_users au where p.annex_users_id notnull and p.annex_users_id=au.id union all select \"name\" from tdms.paid_pass_holders pph where p.paid_pass_holders_id notnull and p.paid_pass_holders_id=pph.id) as pass_holder_name, p.control_no, p.control_hash, "
			+ "(select dob from tdms.annex_users au where p.annex_users_id notnull and p.annex_users_id=au.id union all select dob from tdms.paid_pass_holders pph where p.paid_pass_holders_id notnull and p.paid_pass_holders_id=pph.id) as dob, e.id event_id, e.name event_name, en.id as enclosure_id, en.name as enclosure_name, ps.id as pass_status_id, ps.status as pass_status_name, pc.id as pass_category_id, pc.name as pass_category_name, psc.id as pass_sub_category_id, psc.name as pass_sub_category_name, null as modified_timestamp, p.remarks, "
			+ "(SELECT CASE WHEN (p.annex_users_id notnull) THEN (select salutation_id from tdms.annex_users au where p.annex_users_id = au.id) ELSE "
			+ "(select salutation_id from mdms.ims_user iu where p.ims_user_id notnull and p.ims_user_id=iu.id) END) AS salutation_id, "
			+ "(SELECT CASE WHEN (p.annex_users_id notnull) THEN (select \"name\" from system.salutation s where s.id in (select salutation_id from tdms.annex_users au where p.annex_users_id = au.id)) ELSE (select \"name\" from system.salutation s where s.id in (select salutation_id from mdms.ims_user iu where p.ims_user_id notnull and p.ims_user_id=iu.id)) END) AS salutation_name, "
			+ "(SELECT CASE WHEN (p.annex_users_id notnull) THEN (select marital_status_id from tdms.annex_users au where p.annex_users_id = au.id) ELSE (select marital_status from mdms.ims_user iu where p.ims_user_id notnull and p.ims_user_id=iu.id) END) AS marital_status_id, "
			+ "(SELECT CASE WHEN (p.annex_users_id notnull) THEN (select status from system.marital_status ms where ms.id in (select marital_status_id from tdms.annex_users au where p.annex_users_id = au.id)) ELSE (select status from system.marital_status ms where ms.id in (select marital_status from mdms.ims_user iu where p.ims_user_id notnull and p.ims_user_id=iu.id)) END) AS marital_status_name\r\n"
			+ "From\r\n"
			+ "    tdms.pass as p\r\n"
			+ "    LEFT JOIN mdms.enclosure as en ON p.enclosure_id = en.id \r\n"
			+ "    LEFT JOIN system.pass_status as ps ON p.pass_status_id = ps.id\r\n"
			+ "	LEFT JOIN system.pass_category as pc ON p.pass_category_id = pc.id\r\n"
			+ "	LEFT JOIN system.pass_subcategory as psc ON p.pass_subcategory_id = psc.id\r\n"
			+ "	LEFT JOIN tdms.event as e ON p.event_id = e.id where p.deleted = false ", nativeQuery = true)
	List<Pass> getAllPassesByEventAndEnclosure();

	List<Pass> findByPassCategoryIdAndEventIdAndDepartmentOrganizationIdAndDeleted(int categoryId, int eventId,
			int organizationId, boolean deleted);

	List<Pass> findByPassCategoryIdAndEventIdAndDepartmentIdAndDeleted(int categoryId, int eventId, int departmentId,
			boolean deleted);

	List<Pass> findAllByEnclosureIdAndDeleted(int id, boolean deleted);

	List<Pass> findAllByCarPassCplIdAndDeleted(int id, boolean deleted);

	@Query(value = "select TO_CHAR(pass.created_timestamp, 'yyyy-MM-dd') as passDate, \r\n"
			+ "	CASE WHEN userRole.id = 8 THEN 'Online' ELSE 'Offline' END AS paymentType,\r\n"
			+ "	pt.mode, subcat.name as ticketType, count(pt.pass_id) from tdms.pg_transactions pt\r\n"
			+ "	join tdms.pass as pass on pass.id = pt.pass_id\r\n"
			+ "	join tdms.event as event on event.id = pass.event_id\r\n"
			+ "	join mdms.ims_user as userData on userData.id = pass.ims_user_id\r\n"
			+ "	join system.role as userRole on userRole.id = userData.role_id\r\n"
			+ "	join system.pass_subcategory as subcat on subcat.id = pass.pass_subcategory_id\r\n"
			+ "where event.id = :eventId and\r\n"
			+ "pass.deleted = :deleted and \r\n"
			+ "pass.pass_status_id = 2 and\r\n"
			+ "userRole.id in :roles and\r\n"
			+ "subcat.id in :subCats and\r\n"
			+ "pass.created_timestamp between :startDate and :endDate\r\n"
			+ "group by passDate, pt.mode, ticketType, userRole.name, paymentType", nativeQuery = true)
	List<Tuple> getAllPassStatsData(@Param("eventId") int eventId, @Param("roles") List<Integer> roles, @Param("subCats") List<Integer> subCategory, @Param("startDate")LocalDateTime startDate, @Param("endDate")LocalDateTime endDate, @Param("deleted") boolean deleted);

	List<Pass> findByEventIdAndEnclosureIdAndPassCategoryIdAndDeleted(int eventId, int enclosureId, int type,
			boolean b);

	Pass findByPaidPassHoldersIdAndDeletedAndImsUserByImsUserIdId(int id, boolean b, int id2);

	Pass findByPaidPassHoldersIdAndPassStatusIdAndEventIdAndDeleted(int id, int type, int eventId, boolean b);

	Pass findByIdAndImsUserByImsUserIdRoleId(int id, int roleId);

	List<Pass> findByImsUserByImsUserIdIdAndPassStatusIdNotAndCreatedTimestampBetweenAndAndDeleted(Sort sort, int id, int type,
			LocalDateTime fromDateTime, LocalDateTime toDateTime, boolean deleted);

	List<Pass> findAllByEventIdAndPassCategoryIdAndPassStatusIdAndImsUserByImsUserIdRoleIdAndCreatedTimestampBetween(
			int eventId, int type, int type2, int roleId, LocalDateTime atStartOfDay, LocalDateTime atTime);

	List<Pass> findByPassCategoryIdAndPassSubcategoryIdAndEventIdAndCreatedTimestampBetweenAndImsUserByImsUserIdRoleIdAndPassStatusIdNotAndDeleted(
			int type, int id, int eventId, LocalDateTime fromDateTime, LocalDateTime toDateTime, int roleId, int type2,
			boolean b);

	@Query(value = "select count(*) from tdms.pass where  created_timestamp between :fromDateTime and :toDateTime and  event_id = :eventId and pass_subcategory_id = :passSubcategoryId and pass_status_id != :passStatus and deleted = false", nativeQuery = true)
	int getCountOfCurrentDateAndEventIdAndPassSubCategoryAndPassStatusNotAndDeleted(@Param("fromDateTime") LocalDateTime fromDateTime,
			@Param("toDateTime") LocalDateTime toDateTime, @Param("eventId") int eventId, @Param("passSubcategoryId") int passSubcategoryId, @Param("passStatus") int passStatus);

	List<Pass> findAllByEnclosureIdAndEventIdAndDeleted(int enclId, int eventId, boolean deleted);

	List<Pass> findAllByEnclosureIdAndEventIdAndPassStatusIdNotAndDeleted(int enclId, int eventId, int type, boolean deleted);
}
