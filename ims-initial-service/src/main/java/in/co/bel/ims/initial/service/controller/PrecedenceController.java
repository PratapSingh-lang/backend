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

import in.co.bel.ims.initial.data.repository.PrecedenceRepository;
import in.co.bel.ims.initial.entity.Precedence;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController@CrossOrigin
@RequestMapping("/app/precedence")
public class PrecedenceController extends ImsServiceTemplate<Precedence, PrecedenceRepository>{

	@Autowired
	PrecedenceRepository precedenceRepository;
	
	
	@GetMapping("/getPrecedenceByArticle/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	List<Precedence> getPrecedenceByArticle(@PathVariable int id){
		return precedenceRepository.findByArticleIdAndDeleted(id, false);
	}


	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
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
	public Precedence create(@Valid @RequestBody Precedence t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}


	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<Precedence> createAll(@Valid @RequestBody  List<Precedence> t) {
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
	public ImsResponse update(@Valid @RequestBody Precedence t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}
	
	
}
