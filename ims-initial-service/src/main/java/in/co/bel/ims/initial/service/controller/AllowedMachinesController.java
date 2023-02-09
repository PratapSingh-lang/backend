package in.co.bel.ims.initial.service.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

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

import in.co.bel.ims.initial.data.repository.AllowedMachinesRepository;
import in.co.bel.ims.initial.entity.AllowedMachines;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController
@CrossOrigin
@RequestMapping("/app/allowedMachines")
public class AllowedMachinesController extends ImsServiceTemplate<AllowedMachines, AllowedMachinesRepository> {
	
	
	@Override
	@PostMapping("/save")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public AllowedMachines create(@Valid @RequestBody AllowedMachines t) {
		// TODO Auto-generated method stub
		return sanitizeUserData(super.create(t));
	}

	@SuppressWarnings("unchecked")
	@Override
	@GetMapping("/getAll")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getAll() {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((List<AllowedMachines>) super.getAll().getData()));
		return imsResponse;
	}

	@Override
	@GetMapping("/getById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getById(@PathVariable int id) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((AllowedMachines) super.getById(id).getData()));
		return imsResponse;
	}
	
	
	@PostMapping("/saveAll")
	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<AllowedMachines> createAll(@Valid @RequestBody List<AllowedMachines> t) {
		return sanitizeUserData(super.createAll(t));
	}

	@Override
	@DeleteMapping("/deleteById/{id}")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	@PutMapping("/update")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse update(@Valid @RequestBody AllowedMachines t) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((AllowedMachines) super.update(t).getData()));
		return imsResponse;
	}

	private AllowedMachines sanitizeUserData(AllowedMachines allowedMachines) {
		ImsUser user = allowedMachines.getImsUser();
		if (user != null) {
			user.setPassword(null);
			user.setPasswordHash(null);
			user.setPasswordSalt(null);
			user.setPasswordEmail(null);
			allowedMachines.setImsUser(user);
		}
		return allowedMachines;
	}

	private List<AllowedMachines> sanitizeUserData(List<AllowedMachines> allowedMachiness) {
		List<AllowedMachines> modAllowedMachiness = new ArrayList<>();
		allowedMachiness.forEach(allowedMachines -> {
			modAllowedMachiness.add(sanitizeUserData(allowedMachines));
		});
		return modAllowedMachiness;
	}

}
