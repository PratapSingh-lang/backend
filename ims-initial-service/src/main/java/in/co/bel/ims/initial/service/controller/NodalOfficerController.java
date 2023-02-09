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

import in.co.bel.ims.initial.data.repository.DepartmentRepository;
import in.co.bel.ims.initial.data.repository.EventRepository;
import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.NodalOfficerRepository;
import in.co.bel.ims.initial.data.repository.RoleRepository;
import in.co.bel.ims.initial.entity.Department;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.NodalOfficer;
import in.co.bel.ims.initial.entity.Role;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.dto.NodalOfficerRequest;
import in.co.bel.ims.initial.service.dto.NodalOfficerResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.LogLevelEnum;
import in.co.bel.ims.initial.service.util.LogUtil;
import in.co.bel.ims.initial.service.util.RoleEnum;

@RestController
@CrossOrigin
@RequestMapping("/app/nodalOfficer")
public class NodalOfficerController extends ImsServiceTemplate<NodalOfficer, NodalOfficerRepository> {

	@Autowired
	ImsUserRepository imsUserRepository;
	@Autowired
	EventRepository eventRepository;
	@Autowired
	NodalOfficerRepository nodalOfficerRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	DepartmentRepository departmentRepository;
	@Autowired
	LogUtil log;
	@Override
	@PostMapping("/save")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public NodalOfficer create(@RequestBody NodalOfficer nodalOfficer) {

		ImsUser imsUser = imsUserRepository.findById(nodalOfficer.getImsUser().getId()).get();
		Role role = roleRepository.findById(RoleEnum.ROLE_NODALOFFICER.role).get();
		imsUser.setRole(role);
		imsUserRepository.saveAndFlush(imsUser);
		Event event = eventRepository.findById(nodalOfficer.getEvent().getId()).get();
		nodalOfficer.setEvent(event);
		nodalOfficer.setImsUser(imsUser);
		log.saveLog(null, "A new nodal officer has been created successfully with the mobile number "+ imsUser.getMobileNo(), "USER_CREATION", LogLevelEnum.INFO);
		super.create(nodalOfficer);
		return new NodalOfficer();
	}

