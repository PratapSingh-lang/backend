package in.co.bel.ims.initial.service.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.AnnexUsersRepository;
import in.co.bel.ims.initial.data.repository.CarPassRepository;
import in.co.bel.ims.initial.data.repository.CplRepository;
import in.co.bel.ims.initial.data.repository.DepartmentRepository;
import in.co.bel.ims.initial.data.repository.EnclosureRepository;
import in.co.bel.ims.initial.data.repository.EventRepository;
import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.NodalOfficerRepository;
import in.co.bel.ims.initial.data.repository.OrganizationRepository;
import in.co.bel.ims.initial.data.repository.PaidPassHoldersRepository;
import in.co.bel.ims.initial.data.repository.PassCategoryRepository;
import in.co.bel.ims.initial.data.repository.PassDayLimitCategoryRepository;
import in.co.bel.ims.initial.data.repository.PassDayLimitRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.data.repository.PassSubcategoryRepository;
import in.co.bel.ims.initial.data.repository.UserTypeRepository;
import in.co.bel.ims.initial.entity.AnnexUsers;
import in.co.bel.ims.initial.entity.CarPass;
import in.co.bel.ims.initial.entity.Cpl;
import in.co.bel.ims.initial.entity.Department;
import in.co.bel.ims.initial.entity.Enclosure;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.NodalOfficer;
import in.co.bel.ims.initial.entity.Organization;
import in.co.bel.ims.initial.entity.PaidPassHolders;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.entity.PassCategory;
import in.co.bel.ims.initial.entity.PassDayLimit;
import in.co.bel.ims.initial.entity.PassDayLimitCategory;
import in.co.bel.ims.initial.entity.PassSubcategory;
import in.co.bel.ims.initial.entity.UserType;
import in.co.bel.ims.initial.service.dto.DashboardResponse;
import in.co.bel.ims.initial.service.dto.DownloadPassCategoryDto;
import in.co.bel.ims.initial.service.dto.DownloadPassSubcategoryDto;
import in.co.bel.ims.initial.service.dto.DownloadResponse;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.dto.PassCategoryDto;
import in.co.bel.ims.initial.service.dto.PassSubcategoryDto;
import in.co.bel.ims.initial.service.dto.TicketSummary;
import in.co.bel.ims.initial.service.dto.TicketSummaryRequest;
import in.co.bel.ims.initial.service.util.ImsUserTypeEnum;
import in.co.bel.ims.initial.service.util.PassCategoryEnum;
import in.co.bel.ims.initial.service.util.PassStatusEnum;

@RestController
@CrossOrigin
@RequestMapping("/app/dashboard")
public class DashboardController {
	@Autowired
	PassRepository passRepository;
	
	@Autowired
	PassDayLimitRepository passDayLimitRepository;
	
	@Autowired
	PassDayLimitCategoryRepository passDayLimitCategoryRepository;
	
	@Autowired
	CarPassRepository carPassRepository;
	
	@Autowired
	CplRepository cplRepository;
	
	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	OrganizationRepository organizationRepository;
	
	@Autowired
	DepartmentRepository departmentRepository;
	
	@Autowired
	EnclosureRepository enclosureRepository;
	
	@Autowired
	UserTypeRepository userTypeRepository;

	@Autowired
	PassCategoryRepository passCategoryRepository;
	
	@Autowired
	ImsUserRepository imsUserRepository;

	@Autowired
	PassSubcategoryRepository passSubcategoryRepository;
	
	@Autowired
	PaidPassHoldersRepository paidPassHoldersRepository;
	
	@Autowired
	NodalOfficerRepository nodalOfficerRepository;
	
	@Autowired
	AnnexUsersRepository annexUsersRepository;


	@GetMapping("/overAll/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('HIGHEROFFICER') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public DashboardResponse overAllPassSummary(@PathVariable int eventId) {
		List<Pass> passList = passRepository.findByEventIdAndDeleted(eventId, false);
		DashboardResponse dashboardResponse =  setPassSummary(passList, eventId);
		Event event = eventRepository.findByIdAndDeleted(eventId, false);
		dashboardResponse.setTotalCapacity(event.getVenue().getMaxCapacity());
		passList = passList.stream().filter(pass -> pass.getPassStatus() != null
				&& pass.getPassStatus().getId() == PassStatusEnum.CANCELLED.type).collect(Collectors.toList());
		dashboardResponse.setTotalCancelled(passList.size());
		return dashboardResponse;
	}
	
