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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.EnclosureRepository;
import in.co.bel.ims.initial.data.repository.EventRepository;
import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.InvitationOfficerRepository;
import in.co.bel.ims.initial.data.repository.RoleRepository;
import in.co.bel.ims.initial.entity.Enclosure;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.InvitationOfficer;
import in.co.bel.ims.initial.entity.Role;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.dto.InvitationOfficerRequest;
import in.co.bel.ims.initial.service.dto.InvitationOfficerResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.LogLevelEnum;
import in.co.bel.ims.initial.service.util.LogUtil;
import in.co.bel.ims.initial.service.util.RoleEnum;

@RestController
@CrossOrigin
@RequestMapping("/app/invitationOfficer")
public class InvitationOfficerController extends ImsServiceTemplate<InvitationOfficer, InvitationOfficerRepository> {

	@Autowired
	ImsUserRepository imsUserRepository;
	@Autowired
	InvitationOfficerRepository invitationOfficerRepository;
	@Autowired
	EventRepository eventRepository;
	@Autowired
	EnclosureRepository enclosureRepository;
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	LogUtil log;

	@PostMapping("/assignInvitationAdmin")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse assignInvitationAdmin(@RequestBody InvitationOfficerRequest invitationOfficerRequest) {
		ImsResponse imsResponse = new ImsResponse();

		Event event = eventRepository.findById(invitationOfficerRequest.getEventId()).get();
		ImsUser imsUser = imsUserRepository.findByIdAndDeleted(invitationOfficerRequest.getImsUserId(), false);
		List<Integer> enclList = invitationOfficerRequest.getEnclosureIds();

		imsUser.setRole(new Role(RoleEnum.ROLE_INVITATIONADMIN.role));
		imsUserRepository.saveAndFlush(imsUser);

		List<InvitationOfficer> invitationOfficers = new ArrayList<>();
		InvitationOfficerResponse invitationOfficerResponse = new InvitationOfficerResponse();
		Map<Integer, Enclosure> invEnclMap = new HashMap<>();
		enclList.forEach(enclId -> {
			InvitationOfficer invitationOfficer = new InvitationOfficer();
			invitationOfficer.setEvent(event);
			invitationOfficer.setImsUser(imsUser);
			Enclosure enclosure = enclosureRepository.findById(enclId).get();
			invitationOfficer.setEnclosure(enclosure);
			invitationOfficers.add(invitationOfficer);
			invEnclMap.put(invitationOfficer.getId(), enclosure);
		});
		invitationOfficerRepository.saveAll(invitationOfficers);
		imsResponse.setMessage("Invitation Admin is assigned Successfully!");
		imsResponse.setSuccess(true);
		log.saveLog(null, "Invitation Admin is assigned Successfully!", "USER_CREATION", LogLevelEnum.INFO);
		invitationOfficerResponse.setEnclosuresMap(invEnclMap);
		invitationOfficerResponse.setImsUser(imsUser);
		invitationOfficerResponse.setEvent(event);
		imsResponse.setData(invitationOfficerResponse);
		return imsResponse;

	}
	
	@PostMapping("/updateInvitationAdmin")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse updateInvitationAdmin(@RequestBody InvitationOfficerRequest invitationOfficerRequest) {
		
		ImsResponse imsResponse = new ImsResponse();

		Event event = eventRepository.findById(invitationOfficerRequest.getEventId()).get();
		ImsUser imsUser = imsUserRepository.findById(invitationOfficerRequest.getImsUserId()).get();
		Map<Integer, Integer> enclList = invitationOfficerRequest.getEnclosuresMap();
		List<Integer> invOffIds = invitationOfficerRequest.getInvOffcrIds();
		invitationOfficerRepository.deleteAllById(invOffIds);
		List<InvitationOfficer> invitationOfficers = new ArrayList<>();
		InvitationOfficerResponse invitationOfficerResponse = new InvitationOfficerResponse();
		Map<Integer, Enclosure> invEnclMap = new HashMap<>();
		enclList.forEach((enclId, invOffId) -> {
			InvitationOfficer invitationOfficer = null;
			if(invOffId != null) {
				invitationOfficer = invitationOfficerRepository.findById(invOffId).get();
			} else {
				invitationOfficer = new InvitationOfficer();
			}
			invitationOfficer.setEvent(event);
			invitationOfficer.setImsUser(imsUser);
			Enclosure enclosure = enclosureRepository.findById(enclId).get();
			invitationOfficer.setEnclosure(enclosure);
			invitationOfficers.add(invitationOfficer);
			invEnclMap.put(invitationOfficer.getId(), enclosure);
		});
		invitationOfficerRepository.saveAll(invitationOfficers);
		imsResponse.setMessage("Invitation Admin is updated Successfully!");
		log.saveLog(null, "Invitation Admin is updated Successfully!", "USER_UPDATE", LogLevelEnum.INFO);
		invitationOfficerResponse.setEnclosuresMap(invEnclMap);
		invitationOfficerResponse.setImsUser(imsUser);
		invitationOfficerResponse.setEvent(event);
		imsResponse.setData(new InvitationOfficerResponse());
		return imsResponse;
	}
	
