package in.co.bel.ims.initial.service.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.EnclosureGroupRepository;
import in.co.bel.ims.initial.data.repository.EnclosurePassTypeMappingRepository;
import in.co.bel.ims.initial.data.repository.EnclosureRepository;
import in.co.bel.ims.initial.data.repository.EventRepository;
import in.co.bel.ims.initial.data.repository.InvitationOfficerRepository;
import in.co.bel.ims.initial.data.repository.NodalOfficerRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.data.repository.PassSubcategoryRepository;
import in.co.bel.ims.initial.entity.Enclosure;
import in.co.bel.ims.initial.entity.EnclosureGroup;
import in.co.bel.ims.initial.entity.EnclosurePassTypeMapping;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.InvitationOfficer;
import in.co.bel.ims.initial.entity.NodalOfficer;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.entity.PassSubcategory;
import in.co.bel.ims.initial.service.dto.EnclosureAvailability;
import in.co.bel.ims.initial.service.dto.EnclosureStats;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.dto.PassSubcategoryStats;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.PassCategoryEnum;
import in.co.bel.ims.initial.service.util.PassStatusEnum;
import in.co.bel.ims.initial.service.util.PassSubcategoryEnum;

@RestController
@CrossOrigin
@RequestMapping("/app/enclosure")
public class EnclosureController extends ImsServiceTemplate<Enclosure, EnclosureRepository> {

	@Autowired
	EnclosureRepository enclRepository;
	
	@Autowired
	PassRepository passRepository;
	
	@Autowired
	PassSubcategoryRepository passSubcategoryRepository;

	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	NodalOfficerRepository nodalOfficerRepository;

	@Autowired
	EnclosureGroupRepository enclosureGroupRepository;
	
	@Autowired
	EnclosurePassTypeMappingRepository enclosurePassTypeMappingRepository;
	
	@Autowired
	InvitationOfficerRepository invitationOfficerRepository;

	@GetMapping("/getEnclByEnclGrp/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	List<Enclosure> getEnclByEnclGrp(@PathVariable int id) {
		return enclRepository.findByEnclosureGroupIdAndDeleted(id, false);
	}
	
	@GetMapping("/getEnclForWoPAndOrganizerPassByEnclGrp/{eventId}/{enclGroupId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	List<Enclosure> getEnclForWoPAndOrganizerPassByEnclGrp(@PathVariable int eventId, @PathVariable int enclGroupId) {
		List<Enclosure> enclListByEvent = getEnclosureListByEvent(eventId);
		List<Enclosure> enclosures =  enclListByEvent.stream().filter(encl -> encl.getEnclosureGroup() != null
				&& encl.getEnclosureGroup().getId() == enclGroupId && encl.isDeleted() == false).collect(Collectors.toList());
		List<Enclosure> reqEnclosures = new ArrayList<>();
		for(Enclosure enclosure : enclosures) {
			List<EnclosurePassTypeMapping> redTipEnclosurePassTypeMappings = enclosurePassTypeMappingRepository.findByEnclosureIdAndPassSubcategoryIdAndDeletedAndEventId(enclosure.getId(), PassSubcategoryEnum.RED_TIP.type,false, eventId);
			List<EnclosurePassTypeMapping> yellowTipEnclosurePassTypeMappings = enclosurePassTypeMappingRepository.findByEnclosureIdAndPassSubcategoryIdAndDeletedAndEventId(enclosure.getId(), PassSubcategoryEnum.YELLOW_TIP.type,false, eventId);
			if(redTipEnclosurePassTypeMappings.size() > 0 || yellowTipEnclosurePassTypeMappings.size() > 0) {
				reqEnclosures.add(enclosure);
			}
		}
		return reqEnclosures;
	}

	@GetMapping("/getEnclByEvent/{eventid}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	ImsResponse getEnclByEvent(@PathVariable int eventid) {
		List<Enclosure> enclListByEvent = getEnclosureListByEvent(eventid);
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Enclosures fetched successfully!");
		imsResponse.setData(enclListByEvent);
		return imsResponse;
	}
	