	@GetMapping("/passSummaryByOrganization/{eventId}/{organizationGroupId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public HashMap<Integer, DashboardResponse> passSummaryOrganizationWise(@PathVariable int eventId, @PathVariable int organizationGroupId) {
		HashMap<Integer,DashboardResponse> organizationWiseStats = new HashMap<>();
		List<Organization> organizations = organizationRepository.findByOrganizationGroupIdAndDeleted(organizationGroupId,false);
		for(Organization organization : organizations) {
			DashboardResponse passSummary = new DashboardResponse(); 
			passSummary = passStatisticsEventAndOrganizationWise(eventId, organization.getId());
			organizationWiseStats.put(organization.getId(), passSummary);
		}
		return organizationWiseStats;
	}
	
	@GetMapping("/passSummaryByDepartment/{eventId}/{organizationId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public HashMap<Integer, DashboardResponse> passSummaryDepartmentWise(@PathVariable int eventId, @PathVariable int organizationId) {
		HashMap<Integer,DashboardResponse> departmentWiseStats = new HashMap<>();
		List<Department> departments = departmentRepository.findByOrganizationIdAndDeleted(organizationId, false);
		for(Department department : departments) {
			DashboardResponse passSummary = new DashboardResponse(); 
			passSummary = passStatisticsEventAndDepartmentWise(eventId, department.getId());
			departmentWiseStats.put(department.getId(), passSummary);
		}
		return departmentWiseStats;
	}
	
	@GetMapping("/passSummaryByEnclosure/{eventId}/{enclosureGroupId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public HashMap<Integer, DashboardResponse> passSummaryEnclosureWise(@PathVariable int eventId, @PathVariable int enclosureGroupId) {
		HashMap<Integer,DashboardResponse> enclosureWiseStats = new HashMap<>();
		List<Enclosure> enclosures = enclosureRepository.findByEnclosureGroupIdAndDeleted(enclosureGroupId, false);
		for(Enclosure enclosure : enclosures) {
			DashboardResponse passSummary = new DashboardResponse(); 
			passSummary = passStatisticsEventAndEnclosureWise(eventId, enclosure.getId());
			enclosureWiseStats.put(enclosure.getId(), passSummary);
		}
		return enclosureWiseStats;
	}
	
	@GetMapping("/passSummaryByUserType/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public HashMap<Integer, DashboardResponse> passSummaryUserTypeWise(@PathVariable int eventId) {
		HashMap<Integer,DashboardResponse> userTypeWiseStats = new HashMap<>();
		List<UserType> userTypes = userTypeRepository.findAllByDeleted(false);
		for(UserType userType : userTypes) {
			DashboardResponse passSummary = new DashboardResponse(); 
			passSummary = passStatisticsEventAndUserTypeWise(eventId, userType.getId());
			userTypeWiseStats.put(userType.getId(), passSummary);
		}
		return userTypeWiseStats;
	}
	
	@GetMapping("/passSummaryByPassCategory/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public HashMap<Integer, DashboardResponse> passSummaryPassCategoryWise(@PathVariable int eventId) {
		HashMap<Integer,DashboardResponse> passCategoryWiseStats = new HashMap<>();
		List<PassCategory> passCategories = passCategoryRepository.findAllByDeleted(false);
		for(PassCategory passCategory : passCategories) {
			DashboardResponse passSummary = new DashboardResponse(); 
			passSummary = passStatisticsEventAndPassCategoryWise(eventId, passCategory.getId());
			passCategoryWiseStats.put(passCategory.getId(), passSummary);
		}
		return passCategoryWiseStats;
	}
	
	@GetMapping("/passSummaryByPassSubcategory/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public HashMap<Integer, DashboardResponse> passSummaryPassSubcategoryWise(@PathVariable int eventId) {
		HashMap<Integer,DashboardResponse> passSubcategoryWiseStats = new HashMap<>();
		List<PassSubcategory>  passSubcategories = passSubcategoryRepository.findAllByDeleted(false);
		for(PassSubcategory  passSubcategory :  passSubcategories) {
			DashboardResponse passSummary = new DashboardResponse(); 
			passSummary = passStatisticsEventAndPassSubcategoryWise(eventId, passSubcategory.getId());
			 passSubcategoryWiseStats.put(passSubcategory.getId(), passSummary);
		}
		return  passSubcategoryWiseStats;
	}
	