	@PostMapping("/deleteInvitationAdmin")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse deleteInvitationAdmin(@RequestBody InvitationOfficerRequest invitationOfficerRequest) {
		ImsResponse imsResponse = new ImsResponse();
		Event event = eventRepository.findById(invitationOfficerRequest.getEventId()).get();
		ImsUser imsUser = imsUserRepository.findById(invitationOfficerRequest.getImsUserId()).get();
		List<Integer> invOffIds = invitationOfficerRequest.getInvOffcrIds();
		invitationOfficerRepository.deleteAllById(invOffIds);
		Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
		imsUser.setRole(role);
		imsUserRepository.saveAndFlush(imsUser);
		imsResponse.setMessage("Invitation Admin is deleted Successfully!");
		log.saveLog(null, "Invitation Admin is deleted Successfully!", "USER_UPDATE", LogLevelEnum.INFO);
		return imsResponse;
	}
	
	@Override
	@GetMapping("/getAll")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAll() {
		ImsResponse imsResponse = new ImsResponse();
		List<InvitationOfficerResponse> invitationOfficerResponses = new ArrayList<>();
		List<InvitationOfficer> invitationOfficers = sanitizeUserData(invitationOfficerRepository.findAllByDeleted(false));
		Set<InvitationOfficer> invitationOfficersDistinct = invitationOfficers.stream()
				.filter(distinctByKey(invitationOfficer -> invitationOfficer.getImsUser().getId()))
				.collect(Collectors.toSet());
		Map<ImsUser, Event> imsUsersList = invitationOfficersDistinct.stream()
				.collect(Collectors.toMap(InvitationOfficer::getImsUser, InvitationOfficer::getEvent));
		imsUsersList.forEach((imsUser, event) -> {
			InvitationOfficerResponse invitationOfficerResponse = new InvitationOfficerResponse();
			invitationOfficerResponse.setImsUser(imsUser);
			invitationOfficerResponse.setEvent(event);
			invitationOfficerResponse.setEnclosuresMap(
					invitationOfficers.stream().filter(invOff -> invOff.getImsUser().getId() == imsUser.getId()).collect(Collectors.toMap(InvitationOfficer::getId, InvitationOfficer::getEnclosure)));
			invitationOfficerResponses.add(invitationOfficerResponse);

		});
		imsResponse.setData(invitationOfficerResponses);
		imsResponse.setMessage("Retrieved Invitation admin data!");
		imsResponse.setSuccess(true);
		return imsResponse;

	}
	
	
	@GetMapping("/getAllForEvent/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllForEvent(@PathVariable int eventId) {
		ImsResponse imsResponse = new ImsResponse();
		List<InvitationOfficerResponse> invitationOfficerResponses = new ArrayList<>();
		List<InvitationOfficer> invitationOfficers = sanitizeUserData(invitationOfficerRepository.findByEventIdAndDeleted(eventId,false));
		Set<InvitationOfficer> invitationOfficersDistinct = invitationOfficers.stream()
				.filter(distinctByKey(invitationOfficer -> invitationOfficer.getImsUser().getId()))
				.collect(Collectors.toSet());
		Map<ImsUser, Event> imsUsersList = invitationOfficersDistinct.stream()
				.collect(Collectors.toMap(InvitationOfficer::getImsUser, InvitationOfficer::getEvent));
		imsUsersList.forEach((imsUser, event) -> {
			InvitationOfficerResponse invitationOfficerResponse = new InvitationOfficerResponse();
			invitationOfficerResponse.setImsUser(imsUser);
			invitationOfficerResponse.setEvent(event);
			invitationOfficerResponse.setEnclosuresMap(
					invitationOfficers.stream().filter(invOff -> invOff.getImsUser().getId() == imsUser.getId()).collect(Collectors.toMap(InvitationOfficer::getId, InvitationOfficer::getEnclosure)));
			invitationOfficerResponses.add(invitationOfficerResponse);

		});
		imsResponse.setData(invitationOfficerResponses);
		imsResponse.setMessage("Retrieved Invitation admin data!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	@GetMapping("/getAllInvitationAdminForEvent/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllInvitationAdminForEvent(@PathVariable int eventId) {
		ImsResponse imsResponse = new ImsResponse();
		List<InvitationOfficer> invitationOfficers = sanitizeUserData(invitationOfficerRepository.findByEventIdAndDeleted(eventId,
				false));
		List<InvitationOfficer> distinctInvitationOfficers = invitationOfficers.stream()
				.filter(distinctByKey(invOffcr -> invOffcr.getImsUser().getId())).collect(Collectors.toList());
		imsResponse.setData(distinctInvitationOfficers);
		imsResponse.setMessage("Retrieved Invitation admin data!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	@GetMapping("/getAllEventForInvitationAdmin/{imsUserId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllEventForInvitationAdmin(@PathVariable int imsUserId) {
		ImsResponse imsResponse = new ImsResponse();
		List<InvitationOfficer> invitationAdmins = sanitizeUserData(invitationOfficerRepository.findByImsUserId(imsUserId));
		List<Event> events = invitationAdmins.stream().map(invAdmin -> invAdmin.getEvent())
				.filter(distinctByKey(event -> event.getId())).collect(Collectors.toList());
		imsResponse.setMessage("Retrieved Events for the logged-in Invitation Admin!");
		imsResponse.setData(events);
		imsResponse.setSuccess(true);
		return imsResponse;

	}
	
	
	@Override
	@PostMapping("/save")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public InvitationOfficer create(@Valid @RequestBody  InvitationOfficer t) {
		// TODO Auto-generated method stub
		super.create(t);
		return new InvitationOfficer();
	}

	@Override
	@GetMapping("/getById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getById(@PathVariable int id) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((InvitationOfficer) super.getById(id).getData()));
		return imsResponse;
	}
	
	
	@PostMapping("/saveAll")
	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public List<InvitationOfficer> createAll(@Valid @RequestBody List<InvitationOfficer> t) {
		return sanitizeUserData(super.createAll(t));
	}

	@Override
	@DeleteMapping("/deleteById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		InvitationOfficer invitationOfficer = invitationOfficerRepository.findById(id).get();
		ImsUser imsUser = invitationOfficer.getImsUser();
		Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
		 imsUser.setRole(role);
		 imsUserRepository.save(imsUser);
		super.delete(id);
	}

	@Override
	@PutMapping("/update")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse update(@Valid @RequestBody InvitationOfficer t) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((InvitationOfficer) super.update(t).getData()));
		return imsResponse;
	}

	private InvitationOfficer sanitizeUserData(InvitationOfficer invitationOfficer) {
		ImsUser user = invitationOfficer.getImsUser();
		if (user != null) {
			user.setPassword(null);
			user.setPasswordHash(null);
			user.setPasswordSalt(null);
			user.setPasswordEmail(null);
			invitationOfficer.setImsUser(user);
		}
		return invitationOfficer;
	}
	
	private ImsUser filterUserData(ImsUser imsUser) {
		imsUser.setPassword(null);
		imsUser.setPasswordHash(null);
		imsUser.setPasswordSalt(null);
		imsUser.setPasswordEmail(null);
		return imsUser;
	}


	private List<InvitationOfficer> sanitizeUserData(List<InvitationOfficer> invitationOfficers) {
		List<InvitationOfficer> modInvitationOfficers = new ArrayList<>();
		invitationOfficers.forEach(invitationOfficer -> {
			modInvitationOfficers.add(sanitizeUserData(invitationOfficer));
		});
		return modInvitationOfficers;
	}
	

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
