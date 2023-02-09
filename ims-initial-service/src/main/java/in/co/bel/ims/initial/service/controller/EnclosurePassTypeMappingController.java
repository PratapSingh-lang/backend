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

import in.co.bel.ims.initial.data.repository.EnclosurePassTypeMappingRepository;
import in.co.bel.ims.initial.entity.Enclosure;
import in.co.bel.ims.initial.entity.EnclosurePassTypeMapping;
import in.co.bel.ims.initial.entity.PassCategory;
import in.co.bel.ims.initial.service.dto.EnclosurePassTypeMappingResponse;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController@CrossOrigin
@RequestMapping("/app/enclosurePassTypeMapping")
public class EnclosurePassTypeMappingController extends ImsServiceTemplate<EnclosurePassTypeMapping, EnclosurePassTypeMappingRepository>{

	@Autowired
	EnclosurePassTypeMappingRepository enclosurePassTypeMappingRepository;
	
	@GetMapping("/getAllForEvent/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllForEvent(@PathVariable int eventId) {
		ImsResponse imsResponse = new ImsResponse();
		List<EnclosurePassTypeMapping> enclPassTypeMappings = enclosurePassTypeMappingRepository.findByEventIdAndDeleted(eventId, false);
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Retrieved all the Enclosures and Pass Types for the event!");
		imsResponse.setData(enclPassTypeMappings);
		return imsResponse;
	}
	

	@GetMapping("/getAllForEventAndEnclosure/{eventId}/{enclosureId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllForEventAndEnclosure(@PathVariable int eventId, @PathVariable int enclosureId) {
		ImsResponse imsResponse = new ImsResponse();
		List<EnclosurePassTypeMapping> enclPassTypeMappings = enclosurePassTypeMappingRepository.findByEventIdAndEnclosureIdAndDeleted(eventId, enclosureId, false);
		imsResponse.setSuccess(true);
		imsResponse.setMessage("Retrieved all the Pass Types for the event and enclosure!");
		imsResponse.setData(enclPassTypeMappings);
		return imsResponse;
	}
	
	//@Override
	@GetMapping("/getEnclosurePassTypeMappingForEvent/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getEnclosurePassTypeMappingForEvent(@PathVariable int eventId) {
		ImsResponse imsResponse = new ImsResponse();
		List<EnclosurePassTypeMappingResponse> enclosurePassTypeMappingResponses = new ArrayList<>();
		List<EnclosurePassTypeMapping> enclosurePassTypeMappings = enclosurePassTypeMappingRepository.findByEventIdAndDeleted(eventId,false);
		Set<EnclosurePassTypeMapping> enclosurePassTypeMappingsDistinct = enclosurePassTypeMappings.stream()
				.filter(distinctByKey(enclosurePassTypeMapping -> enclosurePassTypeMapping.getEnclosure().getId()))
				.collect(Collectors.toSet());
		Map<Enclosure, PassCategory> enclosuresList = enclosurePassTypeMappingsDistinct.stream()
				.collect(Collectors.toMap(EnclosurePassTypeMapping::getEnclosure, EnclosurePassTypeMapping::getPassCategory));
		enclosuresList.forEach((enclosure, passCategory) -> {
			EnclosurePassTypeMappingResponse enclosurePassTypeMappingResponse = new EnclosurePassTypeMappingResponse();
			enclosurePassTypeMappingResponse.setEnclosure(enclosure);
			enclosurePassTypeMappingResponse.setEventMap(
					enclosurePassTypeMappings.stream().filter(eventPassTypeMapping -> eventPassTypeMapping.getEnclosure().getId() == enclosure.getId()).collect(Collectors.toMap(EnclosurePassTypeMapping::getId, EnclosurePassTypeMapping::getEvent)));
//			enclosurePassTypeMappingResponse.setPassCategoryMap(
//					enclosurePassTypeMappings.stream().filter(eventPassTypeMapping -> eventPassTypeMapping.getEnclosure().getId() == enclosure.getId()).collect(Collectors.toMap(EnclosurePassTypeMapping::getId, EnclosurePassTypeMapping::getPassCategory)));
			enclosurePassTypeMappingResponse.setPassSubcategoryMap(
					enclosurePassTypeMappings.stream().filter(eventPassTypeMapping -> eventPassTypeMapping.getEnclosure().getId() == enclosure.getId()).collect(Collectors.toMap(EnclosurePassTypeMapping::getId, EnclosurePassTypeMapping::getPassSubcategory)));
			enclosurePassTypeMappingResponses.add(enclosurePassTypeMappingResponse);

		});
		imsResponse.setData(enclosurePassTypeMappingResponses);
		imsResponse.setMessage("Retrieved Enclosure Pass Type Mappings data!");
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
	public EnclosurePassTypeMapping create(@Valid @RequestBody EnclosurePassTypeMapping t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<EnclosurePassTypeMapping> createAll(@Valid @RequestBody  List<EnclosurePassTypeMapping> t) {
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
	public ImsResponse update(@Valid @RequestBody EnclosurePassTypeMapping t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}
	
	
	@PostMapping("/deleteByEnclPassTypeMappingIds")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public void deleteByEnclPassTypeIds(@RequestBody List<Integer> enclPassTypeIds) {
		// TODO Auto-generated method stub
		for(Integer id: enclPassTypeIds) {
			super.delete(id);
		}
	}
	

	
	
}
