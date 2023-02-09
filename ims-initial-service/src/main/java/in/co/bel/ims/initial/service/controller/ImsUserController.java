package in.co.bel.ims.initial.service.controller;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnore;

import in.co.bel.ims.initial.data.repository.AllowedMachinesRepository;
import in.co.bel.ims.initial.data.repository.AnnexUsersRepository;
import in.co.bel.ims.initial.data.repository.CaptchaRepository;
import in.co.bel.ims.initial.data.repository.HigherOfficerRepository;
import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.InvitationOfficerRepository;
import in.co.bel.ims.initial.data.repository.NodalOfficerRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.data.repository.RoleRepository;
import in.co.bel.ims.initial.data.repository.UserPasswordPolicyRepository;
import in.co.bel.ims.initial.data.repository.UserTypeRepository;
import in.co.bel.ims.initial.entity.AllowedMachines;
import in.co.bel.ims.initial.entity.AnnexUsers;
import in.co.bel.ims.initial.entity.Captcha;
import in.co.bel.ims.initial.entity.HigherOfficer;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.InvitationOfficer;
import in.co.bel.ims.initial.entity.NodalOfficer;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.entity.Role;
import in.co.bel.ims.initial.entity.UserPasswordPolicy;
import in.co.bel.ims.initial.security.jwt.JwtUtils;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.dto.ImsSMS;
import in.co.bel.ims.initial.service.dto.PasswordChangeRequest;
import in.co.bel.ims.initial.service.dto.PublicUserRequest;
import in.co.bel.ims.initial.service.dto.UserData;
import in.co.bel.ims.initial.service.dto.UserRequest;
import in.co.bel.ims.initial.service.dto.UserValidationRequest;
import in.co.bel.ims.initial.service.exception.DuplicateUserException;
import in.co.bel.ims.initial.service.util.ImsCipherUtil;
import in.co.bel.ims.initial.service.util.ImsJpaUpdateUtil;
import in.co.bel.ims.initial.service.util.ImsUserTypeEnum;
import in.co.bel.ims.initial.service.util.LogLevelEnum;
import in.co.bel.ims.initial.service.util.LogUtil;
import in.co.bel.ims.initial.service.util.LoginStatusEnum;
import in.co.bel.ims.initial.service.util.PassStatusEnum;
import in.co.bel.ims.initial.service.util.RoleEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@CrossOrigin
@RequestMapping("/app/imsUser")
public class ImsUserController {

	@Autowired
	ImsUserRepository imsUserRepository;
	@Autowired
	ImsSmsSender imsSmsSender;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private CaptchaRepository captchaRepository;
	@Autowired
	NodalOfficerRepository nodalOfficerRepository;
	@Autowired
	HigherOfficerRepository higherOfficerRepository;
	@Autowired
	AllowedMachinesRepository allowedMachinesRepository;
	@Autowired
	PassRepository passRepository;
	@Autowired
	InvitationOfficerRepository invitationOfficerRepository;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UserTypeRepository userTypeRepository;
	@Autowired
	private AnnexUsersRepository annexUsersRepository;
	@Autowired
	private UserPasswordPolicyRepository userPasswordPolicyRepository;
	@Autowired
	ImsEmailService emailService;
	
	@Value("${LOG_OTP_IMS}")
	private String loginOtp;
	@Value("${LOG_OTP_FPWD_IMS}")
	private String loginForgotOtp;
	@Value("${smsEnabled}")
	private boolean smsEnabled;
	@Value("${clientAccessRestricted}")
	private boolean clientAccessRestricted;
	@Value("${jwtSecret}")
	private String jwtSecret;
	
	@Autowired
	LogUtil log;

	private final String SMS_OTP_TEMPLATE = "/templates/sms_OTP_Template.txt";
	private final String SMS_PWD_FORGOT_TEMPLATE = "/templates/sms_Pwd_Forgot_Template.txt";

	@PostMapping("/save")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public UserData create(@RequestBody ImsUser imsUser) {

		String salt = ImsCipherUtil.generateSalt();
//		String password = ImsCipherUtil.generateRandomPassword();
//		emailService.sendPassword(user.getEmail(), password);
	    String hash = ImsCipherUtil.generateHash("@Ims1234", salt);
		imsUser.setPasswordHash(hash);
		imsUser.setPasswordSalt(salt);
		imsUser.setStatus(LoginStatusEnum.FIRST_LOGIN.status);
		imsUser.setPassword("$2a$10$j7Pd/0bsnKt70zzTfLJtU.8OyLqLvq.Fczve.Pd5xYlmFPwsMdxa2");
		imsUser.setPasswordEmail("$2a$10$j7Pd/0bsnKt70zzTfLJtU.8OyLqLvq.Fczve.Pd5xYlmFPwsMdxa2");
		imsUser.setCreatedTimestamp(LocalDateTime.now());
		imsUser.setRole(roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get());
			
		AnnexUsers annexUser = new AnnexUsers();
		annexUser.setAddress(imsUser.getResidentialAddress());
		annexUser.setConstituency(imsUser.getConstituency());
		annexUser.setDeleted(false);
		annexUser.setDepartment(imsUser.getDepartment());
		annexUser.setDesignation(imsUser.getDesignation());
		annexUser.setDob(imsUser.getDateOfBirth());
		annexUser.setDoj(imsUser.getDoj());
		annexUser.setIdentityProof(imsUser.getIdentityProof());
		annexUser.setEmail(imsUser.getEmail());
		//annexUser.setEmpNo(imsUser.getEmpNo());
		annexUser.setEnclosureGroup(imsUser.getEnclosureGroup());
		annexUser.setEquivalentStatus(imsUser.getEquivalentStatus());
		annexUser.setIdproof(imsUser.getIdProofNo());
		annexUser.setMaritalStatus(imsUser.getMaritalStatus());
		annexUser.setMobileNo(imsUser.getMobileNo());
		annexUser.setName(imsUser.getName());
		annexUser.setNationality(imsUser.getNationality());
		annexUser.setOfficeAddress(imsUser.getOfficeAddress());
		annexUser.setPositionMissionConsulate(imsUser.getPositionMissionConsulate());
		annexUser.setUserType(imsUser.getUserType());
		annexUser.setPayLevel(imsUser.getPayLevel());
		annexUser.setBasicPay(imsUser.getBasicPay());
		if(imsUser.getPrecedence()!=null) {
			annexUser.setPrecedence(imsUser.getPrecedence());
		}
			
		annexUser.setRecommendingOfficerDesignation(imsUser.getRecommendingOfficerDesignation());
		annexUser.setRecommendingOfficerName(imsUser.getRecommendingOfficerName());
		annexUser.setRemarks(imsUser.getRemarks());
		annexUser.setSalutation(imsUser.getSalutation());
		
		if(imsUser.getUserType().getId() == ImsUserTypeEnum.WoPUser.type || imsUser.getUserType().getId() == ImsUserTypeEnum.Organizer.type) {
			ImsUser userExisting = imsUserRepository.getByMobileNoAndDeleted(imsUser.getMobileNo(), false);
			annexUsersRepository.save(annexUser);
			if(userExisting == null) {
				log.saveLog(null,"Created user successfully with the mobile number "+ imsUser.getMobileNo(), "USER_CREATION", LogLevelEnum.INFO);
				return filterUserData(imsUserRepository.save(imsUser));
			}
			return filterUserData(imsUser);
		}else {
			ImsUser userExisting = imsUserRepository.getByMobileNoAndDeleted(imsUser.getMobileNo(), false);
			annexUser.setEmpNo(imsUser.getEmpNo());
			List<ImsUser> userExistingWithEmpId = imsUserRepository.getByEmpNoAndDeleted(imsUser.getEmpNo(), false);
			List<ImsUser> userExistingWithIdProofNo = imsUserRepository.getByIdProofNoAndDeleted(imsUser.getIdProofNo(), false);
			if(imsUser.getUserType().getId() == ImsUserTypeEnum.Annexure_D.type) {
				if(userExistingWithIdProofNo.size() > 0) {
					throw new DuplicateUserException("User Already Exists with the Govt ID number " + imsUser.getIdProofNo());
				}else {
					annexUsersRepository.save(annexUser);
					if(userExisting == null) {
						log.saveLog(null,"Created user successfully with the mobile number "+ imsUser.getMobileNo(), "USER_CREATION", LogLevelEnum.INFO);
						UserPasswordPolicy userPasswordPolicy = new UserPasswordPolicy();
						ImsUser imsUserSaved = imsUserRepository.save(imsUser);
						userPasswordPolicy.setImsUser(imsUserSaved);
						userPasswordPolicy.setPasswordHash(imsUserSaved.getPasswordHash());
						userPasswordPolicy.setPasswordSalt(imsUserSaved.getPasswordSalt());
						userPasswordPolicyRepository.save(userPasswordPolicy);
						return filterUserData(imsUserSaved);
					}
				}
			}else {
				if(userExistingWithEmpId.size() > 0) {
					throw new DuplicateUserException("User Already Exists with the Emp ID " + imsUser.getEmpNo());
				}else {
					annexUsersRepository.save(annexUser);
					if(userExisting == null) {
						log.saveLog(null,"Created user successfully with the mobile number "+ imsUser.getMobileNo(), "USER_CREATION", LogLevelEnum.INFO);
						return filterUserData(imsUserRepository.save(imsUser));
					}
				}
			}
			return filterUserData(imsUser);
		}
		
	}

