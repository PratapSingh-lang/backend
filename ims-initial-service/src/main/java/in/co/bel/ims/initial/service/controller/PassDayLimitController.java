package in.co.bel.ims.initial.service.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.PassDayLimitRepository;
import in.co.bel.ims.initial.entity.PassDayLimit;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController
@CrossOrigin
@RequestMapping("/app/passDayLimit")
public class PassDayLimitController extends ImsServiceTemplate<PassDayLimit, PassDayLimitRepository> {
	
}
