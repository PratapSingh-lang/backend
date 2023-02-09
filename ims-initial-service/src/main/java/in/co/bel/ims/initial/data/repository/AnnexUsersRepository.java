package in.co.bel.ims.initial.data.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import in.co.bel.ims.initial.entity.AnnexUsers;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface AnnexUsersRepository extends ImsJPATemplate<AnnexUsers> {
	public AnnexUsers findByIdAndDeleted(int userId, boolean deleted);
	public List<AnnexUsers> findByDepartmentIdInAndUserTypeId(List<Integer> departments, int type);
	public List<AnnexUsers> findByUserTypeId(int type);
	public List<AnnexUsers> findByDepartmentId(int departmentId);
	public List<AnnexUsers> findByUserTypeIdAndDeleted(int userTypeId, boolean b);
	public  List<AnnexUsers> findByMobileNoAndDeleted(String mobileNo,boolean b);
	public List<AnnexUsers> findByDeleted(boolean b);
	public List<AnnexUsers> getByUserTypeIdAndDeleted(int type, boolean b);
	public List<AnnexUsers> findByPrecedenceIdAndDeleted(int precedenceId, boolean b);
	public List<AnnexUsers> findByDepartmentIdAndDeleted(int id, boolean b);
	public List<AnnexUsers> findByDepartmentOrganizationOrganizationGroupIdAndDeleted(int organizationGroupId,
			boolean b);
	public List<AnnexUsers> findByDeletedAndDepartmentIdIn(boolean b, List<Integer> departments);
	public List<AnnexUsers> findByDepartmentIdAndDeletedAndUserTypeIdIsNot(int departmentId, boolean b, int type);
	public List<AnnexUsers> findByDepartmentIdInAndUserTypeIdAndDeleted(List<Integer> departments, int userTypeId,
			boolean b);
	public List<AnnexUsers> findByUserTypeIdAndDeletedAndDepartmentIdIn(int userTypeId, boolean b,
			List<Integer> departments);
	@Query(value = "SELECT * FROM tdms.annex_users WHERE (:name IS NULL OR name iLIKE concat('%', :name, '%')) AND (:mobileNo IS NULL OR mobile_no iLIKE concat('%', :mobileNo, '%')) AND (:email IS NULL OR email iLIKE concat('%', :email, '%')) AND (:empId IS NULL OR emp_no iLIKE concat('%', :empId, '%')) AND (:designation IS NULL OR designation iLIKE concat('%', :designation, '%')) AND (:payLevel IS NULL OR ((select name from system.pay_level where id=tdms.annex_users.pay_level_id) iLIKE concat('%', :payLevel, '%'))) AND (:department IS NULL OR ((select name from mdms.department where id=tdms.annex_users.department_id) iLIKE concat('%', :department, '%'))) AND (user_type_id = :userTypeId OR user_type_id = 9) AND deleted = false", nativeQuery = true)
	public List<AnnexUsers> getAnnexUsersListByUserType(int userTypeId, String name, String mobileNo, String email,
			String empId, String designation, String payLevel, String department, Pageable firstPageWithTwoElements);

	@Query(value = "SELECT * FROM tdms.annex_users WHERE (:name IS NULL OR name iLIKE concat('%', :name, '%')) AND (:mobileNo IS NULL OR mobile_no iLIKE concat('%', :mobileNo, '%')) AND (:email IS NULL OR email iLIKE concat('%', :email, '%')) AND (:empId IS NULL OR emp_no iLIKE concat('%', :empId, '%')) AND (:designation IS NULL OR designation iLIKE concat('%', :designation, '%')) AND (:payLevel IS NULL OR ((select name from system.pay_level where id=tdms.annex_users.pay_level_id) iLIKE concat('%', :payLevel, '%'))) AND (:department IS NULL OR ((select name from mdms.department where id=tdms.annex_users.department_id) iLIKE concat('%', :department, '%'))) AND (user_type_id = :userTypeId OR user_type_id = 9) AND deleted = false", nativeQuery = true)
	public List<AnnexUsers> getAnnexUsersListByUserType(int userTypeId, String name, String mobileNo, String email,
			String empId, String designation, String payLevel, String department);
}

