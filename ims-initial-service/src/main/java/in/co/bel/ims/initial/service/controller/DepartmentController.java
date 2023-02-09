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

import in.co.bel.ims.initial.data.repository.DepartmentRepository;
import in.co.bel.ims.initial.entity.Department;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController@CrossOrigin
@RequestMapping("/app/department")
public class DepartmentController extends ImsServiceTemplate<Department, DepartmentRepository>{

	@Autowired
	DepartmentRepository departmentRepository;
	
	
	@GetMapping("/getDepartmentByOrganization/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	List<Department> getDepartmentByOrganization(@PathVariable int id){
		return departmentRepository.findByOrganizationIdAndDeleted(id, false);
	}


	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
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
	public Department create(@Valid @RequestBody Department t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}


	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<Department> createAll(@Valid @RequestBody  List<Department> t) {
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
	public ImsResponse update(@Valid @RequestBody Department t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}
	
	
}