	@PostMapping("/saveAll")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public List<UserData> createAll(@Valid @RequestBody List<ImsUser> imsUsers) {

		List<UserPasswordPolicy> userPasswordPolicies = new ArrayList<>();
		imsUsers.forEach(imsUser -> {
			ImsUser userExisting = imsUserRepository.getByMobileNoAndDeleted(imsUser.getMobileNo(), false);

			if (userExisting == null) {
				String salt = ImsCipherUtil.generateSalt();
//					String password = ImsCipherUtil.generateRandomPassword();
//					emailService.sendPassword(user.getEmail(), password);
				String hash = ImsCipherUtil.generateHash("@Ims1234", salt);
				imsUser.setPasswordHash(hash);
				imsUser.setPasswordSalt(salt);
				imsUser.setStatus(LoginStatusEnum.FIRST_LOGIN.status);
				imsUser.setCreatedTimestamp(LocalDateTime.now());
				imsUser.setPassword("$2a$10$j7Pd/0bsnKt70zzTfLJtU.8OyLqLvq.Fczve.Pd5xYlmFPwsMdxa2");
				imsUser.setRole(roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get());
			} else {
				log.saveLog(null, "User Already Exists with the mobile number " + imsUser.getMobileNo(),
						"USER_CREATION", LogLevelEnum.WARN);
				throw new DuplicateUserException("User Already Exists!");
			}
		});
		log.saveLog(null, imsUsers.size() + " New users were created successfully!", "USER_CREATION",
				LogLevelEnum.INFO);
		List<ImsUser> imsUsersList = imsUserRepository.saveAll(imsUsers);
		imsUsersList.stream().forEach(imsUser -> {
			UserPasswordPolicy userPasswordPolicy = new UserPasswordPolicy();
			userPasswordPolicy.setImsUser(imsUser);
			userPasswordPolicy.setPasswordHash(imsUser.getPasswordHash());
			userPasswordPolicy.setPasswordSalt(imsUser.getPasswordSalt());
			userPasswordPolicies.add(userPasswordPolicy);
		});
		userPasswordPolicyRepository.saveAll(userPasswordPolicies);
		return filterUserData(imsUsersList);
	}