	@GetMapping("/getDepartmentsForNodalOfficer/{userId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getDepartmentsForNodalOfficer(@PathVariable int userId) {
		ImsResponse imsResponse = new ImsResponse();
		List<NodalOfficer> nodalOfficers = sanitizeUserData(nodalOfficerRepository.findByImsUserId(userId));
		List<Department> departments = new ArrayList<>();
		nodalOfficers.forEach(nodalOfficer -> {
			departments.add(nodalOfficer.getDepartment());
		});
		List<Department> uniqueDepartments = departments.stream()
				.filter(distinctByKey(dep -> dep.getId()))
				.collect(Collectors.toList());
		imsResponse.setData(uniqueDepartments);
		imsResponse.setMessage("Retrieved Departments for the Nodal Officer!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@Override
	@GetMapping("/getAll")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getAll() {
		ImsResponse imsResponse = new ImsResponse();
		List<NodalOfficerResponse> nodalOfficerResponses = new ArrayList<>();
		List<NodalOfficer> nodalOfficers = sanitizeUserData(nodalOfficerRepository.findAllByDeleted(false));
		Set<NodalOfficer> nodalOfficersDistinct = nodalOfficers.stream()
				.filter(distinctByKey(nodalOfficer -> nodalOfficer.getImsUser().getId())).collect(Collectors.toSet());
		Map<ImsUser, Event> imsUsersList = nodalOfficersDistinct.stream()
				.collect(Collectors.toMap(NodalOfficer::getImsUser, NodalOfficer::getEvent));
		imsUsersList.forEach((imsUser, event) -> {
			NodalOfficerResponse nodalOfficerResponse = new NodalOfficerResponse();
			nodalOfficerResponse.setImsUser(filterUserData(imsUser));
			nodalOfficerResponse.setEvent(event);
			nodalOfficerResponse.setNodalDepartmentMap(
					nodalOfficers.stream().filter(nodalOfficer -> nodalOfficer.getImsUser().getId() == imsUser.getId())
							.collect(Collectors.toMap(NodalOfficer::getId, NodalOfficer::getDepartment)));
			nodalOfficerResponses.add(nodalOfficerResponse);

		});
		imsResponse.setData(nodalOfficerResponses);
		imsResponse.setMessage("Retrieved Nodal Officers data!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	
	@GetMapping("/getAllForEvent/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getAllForEvent(@PathVariable int eventId) {
		ImsResponse imsResponse = new ImsResponse();
		List<NodalOfficerResponse> nodalOfficerResponses = new ArrayList<>();
		List<NodalOfficer> nodalOfficers = sanitizeUserData(nodalOfficerRepository.findByEventIdAndDeleted(eventId, false));
		Set<NodalOfficer> nodalOfficersDistinct = nodalOfficers.stream()
				.filter(distinctByKey(nodalOfficer -> nodalOfficer.getImsUser().getId())).collect(Collectors.toSet());
		Map<ImsUser, Event> imsUsersList = nodalOfficersDistinct.stream()
				.collect(Collectors.toMap(NodalOfficer::getImsUser, NodalOfficer::getEvent));
		imsUsersList.forEach((imsUser, event) -> {
			NodalOfficerResponse nodalOfficerResponse = new NodalOfficerResponse();
			nodalOfficerResponse.setImsUser(filterUserData(imsUser));
			nodalOfficerResponse.setEvent(event);
			nodalOfficerResponse.setNodalDepartmentMap(
					nodalOfficers.stream().filter(nodalOfficer -> nodalOfficer.getImsUser().getId() == imsUser.getId())
							.collect(Collectors.toMap(NodalOfficer::getId, NodalOfficer::getDepartment)));
			nodalOfficerResponses.add(nodalOfficerResponse);

		});
		imsResponse.setData(nodalOfficerResponses);
		imsResponse.setMessage("Retrieved Nodal Officers data!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	@PostMapping("/updateNodalOfficer")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse updateNodalOfficer(@RequestBody NodalOfficerRequest nodalOfficerRequest) {

		ImsResponse imsResponse = new ImsResponse();

		Event event = eventRepository.findById(nodalOfficerRequest.getEventId()).get();
		ImsUser imsUser = imsUserRepository.findById(nodalOfficerRequest.getImsUserId()).get();
		Map<Integer, Integer> departmentMap = nodalOfficerRequest.getDepartmentMap();
		List<Integer> nodalOfficerIds = nodalOfficerRequest.getNodalOfficerIds();
		nodalOfficerRepository.deleteAllById(nodalOfficerIds);

		List<NodalOfficer> nodalOfficers = new ArrayList<>();
		NodalOfficerResponse nodalOfficerResponse = new NodalOfficerResponse();
		Map<Integer, Department> nodalDepartmentMap = new HashMap<>();
		departmentMap.forEach((departmentId, nodalOffcrId) -> {
			NodalOfficer nodalOfficer = null;
			if (nodalOffcrId != null) {
				nodalOfficer = sanitizeUserData(nodalOfficerRepository.findById(nodalOffcrId).get());
			} else {
				nodalOfficer = new NodalOfficer();
			}
			nodalOfficer.setEvent(event);
			nodalOfficer.setImsUser(imsUser);
			Department department = departmentRepository.findById(departmentId).get();
			nodalOfficer.setDepartment(department);
			nodalOfficers.add(nodalOfficer);
			nodalDepartmentMap.put(nodalOfficer.getId(), department);
		});
		nodalOfficerRepository.saveAll(nodalOfficers);
		imsResponse.setMessage("Nodal Officer is updated Successfully!");
		log.saveLog(null, "Nodal Officer is updated Successfully : "+ imsUser.getMobileNo(), "USER_UPDATE", LogLevelEnum.INFO);
		nodalOfficerResponse.setNodalDepartmentMap(nodalDepartmentMap);
		nodalOfficerResponse.setImsUser(filterUserData(imsUser));
		nodalOfficerResponse.setEvent(event);
		imsResponse.setData(nodalOfficerResponse);
		return imsResponse;

	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	@Override
	@PostMapping("/saveAll")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<NodalOfficer> createAll(@Valid  @RequestBody List<NodalOfficer> nodalOfficers) {
		ImsUser  imsUserReq = nodalOfficers.stream().findAny().get().getImsUser();
		ImsUser  imsUser = imsUserRepository.findById(imsUserReq.getId()).get();
		Role role = roleRepository.findById(RoleEnum.ROLE_NODALOFFICER.role).get();
		imsUser.setRole(role);
		imsUserRepository.saveAndFlush(imsUser);
		log.saveLog(null, nodalOfficers.size() + " New Nodal Officers were created Successfully!", "USER_CREATION", LogLevelEnum.INFO);
		super.createAll(nodalOfficers);
		return new ArrayList<>();
	}
	
	@GetMapping("/getAllForDepartmentId/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	ImsResponse getAllForDepartmentId(@PathVariable int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<NodalOfficer> nodalOfficers = sanitizeUserData(nodalOfficerRepository.findByDepartmentIdAndDeleted(departmentId, false));
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Retrieved Nodal Officers for the selected department!");
		imsResponse.setData(nodalOfficers);
		return imsResponse;
	}
	
	@GetMapping("/getAllEventForNodalOfficer/{imsUserId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getAllEventForNodalOfficer(@PathVariable int imsUserId) {
		ImsResponse imsResponse = new ImsResponse();
		List<NodalOfficer> nodalOfficers = sanitizeUserData(nodalOfficerRepository.findByImsUserId(imsUserId));
		List<Event> events = nodalOfficers.stream().map(nodalOfficer -> nodalOfficer.getEvent())
				.filter(distinctByKey(event -> event.getId())).collect(Collectors.toList());
		imsResponse.setMessage("Retrieved Events for the logged-in Nodal Officer!");
		imsResponse.setData(events);
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	


	@Override
	@GetMapping("/getById/{id}")
	public ImsResponse getById(@PathVariable int id) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((NodalOfficer) super.getById(id).getData()));
		return imsResponse;
	}
	
	

	@Override
	@DeleteMapping("/deleteById/{id}")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	@PutMapping("/update")
	public ImsResponse update(@Valid @RequestBody NodalOfficer t) {
		ImsResponse imsResponse = new ImsResponse();
		super.update(t);
		imsResponse.setData(new NodalOfficer());
		return imsResponse;
	}

	private NodalOfficer sanitizeUserData(NodalOfficer nodalOfficer) {
		ImsUser user = nodalOfficer.getImsUser();
		if (user != null) {
			user.setPassword(null);
			user.setPasswordHash(null);
			user.setPasswordSalt(null);
			user.setPasswordEmail(null);
			nodalOfficer.setImsUser(user);
		}
		return nodalOfficer;
	}

	private List<NodalOfficer> sanitizeUserData(List<NodalOfficer> nodalOfficers) {
		List<NodalOfficer> modNodalOfficers = new ArrayList<>();
		nodalOfficers.forEach(nodalOfficer -> {
			modNodalOfficers.add(sanitizeUserData(nodalOfficer));
		});
		return modNodalOfficers;
	}
	
	private ImsUser filterUserData(ImsUser imsUser) {
		imsUser.setPassword(null);
		imsUser.setPasswordHash(null);
		imsUser.setPasswordSalt(null);
		imsUser.setPasswordEmail(null);
		return imsUser;
	}
	

}
