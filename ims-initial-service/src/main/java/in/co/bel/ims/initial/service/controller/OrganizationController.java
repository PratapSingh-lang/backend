package in.co.bel.ims.initial.service.controller;

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

import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.OrganizationRepository;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.Organization;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.RoleEnum;

@RestController@CrossOrigin
@RequestMapping("/app/organization")
public class OrganizationController extends ImsServiceTemplate<Organization, OrganizationRepository>{

	@Autowired
	OrganizationRepository organizationRepository;
	@Autowired
	ImsUserRepository imsUserRepository;
	
	@GetMapping("/getOrgByOrgGrp/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	List<Organization> getOrgByOrgGrp(@PathVariable int id){
		return organizationRepository.findByOrganizationGroupIdAndDeleted(id, false);
	}
	
	@GetMapping("/getOrganizationByUser/{imsUserId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getOrganizationByUser(@PathVariable int imsUserId) {
		ImsResponse imsResponse = new ImsResponse();
		ImsUser imsUser = imsUserRepository.findById(imsUserId).get();

		if (imsUser.getRole().getId() == RoleEnum.ROLE_SUPERADMIN.role) {
			imsResponse.setData(organizationRepository.findAll());
		} else {
			List<Organization> organizations = organizationRepository.findAll().stream()
					.filter(organization -> organization.getId() == imsUser.getDepartment().getOrganization().getId())
					.collect(Collectors.toList());
			imsResponse.setData(organizations);
		}
		imsResponse.setMessage("Retrieved Organizations!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER') or hasRole('HIGHEROFFICER')")
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
	public Organization create(@Valid @RequestBody Organization t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<Organization> createAll(@Valid @RequestBody  List<Organization> t) {
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
	public ImsResponse update(@Valid @RequestBody Organization t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}
	
}