	@GetMapping("/getEnclByEventAndPassCategory/{eventid}/{passCategoryId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	ImsResponse getEnclByEvent(@PathVariable int eventid, @PathVariable int passCategoryId) {
		List<EnclosurePassTypeMapping> enclosurePassTypeMappings = enclosurePassTypeMappingRepository.findByEventIdAndPassCategoryIdAndDeleted(eventid, passCategoryId, false);
		List<EnclosurePassTypeMapping> enclosurePassTypeMappingsDistinct = enclosurePassTypeMappings.stream()
				.filter(distinctByKey(enclosurePassTypeMapping -> enclosurePassTypeMapping.getEnclosure().getId()))
				.collect(Collectors.toList());
		List<Enclosure> enclList = new ArrayList<>();
		for(EnclosurePassTypeMapping enclosurePassTypeMapping :enclosurePassTypeMappingsDistinct) {
			Enclosure encl = enclRepository.findByIdAndDeleted(enclosurePassTypeMapping.getEnclosure().getId(), false);
			if(encl != null) {
				enclList.add(encl);
			}
		}
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Enclosures fetched successfully!");
		imsResponse.setData(enclList);
		return imsResponse;
	}
	
	@GetMapping("/getAllEnclosureOfAssignedPass/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	ImsResponse getAllEnclosureOfAssignedPass(@PathVariable int departmentId){
		ImsResponse imsResponse = new ImsResponse();
		List<Enclosure> enclosures = passRepository.findAllEnclosureForDepartment(departmentId);
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Enclosures fetched successfully!");
		imsResponse.setData(enclosures);
		return imsResponse;
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getAll() {
		// TODO Auto-generated method stub
		return super.getAll();
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getById(@PathVariable int id) {
		// TODO Auto-generated method stub
		return super.getById(id);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public Enclosure create(@Valid @RequestBody Enclosure t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<Enclosure> createAll(@Valid @RequestBody  List<Enclosure> t) {
		// TODO Auto-generated method stub
		return super.createAll(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse update(@Valid @RequestBody Enclosure t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}
	
	
	@GetMapping("/getEnclosureWiseAvailability/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public List<EnclosureAvailability> getEnclosureWiseAvailability(@PathVariable int eventId) {
		// TODO Auto-generated method stub
		List<EnclosureAvailability> enclosureAvailabilityList = new ArrayList<>();
		List<Enclosure> enclosureList = getEnclosureListByEvent(eventId);
		for(Enclosure encl: enclosureList) {
			EnclosureAvailability enclosureAvailability = new EnclosureAvailability();
			enclosureAvailability.setEnclosure(encl);
			List<Pass> passList = passRepository.findByEventIdAndDeleted(eventId, false);
			passList = passList.stream().filter(pass -> pass.getEvent() != null && pass.getEnclosure() != null
					&& pass.getEnclosure().getId() == encl.getId() && pass.getEvent().getId() == eventId).collect(Collectors.toList());
			long totalAccepted = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_ACCEPTED.type).collect(Collectors.toList()).size();
			long totalRejected = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_REGRETTED.type).collect(Collectors.toList()).size();
			long totalAllocated = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type).collect(Collectors.toList()).size();
			long totalAttended = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.ATTENDED.type).collect(Collectors.toList()).size();
			long totalIssued = passList.size();
			long totalAvailable = 0;
			totalAllocated = totalAllocated + totalAccepted + totalRejected + totalAttended;
			totalAvailable = totalIssued - totalAllocated;
			enclosureAvailability.setTotalAccepted(totalAccepted);
			enclosureAvailability.setTotalAllocated(totalAllocated);
			enclosureAvailability.setTotalIssued(totalIssued);
			enclosureAvailability.setTotalRejected(totalRejected);
			enclosureAvailability.setTotalAvailable(totalAvailable);
			enclosureAvailabilityList.add(enclosureAvailability);
		}
		return enclosureAvailabilityList;
	}
	
	@GetMapping("/getEnclosureWiseAvailabilityForNO/{eventId}/{nodalOfficerId}")
    @PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER')")
	public List<EnclosureStats> getEnclosureWiseAvailabilityForNO(@PathVariable int eventId, @PathVariable int nodalOfficerId) {
		List<EnclosureStats> enclosureAvailabilityList = new ArrayList<>();		
		List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByImsUserId(nodalOfficerId);
		List<Integer> departments = nodalOfficers.stream().map(nodalOfficer -> nodalOfficer.getDepartment().getId())
				.collect(Collectors.toList());
		List<Pass> passes = passRepository.findByEventIdAndDeletedAndDepartmentIdIn(eventId, false,
				departments);
		
		passes = passes.stream().filter(pass -> pass.getPassCategory() != null
				&& pass.getPassCategory().getId() == PassCategoryEnum.GUESTPASS.type).collect(Collectors.toList());
		Map<Integer, Pass> uniquePasses = new HashMap<>();
		for (Pass pass : passes) {
			if(pass.getEnclosure() != null) {
				if (uniquePasses.get(pass.getEnclosure().getId()) == null)
					uniquePasses.put(pass.getEnclosure().getId(), pass);
			}
		}
		List<Pass> enclosureList = uniquePasses.values().stream().collect(Collectors.toList());
		List<Pass> passList = new ArrayList<>();

		for(Pass p: enclosureList) {
			List<PassSubcategoryStats> passSubcategoryStats = new ArrayList<>();
			EnclosureStats enclosureAvailability = new EnclosureStats();
			enclosureAvailability.setEnclosureId(p.getEnclosure().getId());
			enclosureAvailability.setEnclosureGroupName(p.getEnclosure().getEnclosureGroup().getName());
			enclosureAvailability.setName(p.getEnclosure().getName());
			enclosureAvailability.setEnclosureCapacity(p.getEnclosure().getMaxCapacity());
			enclosureAvailability.setEnclosureGroupId(p.getEnclosure().getEnclosureGroup().getId());
			passList = passes.stream().filter(pass -> pass.getEnclosure() != null
					&& pass.getEnclosure().getId() == p.getEnclosure().getId()).collect(Collectors.toList());
			long totalAccepted = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_ACCEPTED.type).collect(Collectors.toList()).size();
			long totalRejected = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_REGRETTED.type).collect(Collectors.toList()).size();
			long totalAllocated = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type).collect(Collectors.toList()).size();
			long totalAttended = passList.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.ATTENDED.type).collect(Collectors.toList()).size();
			long totalIssued = passList.size();
			totalAllocated = totalAllocated + totalAccepted + totalRejected + totalAttended;
			long totalAvailable = totalIssued - totalAllocated;
			
