package in.co.bel.ims.initial.service.controller;

import java.util.ArrayList;
import java.util.List;

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

import in.co.bel.ims.initial.data.repository.EventRepository;
import in.co.bel.ims.initial.data.repository.HigherOfficerRepository;
import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.RoleRepository;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.HigherOfficer;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.Role;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.LogLevelEnum;
import in.co.bel.ims.initial.service.util.LogUtil;
import in.co.bel.ims.initial.service.util.RoleEnum;

@RestController@CrossOrigin
@RequestMapping("/app/higherOfficer")
public class HigherOfficerController extends ImsServiceTemplate<HigherOfficer, HigherOfficerRepository>{

	@Autowired
	ImsUserRepository imsUserRepository;
	@Autowired
	EventRepository eventRepository;
	@Autowired
	HigherOfficerRepository higherOfficerRepository;
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	LogUtil log;
	
	@Override
	@PostMapping("/save")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public HigherOfficer create(@RequestBody HigherOfficer higherOfficer) {
		
		ImsUser imsUser = imsUserRepository.findById(higherOfficer.getImsUser().getId()).get();
		Role role = roleRepository.findById(RoleEnum.ROLE_HIGHEROFFICER.role).get();
		imsUser.setRole(role);
		imsUserRepository.saveAndFlush(imsUser);
		Event event = eventRepository.findById(higherOfficer.getEvent().getId()).get();
		higherOfficer.setEvent(event);
		higherOfficer.setImsUser(imsUser);
		log.saveLog(null, "Higher Officer has been created successfully with the mobile number "+ higherOfficer.getImsUser().getMobileNo(), "USER_CREATION", LogLevelEnum.INFO);
		super.create(higherOfficer);
		return new HigherOfficer();
	}
	
	@Override
	@GetMapping("/getById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getById(@PathVariable int id) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((HigherOfficer) super.getById(id).getData()));
		return imsResponse;
	}
	
	
	@PostMapping("/saveAll")
	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<HigherOfficer> createAll(@Valid @RequestBody List<HigherOfficer> t) {
		return sanitizeUserData(super.createAll(t));
	}

	@Override
	@DeleteMapping("/deleteById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	@PutMapping("/update")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse update(@Valid @RequestBody HigherOfficer t) {
		ImsResponse imsResponse = new ImsResponse();
		super.update(t);
		imsResponse.setData(new HigherOfficer());
		return imsResponse;
	}

	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	public ImsResponse getAll() {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((List<HigherOfficer>) super.getAll().getData()));
		return imsResponse;
	}
	private HigherOfficer sanitizeUserData(HigherOfficer higherOfficer) {
		ImsUser user = higherOfficer.getImsUser();
		if (user != null) {
			user.setPassword(null);
			user.setPasswordHash(null);
			user.setPasswordSalt(null);
			user.setPasswordEmail(null);
			higherOfficer.setImsUser(user);
		}
		return higherOfficer;
	}

	private List<HigherOfficer> sanitizeUserData(List<HigherOfficer> higherOfficers) {
		List<HigherOfficer> modHigherOfficers = new ArrayList<>();
		higherOfficers.forEach(higherOfficer -> {
			modHigherOfficers.add(sanitizeUserData(higherOfficer));
		});
		return modHigherOfficers;
	}
	
	private ImsUser filterUserData(ImsUser imsUser) {
		imsUser.setPassword(null);
		imsUser.setPasswordHash(null);
		imsUser.setPasswordEmail(null);
		imsUser.setPasswordSalt(null);
		return imsUser;
	}
	
}