	@PostMapping("/validateAdminUser")
	public ImsResponse validateAdminUser(@RequestBody UserRequest userReq) {
		ImsResponse imsResponse = new ImsResponse();
		ImsUser user = imsUserRepository.getByMobileNoAndDeleted(userReq.getMobileNo(), false);
		if (!validateCaptcha(userReq.getCaptcha())) {
			imsResponse.setMessage("Invalid Captcha!");
			imsResponse.setSuccess(false);
		} else {
			if (user != null && user.getRole() != null && user.getRole().getId() != RoleEnum.ROLE_INVITEE.role && user.getRole().getId() != RoleEnum.ROLE_CITIZEN.role){
				AllowedMachines allowedMachines = allowedMachinesRepository.findByImsUserId(user.getId());
				String ipAddress = (allowedMachines != null) ? allowedMachines.getIpAddress() : StringUtils.EMPTY;
				if (clientAccessRestricted && !ipAddress.equals(userReq.getClientAddress())) {
					imsResponse.setSuccess(false);
					imsResponse.setMessage("Not authorized to access the resource!");
					log.saveLog(user, "Not authorized to access the resource!. User tried to login with the mobile number "+ userReq.getMobileNo(), "USER_LOGIN", LogLevelEnum.WARN);
				} else {
					if (user.getLoggedIn() == 0 || releaseLoggedInUser(user)) {
						if (user.getLocked() && !releaseLockedUser(user)) {
							imsResponse.setSuccess(false);
							imsResponse.setMessage(
									"User account is locked!");
						}else {
							String actaulHash = user.getPasswordHash();
							String salt = user.getPasswordSalt();
							String password = ImsCipherUtil.decrypt(userReq.getPassword());
							if (ImsCipherUtil.validate(password, salt, actaulHash)) {
								String otp = "123456";
								String mailOtp = "123456";
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
									log.saveLog(user, "OTP Sent to the officer with the mobile number "+ userReq.getMobileNo(), "NOTIFICATION", LogLevelEnum.INFO);
									
									mailOtp = ImsCipherUtil.generateOTP();
									emailService.sendOTP(user.getEmail(), mailOtp);
								}
								user.setPassword(passwordEncoder.encode(otp));
								user.setPasswordEmail(passwordEncoder.encode(mailOtp));
								invalidateOTP(userReq.getMobileNo());
								String response = Jwts.builder().setSubject(userReq.getCaptcha().getValue()).setIssuedAt(new Date()).setAudience(userReq.getMobileNo()) .setExpiration(new Date((new Date()).getTime() + 1000000))
										.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
								imsResponse.setMessage(response);
								imsResponse.setSuccess(true);
								imsResponse.setData(filterUserData(imsUserRepository.save(user)));
								//log.saveLog(user, "Admin Logged-In Successfully!", "USER_LOGIN", LogLevelEnum.INFO);
							} else {
								int failedAttempts = user.getLoginAttempts();
								if(failedAttempts>=2) {
									user.setLoginAttempts(failedAttempts + 1);
									user.setLocked(true);
									user.setLoggedIn(0);
									user.setLastLocked(LocalDateTime.now());
									imsUserRepository.save(user);
									imsResponse.setSuccess(false);
									imsResponse.setMessage("Your account has been locked due to 3 failed attempts. It will be unlocked after 24 hours.");
									log.saveLog(user, "Your account has been locked due to 3 failed attempts. It will be unlocked after 24 hours."+ userReq.getMobileNo(), "USER_LOGIN", LogLevelEnum.FATAL);
								}else {
								  user.setLoggedIn(0);
								  user.setLoginAttempts(failedAttempts + 1);
								  imsUserRepository.save(user);
								  imsResponse.setSuccess(false);
									imsResponse.setMessage("Invalid Username/Password");
									log.saveLog(user, "Invalid Username/Password. User tried to login with the mobile number "+ userReq.getMobileNo(), "USER_LOGIN", LogLevelEnum.WARN);
								}
								
							}
						}
					} else {
						imsResponse.setSuccess(false);
						imsResponse.setMessage("User already logged-in!");
					}

				}
				
				 
			} else {
				imsResponse.setSuccess(false);
				imsResponse.setMessage("You are not authorized to login!");
				log.saveLog(user, "Invalid Username/Password. User tried to login with the mobile number "+ userReq.getMobileNo(), "USER_LOGIN", LogLevelEnum.WARN);
			}
		}
		return imsResponse;

	}

	@PostMapping("/validateUser")
	public ImsResponse validateUser(@RequestBody UserRequest userReq) {

		ImsResponse imsResponse = new ImsResponse();
		ImsUser user = imsUserRepository.getByMobileNoAndDeleted(userReq.getMobileNo(), false);
		
		
		if (!validateCaptcha(userReq.getCaptcha())) {

			imsResponse.setMessage("Invalid Captcha!");
			imsResponse.setSuccess(false);
		} else {
			if (user != null && user.getRole() != null && (user.getRole().getId() == RoleEnum.ROLE_INVITEE.role || user.getRole().getId() == RoleEnum.ROLE_CITIZEN.role)) {
				if (user.getLoggedIn() == 0 || releaseLoggedInUser(user)) {
					if (user.getLocked() && !releaseLockedUser(user)) {
						imsResponse.setMessage("User account is locked!");
						imsResponse.setSuccess(false);
					}else {
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
								log.saveLog(user, "OTP Sent to the officer with the mobile number "+ userReq.getMobileNo(), "NOTIFICATION", LogLevelEnum.INFO);
								
							}
							user.setPassword(passwordEncoder.encode(otp));
							imsUserRepository.save(user);
							invalidateOTP(userReq.getMobileNo());
							imsResponse.setSuccess(true);
							String response = Jwts.builder().setSubject(userReq.getCaptcha().getValue()).setAudience(userReq.getMobileNo()). setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + 1000000))
									.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
							imsResponse.setMessage(response);
							//log.saveLog(user, "User Logged-In Successfully!", "USER_LOGIN", LogLevelEnum.INFO);
//							imsResponse.setData(imsUserRepository.save(user));
					}
				}else {
					imsResponse.setSuccess(false);
					imsResponse.setMessage("User already logged-in!");
				}

			}else {
				if (user != null && user.getRole() != null) {
					if(user.getRole().getId() != RoleEnum.ROLE_INVITEE.role || user.getRole().getId() != RoleEnum.ROLE_CITIZEN.role) {
						imsResponse.setMessage("You are not authorized to login as User!");
						imsResponse.setSuccess(false);
					}
				} else {
					imsResponse.setMessage("User doesn't exist!");
					imsResponse.setSuccess(false);
				}
			}
		}
	
		return imsResponse;
	}
	
	@PostMapping("/validateMobileNo")
	public ImsResponse validateMobileNo(@RequestBody UserRequest request) {
		ImsResponse imsResponse = new ImsResponse();
		ImsUser user = imsUserRepository.getByMobileNoAndDeleted(request.getMobileNo(), false);
		Captcha captcha = request.getCaptcha();

		if (validateCaptcha(captcha)) {
			if (user == null) {
				imsResponse.setMessage("User doesn't exist!");
				imsResponse.setSuccess(false);
			} else {
				String otp = "123456";
				String emailotp = "123456";
				if (smsEnabled) {
					otp = ImsCipherUtil.generateOTP();
					emailotp = ImsCipherUtil.generateOTP();
					String message = "Dear Officer, Your Login OTP is " + otp + ". From Ministry of Defence";
					ImsSMS sms = new ImsSMS();
					sms.setMobileNo(request.getMobileNo());
					sms.setMessage(message);
					try{
						InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_PWD_FORGOT_TEMPLATE);
						String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
						inputStream.close();
		
						otpMailText = otpMailText.replace("<<OTP>>", otp);
						otpMailText = otpMailText.replace("<<XX>>", "10");
						sms.setMessage(otpMailText);
					} catch(IOException ioe) {
						System.out.println("Error while reading OTP templates : "+ioe.getMessage());
					}
					sms.setTemplateId(loginForgotOtp);
					imsSmsSender.send(sms);
					emailService.sendOTPForForgotPwd(user.getEmail(), emailotp);
					log.saveLog(user, "OTP Sent to the officer with the mobile number "+ request.getMobileNo(), "NOTIFICATION", LogLevelEnum.INFO);
				}
				user.setPassword(passwordEncoder.encode(otp));
				user.setPasswordEmail(passwordEncoder.encode(emailotp));
				imsUserRepository.save(user);
				invalidateOTP(request.getMobileNo());
				invalidateEmailOTP(user.getEmail());
				imsResponse.setSuccess(true);
				String response = Jwts.builder().setSubject(captcha.getValue()).setIssuedAt(new Date()).setAudience(request.getMobileNo()).setExpiration(new Date((new Date()).getTime() + 1000000))
						.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
				imsResponse.setMessage(response);
			}
		} else {
			imsResponse.setMessage("Invalid Captcha");
			imsResponse.setSuccess(false);

		}
		return imsResponse;
	}

	@PostMapping("/sendOTP")
	public void sendOTP(@RequestBody PublicUserRequest userReq) {
		ImsResponse imsResponse = new ImsResponse();
		ImsUser userExisting = imsUserRepository.getByMobileNoAndDeleted(userReq.getMobileNo(), false);
		ImsUser imsUser;
		if (userExisting == null) {
			imsUser = new ImsUser();
		} else {
			imsUser = userExisting;
		}

		String otp = "123456";
		if (smsEnabled) {
			otp = ImsCipherUtil.generateOTP();
			String message = "Dear Officer, Your Login OTP is " + otp + ". From Ministry of Defence";
			ImsSMS sms = new ImsSMS();
			sms.setMobileNo(userReq.getMobileNo());
			sms.setMessage(message);
			try {
				InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_OTP_TEMPLATE);
				String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
				inputStream.close();

				otpMailText = otpMailText.replace("<<OTP>>", otp);
				otpMailText = otpMailText.replace("<<VALIDITY_MIN>>", "10");
				sms.setMessage(otpMailText);
			} catch (IOException ioe) {
				System.out.println("Error while reading OTP templates : " + ioe.getMessage());
			}
			sms.setTemplateId(loginOtp);
			imsSmsSender.send(sms);
		}
		imsUser.setMobileNo(userReq.getMobileNo());
		imsUser.setPassword(passwordEncoder.encode(otp));
		invalidateOTP(imsUser.getMobileNo());
		imsUser.setCreatedTimestamp(LocalDateTime.now());
		imsUserRepository.save(imsUser);
		imsResponse.setMessage("User Registered Successfully!");
		imsResponse.setSuccess(true);

	}
	
	@PostMapping("/register")
	public ImsResponse register(@RequestBody PublicUserRequest userReq) {

		ImsResponse imsResponse = new ImsResponse();
		ImsUser userExisting = imsUserRepository.getByMobileNoAndDeleted(userReq.getMobileNo(), false);

		if (validateCaptcha(userReq.getCaptcha())) {
			if (userExisting == null) {
				imsResponse.setSuccess(false);
				imsResponse.setMessage("Invalid Operation");
			} else if(userExisting.getUserType() == null){
				if (passwordEncoder.matches(ImsCipherUtil.decrypt(userReq.getOtp()), userExisting.getPassword())) {
					userExisting.setRole(roleRepository.findById(RoleEnum.ROLE_CITIZEN.role).get());
					userExisting.setUserType(userTypeRepository.findById(ImsUserTypeEnum.PublicUser.type).get());
					userExisting.setDateOfBirth(userReq.getDob());
					userExisting.setName(userReq.getName());
					userExisting.setResidentialAddress(userReq.getAddress());
					userExisting.setCreatedTimestamp(LocalDateTime.now());
					imsResponse.setSuccess(true);
					imsResponse.setData(filterUserData(imsUserRepository.save(userExisting)));
					imsResponse.setSuccess(true);
					imsResponse.setMessage("User Registered Successfully!");
					log.saveLog(null, "Created user successfully with the mobile number " + userExisting.getMobileNo(),
							"USER_CREATION", LogLevelEnum.INFO);
				} else {
					imsResponse.setSuccess(false);
					imsResponse.setMessage("Invalid OTP!");
				}
			} else {
				imsResponse.setSuccess(false);
				imsResponse.setMessage("User Already Registered!");
			}
		} else {
			imsResponse.setSuccess(false);
			imsResponse.setMessage("Invalid Captcha!");
		}

		return imsResponse;

	}

	@PostMapping("/logout")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER') or hasRole('INVITEE') or hasRole('CITIZEN') or hasRole('COUNTEREMP')")
	public void userLogout(@RequestBody UserRequest userReq, @RequestHeader (name="Authorization") String token) {
		String mobileNo = jwtUtils.getUserNameFromJwtToken(token);
		ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
		imsUser.setLoggedIn(0);
		imsUserRepository.save(imsUser);
		log.saveLog(imsUser, "User Logged-Out Successfully!", "USER_LOGIN", LogLevelEnum.INFO);
	}

	@GetMapping("/getNodalOfficers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getNodalOfficers() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByRoleIdAndDeleted(RoleEnum.ROLE_NODALOFFICER.role, false);
		imsResponse.setMessage("Retrieved Nodal Officers data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	
	@GetMapping("/getScanningOfficers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getScanningOfficers() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByRoleIdAndDeleted(RoleEnum.ROLE_SCANNINGOFFICER.role, false);
		imsResponse.setMessage("Retrieved Scanning Officers data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getInvitationOfficers")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getInvitationAdmins() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByRoleIdAndDeleted(RoleEnum.ROLE_INVITATIONADMIN.role, false);
		imsResponse.setMessage("Retrieved Invitation Admin data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getMinistryUsers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getMinistryUsers() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByUserTypeIdAndDeleted(ImsUserTypeEnum.Annexure_A.type,
				false);
		imsResponse.setMessage("Retrieved Ministry Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getPublicUsers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getPublicUsers() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByUserTypeIdAndDeleted(ImsUserTypeEnum.PublicUser.type,
				false);
		imsResponse.setMessage("Retrieved Public Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	//Service that returns list of WoP users from annex_users table
	@GetMapping("/getWoPUsers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getWoPUsers() {
		ImsResponse imsResponse = new ImsResponse();
		List<AnnexUsers> annexUsersList = annexUsersRepository.getByUserTypeIdAndDeleted(ImsUserTypeEnum.WoPUser.type, false);
//		for(AnnexUsers annexUser: annexUsersList) {
//			System.out.println("=========================annexUser===========================" + annexUser.getName());
//		}
		imsResponse.setMessage("Retrieved WoP Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(annexUsersList);
		return imsResponse;
	}

	@GetMapping("/getAtonomousUsers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAtonomousUsers() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByUserTypeIdAndDeleted(ImsUserTypeEnum.Annexure_B.type,
				false);
		imsResponse.setMessage("Retrieved Anotomous/PSU users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getCommissionUser")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getCommissionUser() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByUserTypeIdAndDeleted(ImsUserTypeEnum.Annexure_C.type,
				false);
		imsResponse.setMessage("Retrieved Commissioner/Committee Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getDelegates")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getDelegates() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByUserTypeIdAndDeleted(ImsUserTypeEnum.Annexure_E.type,
				false);
		imsResponse.setMessage("Retrieved Delegates data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getGuestUsers/{userId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getGuestUsers(@PathVariable int userId) {
		ImsResponse imsResponse = new ImsResponse();
		ImsUser imsUser = imsUserRepository.findByIdAndDeleted(userId, false);
		int roleId = imsUser.getRole().getId();
		List<ImsUser> imsUsersList = null;
		if (roleId == RoleEnum.ROLE_NODALOFFICER.role) {
			List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByImsUserId(userId);
			List<ImsUser> guestList = new ArrayList<>();
			nodalOfficers.forEach(nodalOfficer -> {
				int departmentId = nodalOfficer.getDepartment().getId();
				guestList.addAll(imsUserRepository.findByUserTypeIdAndDepartmentIdAndDeleted(
						ImsUserTypeEnum.Annexure_D.type, departmentId, false));
			});
			imsUsersList = guestList;
		} else {
			imsUsersList = imsUserRepository.getByUserTypeIdAndDeleted(ImsUserTypeEnum.Annexure_D.type, false);
		}
		imsResponse.setMessage("Retrieved Guest Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getOrganizers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getOrganizers() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByUserTypeIdAndDeleted(ImsUserTypeEnum.Organizer.type, false);
		imsResponse.setMessage("Retrieved Organizers data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getMPUsers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getMPUsers() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByUserTypeIdAndDeleted(ImsUserTypeEnum.Annexure_F.type,
				false);
		imsResponse.setMessage("Retrieved MP Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getHigherOfficers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getHigherOfficers() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByRoleIdAndDeleted(RoleEnum.ROLE_HIGHEROFFICER.role, false);
		imsResponse.setMessage("Retrieved Higher Officers data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getAllInvitees")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllInvitees() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByRoleIdAndDeleted(RoleEnum.ROLE_INVITEE.role, false);
		imsResponse.setMessage("Retrieved Invitee Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getUsersByDepartment/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getUsersByDepartment(@PathVariable int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.findByDepartmentIdAndDeleted(departmentId, false);
		imsResponse.setMessage("Retrieved Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}
	
	@GetMapping("/getUnassignedCounterEmpByDepartment/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getUnassignedCounterEmpByDepartment(@PathVariable int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.findByDepartmentIdAndDeleted(departmentId, false);
		imsUsersList = imsUsersList.stream()
				.filter(user -> user.getUserType() != null && user.getRole() != null
						&& user.getRole().getId() != RoleEnum.ROLE_COUNTEREMP.role
						&& user.getRole().getId() != RoleEnum.ROLE_HIGHEROFFICER.role
						&& user.getRole().getId() != RoleEnum.ROLE_INVITATIONADMIN.role
						&& user.getRole().getId() != RoleEnum.ROLE_NODALOFFICER.role
						&& user.getUserType().getId() != ImsUserTypeEnum.Organizer.type
						&& user.getUserType().getId() != ImsUserTypeEnum.Annexure_D.type)
				.collect(Collectors.toList());
		imsResponse.setMessage("Retrieved Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}
	
	@GetMapping("/getAllCounterOfficers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getCounterOfficers() {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.getByRoleIdAndDeleted(RoleEnum.ROLE_COUNTEREMP.role , false);
		imsResponse.setMessage("Retrieved Counter Officer data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}
	
	@PostMapping("/CounterOfficers/save")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse saveCounterOfficers(@RequestBody ImsUser imsUser) {
		ImsResponse imsResponse = new ImsResponse();
		ImsUser imsUserExisting = imsUserRepository.findById(imsUser.getId()).get();
		Role role = roleRepository.findById(RoleEnum.ROLE_COUNTEREMP.role).get();
		imsUserExisting.setRole(role);
		
		imsResponse.setMessage("Saved Counter Officer data!");
		imsResponse.setSuccess(true);
		imsUserRepository.saveAndFlush(imsUserExisting);
		imsResponse.setData(new ImsUser());
		return imsResponse;
	}
	
	
	
	@GetMapping("/getGuestsByDepartment/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getGuestsByDepartment(@PathVariable int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<AnnexUsers> annexUsersList = annexUsersRepository.findAll();
		List<AnnexUsers> annexUsersWithoutPass = annexUsersList.stream().filter(
				annexUser -> annexUser.getPasses().size() == 0 && annexUser.getDepartment() == null ? false : annexUser.getDepartment().getId() == departmentId)
				.collect(Collectors.toList());
		imsResponse.setData(annexUsersWithoutPass);
		imsResponse.setMessage("Retrieved Guest Users data!");
		imsResponse.setSuccess(true);
		
		return imsResponse;
	}

	@GetMapping("/getInviteesByDepartment/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getInviteesByDepartment(@PathVariable int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.findByRoleIdAndDepartmentIdAndDeleted(RoleEnum.ROLE_INVITEE.role,
				departmentId, false);
		imsResponse.setMessage("Retrieved Invitees data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}
	
	@GetMapping("/getEligibleOrganizersByDepartment/{eventId}/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getEligibleOrganizersByDepartment(@PathVariable int eventId, @PathVariable int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<AnnexUsers> annexUsersList = annexUsersRepository.findByDeleted(false);
		List<AnnexUsers> organizersList = annexUsersRepository
				.findByUserTypeIdAndDeleted(ImsUserTypeEnum.Organizer.type, false);
		List<AnnexUsers> uniqueOrganizersList = new ArrayList<>();
		for (AnnexUsers annexUser : organizersList) {
			ImsUser imsUser = imsUserRepository.findByMobileNo(annexUser.getMobileNo());
			List<Pass> passes = passRepository.findAllByImsUserByImsUserIdIdAndEventIdAndDeleted(imsUser.getId(),
					eventId, false);
			passes = passes.stream()
					.filter(pass -> pass.getPassStatus() != null
							&& pass.getPassStatus().getId() != PassStatusEnum.CANCELLED.type
							&& pass.getPassStatus().getId() != PassStatusEnum.RSVP_REGRETTED.type)
					.collect(Collectors.toList());
			if (passes.size() == 0) {
				uniqueOrganizersList.add(annexUser);
			}
		}
		annexUsersList = annexUsersList.stream().filter(
				pass -> pass.getUserType() != null && (pass.getUserType().getId() == ImsUserTypeEnum.Annexure_A.type
						|| pass.getUserType().getId() == ImsUserTypeEnum.Annexure_B.type
						|| pass.getUserType().getId() == ImsUserTypeEnum.Annexure_C.type
						|| pass.getUserType().getId() == ImsUserTypeEnum.Annexure_E.type
						|| pass.getUserType().getId() == ImsUserTypeEnum.Annexure_F.type))
				.collect(Collectors.toList());
		annexUsersList.addAll(uniqueOrganizersList);
		imsResponse.setMessage("Retrieved Invitees data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(annexUsersList);
		return imsResponse;
	}
	
	@GetMapping("/getUnassignedIOAndInviteesByDepartment/{eventId}/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getUnassignedIOAndInviteesByDepartment(@PathVariable int eventId, @PathVariable int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsInviteeList = imsUserRepository.findByRoleIdAndDepartmentIdAndDeleted(RoleEnum.ROLE_INVITEE.role,
				departmentId, false);
		imsInviteeList.addAll(imsUserRepository.findByRoleIdAndDepartmentIdAndDeleted(RoleEnum.ROLE_INVITATIONADMIN.role,
				departmentId, false));
		List<InvitationOfficer> invitationOfficers = invitationOfficerRepository.findByEventIdAndDeleted(eventId, false);
		List<ImsUser> unAssignedUsersList = new ArrayList<>();
		unAssignedUsersList.addAll(imsInviteeList);
		for(ImsUser imsUser: imsInviteeList) {
			for(InvitationOfficer invitationOfficer: invitationOfficers) {
				if(imsUser.getId() == invitationOfficer.getImsUser().getId()) {
					unAssignedUsersList.remove(imsUser);
				}
			}
		}
		//Filtering guest users from InvitationAdmin users list 
		List<ImsUser> nonGuestUsersList = unAssignedUsersList.stream()
				.filter(imsUser -> imsUser.getUserType().getId() != ImsUserTypeEnum.Annexure_D.type)
				.collect(Collectors.toList());
		nonGuestUsersList = nonGuestUsersList.stream().filter(user -> user.getUserType() != null
				&& user.getUserType().getId() != ImsUserTypeEnum.Organizer.type).collect(Collectors.toList());
		imsResponse.setMessage("Retrieved Invitees data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(nonGuestUsersList));
		return imsResponse;
	}
	
	@GetMapping("/getUnassignedNOAndInviteesByDepartment/{eventId}/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getUnassignedNOAndInviteesByDepartment(@PathVariable int eventId, @PathVariable int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsInviteeList = imsUserRepository.findByRoleIdAndDepartmentIdAndDeleted(RoleEnum.ROLE_INVITEE.role,
				departmentId, false);
		imsInviteeList.addAll(imsUserRepository.findByRoleIdAndDepartmentIdAndDeleted(RoleEnum.ROLE_NODALOFFICER.role,
				departmentId, false));
		List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByEventIdAndDeleted(eventId, false);
		List<ImsUser> unAssignedUsersList = new ArrayList<>();
		unAssignedUsersList.addAll(imsInviteeList);
		for(ImsUser imsUser: imsInviteeList) {
			for(NodalOfficer nodalOfficer: nodalOfficers) {
				if(imsUser.getId() == nodalOfficer.getImsUser().getId()) {
					unAssignedUsersList.remove(imsUser);
				}
			}
		}
		
		//Filtering guest users from Nodal Officer users list 
		List<ImsUser> nonGuestUsersList = unAssignedUsersList.stream()
				.filter(imsUser -> imsUser.getUserType().getId() != ImsUserTypeEnum.Annexure_D.type)
				.collect(Collectors.toList());
		nonGuestUsersList = nonGuestUsersList.stream().filter(user -> user.getUserType() != null
				&& user.getUserType().getId() != ImsUserTypeEnum.Organizer.type ).collect(Collectors.toList());
		imsResponse.setMessage("Retrieved Invitees data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(nonGuestUsersList));
		return imsResponse;
	}
	
	@GetMapping("/getUnassignedHOAndInviteesByDepartment/{eventId}/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getUnassignedHOAndInviteesByDepartment(@PathVariable int eventId, @PathVariable int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsInviteeList = imsUserRepository.findByRoleIdAndDepartmentIdAndDeleted(RoleEnum.ROLE_INVITEE.role,
				departmentId, false);
		imsInviteeList.addAll(imsUserRepository.findByRoleIdAndDepartmentIdAndDeleted(RoleEnum.ROLE_HIGHEROFFICER.role,
				departmentId, false));
		List<HigherOfficer> higherOfficers = higherOfficerRepository.findByEventIdAndDeleted(eventId, false);
		List<ImsUser> unAssignedUsersList = new ArrayList<>();
		unAssignedUsersList.addAll(imsInviteeList);
		for(ImsUser imsUser: imsInviteeList) {
			for(HigherOfficer higherOfficer: higherOfficers) {
				if(imsUser.getId() == higherOfficer.getImsUser().getId()) {
					unAssignedUsersList.remove(imsUser);
				}
			}
		}
		//Filtering guest users from Nodal Officer users list 
		List<ImsUser> nonGuestUsersList = unAssignedUsersList.stream()
				.filter(imsUser -> imsUser.getUserType().getId() != ImsUserTypeEnum.Annexure_D.type)
				.collect(Collectors.toList());
		nonGuestUsersList = nonGuestUsersList.stream().filter(user -> user.getUserType() != null
				&& user.getUserType().getId() != ImsUserTypeEnum.Organizer.type).collect(Collectors.toList());
		imsResponse.setMessage("Retrieved Unassigned Higher Officers and Invitee data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(nonGuestUsersList));
		return imsResponse;
	}

	@GetMapping("/getUsersByOrganization/{organizationId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getUsersByOrganization(@PathVariable int organizationId) {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.findByDepartmentOrganizationIdAndDeleted(organizationId, false);
		imsResponse.setMessage("Retrieved Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}

	@GetMapping("/getUsersByOrganizationGroup/{organizationGroupId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getUsersByOrganizationGroup(@PathVariable int organizationGroupId) {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository
				.findByDepartmentOrganizationOrganizationGroupIdAndUserTypeIdNotAndDeleted(organizationGroupId, ImsUserTypeEnum.Annexure_D.type, false);
		imsResponse.setMessage("Retrieved Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}
	
	@GetMapping("/getAnnexUsersByOrganizationGroup/{organizationGroupId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getAnnexUsersByOrganizationGroup(@PathVariable int organizationGroupId) {
		ImsResponse imsResponse = new ImsResponse();
		List<AnnexUsers> annexUsersList = annexUsersRepository
				.findByDepartmentOrganizationOrganizationGroupIdAndDeleted(organizationGroupId, false);
		imsResponse.setMessage("Retrieved AnnexUsers data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(annexUsersList);
		return imsResponse;
	}
	
	@GetMapping("/getAnnexUsersByUserType/{userType}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse getAnnexUsersByUserType(@PathVariable int userType) {
		ImsResponse imsResponse = new ImsResponse();
		List<AnnexUsers> annexUsersList = annexUsersRepository
				.getByUserTypeIdAndDeleted(userType, false);
		imsResponse.setMessage("Retrieved AnnexUsers data for the selected user type!");
		imsResponse.setSuccess(true);
		imsResponse.setData(annexUsersList);
		return imsResponse;
	}
	
	@GetMapping("/getWopUserByPrecedence/{precedenceId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getWopUserByPrecedence(@PathVariable int precedenceId) {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> imsUsersList = imsUserRepository.findByPrecedenceIdAndDeleted(precedenceId, false);
		imsResponse.setMessage("Retrieved WoP Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsersList));
		return imsResponse;
	}
	
	@PostMapping("/forgotPassword")
	public ImsResponse forgotPassword(@RequestBody PasswordChangeRequest request) {
		ImsResponse imsResponse = new ImsResponse();
		ImsUser user = imsUserRepository.getByMobileNoAndDeleted(request.getMobileNo(), false);
		String otp = ImsCipherUtil.decrypt(request.getOtp());
		String emailOtp = ImsCipherUtil.decrypt(request.getEmailOtp());
		String newPassword = ImsCipherUtil.decrypt(request.getNewPassword());

		if (!isValidPassword(newPassword)) {
			imsResponse
					.setMessage("Password length should be between 8-20 chars with alpha-numeric and special chars!");
			imsResponse.setSuccess(false);
		} else {
			if (user != null) {
				if (passwordEncoder.matches(otp, user.getPassword()) && passwordEncoder.matches(emailOtp, user.getPasswordEmail())) {
					if (!checkPasswordPolicy(user, newPassword)) {
						imsResponse.setMessage("New password shouldn't be same as past 3 old passwords!");
					} else {
					String salt = user.getPasswordSalt();
					String hash = ImsCipherUtil.generateHash(newPassword, salt);
					user.setPasswordHash(hash);
					user.setPasswordSalt(salt);
					user.setStatus(LoginStatusEnum.ACTIVE.status);
					imsUserRepository.save(user);
					imsResponse.setMessage("Password is updated Successfully!");
					imsResponse.setSuccess(true);
					List<UserPasswordPolicy> userPasswordPolicies = userPasswordPolicyRepository.findAllByImsUserId(user.getId());
					UserPasswordPolicy userPasswordPolicy;
					if(userPasswordPolicies != null && userPasswordPolicies.size() < 3) {
						 userPasswordPolicy = new UserPasswordPolicy();
					} else {
						 userPasswordPolicy = userPasswordPolicies.stream().findFirst().get();
					}
					userPasswordPolicy.setImsUser(user);
					userPasswordPolicy.setPasswordHash(hash);
					userPasswordPolicy.setPasswordSalt(salt);
					userPasswordPolicyRepository.save(userPasswordPolicy);
					}
				} else {
					imsResponse.setMessage("Invalid Username/OTP");
					imsResponse.setSuccess(false);
				}
			} else {
				imsResponse.setMessage("Invalid Username/Password");
				imsResponse.setSuccess(false);
			}
		}
		return imsResponse;
	}
	
	
	@PostMapping("/resendOtp")
	public void resendOtp(@RequestHeader(name = "Authorization") String token) {
		String mobileNo = jwtUtils.getMobileNoFromJwtToken(token);
		ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
		if (imsUser != null) {
			String otp = "123456";
			if (smsEnabled) {
				otp = ImsCipherUtil.generateOTP();
				imsUser.setPassword(passwordEncoder.encode(otp));
				String message = "Dear Officer, Your Login OTP is " + otp + ". From Ministry of Defence";
				ImsSMS sms = new ImsSMS();
				sms.setMobileNo(mobileNo);
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
			imsUserRepository.save(imsUser);
			invalidateOTP(mobileNo);
		}
	}
	
	@PostMapping("/resendRegisterOtp")
	public void resendRegisterOtp(@RequestBody UserRequest userRequest) {
		String mobileNo = userRequest.getMobileNo();
		ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
		if (imsUser != null) {
			String otp = "123456";
			if (smsEnabled) {
				otp = ImsCipherUtil.generateOTP();
				imsUser.setPassword(passwordEncoder.encode(otp));
				String message = "Dear Officer, Your Login OTP is " + otp + ". From Ministry of Defence";
				ImsSMS sms = new ImsSMS();
				sms.setMobileNo(mobileNo);
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
			imsUserRepository.save(imsUser);
			invalidateOTP(mobileNo);
		}
	}
	
	@PostMapping("/resendEmailOtp")
	public void resendEmailOtp(@RequestHeader(name = "Authorization") String token) {
		String mobileNo = jwtUtils.getMobileNoFromJwtToken(token);
		ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
		String emailId = imsUser.getEmail();
		List<ImsUser> imsUsers = imsUserRepository.getByEmailAndDeleted(emailId, false);
		if (smsEnabled) {
			final String otp = ImsCipherUtil.generateOTP();
			emailService.sendOTP(emailId, otp);
			imsUsers.forEach(imsUserFetched -> {
				if (imsUser != null) {
					imsUser.setPasswordEmail(passwordEncoder.encode(otp));
					imsUserRepository.save(imsUserFetched);
				invalidateEmailOTP(emailId);
				}
			});
		}

	}

	@PostMapping("/resendPwdForgotOtp")
	public void resendPwdForgotOtp(@RequestHeader(name = "Authorization") String token) {
		String mobileNo = jwtUtils.getMobileNoFromJwtToken(token);
		ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
		if (imsUser != null) {
			String otp = "123456";
			if (smsEnabled) {
				otp = ImsCipherUtil.generateOTP();
				imsUser.setPassword(passwordEncoder.encode(otp));
				String message = "Dear Officer, Your Login OTP is " + otp + ". From Ministry of Defence";
				ImsSMS sms = new ImsSMS();
				sms.setMobileNo(mobileNo);
				sms.setMessage(message);
				try{
					InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_PWD_FORGOT_TEMPLATE);
					String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
					inputStream.close();

					otpMailText = otpMailText.replace("<<OTP>>", otp);
					otpMailText = otpMailText.replace("<<XX>>", "10");
					sms.setMessage(otpMailText);
				} catch(IOException ioe) {
					System.out.println("Error while reading OTP templates : "+ioe.getMessage());
				}
				sms.setTemplateId(loginForgotOtp);
				imsSmsSender.send(sms);
			}
			imsUserRepository.save(imsUser);
			invalidateOTP(mobileNo);
		}
	}

	@PostMapping("/resendPwdForgotEmailOtp")
	public void resendPwdForgotEmailOtp(@RequestHeader(name = "Authorization") String token) {
		String mobileNo = jwtUtils.getMobileNoFromJwtToken(token);
		ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
		if (imsUser != null) {
			String otp = "123456";
			if (smsEnabled) {
				otp = ImsCipherUtil.generateOTP();
				emailService.sendOTPForForgotPwd(imsUser.getEmail(), otp);
			}
			imsUser.setPasswordEmail(passwordEncoder.encode(otp));
			imsUserRepository.save(imsUser);
			invalidateEmailOTP(imsUser.getEmail());
		}
	}
	
	@GetMapping("/getAllUsersUnassigned/{eventId}/{deptId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllUsersUnassigned(@PathVariable int eventId, @PathVariable int deptId) {
		ImsResponse imsResponse = new ImsResponse();
		List<ImsUser> userList = imsUserRepository.findByDepartmentIdAndDeleted(deptId, false);
		List<Pass> passes = passRepository.findAllByEventIdAndDeleted(eventId, false);
		List<Integer> userIdFromPass = passes.stream().map( pass -> pass.getImsUserByImsUserId().getId()).collect(Collectors.toList());
		List<ImsUser> imsUsers = userList.stream().filter(user -> !userIdFromPass.contains(user.getId())).collect(Collectors.toList());
		imsResponse.setMessage("Retrieved Users not assigned with Pass!");
		imsResponse.setSuccess(true);
		imsResponse.setData(filterUserData(imsUsers));
		return imsResponse;
	}

	@GetMapping("/getById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getById(@PathVariable int id) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(filterUserData((ImsUser) imsUserRepository.findById(id).get()));
		return imsResponse;
	}

	@PutMapping("/update") 
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse update(@Valid @RequestBody ImsUser t) {
		ImsResponse imsResponse = new ImsResponse();
		ImsUser imsUser = imsUserRepository.findById(t.getId()).get();
		ImsJpaUpdateUtil.copyEntityProperties(t, imsUser);
		imsUser.setModifiedTimestamp(LocalDateTime.now());
		imsResponse.setData(filterUserData((ImsUser) imsUserRepository.save(imsUser)));
		return imsResponse;
	}

	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER') or hasRole('COUNTEREMP') or hasRole('HIGHEROFFICER')")
	@PostMapping("/changePassword")
	public ImsResponse changePassword(@Valid @RequestBody PasswordChangeRequest request,
			@RequestHeader(name = "Authorization") String token) {
		ImsResponse imsResponse = new ImsResponse();
		String mobileNo = jwtUtils.getUserNameFromJwtToken(token);
		ImsUser user = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
		String oldPassword = ImsCipherUtil.decrypt(request.getOldPassword());
		String newPassword = ImsCipherUtil.decrypt(request.getNewPassword());
		if (!isValidPassword(newPassword)) {
			imsResponse
					.setMessage("Password length should be between 8-20 chars with alpha-numeric and special chars!");
			imsResponse.setSuccess(false);
		} else {
			if (user != null) {
				String actaulHash = user.getPasswordHash();
				String salt = user.getPasswordSalt();
				if (ImsCipherUtil.validate(oldPassword, salt, actaulHash)) {
					if (!checkPasswordPolicy(user, newPassword)) {
						imsResponse.setMessage("New password shouldn't be same as past 3 old passwords!");
					} else {
						String hash = ImsCipherUtil.generateHash(newPassword, salt);
						user.setPasswordHash(hash);
						user.setPasswordSalt(salt);
						user.setStatus(LoginStatusEnum.ACTIVE.status);
						imsUserRepository.save(user);
						imsResponse.setMessage("Password is changed Successfully!");
						imsResponse.setSuccess(true);
						List<UserPasswordPolicy> userPasswordPolicies = userPasswordPolicyRepository.findAllByImsUserId(user.getId());
						UserPasswordPolicy userPasswordPolicy;
						if(userPasswordPolicies != null && userPasswordPolicies.size() < 3) {
							 userPasswordPolicy = new UserPasswordPolicy();
						} else {
							 userPasswordPolicy = userPasswordPolicies.stream().findFirst().get();
						}
						userPasswordPolicy.setImsUser(user);
						userPasswordPolicy.setPasswordHash(hash);
						userPasswordPolicy.setPasswordSalt(salt);
						userPasswordPolicyRepository.save(userPasswordPolicy);
					}
				} else {
					imsResponse.setMessage("Invalid Username/Password");
					imsResponse.setSuccess(false);
				}
			} else {
				imsResponse.setMessage("Invalid Username/Password");
				imsResponse.setSuccess(false);
			}
		}
		return imsResponse;
	}
	
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	@DeleteMapping("/deleteById/{id}")
	public void delete(@PathVariable int id) {
		ImsUser entityToDelete = imsUserRepository.findById(id).get();
		try {
			Field[] entities = entityToDelete.getClass().getDeclaredFields();
			for (Field field : entities) {
				if (field.getDeclaredAnnotation(JsonIgnore.class) != null) {
					@SuppressWarnings("unchecked")
					Set<T> referenceEntities = (Set<T>) entityToDelete.getClass()
							.getMethod("get" + StringUtils.capitalize(field.getName())).invoke(entityToDelete);
					referenceEntities.forEach(refEntity -> {
						try {
							refEntity.getClass().getMethod("setDeleted", Boolean.class).invoke(refEntity, true);
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
				}
			}
			entityToDelete.getClass().getMethod("setDeleted", Boolean.class).invoke(entityToDelete, true);
			imsUserRepository.save(entityToDelete);

		} catch (IllegalArgumentException | SecurityException | NoSuchMethodException | IllegalAccessException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
		
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	@PostMapping("/validateUsers")
	public List<UserValidationRequest> validateUsers(@RequestBody List<String> users) {
		List<UserValidationRequest> validatedUsers = new ArrayList<>();
		for(String mobileNo: users) {
			ImsUser user = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
			if(user!=null) {
				UserValidationRequest userValidationRequest = new UserValidationRequest();
				userValidationRequest.setMobileNo(mobileNo);
				userValidationRequest.setDuplicate(false);
				validatedUsers.add(userValidationRequest);
			}else {
				UserValidationRequest userValidationRequest = new UserValidationRequest();
				userValidationRequest.setMobileNo(mobileNo);
				userValidationRequest.setDuplicate(true);
				validatedUsers.add(userValidationRequest);
			}
		}
		return validatedUsers;
	}
	
	@GetMapping("/getUnassignedWopUserByPrecedence/{precedenceId}/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getUnassignedWopUserByPrecedence(@PathVariable int precedenceId, @PathVariable int eventId) {
		ImsResponse imsResponse = new ImsResponse();
		List<AnnexUsers> annexUsersList = annexUsersRepository.findByPrecedenceIdAndDeleted(precedenceId, false);
		List<AnnexUsers> unassignedWoPUsers = new ArrayList<>();
		for(AnnexUsers annexUser: annexUsersList) {
			ImsUser imsUser = imsUserRepository.findByMobileNo(annexUser.getMobileNo());
			if(imsUser != null) {
				List<Pass> passes = passRepository.findAllByImsUserByImsUserIdIdAndEventIdAndDeleted(imsUser.getId(), eventId, false);
				passes =  passes.stream().filter(pass -> pass.getPassStatus() != null
						&& pass.getPassStatus().getId() != PassStatusEnum.CANCELLED.type &&   pass.getPassStatus().getId() != PassStatusEnum.RSVP_REGRETTED.type).collect(Collectors.toList());
				if(passes.size() == 0) {
					unassignedWoPUsers.add(annexUser);
				}
			}
		}
		imsResponse.setMessage("Retrieved WoP Users data!");
		imsResponse.setSuccess(true);
		imsResponse.setData(unassignedWoPUsers);
		return imsResponse;
	}

	private boolean validateCaptcha(Captcha captcha) {
		Captcha captchaEntity = captchaRepository.findByIdAndValid(captcha.getId(), true);
		if (captchaEntity != null && captchaEntity.getValue().equals(captcha.getValue())) {
			captcha.setValid(false);
			captchaRepository.save(captcha);
			return true;
		} else {
			return false;
		}
	}
	
	private List<UserData> filterUserData(List<ImsUser> imsUsers) {
		List<UserData> dataList = new ArrayList<>();
		if(imsUsers != null) {
			imsUsers.forEach( imsUser -> {
				dataList.add(filterUserData(imsUser));
			});
			
		}
		return dataList;
	}

	private UserData filterUserData(ImsUser imsUser) {
		UserData userData = new UserData();
		ImsJpaUpdateUtil.copyEntityProperties(imsUser, userData);
		return userData;
	}
	
	private boolean releaseLoggedInUser(ImsUser user) {

		if (user.getLastLogin() != null) {
			LocalDateTime login2Hrs = user.getLastLogin().plus(30, ChronoUnit.MINUTES);
			
			System.out.println(login2Hrs+" Before "+LocalDateTime.now()+"  condition "+login2Hrs.isBefore(LocalDateTime.now()));
			if (login2Hrs.isBefore(LocalDateTime.now())) {
				user.setLoggedIn(0);
				user.setLastLogin(null);
				imsUserRepository.save(user);
				return true;
			}
		}

		return false;
	}

	private boolean releaseLockedUser(ImsUser user) {
		if (user.getLastLocked() != null) {
			LocalDateTime locked24Hrs = user.getLastLocked().plus(60, ChronoUnit.MINUTES);
			if (locked24Hrs.isBefore(LocalDateTime.now())) {
				user.setLocked(false);
				user.setLastLocked(null);
				user.setLoginAttempts(0);
				imsUserRepository.save(user);
				return true;
			}
		}
		return false;
	}
	
	private boolean isValidPassword(String password) {
		String regex = "^(?=.*[0-9])" + "(?=.*[a-z])(?=.*[A-Z])" + "(?=.*[@#$%^&+=])" + "(?=\\S+$).{8,20}$";
		Pattern p = Pattern.compile(regex);
		if (password == null) {
			return false;
		}
		Matcher m = p.matcher(password);
		return m.matches();
	}
	
	private void invalidateOTP(String mobileNo) {
		if (mobileNo != null) {
			TimerTask task = new TimerTask() {
				public void run() {
					ImsUser user = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
					if (user != null) {
						user.setPassword(passwordEncoder.encode(ImsCipherUtil.generateOTP()));
//						user.setPassword(passwordEncoder.encode("123456"));
						imsUserRepository.save(user);
					}
				}
			};
			Timer timer = new Timer("Invalidate OTP");

			long delay = 600000L;
			timer.schedule(task, delay);
		}
	}

	private void invalidateEmailOTP(String email) {
		if (email != null) {
			TimerTask task = new TimerTask() {
				public void run() {
					List<ImsUser> users = imsUserRepository.getByEmailAndDeleted(email, false);
					users.forEach(user -> {
						if (user != null) {
							user.setPassword(passwordEncoder.encode(ImsCipherUtil.generateOTP()));
//							user.setPassword(passwordEncoder.encode("123456"));
							imsUserRepository.save(user);
						}

					});
				}
			};
			Timer timer = new Timer("Invalidate OTP");

			long delay = 600000L;
			timer.schedule(task, delay);
		}
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	public String handleCustomExceptions(Exception ex) {
		ex.printStackTrace();
		return "Something went wrong at the server!";
	}
	
	private boolean checkPasswordPolicy(ImsUser imsUser, String newPassword) {
		boolean isValid = true;
		List<UserPasswordPolicy> userPasswordPolicies = userPasswordPolicyRepository.findAllByImsUserId(imsUser.getId());
		for( UserPasswordPolicy userPasswordPolicy : userPasswordPolicies) {
			String oldHash = userPasswordPolicy.getPasswordHash();
			String oldSalt = userPasswordPolicy.getPasswordSalt();
			boolean isMatchesOldPassWord = ImsCipherUtil.validate(newPassword, oldSalt, oldHash);
			if(isMatchesOldPassWord) {
				return false;
				
			} else {
				isValid = true;
			}
		}
		return isValid;
	}
	

}
