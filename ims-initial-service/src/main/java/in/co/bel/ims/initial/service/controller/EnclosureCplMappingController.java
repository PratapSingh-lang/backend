package in.co.bel.ims.initial.service.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.EnclosureCplMappingRepository;
import in.co.bel.ims.initial.entity.EnclosureCplMapping;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController@CrossOrigin
@RequestMapping("/app/enclosureCplMapping")
public class EnclosureCplMappingController extends ImsServiceTemplate<EnclosureCplMapping, EnclosureCplMappingRepository>{

	@Autowired
	EnclosureCplMappingRepository cplMappingRepository;
	
	@GetMapping("/findAllByVenueId/{venueId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	ImsResponse findAllByVenueId(@PathVariable int venueId){
		ImsResponse imsResponse = new ImsResponse();
		List<EnclosureCplMapping> enclosureCplMappings = cplMappingRepository.findByEnclosureEnclosureGroupVenueIdAndDeleted(venueId, false);
		imsResponse.setData(enclosureCplMappings);
		imsResponse.setMessage("Retrieved Enclosure to CPL Mappings for the venue id!");
		imsResponse.setSuccess(true);
		return imsResponse;
		
	}
	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER') or hasRole('CITIZEN')")
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
	public EnclosureCplMapping create(@Valid @RequestBody EnclosureCplMapping t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<EnclosureCplMapping> createAll(@Valid @RequestBody  List<EnclosureCplMapping> t) {
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
	public ImsResponse update(@Valid @RequestBody EnclosureCplMapping t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}

}
