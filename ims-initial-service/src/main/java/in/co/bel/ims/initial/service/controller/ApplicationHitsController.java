package in.co.bel.ims.initial.service.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.ApplicationHitsRepository;
import in.co.bel.ims.initial.entity.ApplicationHits;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController
@CrossOrigin
@RequestMapping("/app/applicationHits")
public class ApplicationHitsController extends ImsServiceTemplate<ApplicationHits, ApplicationHitsRepository> {
	
}
