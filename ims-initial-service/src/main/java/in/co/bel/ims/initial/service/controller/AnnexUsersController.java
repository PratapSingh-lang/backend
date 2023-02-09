package in.co.bel.ims.initial.service.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.AnnexUsersRepository;
import in.co.bel.ims.initial.data.repository.DepartmentRepository;
import in.co.bel.ims.initial.data.repository.NodalOfficerRepository;
import in.co.bel.ims.initial.data.repository.OrganizationRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.entity.AnnexUsers;
import in.co.bel.ims.initial.entity.Department;
import in.co.bel.ims.initial.entity.NodalOfficer;
import in.co.bel.ims.initial.entity.Organization;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.dto.PaginationResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.ImsUserTypeEnum;

@RestController
@CrossOrigin
@RequestMapping("/app/annexUsers")
public class AnnexUsersController extends ImsServiceTemplate<AnnexUsers, AnnexUsersRepository> {

	@Autowired
	private AnnexUsersRepository annexUsersRepository;
	@Autowired
	private NodalOfficerRepository nodalOfficerRepository;
	@Autowired
	private PassRepository passRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@GetMapping("/getAllUserByUserType/{userTypeId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllUserByUserType(@PathVariable int userTypeId) {
		List<AnnexUsers> annexUsersList = annexUsersRepository.findByUserTypeIdAndDeleted(userTypeId, false);

		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(annexUsersList);
		imsResponse.setMessage("Data retrieved Successfully!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	@GetMapping("/getAllUserByUserTypeAndOrganzationGroup/{userTypeId}/{orgGrpId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllUserByUserType(@PathVariable int userTypeId, @PathVariable int orgGrpId) {
		List<AnnexUsers> annexUsersList = annexUsersRepository.findByUserTypeIdAndDeleted(userTypeId, false);
		List<Organization> organizations = organizationRepository.findByOrganizationGroupIdAndDeleted(orgGrpId, false);
		List<Integer> departments = new ArrayList<Integer>();
		for(Organization organization:organizations) {
			List<Department> depList = departmentRepository.findByOrganizationIdAndDeleted(organization.getId(), false);
			if(depList != null) {
				departments.addAll(depList.stream().map(item -> item.getId())
						.collect(Collectors.toList()));
			}
		}
		List<AnnexUsers> annexUsers = annexUsersRepository.findByDeletedAndDepartmentIdIn(false, departments);
		annexUsers = annexUsers.stream().filter(item-> item.getUserType() != null
				&& item.getUserType().getId() == ImsUserTypeEnum.Organizer.type).collect(Collectors.toList());
		annexUsersList.addAll(annexUsers);
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(annexUsersList);
		imsResponse.setMessage("Data retrieved Successfully!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	@GetMapping("/getAllUserByUserTypeAndOrganzationAndDepartment/{userTypeId}/{orgId}/{deptId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllUserByUserType(@PathVariable int userTypeId, @PathVariable int orgId,  @PathVariable int deptId) {
		List<Integer> departments = new ArrayList<Integer>();
		if(deptId == -1) {
			List<Department> depList = departmentRepository.findByOrganizationIdAndDeleted(orgId, false);
			if(depList != null) {
				departments.addAll(depList.stream().map(item -> item.getId())
						.collect(Collectors.toList()));
			}
		}else {
			departments.add(deptId);
		}
		List<AnnexUsers> annexUsersList = annexUsersRepository.findByUserTypeIdAndDeletedAndDepartmentIdIn(userTypeId, false, departments);
		List<AnnexUsers> annexUsers = annexUsersRepository.findByDeletedAndDepartmentIdIn(false, departments);
		annexUsers = annexUsers.stream().filter(item-> item.getUserType() != null
				&& item.getUserType().getId() == ImsUserTypeEnum.Organizer.type).collect(Collectors.toList());
		annexUsersList.addAll(annexUsers);
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(annexUsersList);
		imsResponse.setMessage("Data retrieved Successfully!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	@GetMapping("/getAllAnnexUsersByUserType/{userTypeId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllUserByUserType(@PathVariable int userTypeId, @RequestParam(required = true) int pageNo, 
			@RequestParam(required = true) int pageSize,  @RequestParam(required = false) String sortingLabel, @RequestParam(required = false) String sortingDirection,@RequestParam(required = false) String name,  @RequestParam(required = false) String department, @RequestParam(required = false) String designation,
			@RequestParam(required = false) String payLevel,  @RequestParam(required = false) String mobileNo, @RequestParam(required = false) String email, @RequestParam(required = false) String empId) {
		Pageable firstPageWithTwoElements = null;
		PaginationResponse paginationResponse = new PaginationResponse();
		if(sortingDirection != null && sortingDirection.equals("ASC")) {
			firstPageWithTwoElements = PageRequest.of(pageNo, pageSize, Sort.by(sortingLabel).and(Sort.by("id")));
		}else if(sortingDirection != null && sortingDirection.equals("DESC")) {
			firstPageWithTwoElements = PageRequest.of(pageNo, pageSize, Sort.by(sortingLabel).descending().and(Sort.by("id")));
		}else {
			firstPageWithTwoElements = PageRequest.of(pageNo, pageSize, Sort.by("id"));
		}
		List<AnnexUsers> annexUsersList = annexUsersRepository.getAnnexUsersListByUserType(userTypeId,name,mobileNo,email,empId,designation,payLevel,department,firstPageWithTwoElements);
		List<AnnexUsers> allAnnexUsersList = annexUsersRepository.getAnnexUsersListByUserType(userTypeId,name,mobileNo,email,empId,designation,payLevel,department);
		paginationResponse.setRecordsList(annexUsersList);
		paginationResponse.setTotalRecords(allAnnexUsersList.size());
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(paginationResponse);
		imsResponse.setMessage("Data retrieved Successfully!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getAllWithNoPass/{eventId}/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllUsersByDepartmentId(@PathVariable("eventId") int eventId,
			@PathVariable("departmentId") int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passListByEventAndDepartment = passRepository.findAllByEventIdAndDepartmentIdAndDeleted(eventId,
				departmentId, false);
		List<Integer> annexUserIds = Optional.ofNullable(passListByEventAndDepartment)
	            .orElseGet(Collections::emptyList).stream()
				.filter(pass -> pass.getAnnexUsers() != null)
				.map(pass -> pass.getAnnexUsers().getId())
				.collect(Collectors.toList());

		List<AnnexUsers> annexUsersWithoutPass = new ArrayList<>();
		if(annexUserIds != null) {
			List<AnnexUsers> annexUsersList = annexUsersRepository.findByDepartmentIdAndDeleted(departmentId, false);
			annexUsersWithoutPass = Optional.ofNullable(annexUsersList)
		            .orElseGet(Collections::emptyList).stream()
		            .filter(annexUser -> annexUser.getUserType() != null)
					.filter(annexUser -> !annexUserIds.contains(annexUser.getId())
							&& (annexUser.getUserType().getId() != ImsUserTypeEnum.Annexure_D.type
									&& annexUser.getUserType().getId() != ImsUserTypeEnum.WoPUser.type
									&& annexUser.getUserType().getId() != ImsUserTypeEnum.Organizer.type))
					.collect(Collectors.toList());
		}

		imsResponse.setData(annexUsersWithoutPass);
		imsResponse.setMessage("Data retrieved Successfully!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getAllWithNoGuestPass/{eventId}/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllGuestUsersByDepartmentId(@PathVariable("eventId") int eventId,
			@PathVariable("departmentId") int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passListByEventAndDepartment = passRepository.findAllByEventIdAndDepartmentIdAndDeleted(eventId,
				departmentId, false);
		List<Integer> annexUserIds = Optional.ofNullable(passListByEventAndDepartment)
	            .orElseGet(Collections::emptyList).stream()
				.filter(pass -> pass.getAnnexUsers() != null)
				.map(pass -> pass.getAnnexUsers().getId())
				.collect(Collectors.toList());
		List<AnnexUsers> annexUsersWithoutPass = new ArrayList<>();
		if(annexUserIds != null) {
			List<AnnexUsers> annexUsersList = annexUsersRepository.findByDepartmentIdAndDeleted(departmentId, false);
			annexUsersWithoutPass = Optional.ofNullable(annexUsersList)
		            .orElseGet(Collections::emptyList).stream()
		            .filter(annexUser -> annexUser.getUserType() != null)
					.filter(annexUser -> !annexUserIds.contains(annexUser.getId())
							&& annexUser.getUserType().getId() == ImsUserTypeEnum.Annexure_D.type)
					.collect(Collectors.toList());
		}

		imsResponse.setData(annexUsersWithoutPass);
		imsResponse.setMessage("Data retrieved Successfully!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getAllByNodalOfficerId/{nodalOfficerId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllUsersByNodalOfficerId(@PathVariable("nodalOfficerId") int nodalOfficerId) {
		ImsResponse imsResponse = new ImsResponse();
		List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByImsUserId(nodalOfficerId);
		List<Integer> departments = nodalOfficers.stream().map(nodalOfficer -> nodalOfficer.getDepartment().getId())
				.collect(Collectors.toList());

		List<AnnexUsers> annexUsersList = annexUsersRepository.findByDepartmentIdInAndUserTypeIdAndDeleted(departments,
				ImsUserTypeEnum.Annexure_D.type, false);

		imsResponse.setData(annexUsersList);
		imsResponse.setMessage("Data retrieved Successfully!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	@GetMapping("/getAllByNodalOfficerIdAndUserType/{nodalOfficerId}/{userTypeId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllUsersByNodalOfficerId(@PathVariable("nodalOfficerId") int nodalOfficerId, @PathVariable("userTypeId") int userTypeId) {
		ImsResponse imsResponse = new ImsResponse();
		List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByImsUserId(nodalOfficerId);
		List<Integer> departments = nodalOfficers.stream().map(nodalOfficer -> nodalOfficer.getDepartment().getId())
				.collect(Collectors.toList());

		List<AnnexUsers> annexUsersList = annexUsersRepository.findByDepartmentIdInAndUserTypeIdAndDeleted(departments,
				userTypeId, false);
		List<AnnexUsers> organizerAnnexUsersList = annexUsersRepository.findByDepartmentIdInAndUserTypeIdAndDeleted(departments,
				ImsUserTypeEnum.Organizer.type, false);
		annexUsersList.addAll(organizerAnnexUsersList);
		imsResponse.setData(annexUsersList);
		imsResponse.setMessage("Data retrieved Successfully!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
}
