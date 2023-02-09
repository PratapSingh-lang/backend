package in.co.bel.ims.initial.service.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

import in.co.bel.ims.initial.data.repository.CplRepository;
import in.co.bel.ims.initial.data.repository.EnclosureCplMappingRepository;
import in.co.bel.ims.initial.data.repository.EnclosureGroupRepository;
import in.co.bel.ims.initial.data.repository.EventRepository;
import in.co.bel.ims.initial.entity.Cpl;
import in.co.bel.ims.initial.entity.EnclosureCplMapping;
import in.co.bel.ims.initial.entity.EnclosureGroup;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController@CrossOrigin
@RequestMapping("/app/cpl")
public class CplController extends ImsServiceTemplate<Cpl, CplRepository>{

	@Autowired
	CplRepository cplRepository;
	
	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	EnclosureGroupRepository enclosureGroupRepository;
	
	@Autowired
	EnclosureCplMappingRepository cplMappingRepository;
	
	@GetMapping("/getCplByEnclosureGroup/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	List<Cpl> getCplByEnclosureGroup(@PathVariable int id){
		return cplRepository.findByEnclosureGroupIdAndDeleted(id, false);
	}
	
	
	@GetMapping("/getCplByEvent/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER') or hasRole('HIGHEROFFICER')")
	List<Cpl> getCplByEvent(@PathVariable int eventId){
		Event event = eventRepository.findById(eventId).get();
		List<EnclosureGroup> enclosureGroups = enclosureGroupRepository.findByVenueIdAndDeleted(event.getVenue().getId(), false);
		List<Integer> enclosureGrpIds = enclosureGroups.stream().map(item -> item.getId())
				.collect(Collectors.toList());
		List<Cpl> cplList = cplRepository.findByEnclosureGroupIdInAndDeleted(enclosureGrpIds, false);
		cplList = cplList.stream()
				.filter(distinctByKey(pass -> pass.getName()))
				.collect(Collectors.toList());
		return cplList;
	}
	
	@GetMapping("/getCplByEnclosure/{enclosureId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER') or hasRole('HIGHEROFFICER')")
	ImsResponse getCplByEventAndEnclosure(@PathVariable int enclosureId){
		ImsResponse imsResponse = new ImsResponse();
		List<Cpl> cplList = new ArrayList<>();
		List<EnclosureCplMapping> enclosureCplMappings = cplMappingRepository.findByEnclosureIdAndDeleted(enclosureId, false);
		for(EnclosureCplMapping enclosureCplMapping : enclosureCplMappings) {
			cplList.add(enclosureCplMapping.getCpl());
		}
		imsResponse.setData(cplList);
		imsResponse.setMessage("Data retrieved successfully");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
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
	public Cpl create(@Valid @RequestBody Cpl t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}


	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<Cpl> createAll(@Valid @RequestBody  List<Cpl> t) {
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
	public ImsResponse update(@Valid @RequestBody Cpl t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}
	
	
}