			//Yellow Tip Passes Stats
			List<Pass> yellowTipPasses = passList.stream().filter(pass ->  pass.getPassSubcategory() != null 
					&& pass.getPassSubcategory().getId() == PassSubcategoryEnum.GUEST_YELLOW_TIP.type).collect(Collectors.toList());
			long totalYellowAccepted = yellowTipPasses.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_ACCEPTED.type).collect(Collectors.toList()).size();
			long totalYellowRejected = yellowTipPasses.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_REGRETTED.type).collect(Collectors.toList()).size();
			long totalYellowAllocated = yellowTipPasses.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type).collect(Collectors.toList()).size();
			long totalYellowAttend = yellowTipPasses.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.ATTENDED.type).collect(Collectors.toList()).size();
			long totalYellowIssued = yellowTipPasses.size();
			totalYellowAllocated = totalYellowAllocated + totalYellowAccepted + totalYellowRejected + totalYellowAttend;
			long totalYellowAvailable = totalYellowIssued - totalYellowAllocated;			
			PassSubcategoryStats yellowTipStats = new PassSubcategoryStats();
			PassSubcategory subYellow = passSubcategoryRepository.findByIdAndDeleted(PassSubcategoryEnum.GUEST_YELLOW_TIP.type, false);
			yellowTipStats.setPassSubcategoryId(subYellow.getId());
			yellowTipStats.setPassSubcategoryName(subYellow.getName());
			yellowTipStats.setPassCategoryId(subYellow.getPassCategory().getId());
			yellowTipStats.setPassCategoryName(subYellow.getPassCategory().getName());
			yellowTipStats.setTotalAccepted(totalYellowAccepted);
			yellowTipStats.setTotalAllocated(totalYellowAllocated);
			yellowTipStats.setTotalIssued(totalYellowIssued);
			yellowTipStats.setTotalRejected(totalYellowRejected);
			yellowTipStats.setTotalAvailable(totalYellowAvailable);
			passSubcategoryStats.add(yellowTipStats);

			//No Tip Passes Stats
			List<Pass> noTipPasses = passList.stream().filter(pass ->  pass.getPassSubcategory() != null 
					&& pass.getPassSubcategory().getId() == PassSubcategoryEnum.GUEST_NO_TIP.type).collect(Collectors.toList());
			long totalNoTipAccepted = noTipPasses.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_ACCEPTED.type).collect(Collectors.toList()).size();
			long totalNoTipRejected = noTipPasses.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_REGRETTED.type).collect(Collectors.toList()).size();
			long totalNoTipAllocated = noTipPasses.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type).collect(Collectors.toList()).size();
			long totalNoTipAttend = noTipPasses.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() == PassStatusEnum.ATTENDED.type).collect(Collectors.toList()).size();
			long totalNoTipIssued = noTipPasses.size();
			totalNoTipAllocated = totalNoTipAllocated + totalNoTipAccepted + totalNoTipRejected + totalNoTipAttend;
			long totalNoTipAvailable = totalNoTipIssued - totalNoTipAllocated;			
			PassSubcategoryStats noTipStats = new PassSubcategoryStats();
			PassSubcategory subNoTip = passSubcategoryRepository.findByIdAndDeleted(PassSubcategoryEnum.GUEST_NO_TIP.type, false);
			noTipStats.setPassSubcategoryId(subNoTip.getId());
			noTipStats.setPassSubcategoryName(subNoTip.getName());
			noTipStats.setPassCategoryId(subNoTip.getPassCategory().getId());
			noTipStats.setPassCategoryName(subNoTip.getPassCategory().getName());
			noTipStats.setTotalAccepted(totalNoTipAccepted);
			noTipStats.setTotalAllocated(totalNoTipAllocated);
			noTipStats.setTotalIssued(totalNoTipIssued);
			noTipStats.setTotalRejected(totalNoTipRejected);
			noTipStats.setTotalAvailable(totalNoTipAvailable);
			passSubcategoryStats.add(noTipStats);
			
			enclosureAvailability.setTotalAccepted(totalAccepted);
			enclosureAvailability.setTotalAllocated(totalAllocated);
			enclosureAvailability.setTotalIssued(totalIssued);
			enclosureAvailability.setTotalRejected(totalRejected);
			enclosureAvailability.setTotalAvailable(totalAvailable);
			enclosureAvailability.setTotalAttend(totalAttended);
			enclosureAvailability.setPassSubcategoryStats(passSubcategoryStats);
			enclosureAvailabilityList.add(enclosureAvailability);
		}
		return enclosureAvailabilityList;
	}
	
	@GetMapping("/getPassSummaryForNOByEventAndEnclosure/{nodalOfficerId}/{eventId}/{enclosureId}")
    @PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER')")
	public EnclosureStats getPassSummaryForNOByEventAndEnclosure(@PathVariable int nodalOfficerId, @PathVariable int eventId, @PathVariable int enclosureId) {
		EnclosureStats enclosureAvailability = new EnclosureStats();		
		List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByImsUserId(nodalOfficerId);
		List<Integer> departments = nodalOfficers.stream().map(nodalOfficer -> nodalOfficer.getDepartment().getId())
				.collect(Collectors.toList());
		List<Pass> passes = passRepository.findByEventIdAndDeletedAndDepartmentIdIn(eventId, false,
				departments);
		
		passes = passes.stream().filter(pass -> pass.getPassCategory() != null
				&& pass.getPassCategory().getId() == PassCategoryEnum.GUESTPASS.type).collect(Collectors.toList());

		passes = passes.stream().filter(pass -> pass.getEnclosure() != null
				&& pass.getEnclosure().getId() == enclosureId).collect(Collectors.toList());
		long totalAccepted = passes.stream().filter(pass -> pass.getPassStatus() != null
				&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_ACCEPTED.type).collect(Collectors.toList()).size();
		long totalRejected = passes.stream().filter(pass -> pass.getPassStatus() != null
				&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_REGRETTED.type).collect(Collectors.toList()).size();
		long totalAllocated = passes.stream().filter(pass -> pass.getPassStatus() != null
				&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type).collect(Collectors.toList()).size();
		long totalAttended = passes.stream().filter(pass -> pass.getPassStatus() != null
				&& pass.getPassStatus().getId() == PassStatusEnum.ATTENDED.type).collect(Collectors.toList()).size();
		long totalIssued = passes.size();
		totalAllocated = totalAllocated + totalAccepted + totalRejected + totalAttended;
		long totalAvailable = totalIssued - totalAllocated;
			
	    enclosureAvailability.setTotalAccepted(totalAccepted);
		enclosureAvailability.setTotalAllocated(totalAllocated);
		enclosureAvailability.setTotalIssued(totalIssued);
		enclosureAvailability.setTotalRejected(totalRejected);
		enclosureAvailability.setTotalAvailable(totalAvailable);
		enclosureAvailability.setTotalAttend(totalAttended);
		return enclosureAvailability;
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	private List<Enclosure> getEnclosureListByEvent(int eventid) {
		List<Enclosure> enclListByEvent = new ArrayList<>();
		Event event = eventRepository.findById(eventid).get();
		int venueId = event.getVenue().getId();
		List<EnclosureGroup> enclGroups = enclosureGroupRepository.findByVenueIdAndDeleted(venueId, false);
		enclGroups.forEach(enclGroup -> {
			List<Enclosure> enclList = enclRepository.findByEnclosureGroupIdAndDeleted(enclGroup.getId(), false);
			enclListByEvent.addAll(enclList);
		});
		return enclListByEvent;
	}
	
	@GetMapping("/getEnclByInvitationAdminAndEvent/{imsUserId}/{eventId}/{enclGroupId}")
	@PreAuthorize("hasRole('INVITATIONADMIN')")
	ImsResponse getEnclByInvitationAdminAndEvent(@PathVariable int imsUserId, @PathVariable int eventId, @PathVariable int enclGroupId) {
		List<InvitationOfficer> enclListByEventAndImsUserId = invitationOfficerRepository.findByImsUserIdAndEventIdAndDeleted(imsUserId, eventId, false);
		List<Enclosure> enclosures = new ArrayList<>();
		for(InvitationOfficer invitationOfficer: enclListByEventAndImsUserId) {
			Enclosure encl = enclRepository.findByIdAndDeleted(invitationOfficer.getEnclosure().getId(), false);
			if(encl != null && encl.getEnclosureGroup().getId() == enclGroupId) {
				enclosures.add(encl);
			}
		}
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Enclosures fetched successfully!");
		imsResponse.setData(enclosures);
		return imsResponse;
	}
	
	@GetMapping("/getAllEnclByInvitationAdminAndEvent/{imsUserId}/{eventId}")
	@PreAuthorize("hasRole('INVITATIONADMIN')")
	ImsResponse getAllEnclByInvitationAdminAndEvent(@PathVariable int imsUserId, @PathVariable int eventId) {
		List<InvitationOfficer> enclListByEventAndImsUserId = invitationOfficerRepository.findByImsUserIdAndEventIdAndDeleted(imsUserId, eventId, false);
		List<Enclosure> enclosures = new ArrayList<>();
		for(InvitationOfficer invitationOfficer: enclListByEventAndImsUserId) {
			Enclosure encl = enclRepository.findByIdAndDeleted(invitationOfficer.getEnclosure().getId(), false);
			if(encl != null) {
				enclosures.add(encl);
			}
		}
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Enclosures fetched successfully!");
		imsResponse.setData(enclosures);
		return imsResponse;
	}
	
	@GetMapping("/getAllEnclByInvitationAdminAndEventAndPassCategory/{imsUserId}/{eventId}/{passCategoryId}")
	@PreAuthorize("hasRole('INVITATIONADMIN')")
	ImsResponse getAllEnclByInvitationAdminAndEvent(@PathVariable int imsUserId, @PathVariable int eventId, @PathVariable int passCategoryId) {
		List<InvitationOfficer> enclListByEventAndImsUserId = invitationOfficerRepository.findByImsUserIdAndEventIdAndDeleted(imsUserId, eventId, false);
		List<Enclosure> enclosures = new ArrayList<>();
		for(InvitationOfficer invitationOfficer: enclListByEventAndImsUserId) {
			Enclosure encl = enclRepository.findByIdAndDeleted(invitationOfficer.getEnclosure().getId(), false);
			if(encl != null) {
				List<EnclosurePassTypeMapping> enclosurePassTypeMappings = enclosurePassTypeMappingRepository.findByEventIdAndPassCategoryIdAndEnclosureIdAndDeleted(eventId, passCategoryId, invitationOfficer.getEnclosure().getId(),  false);
				if(encl != null && enclosurePassTypeMappings.size() > 0) {
					enclosures.add(encl);
				}
			}
		}
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Enclosures fetched successfully!");
		imsResponse.setData(enclosures);
		return imsResponse;
	}
	
	@GetMapping("/getEnclGrpByInvitationAdminAndEvent/{imsUserId}/{eventId}")
	@PreAuthorize("hasRole('INVITATIONADMIN') or hasRole('SUPERADMIN')")
	ImsResponse getEnclByInvitationAdminAndEvent(@PathVariable int imsUserId, @PathVariable int eventId) {
		List<InvitationOfficer> enclListByEventAndImsUserId = invitationOfficerRepository.findByImsUserIdAndEventIdAndDeleted(imsUserId, eventId, false);
		List<EnclosureGroup> enclosureGrps = new ArrayList<>();
		Map<Integer, EnclosureGroup> enclGrpList = new HashMap<>();
		for(InvitationOfficer invitationOfficer: enclListByEventAndImsUserId) {
			EnclosureGroup enclGrp = enclosureGroupRepository.findByIdAndDeleted(invitationOfficer.getEnclosure().getEnclosureGroup().getId(), false);
			if(enclGrp != null && enclGrpList.get(enclGrp.getId()) == null) {
				enclGrpList.put(enclGrp.getId(), enclGrp);
			}
		}
		
		enclosureGrps = enclGrpList.values().stream().collect(Collectors.toList());
		
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Enclosures fetched successfully!");
		imsResponse.setData(enclosureGrps);
		return imsResponse;
	}
	
	@GetMapping("/getEnclForWopAndOrganizerPassByInvitationAdminAndEvent/{imsUserId}/{eventId}/{enclGroupId}")
	@PreAuthorize("hasRole('INVITATIONADMIN')")
	ImsResponse getEnclForWopAndOrganizerPassByInvitationAdminAndEvent(@PathVariable int imsUserId, @PathVariable int eventId, @PathVariable int enclGroupId) {
		List<InvitationOfficer> enclListByEventAndImsUserId = invitationOfficerRepository.findByImsUserIdAndEventIdAndDeleted(imsUserId, eventId, false);
		List<Enclosure> enclosures = new ArrayList<>();
		for(InvitationOfficer invitationOfficer: enclListByEventAndImsUserId) {
			Enclosure encl = enclRepository.findByIdAndDeleted(invitationOfficer.getEnclosure().getId(), false);
			if(encl != null && encl.getEnclosureGroup().getId() == enclGroupId) {
				enclosures.add(encl);
			}
		}
		List<Enclosure> reqEnclosures = new ArrayList<>();
		for(Enclosure enclosure : enclosures) {
			List<EnclosurePassTypeMapping> redTipEnclosurePassTypeMappings = enclosurePassTypeMappingRepository.findByEnclosureIdAndPassSubcategoryIdAndDeletedAndEventId(enclosure.getId(), PassSubcategoryEnum.RED_TIP.type,false, eventId);
			List<EnclosurePassTypeMapping> yellowTipEnclosurePassTypeMappings = enclosurePassTypeMappingRepository.findByEnclosureIdAndPassSubcategoryIdAndDeletedAndEventId(enclosure.getId(), PassSubcategoryEnum.YELLOW_TIP.type,false, eventId);
			if(redTipEnclosurePassTypeMappings.size() > 0 || yellowTipEnclosurePassTypeMappings.size() > 0) {
				reqEnclosures.add(enclosure);
			}
		}
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Enclosures fetched successfully!");
		imsResponse.setData(reqEnclosures);
		return imsResponse;
	}

}
