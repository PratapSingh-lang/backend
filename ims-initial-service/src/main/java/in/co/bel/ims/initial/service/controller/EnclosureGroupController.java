package in.co.bel.ims.initial.service.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import in.co.bel.ims.initial.data.repository.PassDayLimitCategoryRepository;
import in.co.bel.ims.initial.entity.Enclosure;
import in.co.bel.ims.initial.entity.EnclosureGroup;
import in.co.bel.ims.initial.entity.EnclosurePassTypeMapping;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.PassDayLimitCategory;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController@CrossOrigin
@RequestMapping("/app/enclosureGroup")
public class EnclosureGroupController extends ImsServiceTemplate<EnclosureGroup, EnclosureGroupRepository>{

	@Autowired
	EnclosureGroupRepository enclosureGroupRepository;
	@Autowired
	EnclosureRepository enclosureRepository;
	@Autowired
	EnclosurePassTypeMappingRepository enclosurePassTypeMappingRepository;
	@Autowired
	PassDayLimitCategoryRepository passDayLimitCategoryRepository;
	@Autowired
	EventRepository eventRepository;
	
	@GetMapping("/getEnclosureGroupByVenue/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	List<EnclosureGroup> getEnclosureGroupByVenue(@PathVariable int id){
		return enclosureGroupRepository.findByVenueIdAndDeleted(id, false); 
	}
	
	@GetMapping("/getEnclosureGroupByPassSubcategoryAndEvent/{eventId}/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	List<EnclosureGroup> getEnclosureGroupByPassSubcategoryAndEvent(@PathVariable int eventId, @PathVariable int id){
		List<EnclosurePassTypeMapping> enclosurePassTypeMappings = enclosurePassTypeMappingRepository.findAllByEventIdAndPassSubcategoryIdAndDeleted(eventId, id, false);
		List<EnclosureGroup> enclosureGrpList = new ArrayList<>();
		for(EnclosurePassTypeMapping enclosurePassTypeMapping: enclosurePassTypeMappings) {
			Enclosure encl = enclosureRepository.findById(enclosurePassTypeMapping.getEnclosure().getId()).get();
			enclosureGrpList.add(encl.getEnclosureGroup());
		}
		return enclosureGrpList; 
	}
	
	@GetMapping("/getEnclosureGroupByEvent/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('CITIZEN') or hasRole('COUNTEREMP')")
	List<EnclosureGroup> getEnclosureGroupByEvent(@PathVariable int eventId){
		Event event = eventRepository.findByIdAndDeleted(eventId, false);
		return enclosureGroupRepository.findByVenueIdAndDeleted(event.getVenue().getId(), false); 
	}
	
	@GetMapping("/getEnclosureGroupMappedForPaidTickets/{eventId}/{passSubcategoryId}/{roleId}")
	List<EnclosureGroup> getEnclosureGroupMappedForPaidTickets(@PathVariable int eventId,
			@PathVariable int passSubcategoryId, @PathVariable int roleId) {
		List<EnclosureGroup> enclosureGroups = new ArrayList<>();
		List<EnclosurePassTypeMapping> enclosurePassTypeMappings = enclosurePassTypeMappingRepository
				.findAllByEventIdAndPassSubcategoryIdAndDeleted(eventId, passSubcategoryId, false);

		System.out.println("EnclosureGroupController.getEnclosureGroupMappedForPaidTickets() Enclosure to Pass Type Mapping "+enclosurePassTypeMappings.size());
		List<Integer> enclosureGroupsFromEnclToPassTypeMappings = new ArrayList<>();
		if (enclosurePassTypeMappings != null && !enclosurePassTypeMappings.isEmpty()) {
			enclosureGroupsFromEnclToPassTypeMappings = enclosurePassTypeMappings.stream().map(
					enclosurePassTypeMapping -> enclosurePassTypeMapping.getEnclosure().getEnclosureGroup().getId())
					.collect(Collectors.toList());
		}

		List<Integer> enclosureGroupsFromPassDayLimitCategories = new ArrayList<>();
		List<PassDayLimitCategory> passDayLimitCategories = passDayLimitCategoryRepository
				.findByPassDayLimitEventIdAndPassDayLimitDateAndRoleIdAndPassSubcategoryIdAndDeleted(eventId,
						LocalDate.now(), roleId, passSubcategoryId, false);
		System.out.println("EnclosureGroupController.getEnclosureGroupMappedForPaidTickets() Pass Day Limit "+enclosurePassTypeMappings.size());
		if (passDayLimitCategories != null && !passDayLimitCategories.isEmpty()) {
			enclosureGroupsFromPassDayLimitCategories = passDayLimitCategories.stream()
					.map(passDayLimitCategory -> passDayLimitCategory.getEnclosureGroup().getId())
					.collect(Collectors.toList());
		}

		if (enclosureGroupsFromEnclToPassTypeMappings != null && !enclosureGroupsFromEnclToPassTypeMappings.isEmpty()) {
			List<Integer> enclGroupIds = enclosureGroupsFromEnclToPassTypeMappings.stream()
					.filter(enclosureGroupsFromPassDayLimitCategories::contains).collect(Collectors.toList());
			if (enclGroupIds != null && !enclGroupIds.isEmpty()) {
				enclosureGroups = enclGroupIds.stream().distinct().map(id -> enclosureGroupRepository.findById(id).get())
						.collect(Collectors.toList());
			}

		}

		return enclosureGroups;

	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER') or hasRole('CITIZEN')")
	public ImsResponse getAll() {
		// TODO Auto-generated method stub
		return super.getAll();
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getById(@PathVariable int id) {
		// TODO Auto-generated method stub
		return super.getById(id);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public EnclosureGroup create(@Valid @RequestBody EnclosureGroup t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public List<EnclosureGroup> createAll(@Valid @RequestBody  List<EnclosureGroup> t) {
		// TODO Auto-generated method stub
		return super.createAll(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse update(@Valid @RequestBody EnclosureGroup t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}
	
	
}
