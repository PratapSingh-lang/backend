package in.co.bel.ims.initial.service.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.UserIpaddressRepository;
import in.co.bel.ims.initial.entity.UserIpaddress;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController
@CrossOrigin
@RequestMapping("/app/userIpaddress")
public class UserIpaddressController extends ImsServiceTemplate<UserIpaddress, UserIpaddressRepository> {
	
}
