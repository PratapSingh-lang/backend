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
import in.co.bel.ims.initial.data.repository.OrganizationGroupRepository;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.OrganizationGroup;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.ImsUserTypeEnum;
import in.co.bel.ims.initial.service.util.RoleEnum;

@RestController
@CrossOrigin
@RequestMapping("/app/organizationGroup")
public class OrganizationGroupController extends ImsServiceTemplate<OrganizationGroup, OrganizationGroupRepository> {

	@Autowired
	OrganizationGroupRepository organizationGroupRepository;
	@Autowired
	ImsUserRepository imsUserRepository;

	@GetMapping("/getOrganizationGroupByUser/{imsUserId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getOrganizationGroupByRole(@PathVariable int imsUserId) {
		ImsResponse imsResponse = new ImsResponse();
		ImsUser imsUser = imsUserRepository.findById(imsUserId).get();

		if (imsUser.getRole().getId() == RoleEnum.ROLE_SUPERADMIN.role || imsUser.getRole().getId() == RoleEnum.ROLE_INVITATIONADMIN.role) {
			imsResponse.setData(organizationGroupRepository.findAll());
		} else {
			List<OrganizationGroup> organizationGroups = organizationGroupRepository.findAll().stream()
					.filter(organizationGroup -> organizationGroup.getAppendix().getId() == imsUser.getUserType()
							.getId() || organizationGroup.getAppendix().getId() == ImsUserTypeEnum.Annexure_D.type)
					.collect(Collectors.toList());
			imsResponse.setData(organizationGroups);
		}
		imsResponse.setMessage("Retrieved OrganizationGroup!");
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
	public OrganizationGroup create(@Valid @RequestBody OrganizationGroup t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<OrganizationGroup> createAll(@Valid @RequestBody  List<OrganizationGroup> t) {
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
	public ImsResponse update(@Valid @RequestBody OrganizationGroup t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}
	

}
