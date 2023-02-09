package in.co.bel.ims.initial.service.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.AppendixRepository;
import in.co.bel.ims.initial.entity.Appendix;
import in.co.bel.ims.initial.infra.util.GenerateExcelTemplate;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.LogLevelEnum;
import in.co.bel.ims.initial.service.util.LogUtil;

@RestController
@CrossOrigin
@RequestMapping("/app/appendix")
public class AppendixController extends ImsServiceTemplate<Appendix, AppendixRepository> {

	@Autowired
	GenerateExcelTemplate generateTemp;

	@Autowired
	LogUtil log;
	
	@GetMapping("/downloadAnnexATemplate")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ResponseEntity<Resource> downloadAnnexATemplate() throws IOException {
		String filename = "Template A- Official-Ministry-Dept.xlsx";
		String filepath = "/templates/";
		InputStream in = getClass().getResourceAsStream(filepath + filename);
		generateTemp.generateTemplate(in, filename);
		Path path = Paths.get(filename);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.saveLog(null, "An error occurred while downloading the Annex A template ","DOWNLOAD", LogLevelEnum.ERROR);
			e.printStackTrace();
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.body(resource);
	}

	@GetMapping("/downloadAnnexBTemplate")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ResponseEntity<Resource> downloadAnnexBTemplate() throws IOException {
		String filename = "Template B- PSU-Autonomous.xlsx";
		String filepath = "/templates/";
		InputStream in = getClass().getResourceAsStream(filepath + filename);
		generateTemp.generateTemplate(in, filename);
		Path path = Paths.get(filename);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.saveLog(null, "An error occurred while downloading the Annex B template ","DOWNLOAD", LogLevelEnum.ERROR);
			e.printStackTrace();
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.body(resource);
	}

	@GetMapping("/downloadAnnexCTemplate")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ResponseEntity<Resource> downloadAnnexCTemplate() throws IOException {
		String filename = "Template C- Comission-Committees-Courts.xlsx";
		String filepath = "/templates/";
		InputStream in = getClass().getResourceAsStream(filepath + filename);
		generateTemp.generateTemplate(in, filename);
		Path path = Paths.get(filename);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.saveLog(null, "An error occurred while downloading the Annex C template ","DOWNLOAD", LogLevelEnum.ERROR);
			e.printStackTrace();
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.body(resource);
	}

	@GetMapping("/downloadAnnexDTemplate")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ResponseEntity<Resource> downloadAnnexDTemplate() throws IOException {
		String filename = "Template D- Others-Guest.xlsx";
		String filepath = "/templates/";
		InputStream in = getClass().getResourceAsStream(filepath + filename);
		generateTemp.generateTemplate(in, filename);
		Path path = Paths.get(filename);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.saveLog(null, "An error occurred while downloading the Annex D template ","DOWNLOAD", LogLevelEnum.ERROR);
			e.printStackTrace();
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.body(resource);
	}

	@GetMapping("/downloadAnnexETemplate")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ResponseEntity<Resource> downloadAnnexETemplate() throws IOException {
		String filename = "Template E- Delegates-Consulate.xlsx";
		String filepath = "/templates/";
		InputStream in = getClass().getResourceAsStream(filepath + filename);
		generateTemp.generateTemplate(in, filename);
		Path path = Paths.get(filename);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.saveLog(null, "An error occurred while downloading the Annex E template ","DOWNLOAD", LogLevelEnum.ERROR);
			e.printStackTrace();
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.body(resource);
	}

	@GetMapping("/downloadAnnexFTemplate")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ResponseEntity<Resource> downloadAnnexFTemplate() throws IOException {
		String filename = "Template F- MP.xlsx";
		String filepath = "/templates/";
		InputStream in = getClass().getResourceAsStream(filepath + filename);
		generateTemp.generateTemplate(in, filename);
		Path path = Paths.get(filename);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.saveLog(null, "An error occurred while downloading the Annex F template ","DOWNLOAD", LogLevelEnum.ERROR);
			e.printStackTrace();
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.body(resource);
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
	public Appendix create(@Valid @RequestBody Appendix t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<Appendix> createAll(@Valid @RequestBody  List<Appendix> t) {
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
	public ImsResponse update(@Valid @RequestBody Appendix t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}

}
