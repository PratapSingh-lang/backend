package in.co.bel.ims.initial.service.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.EventPassTypeMappingRepository;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.EventPassTypeMapping;
import in.co.bel.ims.initial.entity.PassCategory;
import in.co.bel.ims.initial.service.dto.EventPassTypeMappingResponse;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController@CrossOrigin
@RequestMapping("/app/eventPassTypeMapping")
public class EventPassTypeMappingController extends ImsServiceTemplate<EventPassTypeMapping, EventPassTypeMappingRepository>{
	
	@Autowired
	EventPassTypeMappingRepository eventPassTypeMappingRepository;
	
	
	@GetMapping("/getAllForEvent/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllForEvent(@PathVariable int eventId) {
		ImsResponse imsResponse = new ImsResponse();
		List<EventPassTypeMapping> eventPassTypeMappings = eventPassTypeMappingRepository.findByEventIdAndDeleted(eventId, false);
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Retrieved all the Pass Types!");
		imsResponse.setData(eventPassTypeMappings);
		return imsResponse;
	}
	
	@Override
	@GetMapping("/getAll")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAll() {
		ImsResponse imsResponse = new ImsResponse();
		List<EventPassTypeMappingResponse> eventPassTypeMappingResponses = new ArrayList<>();
		List<EventPassTypeMapping> eventPassTypeMappings = eventPassTypeMappingRepository.findAllByDeleted(false);
		Set<EventPassTypeMapping> eventPassTypeMappingsDistinct = eventPassTypeMappings.stream()
				.filter(distinctByKey(eventPassTypeMapping -> eventPassTypeMapping.getEvent().getId()))
				.collect(Collectors.toSet());
		Map<Event, PassCategory> eventsList = eventPassTypeMappingsDistinct.stream()
				.collect(Collectors.toMap(EventPassTypeMapping::getEvent, EventPassTypeMapping::getPassCategory));
		eventsList.forEach((event, passCategory) -> {
			EventPassTypeMappingResponse eventPassTypeMappingResponse = new EventPassTypeMappingResponse();
			
			eventPassTypeMappingResponse.setEvent(event);
			eventPassTypeMappingResponse.setPassSubcategoryMap(
					eventPassTypeMappings.stream().filter(eventPassTypeMapping -> eventPassTypeMapping.getEvent().getId() == event.getId()).collect(Collectors.toMap(EventPassTypeMapping::getId, EventPassTypeMapping::getPassSubcategory)));
//			eventPassTypeMappingResponse.setPassSubcategoryMap(
//					eventPassTypeMappings.stream().filter(eventPassTypeMapping -> eventPassTypeMapping.getEvent().getId() == event.getId()).collect(Collectors.toMap(EventPassTypeMapping::getId, EventPassTypeMapping::getPassSubcategory)));
			eventPassTypeMappingResponses.add(eventPassTypeMappingResponse);

		});

		imsResponse.setData(eventPassTypeMappingResponses);
		imsResponse.setMessage("Retrieved Event Pass Type Mappings data!");
		imsResponse.setSuccess(true);
		return imsResponse;

	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getById(@PathVariable int id) {
		// TODO Auto-generated method stub
		return super.getById(id);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public EventPassTypeMapping create(@Valid @RequestBody EventPassTypeMapping t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<EventPassTypeMapping> createAll(@Valid @RequestBody  List<EventPassTypeMapping> t) {
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
	public ImsResponse update(@Valid @RequestBody EventPassTypeMapping t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}
	
	@PostMapping("/deleteByEventPassTypeMappingIds")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public void deleteByEventPassTypeIds(@RequestBody List<Integer> eventPassTypeIds) {
		// TODO Auto-generated method stub
		for(Integer id: eventPassTypeIds) {
			super.delete(id);
		}
	}

}
