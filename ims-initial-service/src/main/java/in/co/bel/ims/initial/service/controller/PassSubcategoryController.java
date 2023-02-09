package in.co.bel.ims.initial.service.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import in.co.bel.ims.initial.data.repository.EventPassTypeMappingRepository;
import in.co.bel.ims.initial.data.repository.PassSubcategoryRepository;
import in.co.bel.ims.initial.entity.EventPassTypeMapping;
import in.co.bel.ims.initial.entity.PassSubcategory;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController@CrossOrigin
@RequestMapping("/app/passSubcategory")
public class PassSubcategoryController extends ImsServiceTemplate<PassSubcategory, PassSubcategoryRepository>{

	@Autowired
	PassSubcategoryRepository passSubcategoryRepository;
	
	@Autowired
	EventPassTypeMappingRepository eventPassTypeMappingRepository;
	
	@GetMapping("/getPassSubcategoryByCategory/{passCategoryId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('CITIZEN') or hasRole('COUNTEREMP') or hasRole('HIGHEROFFICER')")
	List<PassSubcategory> getPassSubcategoryByCategory(@PathVariable int passCategoryId){
		return passSubcategoryRepository.findByPassCategoryIdAndDeleted(passCategoryId, false);
	}
	
	@GetMapping("/getPassSubcategoryByCategory/{passCategoryId}/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('CITIZEN') or hasRole('COUNTEREMP')")
	List<PassSubcategory> getPassSubcategoryByCategory(@PathVariable int passCategoryId, @PathVariable int eventId){
		List<PassSubcategory> passSubcategoryList = new ArrayList<>();
		List<EventPassTypeMapping> eventPassTypeMappings =  eventPassTypeMappingRepository.findByEventIdAndPassCategoryIdAndDeleted(eventId, passCategoryId, false);
		Map<Integer, EventPassTypeMapping> uniqueEventPassTypeMappings = new HashMap<>();
		for (EventPassTypeMapping eventPassTypeMapping : eventPassTypeMappings) {
			if(eventPassTypeMapping.getPassSubcategory() != null) {
				if (uniqueEventPassTypeMappings.get(eventPassTypeMapping.getPassSubcategory().getId()) == null)
					uniqueEventPassTypeMappings.put(eventPassTypeMapping.getPassSubcategory().getId(), eventPassTypeMapping);
			}
		}    
		List<EventPassTypeMapping> eventPassTypeMappingDistinct = uniqueEventPassTypeMappings.values().stream().collect(Collectors.toList());
		for(EventPassTypeMapping eventPassTypeMapping: eventPassTypeMappingDistinct) {
			passSubcategoryList.add(passSubcategoryRepository.findByIdAndDeleted(eventPassTypeMapping.getPassSubcategory().getId(), false));
		}
		return passSubcategoryList;
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('CITIZEN')")
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
	public PassSubcategory create(@Valid @RequestBody PassSubcategory t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<PassSubcategory> createAll(@Valid @RequestBody  List<PassSubcategory> t) {
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
	public ImsResponse update(@Valid @RequestBody PassSubcategory t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}
}