	@GetMapping("/passSummaryByEventAndCpl/{eventId}/{cplId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public DashboardResponse passSummaryByEventAndCpl(@PathVariable int eventId, @PathVariable int cplId) {
		List<CarPass> carPasses = carPassRepository.findByEventIdAndDeletedAndCplId(eventId, false, cplId);
		DashboardResponse dashboardResponse = new DashboardResponse();
		Cpl cpl = cplRepository.findByIdAndDeleted(cplId, false); 
		if(cpl != null)
			dashboardResponse.setTotalPasses(cpl.getMaxCapacity());
		long totalAllocated = carPasses.stream().filter(pass -> pass.getPassStatus() != null
				&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type).collect(Collectors.toList()).size();
		dashboardResponse.setTotalAllocated(totalAllocated);
		return  dashboardResponse;
	}
	
	
	@GetMapping("/passCplSummaryByNodalOfficer/{eventId}/{cplId}/{nodalOfficerId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public DashboardResponse passCplSummaryByNodalOfficer(@PathVariable int eventId, @PathVariable int cplId, @PathVariable int nodalOfficerId) {
		List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByImsUserId(nodalOfficerId);
		List<Integer> departments = nodalOfficers.stream().map(nodalOfficer -> nodalOfficer.getDepartment().getId())
				.collect(Collectors.toList());
		List<CarPass> carPasses = carPassRepository.findByEventIdAndDeletedAndCplIdAndDepartmentIdIn(eventId, false, cplId, departments);
		DashboardResponse dashboardResponse = new DashboardResponse();
		Cpl cpl = cplRepository.findByIdAndDeleted(cplId, false); 
		if(cpl != null)
			dashboardResponse.setTotalPasses(cpl.getMaxCapacity());
		long totalAllocated = carPasses.stream().filter(pass -> pass.getPassStatus() != null
				&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type).collect(Collectors.toList()).size();
		dashboardResponse.setTotalAllocated(totalAllocated);
		return  dashboardResponse;
	}
	
	@GetMapping("/passSummaryByEventAndOrganization/{eventId}/{organizationId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public DashboardResponse passSummaryEventAndOrganizationWise(@PathVariable int eventId, @PathVariable int organizationId) {
		DashboardResponse dashboardResponse =  passStatisticsEventAndOrganizationWise( eventId, organizationId);
		long totalUsersUploaded = 0;
		List<Department> departments = departmentRepository.findByOrganizationIdAndDeleted(organizationId, false);
		for(Department department : departments) {
			List<AnnexUsers> annexUsers = annexUsersRepository.findByDepartmentIdAndDeleted(department.getId(), false);
			totalUsersUploaded = totalUsersUploaded + annexUsers.size();
		}
		dashboardResponse.setTotalUsersUploaded(totalUsersUploaded);
		return dashboardResponse;
	}
	
	@GetMapping("/passSummaryByEventAndOrganizationGroupId/{eventId}/{organizationGroupId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public DashboardResponse passSummaryOrganizatioGroupWise(@PathVariable int eventId, @PathVariable int organizationGroupId) {
		DashboardResponse result = new DashboardResponse();
		List<Organization> organizations = organizationRepository.findByOrganizationGroupIdAndDeleted(organizationGroupId,false);
	    long totalPasses = eventRepository.findById(eventId).get().getTotalInvitations();
		long totalIssued = 0;
		long totalAllocated = 0;
		long totalAvailable = 0;
		long totalAccepted = 0;
		long totalRejected = 0;
		long totalAttended = 0;
		long totalDownloads = 0;
		long totalUsersUploaded = 0;
		
		for(Organization organization : organizations) {
			List<Department> departments = departmentRepository.findByOrganizationIdAndDeleted(organization.getId(), false);
			for(Department department : departments) {
				List<AnnexUsers> annexUsers = annexUsersRepository.findByDepartmentIdAndDeleted(department.getId(), false);
				totalUsersUploaded = totalUsersUploaded + annexUsers.size();
			}
			DashboardResponse passSummary = new DashboardResponse(); 
			passSummary = passStatisticsEventAndOrganizationWise(eventId, organization.getId());
			totalIssued = totalIssued + passSummary.getTotalIssued();
			totalAccepted = totalAccepted + passSummary.getTotalAccepted();
			totalRejected = totalRejected + passSummary.getTotalRejected();
			totalAttended = totalAttended + passSummary.getTotalAttended();
			totalAllocated = totalAllocated + passSummary.getTotalAllocated();
			totalDownloads = totalDownloads + passSummary.getTotalDownloads();
		}
		totalAllocated = totalAllocated + totalAccepted + totalRejected + totalAttended;
		totalAvailable = totalPasses - totalIssued;
		result.setTotalPasses(totalPasses);
		result.setTotalIssued(totalIssued);
		result.setTotalAvailable(totalAvailable);
		result.setTotalAccepted(totalAccepted);
		result.setTotalRejected(totalRejected);
		result.setTotalAttended(totalAttended);
		result.setTotalAllocated(totalAllocated);
		result.setTotalDownloads(totalDownloads);
		result.setTotalUsersUploaded(totalUsersUploaded);
		return result;
	}
	
