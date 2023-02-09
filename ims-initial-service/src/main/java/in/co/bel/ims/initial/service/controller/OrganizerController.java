package in.co.bel.ims.initial.service.controller;

import java.util.ArrayList;
import java.util.List;
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

import in.co.bel.ims.initial.data.repository.AnnexUsersRepository;
import in.co.bel.ims.initial.data.repository.EventRepository;
import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.OrganizerRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.data.repository.UserTypeRepository;
import in.co.bel.ims.initial.entity.AnnexUsers;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.Organizer;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.entity.UserType;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.ImsUserTypeEnum;
import in.co.bel.ims.initial.service.util.LogLevelEnum;
import in.co.bel.ims.initial.service.util.LogUtil;
import in.co.bel.ims.initial.service.util.PassStatusEnum;

@RestController@CrossOrigin
@RequestMapping("/app/organizer")
public class OrganizerController extends ImsServiceTemplate<Organizer, OrganizerRepository>{

	@Autowired
	ImsUserRepository imsUserRepository;
	@Autowired
	AnnexUsersRepository annexUserRepository;
	@Autowired
	EventRepository eventRepository;
	@Autowired
	PassRepository passRepository;
	@Autowired
	OrganizerRepository organizerRepository;
	@Autowired
	UserTypeRepository userTypeRepository;
	@Autowired
	private AnnexUsersRepository annexUsersRepository;
	@Autowired
	LogUtil log;
	@Override
	@PostMapping("/save")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public Organizer create(@RequestBody Organizer organizer) {
		ImsUser imsUser = imsUserRepository.findById(organizer.getImsUser().getId()).get();
		List<AnnexUsers> annexUsers = annexUserRepository.findByMobileNoAndDeleted(imsUser.getMobileNo(), false);
		UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Organizer.type).get();
		imsUser.setUserType(userType);
		imsUserRepository.save(imsUser);
		for(AnnexUsers annexUser: annexUsers) {
			annexUser.setUserType(userType);
			annexUserRepository.save(annexUser);
		}
		Event event = eventRepository.findById(organizer.getEvent().getId()).get();
		organizer.setEvent(event);
		organizer.setImsUser(imsUser);
		log.saveLog(null, "A new organizer has been created successfully with the mobile number "+ imsUser.getMobileNo(), "USER_CREATION", LogLevelEnum.INFO);
		return sanitizeUserData(super.create(organizer));
		
	}
	@Override
	@GetMapping("/getById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getById(@PathVariable int id) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((Organizer) super.getById(id).getData()));
		return imsResponse;
	}
	
	
	@PostMapping("/saveAll")
	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<Organizer> createAll(@Valid @RequestBody  List<Organizer> t) {
		return sanitizeUserData(super.createAll(t));
	}

	@Override
	@DeleteMapping("/deleteById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	@PutMapping("/update")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse update(@Valid @RequestBody Organizer t) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((Organizer) super.update(t).getData()));
		return imsResponse;
	}

	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAll() {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((List<Organizer>) super.getAll().getData()));
		return imsResponse;
	}
	
	//Service that returns list of Organizers from annex_users table
	@GetMapping("/getOrganizers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getWoPUsers() {
		ImsResponse imsResponse = new ImsResponse();
		List<AnnexUsers> annexUsersList = annexUsersRepository.findByUserTypeIdAndDeleted(ImsUserTypeEnum.Organizer.type, false);
		imsResponse.setMessage("Retrieved Organizers data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(annexUsersList);
		return imsResponse;
	}
	
	private Organizer sanitizeUserData(Organizer organizer) {
		ImsUser user = organizer.getImsUser();
		if (user != null) {
			user.setPassword(null);
			user.setPasswordHash(null);
			user.setPasswordSalt(null);
			user.setPasswordEmail(null);
			organizer.setImsUser(user);
		}
		return organizer;
	}

	private List<Organizer> sanitizeUserData(List<Organizer> organizers) {
		List<Organizer> modOrganizers = new ArrayList<>();
		organizers.forEach(organizer -> {
			modOrganizers.add(sanitizeUserData(organizer));
		});
		return modOrganizers;
	}
	
	@GetMapping("/getUnAssignedOrganizersByEventId/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getUnAssignedOrganizersByEventId(@PathVariable int eventId) {
		List<AnnexUsers> unassignedOrganizers = new ArrayList<>();
		List<AnnexUsers> annexUsersList = new ArrayList<>();
		List<AnnexUsers> annexUsers = annexUserRepository.findByUserTypeIdAndDeleted(ImsUserTypeEnum.Organizer.type,false);
		for(AnnexUsers annexUser: annexUsers) {
			ImsUser imsUser = imsUserRepository.findByMobileNoAndDeleted(annexUser.getMobileNo(), false);
			if(imsUser != null) {
			List<Organizer> organizers = organizerRepository.findByImsUserIdAndEventIdAndDeleted(imsUser.getId(), eventId, false);
			if(organizers.size() > 0) {
				annexUsersList.add(annexUser);
			}
			}
		}
		for(AnnexUsers annexUser: annexUsersList) {
			ImsUser imsUser = imsUserRepository.findByMobileNoAndDeleted(annexUser.getMobileNo(), false);
			if(imsUser != null) {
			List<Pass> passes = passRepository.findAllByImsUserByImsUserIdIdAndEventIdAndDeleted(imsUser.getId(), eventId, false);
			passes =  passes.stream().filter(pass -> pass.getPassStatus() != null
					&& pass.getPassStatus().getId() != PassStatusEnum.CANCELLED.type &&   pass.getPassStatus().getId() != PassStatusEnum.RSVP_REGRETTED.type).collect(Collectors.toList());
			if(passes.size() == 0) {
				unassignedOrganizers.add(annexUser);
			}
			}
		}
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(unassignedOrganizers);
		return imsResponse;
	}
}
