package in.co.bel.ims.initial.service.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.ScanningOfficerRepository;
import in.co.bel.ims.initial.entity.ScanningOfficer;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.dto.ImsSMS;
import in.co.bel.ims.initial.service.dto.LoginRequest;
import in.co.bel.ims.initial.service.dto.UserRequest;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.ImsCipherUtil;

@RestController
@CrossOrigin
@RequestMapping("/app/scanningOfficer")
public class ScanningOfficerController extends ImsServiceTemplate<ScanningOfficer, ScanningOfficerRepository> {

	@Autowired
	ScanningOfficerRepository scanningOfficerRepository;

	@Value("${smsEnabled}")
	private boolean smsEnabled;
	@Value("${LOG_OTP_IMS}")
	private String loginOtp;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	ImsSmsSender imsSmsSender;

	private final String SMS_OTP_TEMPLATE = "/templates/sms_OTP_Template.txt";

	@PostMapping("/validateUser")
	public ImsResponse validateUser(@RequestBody UserRequest userReq) {
		ImsResponse imsResponse = new ImsResponse();
		ScanningOfficer user = scanningOfficerRepository.getByMobileNoAndDeleted(userReq.getMobileNo(), false);
		if (user != null) {
			String otp = "123456";
			if (smsEnabled) {
				otp = ImsCipherUtil.generateOTP();
				String message = "Dear Officer, Your Login OTP is " + otp + ". From Ministry of Defence";
				ImsSMS sms = new ImsSMS();
				sms.setMobileNo(userReq.getMobileNo());
				sms.setMessage(message);
				try{
					InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_OTP_TEMPLATE);
					String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
					inputStream.close();
	
					otpMailText = otpMailText.replace("<<OTP>>", otp);
					otpMailText = otpMailText.replace("<<VALIDITY_MIN>>", "10");
					sms.setMessage(otpMailText);
				} catch(IOException ioe) {
					System.out.println("Error while reading OTP templates : "+ioe.getMessage());
				}
				sms.setTemplateId(loginOtp);
				imsSmsSender.send(sms);
			}
			user.setPasswordHash(passwordEncoder.encode(otp));
			super.update(user);
			imsResponse.setSuccess(true);

		}
		return imsResponse;

	}

	@PostMapping("/signin")
	public boolean authenticateUser(@RequestBody LoginRequest loginRequest) {
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();
		ScanningOfficer user = scanningOfficerRepository.getByMobileNoAndDeleted(username, false);
		if (passwordEncoder.matches(password, user.getPasswordHash())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	@PostMapping("/save")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('SCANNINGOFFICER') or hasRole('INVITATIONADMIN')")
	public ScanningOfficer create(@Valid @RequestBody  ScanningOfficer t) {
		// TODO Auto-generated method stub
		return sanitizeUserData(super.create(t));
	}

	@SuppressWarnings("unchecked")
	@Override
	@GetMapping("/getAll")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('SCANNINGOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAll() {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((List<ScanningOfficer>) super.getAll().getData()));
		return imsResponse;
	}

	@GetMapping("/getAllScanningOfficers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('SCANNINGOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllScanningOfficers() {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((List<ScanningOfficer>) scanningOfficerRepository.getAllByUsher(false)));
		return imsResponse;
	}
	
	@GetMapping("/getAllUshers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('SCANNINGOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllUshers() {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((List<ScanningOfficer>) scanningOfficerRepository.getAllByUsher(true)));
		return imsResponse;
	}
	
	
	@Override
	@GetMapping("/getById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('SCANNINGOFFICER')")
	public ImsResponse getById(@PathVariable int id) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((ScanningOfficer) super.getById(id).getData()));
		return imsResponse;
	}

	@PostMapping("/saveAll")
	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<ScanningOfficer> createAll(@Valid @RequestBody List<ScanningOfficer> t) {
		return sanitizeUserData(super.createAll(t));
	}

	@Override
	@DeleteMapping("/deleteById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	@PutMapping("/update")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse update(@Valid @RequestBody ScanningOfficer t) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((ScanningOfficer) super.update(t).getData()));
		return imsResponse;
	}

	private ScanningOfficer sanitizeUserData(ScanningOfficer scanningOfficer) {
		scanningOfficer.setPasswordHash(null);
		scanningOfficer.setPasswordSalt(null);
		return scanningOfficer;
	}

	private List<ScanningOfficer> sanitizeUserData(List<ScanningOfficer> ScanningOfficers) {
		List<ScanningOfficer> modScanningOfficers = new ArrayList<>();
		ScanningOfficers.forEach(ScanningOfficer -> {
			modScanningOfficers.add(sanitizeUserData(ScanningOfficer));
		});
		return modScanningOfficers;
	}

}