	@GetMapping("/passSummaryByEventAndDepartment/{eventId}/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public DashboardResponse passSummaryEventAndDepartmentWise(@PathVariable int eventId, @PathVariable int departmentId) {
		DashboardResponse dashboardResponse =  passStatisticsEventAndDepartmentWise( eventId, departmentId);
		List<AnnexUsers> annexUsers = annexUsersRepository.findByDepartmentIdAndDeletedAndUserTypeIdIsNot(departmentId, false, ImsUserTypeEnum.Annexure_D.type);
		dashboardResponse.setTotalUsersUploaded(annexUsers.size());
		return dashboardResponse;
	}
	
	@GetMapping("/passSummaryByEventAndEnclosure/{eventId}/{enclosureId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public DashboardResponse passSummaryEventAndEnclosureWise(@PathVariable int eventId, @PathVariable int enclosureId) {
		DashboardResponse dashboardResponse =   passStatisticsEventAndEnclosureWise(eventId, enclosureId);
		Enclosure encl = enclosureRepository.findById(enclosureId).get();
		dashboardResponse.setTotalCapacity(encl.getMaxCapacity());
		return dashboardResponse;
	}
	
	@GetMapping("/passSummaryByEventAnduserType/{eventId}/{userTypeId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public DashboardResponse passSummaryEventAndUserTypeWise(@PathVariable int eventId, @PathVariable int userTypeId) {
		return  passStatisticsEventAndUserTypeWise( eventId, userTypeId);
	}
	
	@GetMapping("/passSummaryByEventAndPassCategory/{eventId}/{passCategoryId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public DashboardResponse passSummaryEventAndPassCategoryWise(@PathVariable int eventId, @PathVariable int passCategoryId) {
		return  passStatisticsEventAndPassCategoryWise( eventId, passCategoryId);
	}
	
	@GetMapping("/passSummaryByEventAndPassSubcategory/{eventId}/{passSubcategoryId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public DashboardResponse passSummaryEventAndPassSubcategoryWise(@PathVariable int eventId, @PathVariable int passSubcategoryId) {
		return  passStatisticsEventAndPassSubcategoryWise(eventId, passSubcategoryId);
	}
	
	@GetMapping("/passSummaryByEventAndPassCategoryAndPassSubcategory/{eventId}/{passCategoryId}/{passSubcategoryId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public DashboardResponse passSummaryByEventAndPassCategoryAndPassSubcategory(@PathVariable int eventId, @PathVariable int passCategoryId, @PathVariable int passSubcategoryId) {
		return  passStatisticsEventAndPassCategoryAndPassSubcategoryWise(eventId,passCategoryId, passSubcategoryId);
	}
	
	@GetMapping("/downloadPassSummary/{eventId}/{passCategoryId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public DownloadResponse downloadPassSummary(@PathVariable int eventId, @PathVariable int passCategoryId) {
		List<Pass> passList = passRepository.findByEventIdAndDeleted(eventId, false);
		if(passCategoryId != -1) {
			passList = passList.stream().filter(pass -> pass.getPassCategory() != null
					&& pass.getPassCategory().getId() == passCategoryId).collect(Collectors.toList());
		}
		return  getDownloadPassSummary(passList, eventId);
	}
	
