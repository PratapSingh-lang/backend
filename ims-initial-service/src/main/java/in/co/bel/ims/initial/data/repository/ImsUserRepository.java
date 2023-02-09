package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface ImsUserRepository extends ImsJPATemplate<ImsUser> {
	
	public ImsUser getByMobileNoAndDeleted(String mobileNo, boolean isDeleted);
	public List<ImsUser> getByUserTypeIdAndDeleted(int userType, boolean deleted);
	public List<ImsUser> getByRoleIdAndDeleted(int role, boolean deleted);
	public List<ImsUser> findByDepartmentIdAndDeleted(int departmentId, boolean deleted);
	public List<ImsUser> findByDepartmentOrganizationIdAndDeleted(int organizationId, boolean deleted);
	public List<ImsUser> findByDepartmentOrganizationOrganizationGroupIdAndDeleted(int organizationGroupId, boolean b);
	public List<ImsUser> findAllByRoleIdAndDepartmentId(int role, int departmentId);
	public List<ImsUser> findByPrecedenceIdAndDeleted(int precedenceId, boolean deleted);
	public ImsUser findByIdAndDeleted(int userId, boolean deleted);
	public List<ImsUser> findByUserTypeIdAndDepartmentIdAndDeleted(int type, int departmentId, boolean deleted);
	public List<ImsUser> findByRoleIdAndDepartmentIdAndDeleted(int role, int departmentId, boolean deleted);
	public List<ImsUser> findByDepartmentIdAndUserTypeIdAndDeleted(int departmentId, int type, boolean deleted);
	public List<ImsUser> findByDepartmentOrganizationOrganizationGroupIdAndUserTypeIdNotAndDeleted(
			int organizationGroupId, int type, boolean b);
	public List<ImsUser> getByEmpNoAndDeleted(String empId, boolean b);
	public List<ImsUser> getByIdProofNoAndDeleted(String govtIdNumber, boolean b);
	public List<ImsUser> getByMobileNoInAndDeleted(List<String> uniqueUsersMobiles, boolean b);
	public List<ImsUser> getByEmpNoInAndDeleted(List<String> empIdList, boolean b);
	public List<ImsUser> getByIdProofNoInAndDeleted(List<String> govtIdListFromRequest, boolean b);
	public List<ImsUser> findByDeletedAndDepartmentIdIn(boolean b, List<Integer> departments);
	public ImsUser findByMobileNoAndDeleted(String mobileNo, boolean b);
	public ImsUser findByMobileNo(String mobileNo);
	public List<ImsUser> getByEmailAndDeleted(String emailId, boolean b);
}