	@PostMapping("/ticketSummary")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public ImsResponse getTicketSummary(@RequestBody TicketSummaryRequest ticketSummaryRequest) {
		ImsResponse imsResponse = new ImsResponse();
		TicketSummary ticketSummary = new TicketSummary();
		if(ticketSummaryRequest.getFromDate() != null && ticketSummaryRequest.getToDate() != null && ticketSummaryRequest.getEnclosureGroupId() > 0 && ticketSummaryRequest.getEventId() > 0 && ticketSummaryRequest.getRoleId() > 0 && ticketSummaryRequest.getPassSubcategoryId() > 0) { 
			List<PassDayLimit> passDayLimits = passDayLimitRepository.getAllBetweenDates(
					ticketSummaryRequest.getFromDate(),
					ticketSummaryRequest.getToDate(),
					ticketSummaryRequest.getEventId());
			passDayLimits = passDayLimits.stream().filter(item -> item.getDeleted() == false).collect(Collectors.toList());
			List<Integer> passDayLimitIds = passDayLimits.stream().map(item -> item.getId())
					.collect(Collectors.toList());
			List<PassDayLimitCategory> passDayLimitCategories = passDayLimitCategoryRepository.findByDeletedAndRoleIdAndEnclosureGroupIdAndPassSubcategoryIdAndPassDayLimitIdIn(false, ticketSummaryRequest.getRoleId(), ticketSummaryRequest.getEnclosureGroupId(), ticketSummaryRequest.getPassSubcategoryId(), passDayLimitIds);
			long totalTickets = 0;
			long soldTickets = 0;
			for(PassDayLimitCategory passDayLimitCategory: passDayLimitCategories) {
				totalTickets = totalTickets + passDayLimitCategory.getPassLimit();
			}
			if(totalTickets > 0) {
				List<Pass> listOfPasses = passRepository
						.findAllByEventIdAndPassCategoryIdAndPassStatusIdAndImsUserByImsUserIdRoleIdAndCreatedTimestampBetween(
								ticketSummaryRequest.getEventId(), PassCategoryEnum.PAIDTICKET.type, PassStatusEnum.ALLOCATED.type,
								ticketSummaryRequest.getRoleId(), ticketSummaryRequest.getFromDate().atStartOfDay(),
								ticketSummaryRequest.getToDate().atTime(LocalTime.of(23, 59, 59)));
				listOfPasses = listOfPasses.stream().filter(item -> item.getEnclosure() != null && item.getPassSubcategory() != null && item.getEnclosure().getEnclosureGroup().getId() == ticketSummaryRequest.getEnclosureGroupId() && item.getPassSubcategory().getId() == ticketSummaryRequest.getPassSubcategoryId()).collect(Collectors.toList());
//				List<PaidPassHolders> paidPassHolders = paidPassHoldersRepository.getAllRecordsBetweenDates(
//						ticketSummaryRequest.getFromDate().atStartOfDay(),
//						ticketSummaryRequest.getToDate().atTime(23, 59, 59),
//						ticketSummaryRequest.getEventId(),
//						ticketSummaryRequest.getEnclosureGroupId(),
//						ticketSummaryRequest.getPassSubcategoryId());
//				for(PaidPassHolders paidPassHolder: paidPassHolders) {
//					if(paidPassHolder.getImsUser() != null && paidPassHolder.getImsUser().getRole().getId() == ticketSummaryRequest.getRoleId()) {
//						soldTickets = soldTickets + 1;
//					}
//				}
				soldTickets = listOfPasses.size();
				ticketSummary.setOverallTickets(totalTickets);
				ticketSummary.setSoldTickets(soldTickets);
				ticketSummary.setAvailableTickets(totalTickets - soldTickets);
			}else {
				ticketSummary.setSoldTickets(0);
				ticketSummary.setAvailableTickets(0);
			}
			
			imsResponse.setMessage("Data retrieved successfully!");
			imsResponse.setSuccess(true);
			imsResponse.setData(ticketSummary);
		}else {
			imsResponse.setMessage("Something went wrong with the server");
			imsResponse.setSuccess(false);
		}
		
		return imsResponse;
	}
	
	
	@GetMapping("/getPassSummaryByNodalOfficer/{eventId}/{nodalOfficerId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER')")
	DashboardResponse getPassByNodalOfficer(@PathVariable("eventId") int eventId,
			@PathVariable("nodalOfficerId") int nodalOfficerId) {
		DashboardResponse dashboardResponse = new DashboardResponse();
		try {
			List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByImsUserId(nodalOfficerId);
			List<Integer> departments = nodalOfficers.stream().map(nodalOfficer -> nodalOfficer.getDepartment().getId())
					.collect(Collectors.toList());
			List<Pass> passes = passRepository.findByEventIdAndDeletedAndDepartmentIdIn(eventId, false, departments);
			passes = passes.stream().filter(pass -> pass.getPassCategory() != null
					&& pass.getPassCategory().getId() == PassCategoryEnum.GUESTPASS.type).collect(Collectors.toList());
			List<ImsUser> imsUsers = imsUserRepository.findByDeletedAndDepartmentIdIn(false, departments);
			imsUsers = imsUsers.stream().filter(imsUser -> imsUser.getUserType() != null
					&& (imsUser.getUserType().getId() == ImsUserTypeEnum.Annexure_A.type || imsUser.getUserType().getId() == ImsUserTypeEnum.Annexure_B.type || imsUser.getUserType().getId() == ImsUserTypeEnum.Annexure_C.type || imsUser.getUserType().getId() == ImsUserTypeEnum.Annexure_E.type || imsUser.getUserType().getId() == ImsUserTypeEnum.Annexure_F.type)).collect(Collectors.toList());
			dashboardResponse =  setPassSummary(passes, eventId);
			dashboardResponse.setTotalUsersUploaded(imsUsers.size());
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}
		return dashboardResponse;
	}
	
	
	@GetMapping("/getGuestPassSummaryByNodalOfficer/{eventId}/{nodalOfficerId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER')")
	DashboardResponse getGuestPassSummaryByNodalOfficer(@PathVariable("eventId") int eventId,
			@PathVariable("nodalOfficerId") int nodalOfficerId) {
		DashboardResponse dashboardResponse = new DashboardResponse();
		try {
			List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByImsUserId(nodalOfficerId);
			List<Integer> departments = nodalOfficers.stream().map(nodalOfficer -> nodalOfficer.getDepartment().getId())
					.collect(Collectors.toList());
			List<Pass> passes = passRepository.findByEventIdAndDeletedAndDepartmentIdIn(eventId, false, departments);
			passes = passes.stream().filter(pass -> pass.getPassCategory() != null
					&& pass.getPassCategory().getId() == PassCategoryEnum.GUESTPASS.type).collect(Collectors.toList());
			List<Pass> guestPasses = passes.stream().filter(pass -> pass.getImsUserByImsUserId() != null
					&& pass.getImsUserByImsUserId().getUserType().getId() == ImsUserTypeEnum.Annexure_D.type).collect(Collectors.toList());
			List<AnnexUsers> annexUsers = annexUsersRepository.findByDeletedAndDepartmentIdIn(false, departments);
			annexUsers = annexUsers.stream().filter(annexUser -> annexUser.getUserType() != null
					&& annexUser.getUserType().getId() == ImsUserTypeEnum.Annexure_D.type).collect(Collectors.toList());
			dashboardResponse =  setPassSummary(guestPasses, eventId);
			dashboardResponse.setTotalUsersUploaded(annexUsers.size());
			dashboardResponse.setTotalIssued(passes.size());
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}
		return dashboardResponse;
	}
	
	public DashboardResponse passStatisticsEventAndOrganizationWise(int eventId,int organizationId) {
		List<Pass> passList = passRepository.findByEventIdAndDeleted(eventId, false);
		passList = passList.stream().filter(pass -> pass.getDepartment() != null
				&& pass.getDepartment().getOrganization().getId() == organizationId).collect(Collectors.toList());
		return setPassSummary(passList, eventId);
	}
	
	public DashboardResponse passStatisticsEventAndDepartmentWise(int eventId,int departmentId) {
		List<Pass> passList = passRepository.findByEventIdAndDeleted(eventId, false);
		passList = passList.stream().filter(pass -> pass.getDepartment() != null
				&& pass.getDepartment().getId() == departmentId).collect(Collectors.toList());
		return setPassSummary(passList, eventId);
	}
	
	public DashboardResponse passStatisticsEventAndEnclosureWise(int eventId,int enclosureId) {
		List<Pass> passList = passRepository.findByEventIdAndDeleted(eventId, false);
		passList = passList.stream().filter(pass -> pass.getEnclosure() != null
				&& pass.getEnclosure().getId() == enclosureId).collect(Collectors.toList());
		return setPassSummary(passList, eventId);
	}
	
	public DashboardResponse passStatisticsEventAndUserTypeWise(int eventId,int userTypeId) {
		List<Pass> passList = passRepository.findByEventIdAndDeleted(eventId, false);
		passList = passList.stream().filter(pass -> pass.getImsUserByImsUserId() != null
				&& pass.getImsUserByImsUserId().getUserType().getId() == userTypeId).collect(Collectors.toList());
		return setPassSummary(passList, eventId);
	}

	public DashboardResponse passStatisticsEventAndPassCategoryWise(int eventId,int passCategoryId) {
		List<Pass> passList = passRepository.findByEventIdAndDeleted(eventId, false);
		passList = passList.stream().filter(pass -> pass.getPassCategory() != null
				&& pass.getPassCategory().getId() == passCategoryId).collect(Collectors.toList());
		return setPassSummary(passList, eventId);
	}

	public DashboardResponse passStatisticsEventAndPassSubcategoryWise(int eventId,int passSubcategoryId) {
		List<Pass> passList = passRepository.findByEventIdAndDeleted(eventId, false);
		passList = passList.stream().filter(pass -> pass.getPassSubcategory() != null
				&& pass.getPassSubcategory().getId() == passSubcategoryId).collect(Collectors.toList());
		return setPassSummary(passList, eventId);
	}
	
	public DashboardResponse passStatisticsEventAndPassCategoryAndPassSubcategoryWise(int eventId,int passCategoryId, int passSubcategoryId) {
		List<Pass> passList = passRepository.findByEventIdAndDeleted(eventId, false);
		passList = passList.stream().filter(pass -> pass.getPassSubcategory() != null && pass.getPassCategory() != null
				&& pass.getPassSubcategory().getId() == passSubcategoryId && pass.getPassCategory().getId() == passCategoryId).collect(Collectors.toList());
		return setPassSummary(passList, eventId);
	}


	private DashboardResponse setPassSummary(List<Pass> passList, int eventId) {
		DashboardResponse passSummary = new DashboardResponse();
		
		long totalPasses = eventRepository.findById(eventId).get().getTotalInvitations();
		passSummary.setTotalPasses(totalPasses);
		if (passList != null && passList.size() > 0) {
			long totalAllocated = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type).collect(Collectors.toList()).size();
			long totalAccepted = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_ACCEPTED.type).collect(Collectors.toList()).size();
			long totalRejected = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_REGRETTED.type).collect(Collectors.toList()).size();
			long totalAttended = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.ATTENDED.type).collect(Collectors.toList()).size();
			
			long totalDownloads = passList.stream().filter(pass -> pass.isDownloaded() == true).collect(Collectors.toList()).size();

			Map<Integer, Pass> uniquePasses = new HashMap<>();
			for (Pass pass : passList) {
				if(pass.getPassCategory() != null) {
					if (uniquePasses.get(pass.getPassCategory().getId()) == null)
						uniquePasses.put(pass.getPassCategory().getId(), pass);
				}
			}
			List<Pass> passCategoriesDistinct = uniquePasses.values().stream().collect(Collectors.toList());
			
			List<PassCategoryDto> passCategoryWiseStats = new ArrayList<>();
			long totalIssued = passList.size() - totalRejected;
			totalAllocated = totalAllocated + totalAccepted + totalRejected + totalAttended;
			long totalAvailable = totalIssued - totalAllocated;
			for(int i = 0 ; i < passCategoriesDistinct.size() ; i++){
				List<PassSubcategoryDto> passSubcategoryWiseStats = new ArrayList<>();
				 PassCategoryDto passCategoryDto = new PassCategoryDto();
				 if(passCategoriesDistinct.get(i).getPassCategory() != null) {
					 int passCategoryId = passCategoriesDistinct.get(i).getPassCategory().getId();
					 passCategoryDto.setPassCategoryId(passCategoryId);
					 passCategoryDto.setTotalInvitations(findPassCountByCategoryId(passList, passCategoryId).size());
					 Set<PassSubcategory> passSubCategories = passCategoriesDistinct.get(i).getPassCategory().getPassSubcategories();
					 for (PassSubcategory passSubcategory : passSubCategories) {
						 PassSubcategoryDto passSubcategoryDto = new PassSubcategoryDto(); 
						 passSubcategoryDto.setPassSubCategoryId(passSubcategory.getId());
						 passSubcategoryDto.setTotalInvitations(findPassCountByCategoryAndSubCategory(passList,passCategoryId, passSubcategory.getId()).size());
						 passSubcategoryWiseStats.add(passSubcategoryDto);
					 }
					 passCategoryDto.setPassSubcategories(passSubcategoryWiseStats);
					 passCategoryWiseStats.add(passCategoryDto);
				 }
		    }
			passSummary.setTotalPasses(totalPasses);
			passSummary.setTotalAllocated(totalAllocated);
			passSummary.setTotalDownloads(totalDownloads);
			passSummary.setTotalIssued(totalIssued);
			passSummary.setTotalAvailable(totalAvailable);
			passSummary.setTotalAccepted(totalAccepted);
			passSummary.setTotalAttended(totalAttended);
			passSummary.setCategoryWiseStatistics(passCategoryWiseStats);
			passSummary.setTotalRejected(totalRejected);
			passSummary.setEventId(eventId);
		}
		return passSummary;

	}
	
	
	private DownloadResponse getDownloadPassSummary(List<Pass> passList, int eventId) {
		DownloadResponse passSummary = new DownloadResponse();
		
		long totalPasses = eventRepository.findById(eventId).get().getTotalInvitations();
		passSummary.setTotalPasses(totalPasses);
		if (passList != null && passList.size() > 0) {
			long totalDownloads = passList.stream().filter(pass -> pass.isDownloaded() == true).collect(Collectors.toList()).size();
			long totalAccepted = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_ACCEPTED.type).collect(Collectors.toList()).size();
			long totalRejected = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_REGRETTED.type).collect(Collectors.toList()).size();
			long totalAttended = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.ATTENDED.type).collect(Collectors.toList()).size();
			List<Pass> passCategoriesDistinct = passList.stream()
					.filter(distinctByKey(pass -> pass.getPassCategory().getId()))
					.collect(Collectors.toList());
			List<DownloadPassCategoryDto> passCategoryWiseStats = new ArrayList<>();
			long totalIssued = passList.size() - totalRejected;
			long totalAvailable = totalPasses - totalIssued;
			for(int i = 0 ; i < passCategoriesDistinct.size() ; i++){
				List<DownloadPassSubcategoryDto> passSubcategoryWiseStats = new ArrayList<>();
				DownloadPassCategoryDto passCategoryDto = new DownloadPassCategoryDto();
				 int passCategoryId = passCategoriesDistinct.get(i).getPassCategory().getId();
				 passCategoryDto.setPassCategoryId(passCategoryId);
				 passCategoryDto.setTotalDownloads(findByPassCategoryAndDownloaded(passList, passCategoryId,true).size());
				 Set<PassSubcategory> passSubCategories = passCategoriesDistinct.get(i).getPassCategory().getPassSubcategories();
				 for (PassSubcategory passSubcategory : passSubCategories) {
					 DownloadPassSubcategoryDto passSubcategoryDto = new DownloadPassSubcategoryDto(); 
					 passSubcategoryDto.setPassSubCategoryId(passSubcategory.getId());
					 passSubcategoryDto.setName(passSubcategory.getName());
					 passSubcategoryDto.setTotalDownloads(findDownloadCountByCategoryAndSubCategory(passList,passCategoryId, passSubcategory.getId(), true).size());
					 passSubcategoryWiseStats.add(passSubcategoryDto);
				 }
				 passCategoryDto.setPassSubcategories(passSubcategoryWiseStats);
				 passCategoryWiseStats.add(passCategoryDto);

		    }
			passSummary.setTotalPasses(totalPasses);
			passSummary.setTotalDownloads(totalDownloads);
			passSummary.setTotalIssued(totalIssued);
			passSummary.setTotalAvailable(totalAvailable);
			passSummary.setTotalAccepted(totalAccepted);
			passSummary.setTotalAttended(totalAttended);
			passSummary.setDownloadStatistics(passCategoryWiseStats);
			passSummary.setTotalRejected(totalRejected);
			passSummary.setEventId(eventId);
		}
		return passSummary;

	}
	

	private List<Pass> findDownloadCountByCategoryAndSubCategory(List<Pass> passList, int passCategoryId, int id, boolean b) {
		return passList.stream().filter(pass -> pass.getPassCategory() != null && pass.getPassSubcategory() != null
				&& pass.getPassCategory().getId() == passCategoryId && pass.getPassSubcategory().getId() == id && pass.isDownloaded() == b).collect(Collectors.toList());
	}

	private List<Pass> findByPassCategoryAndDownloaded(List<Pass> passList, int passCategoryId, boolean b) {
		return passList.stream().filter(pass -> pass.getPassCategory() != null
				&& pass.getPassCategory().getId() == passCategoryId && pass.isDownloaded() == b).collect(Collectors.toList());
	}

	private List<Pass> findPassCountByCategoryAndSubCategory(List<Pass> passList, int passCategoryId, int id) {
		return passList.stream().filter(pass -> pass.getPassCategory() != null && pass.getPassSubcategory() != null
				&& pass.getPassCategory().getId() == passCategoryId && pass.getPassSubcategory().getId() == id).collect(Collectors.toList());
    }
	private List<Pass> findPassCountByCategoryId(List<Pass> passList, int passCategoryId) {
		return passList.stream().filter(pass -> pass.getPassCategory() != null
				&& pass.getPassCategory().getId() == passCategoryId).collect(Collectors.toList());
   }
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
