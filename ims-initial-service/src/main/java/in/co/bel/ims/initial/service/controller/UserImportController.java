package in.co.bel.ims.initial.service.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.AnnexUsersRepository;
import in.co.bel.ims.initial.data.repository.DepartmentRepository;
import in.co.bel.ims.initial.data.repository.EnclosureGroupRepository;
import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.MaritalStatusRepository;
import in.co.bel.ims.initial.data.repository.PayLevelRepository;
import in.co.bel.ims.initial.data.repository.RoleRepository;
import in.co.bel.ims.initial.data.repository.SalutationRepository;
import in.co.bel.ims.initial.data.repository.UserTypeRepository;
import in.co.bel.ims.initial.entity.AnnexUsers;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.PayLevel;
import in.co.bel.ims.initial.entity.Role;
import in.co.bel.ims.initial.entity.UserType;
import in.co.bel.ims.initial.infra.dto.AutonomousUserData;
import in.co.bel.ims.initial.infra.dto.CommissionUserData;
import in.co.bel.ims.initial.infra.dto.DelegatesUserData;
import in.co.bel.ims.initial.infra.dto.GuestUserData;
import in.co.bel.ims.initial.infra.dto.MinistryUserData;
import in.co.bel.ims.initial.infra.dto.MlaMpUserData;
import in.co.bel.ims.initial.infra.dto.validator.AutonomousUserDataValidator;
import in.co.bel.ims.initial.infra.dto.validator.CommissionUserDataValidator;
import in.co.bel.ims.initial.infra.dto.validator.DelegatesUserDataValidator;
import in.co.bel.ims.initial.infra.dto.validator.GuestUserDataValidator;
import in.co.bel.ims.initial.infra.dto.validator.MinistryUserDataValidator;
import in.co.bel.ims.initial.infra.dto.validator.MlaMpUserDataValidator;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.exception.DuplicateUserException;
import in.co.bel.ims.initial.service.util.ImsCipherUtil;
import in.co.bel.ims.initial.service.util.ImsUserTypeEnum;
import in.co.bel.ims.initial.service.util.LogLevelEnum;
import in.co.bel.ims.initial.service.util.LogUtil;
import in.co.bel.ims.initial.service.util.LoginStatusEnum;
import in.co.bel.ims.initial.service.util.RoleEnum;

@RestController
@CrossOrigin
@RequestMapping("/app/import")
public class UserImportController {

	@Autowired
	private ImsUserRepository imsUserRepository;
//	@Autowired
//	private ExcelTemplateParser excelTemplateParser;
	@Autowired
	private UserTypeRepository userTypeRepository;
	@Autowired
	private SalutationRepository salutationRepository;
	@Autowired
	private PayLevelRepository payLevelRepository;
	@Autowired
	private MaritalStatusRepository maritalStatusRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private EnclosureGroupRepository enclosureGroupRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private AnnexUsersRepository annexUsersRepository;

	@Autowired
	LogUtil log;
	
	
	private static final int OTHERS_PAYLEVEL = 10;

	@PostMapping("/importAnnexAUsers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse importAnnexAUsers(@RequestBody List<MinistryUserData> ministryUserData) throws MethodArgumentNotValidException {
		ImsResponse imsResponse = new ImsResponse();
		for(MinistryUserData user:ministryUserData) {
			DataBinder dataBinder = new DataBinder(user);
		      dataBinder.addValidators(new MinistryUserDataValidator());
		      dataBinder.validate();
		      if(dataBinder.getBindingResult().hasErrors())
		    	  throw new MethodArgumentNotValidException(null, dataBinder.getBindingResult());
		}
		
		List<ImsUser> imsUsers = new ArrayList<>();
		List<AnnexUsers> annexUsers = new ArrayList<>();
		HashMap<String,String> importResult = new HashMap<String,String>();
		AtomicReference<UserType> userType = new AtomicReference<>();
		userType.set(null);
		if(ministryUserData != null && !ministryUserData.isEmpty())
			userType.set(getUserType(ImsUserTypeEnum.Annexure_A));

//		List<String> empIdListFromRequest = Optional.ofNullable(ministryUserData)
//	            .orElseGet(Collections::emptyList).stream()
//				.filter(userData -> userData.getEmpId() != null)
//				.map(userData -> userData.getEmpId())
//				.collect(Collectors.toList());
		//List<ImsUser> existingImsUsersByEmpId = imsUserRepository.getByEmpNoInAndDeleted(empIdListFromRequest, false);
//		final List<String> existingEmpIdList = Optional.ofNullable(existingImsUsersByEmpId)
//	            .orElseGet(Collections::emptyList).stream()
//				.filter(imsUser -> imsUser.getEmpNo() != null)
//				.map(imsUser -> imsUser.getEmpNo())
//				.collect(Collectors.toList());
		
		List<AnnexUsers> annexUsersList = annexUsersRepository.findByDeleted(false);
		annexUsersList = annexUsersList.stream().filter(user -> user.getEmpNo() != null).collect(Collectors.toList());
		List<String> empIds = annexUsersList.stream().map(user -> user.getEmpNo()).collect(Collectors.toList());
		
		ministryUserData.forEach(user -> {
			ImsUser imsUser = new ImsUser();
			AnnexUsers annexUser = new AnnexUsers();
			if(empIds.contains(user.getEmpId())) {
				importResult.put(user.getEmpId(), "User with Emp ID "+ user.getEmpId() + " already exists!" );
			}else {
				empIds.add(user.getEmpId());
				if (user.getEmpId() != null && !user.getEmpId().isEmpty()) {
					imsUser.setEmpNo(user.getEmpId());
					annexUser.setEmpNo(user.getEmpId());
				}
				if (user.getDepartmentId() > 0) {
					imsUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
					annexUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
				}
				if (user.getSalutationId() > 0) {
					imsUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
					annexUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
				}
				if (user.getName() != null && !user.getName().isEmpty()) {
					imsUser.setName(user.getName());
					annexUser.setName(user.getName());
				}
				if (user.getDateOfBirth() != null) {
					imsUser.setDateOfBirth(user.getDateOfBirth());
					annexUser.setDob(user.getDateOfBirth());
				}
				if (user.getDesignation() != null && !user.getDesignation().isEmpty()) {
					imsUser.setDesignation(user.getDesignation());
					annexUser.setDesignation(user.getDesignation());
				}
				if (user.getPayLevelId() > 0) {
					PayLevel paylevel;
					if(user.getPayLevelId() < 10)
						paylevel = payLevelRepository.findById(user.getPayLevelId()).get();
					else
						paylevel = payLevelRepository.findById(OTHERS_PAYLEVEL).get();
					imsUser.setPayLevel(paylevel);
					annexUser.setPayLevel(paylevel);
				}
				if (user.getBasicPay() != null) {
					imsUser.setBasicPay(user.getBasicPay());
					annexUser.setBasicPay(user.getBasicPay());
				}
				if (user.getMobileNo() != null && !user.getMobileNo().isEmpty()) {
					imsUser.setMobileNo(user.getMobileNo());
					annexUser.setMobileNo(user.getMobileNo());
				}
				if (user.getEmail() != null && !user.getEmail().isEmpty()) {
					imsUser.setEmail(user.getEmail());
					annexUser.setEmail(user.getEmail());
				}
				if (user.getMobileNo() != null && !user.getMobileNo().isEmpty()) {
					imsUser.setMobileNo(user.getMobileNo());
				}
				if (user.getMaritalStatusId() > 0) {
					imsUser.setMaritalStatus(maritalStatusRepository.findById(user.getMaritalStatusId()).get());
					annexUser.setMaritalStatus(maritalStatusRepository.findById(user.getMaritalStatusId()).get());
				}
				if (user.getOfficialAddress() != null && !user.getOfficialAddress().isEmpty()) {
					imsUser.setOfficeAddress(user.getOfficialAddress());
					annexUser.setOfficeAddress(user.getOfficialAddress());
				}
				if (user.getResidentialAddress() != null && !user.getResidentialAddress().isEmpty()) {
					imsUser.setResidentialAddress(user.getResidentialAddress());
					annexUser.setAddress(user.getResidentialAddress());
				}
				if (user.getRemarks() != null && !user.getRemarks().isEmpty()) {
					imsUser.setRemarks(user.getRemarks());
					annexUser.setRemarks(user.getRemarks());
				}
				if (user.getEnclosureGroupId() > 0) {
					imsUser.setEnclosureGroup(enclosureGroupRepository.findById(user.getEnclosureGroupId()).get());
					annexUser.setEnclosureGroup(enclosureGroupRepository.findById(user.getEnclosureGroupId()).get());
				}
				imsUser.setUserType(userType.get());
				imsUser.setRole(roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get());
				imsUsers.add(imsUser);
				annexUser.setDeleted(false);
				annexUser.setUserType(userType.get());
				annexUsers.add(annexUser);
			}
		});

		Map<String, ImsUser> uniqueUsers = new HashMap<>();
		List<String> uniqueUsersMobiles = new ArrayList<>();
		for (ImsUser imsUser : imsUsers) {
			if (uniqueUsers.get(imsUser.getMobileNo()) == null) {
				uniqueUsersMobiles.add(imsUser.getMobileNo());
				uniqueUsers.put(imsUser.getMobileNo(), imsUser);
			}
		}
		List<ImsUser> listOfUniqueUsers = uniqueUsers.values().stream().collect(Collectors.toList());

		List<ImsUser> existingImsUsers = imsUserRepository.getByMobileNoInAndDeleted(uniqueUsersMobiles, false);
		List<String> existingImsUserMobiles = Optional.ofNullable(existingImsUsers)
	            .orElseGet(Collections::emptyList).stream()
				.filter(imsUser -> imsUser.getMobileNo() != null)
				.map(imsUser -> imsUser.getMobileNo())
				.collect(Collectors.toList());
		List<ImsUser> nonExistingUsers = listOfUniqueUsers.stream()
				.filter(imsUser -> !existingImsUserMobiles.contains(imsUser.getMobileNo()))
				.collect(Collectors.toList());
		
		if(nonExistingUsers.size() > 50) {
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers.subList(0, 50)));
			saveImsUsers(nonExistingUsers.subList(50, nonExistingUsers.size()));
		} else
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers));
		
		if(annexUsers.size() > 50) {
			annexUsersRepository.saveAll(annexUsers.subList(0, 50));
			saveAnnexUsers(annexUsers.subList(50, annexUsers.size()));
		} else
			annexUsersRepository.saveAll(annexUsers);
		
		if(importResult.size() > 0) {
			imsResponse.setData(importResult);
			imsResponse.setSuccess(false);
			imsResponse.setMessage("Users with duplicate Emp Ids found. And the users with unique Emp Ids were saved successfully!");
			log.saveLog(null, "Users with duplicate Emp Ids found. And the users with unique Emp Ids were saved successfully!", "USER_CREATION",
					LogLevelEnum.WARN);
		}else {
			imsResponse.setSuccess(true);
			imsResponse.setMessage("Annex A users were imported and saved successfully!");
			log.saveLog(null, "Annex A users were imported and saved successfully!", "USER_CREATION",
					LogLevelEnum.INFO);
		}
		return imsResponse;

	}

	@PostMapping("/importAnnexBUsers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse importAnnexBUsers(@RequestBody List<AutonomousUserData> autonomousUserData) throws MethodArgumentNotValidException {
		ImsResponse imsResponse = new ImsResponse();
		for(AutonomousUserData user:autonomousUserData) {
			DataBinder dataBinder = new DataBinder(user);
		      dataBinder.addValidators(new AutonomousUserDataValidator());
		      dataBinder.validate();
		      if(dataBinder.getBindingResult().hasErrors())
		    	  throw new MethodArgumentNotValidException(null, dataBinder.getBindingResult());
		}
	      
		List<ImsUser> imsUsers = new ArrayList<>();
		List<AnnexUsers> annexUsers = new ArrayList<>();
		
		HashMap<String,String> importResult = new HashMap<String,String>();
		AtomicReference<UserType> userType = new AtomicReference<>();
		userType.set(null);
		if(autonomousUserData != null && !autonomousUserData.isEmpty())
			userType.set(getUserType(ImsUserTypeEnum.Annexure_B));

		List<AnnexUsers> annexUsersList = annexUsersRepository.findByDeleted(false);
		annexUsersList = annexUsersList.stream().filter(user -> user.getEmpNo() != null).collect(Collectors.toList());
		List<String> empIds = annexUsersList.stream().map(user -> user.getEmpNo()).collect(Collectors.toList());
		
		autonomousUserData.forEach(user -> {
			ImsUser imsUser = new ImsUser();
			AnnexUsers annexUser = new AnnexUsers();
			if(empIds.contains(user.getEmpId())) {
				importResult.put(user.getEmpId(), "User with Emp ID "+ user.getEmpId() + " already exists!" );
			}else {
				empIds.add(user.getEmpId());
				if (user.getEmpId() != null && !user.getEmpId().isEmpty()) {
					imsUser.setEmpNo(user.getEmpId());
					annexUser.setEmpNo(user.getEmpId());
				}
				if (user.getDepartmentId() > 0) {
					imsUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
					annexUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
				}
				if (user.getSalutationId() > 0) {
					imsUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
					annexUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
				}
				if (user.getName() != null && !user.getName().isEmpty()) {
					imsUser.setName(user.getName());
					annexUser.setName(user.getName());
				}
				if (user.getDesignation() != null && !user.getDesignation().isEmpty()) {
					imsUser.setDesignation(user.getDesignation());
					annexUser.setDesignation(user.getDesignation());
				}
				if (user.getPayLevelId() > 0) {
					PayLevel paylevel;
					if(user.getPayLevelId() >= 11 && user.getPayLevelId() <=18)
						paylevel = payLevelRepository.findById(getPayLevelId(String.valueOf(user.getPayLevelId()))).get();
					else
						paylevel = payLevelRepository.findById(OTHERS_PAYLEVEL).get();
					imsUser.setPayLevel(paylevel);
					annexUser.setPayLevel(paylevel);
				}
				if (user.getBasicPay() != null) {
					imsUser.setBasicPay(user.getBasicPay());
					annexUser.setBasicPay(user.getBasicPay());
				}
				if (user.getMobileNo() != null && !user.getMobileNo().isEmpty()) {
					imsUser.setMobileNo(user.getMobileNo());
					annexUser.setMobileNo(user.getMobileNo());
				}
				if (user.getDateOfBirth() != null) {
					imsUser.setDateOfBirth(user.getDateOfBirth());
					annexUser.setDob(user.getDateOfBirth());
				}
				if (user.getEmail() != null && !user.getEmail().isEmpty()) {
					imsUser.setEmail(user.getEmail());
					annexUser.setEmail(user.getEmail());
				}
				if (user.getMaritalStatusId() > 0) {
					imsUser.setMaritalStatus(maritalStatusRepository.findById(user.getMaritalStatusId()).get());
					annexUser.setMaritalStatus(maritalStatusRepository.findById(user.getMaritalStatusId()).get());
				}
				if (user.getOfficialAddress() != null && !user.getOfficialAddress().isEmpty()) {
					imsUser.setOfficeAddress(user.getOfficialAddress());
					annexUser.setOfficeAddress(user.getOfficialAddress());
				}
				if (user.getResidentialAddress() != null && !user.getResidentialAddress().isEmpty()) {
					imsUser.setResidentialAddress(user.getResidentialAddress());
					annexUser.setAddress(user.getResidentialAddress());
				}
				if (user.getRemarks() != null && !user.getRemarks().isEmpty()) {
					imsUser.setRemarks(user.getRemarks());
					annexUser.setRemarks(user.getRemarks());
				}
				if (user.getEnclosureGroupId() > 0) {
					imsUser.setEnclosureGroup(enclosureGroupRepository.findById(user.getEnclosureGroupId()).get());
					annexUser.setEnclosureGroup(enclosureGroupRepository.findById(user.getEnclosureGroupId()).get());
				}
				if (user.getEquivalentStatus() != null && !user.getEquivalentStatus().isEmpty()) {
					imsUser.setEquivalentStatus(user.getEquivalentStatus());
					annexUser.setEquivalentStatus(user.getEquivalentStatus());
				}
				imsUser.setUserType(userType.get());
				imsUser.setRole(roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get());
				imsUsers.add(imsUser);
				annexUser.setDeleted(false);
				annexUser.setUserType(userType.get());
				annexUsers.add(annexUser);
			}

		});

		Map<String, ImsUser> uniqueUsers = new HashMap<>();
		List<String> uniqueUsersMobiles = new ArrayList<>();
		for (ImsUser imsUser : imsUsers) {
			if (uniqueUsers.get(imsUser.getMobileNo()) == null) {
				uniqueUsersMobiles.add(imsUser.getMobileNo());
				uniqueUsers.put(imsUser.getMobileNo(), imsUser);
			}
		}
		List<ImsUser> listOfUniqueUsers = uniqueUsers.values().stream().collect(Collectors.toList());

		List<ImsUser> existingImsUsers = imsUserRepository.getByMobileNoInAndDeleted(uniqueUsersMobiles, false);
		List<String> existingImsUserMobiles = Optional.ofNullable(existingImsUsers)
	            .orElseGet(Collections::emptyList).stream()
				.filter(imsUser -> imsUser.getMobileNo() != null)
				.map(imsUser -> imsUser.getMobileNo())
				.collect(Collectors.toList());
		List<ImsUser> nonExistingUsers = listOfUniqueUsers.stream()
				.filter(imsUser -> !existingImsUserMobiles.contains(imsUser.getMobileNo()))
				.collect(Collectors.toList());
		
		if(nonExistingUsers.size() > 50) {
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers.subList(0, 50)));
			saveImsUsers(nonExistingUsers.subList(50, nonExistingUsers.size()));
		} else
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers));
		
		if(annexUsers.size() > 50) {
			annexUsersRepository.saveAll(annexUsers.subList(0, 50));
			saveAnnexUsers(annexUsers.subList(50, annexUsers.size()));
		} else
			annexUsersRepository.saveAll(annexUsers);
		
		if(importResult.size() > 0) {
			imsResponse.setData(importResult);
			imsResponse.setSuccess(false);
			imsResponse.setMessage("Users with duplicate Emp Ids found. And the users with unique Emp Ids were saved successfully!");
			log.saveLog(null, "Users with duplicate Emp Ids found. And the users with unique Emp Ids were saved successfully!", "USER_CREATION",
					LogLevelEnum.WARN);
		}else {
			imsResponse.setSuccess(true);
			imsResponse.setMessage("Annex B users were imported and saved successfully!");
			log.saveLog(null, "Annex B users were imported and saved successfully!", "USER_CREATION",
					LogLevelEnum.INFO);
		}
		return imsResponse;

	}

	@PostMapping("/importAnnexCUsers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse importAnnexCUsers(@RequestBody List<CommissionUserData> commissionUserData) throws MethodArgumentNotValidException {
		ImsResponse imsResponse = new ImsResponse();
		for(CommissionUserData user:commissionUserData) {
			DataBinder dataBinder = new DataBinder(user);
		      dataBinder.addValidators(new CommissionUserDataValidator());
		      dataBinder.validate();
		      if(dataBinder.getBindingResult().hasErrors())
		    	  throw new MethodArgumentNotValidException(null, dataBinder.getBindingResult());
		}
		
		List<ImsUser> imsUsers = new ArrayList<>();
		List<AnnexUsers> annexUsers = new ArrayList<>();
		HashMap<String,String> importResult = new HashMap<String,String>();
		AtomicReference<UserType> userType = new AtomicReference<>();
		userType.set(null);
		if(commissionUserData != null && !commissionUserData.isEmpty())
			userType.set(getUserType(ImsUserTypeEnum.Annexure_C));

		List<AnnexUsers> annexUsersList = annexUsersRepository.findByDeleted(false);
		annexUsersList = annexUsersList.stream().filter(user -> user.getEmpNo() != null).collect(Collectors.toList());
		List<String> empIds = annexUsersList.stream().map(user -> user.getEmpNo()).collect(Collectors.toList());
		
		commissionUserData.forEach(user -> {
			ImsUser imsUser = new ImsUser();
			AnnexUsers annexUser = new AnnexUsers();
			if(empIds.contains(user.getEmpId())) {
				importResult.put(user.getEmpId(), "User with Emp ID "+ user.getEmpId() + " already exists!" );
			}else {
				empIds.add(user.getEmpId());
				if (user.getEmpId() != null && !user.getEmpId().isEmpty()) {
					imsUser.setEmpNo(user.getEmpId());
					annexUser.setEmpNo(user.getEmpId());
				}
				if (user.getDepartmentId() > 0) {
					imsUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
					annexUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
				}
				if (user.getSalutationId() > 0) {
					imsUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
					annexUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
				}
				if (user.getName() != null && !user.getName().isEmpty()) {
					imsUser.setName(user.getName());
					annexUser.setName(user.getName());
				}
				if (user.getDesignation() != null && !user.getDesignation().isEmpty()) {
					imsUser.setDesignation(user.getDesignation());
					annexUser.setDesignation(user.getDesignation());
				}
				if (user.getPayLevelId() > 0) {
					PayLevel paylevel;
					if(user.getPayLevelId() >= 11 && user.getPayLevelId() <=18)
						paylevel = payLevelRepository.findById(getPayLevelId(String.valueOf(user.getPayLevelId()))).get();
					else
						paylevel = payLevelRepository.findById(OTHERS_PAYLEVEL).get();
					imsUser.setPayLevel(paylevel);
					annexUser.setPayLevel(paylevel);
				}
				if (user.getDateOfBirth() != null) {
					imsUser.setDateOfBirth(user.getDateOfBirth());
					annexUser.setDob(user.getDateOfBirth());
				}
				if (user.getBasicPay() != null) {
					imsUser.setBasicPay(user.getBasicPay());
					annexUser.setBasicPay(user.getBasicPay());
				}
				if (user.getMobileNo() != null && !user.getMobileNo().isEmpty()) {
					imsUser.setMobileNo(user.getMobileNo());
					annexUser.setMobileNo(user.getMobileNo());
				}
				if (user.getEmail() != null && !user.getEmail().isEmpty()) {
					imsUser.setEmail(user.getEmail());
					annexUser.setEmail(user.getEmail());
				}
				if (user.getMaritalStatusId() > 0) {
					imsUser.setMaritalStatus(maritalStatusRepository.findById(user.getMaritalStatusId()).get());
					annexUser.setMaritalStatus(maritalStatusRepository.findById(user.getMaritalStatusId()).get());
				}
				if (user.getOfficialAddress() != null && !user.getOfficialAddress().isEmpty()) {
					imsUser.setOfficeAddress(user.getOfficialAddress());
					annexUser.setOfficeAddress(user.getOfficialAddress());
				}
				if (user.getResidentialAddress() != null && !user.getResidentialAddress().isEmpty()) {
					imsUser.setResidentialAddress(user.getResidentialAddress());
					annexUser.setAddress(user.getResidentialAddress());
				}
				if (user.getRemarks() != null && !user.getRemarks().isEmpty()) {
					imsUser.setRemarks(user.getRemarks());
					annexUser.setRemarks(user.getRemarks());
				}
				if (user.getEnclosureGroupId() > 0) {
					imsUser.setEnclosureGroup(enclosureGroupRepository.findById(user.getEnclosureGroupId()).get());
					annexUser.setEnclosureGroup(enclosureGroupRepository.findById(user.getEnclosureGroupId()).get());
				}
				if (user.getEquivalentStatus() != null && !user.getEquivalentStatus().isEmpty()) {
					imsUser.setEquivalentStatus(user.getEquivalentStatus());
					annexUser.setEquivalentStatus(user.getEquivalentStatus());
				}
				if (user.getPrecedenceId() != null) {
					//Precedence not mandatory in ImsUser hence skipped
					//imsUser.setPrecedence(precedenceRepository.findById(user.getPrecedenceId()).get());
					annexUser.setPrecedenceInfo(user.getPrecedenceId());
				}
				imsUser.setUserType(userType.get());
				imsUser.setRole(roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get());
				imsUsers.add(imsUser);
				annexUser.setDeleted(false);
				annexUser.setUserType(userType.get());
				annexUsers.add(annexUser);
			}
		
		});

		Map<String, ImsUser> uniqueUsers = new HashMap<>();
		List<String> uniqueUsersMobiles = new ArrayList<>();
		for (ImsUser imsUser : imsUsers) {
			if (uniqueUsers.get(imsUser.getMobileNo()) == null) {
				uniqueUsersMobiles.add(imsUser.getMobileNo());
				uniqueUsers.put(imsUser.getMobileNo(), imsUser);
			}
		}
		List<ImsUser> listOfUniqueUsers = uniqueUsers.values().stream().collect(Collectors.toList());

		List<ImsUser> existingImsUsers = imsUserRepository.getByMobileNoInAndDeleted(uniqueUsersMobiles, false);
		List<String> existingImsUserMobiles = Optional.ofNullable(existingImsUsers)
	            .orElseGet(Collections::emptyList).stream()
				.filter(imsUser -> imsUser.getMobileNo() != null)
				.map(imsUser -> imsUser.getMobileNo())
				.collect(Collectors.toList());
		List<ImsUser> nonExistingUsers = listOfUniqueUsers.stream()
				.filter(imsUser -> !existingImsUserMobiles.contains(imsUser.getMobileNo()))
				.collect(Collectors.toList());
		
		if(nonExistingUsers.size() > 50) {
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers.subList(0, 50)));
			saveImsUsers(nonExistingUsers.subList(50, nonExistingUsers.size()));
		} else
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers));
		
		if(annexUsers.size() > 50) {
			annexUsersRepository.saveAll(annexUsers.subList(0, 50));
			saveAnnexUsers(annexUsers.subList(50, annexUsers.size()));
		} else
			annexUsersRepository.saveAll(annexUsers);
		
		if(importResult.size() > 0) {
			imsResponse.setData(importResult);
			imsResponse.setSuccess(false);
			imsResponse.setMessage("Users with duplicate Emp Ids found. And the users with unique Emp Ids were saved successfully!");
			log.saveLog(null, "Users with duplicate Emp Ids found. And the users with unique Emp Ids were saved successfully!", "USER_CREATION",
					LogLevelEnum.WARN);
		}else {
			imsResponse.setSuccess(true);
			imsResponse.setMessage("Annex C users were imported and saved successfully!");
			log.saveLog(null, "Annex C users were imported and saved successfully!", "USER_CREATION",
					LogLevelEnum.INFO);
		}
		return imsResponse;

	}

	@PostMapping("/importAnnexDUsers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse importAnnexDUsers(@RequestBody List<GuestUserData> guestUserData) throws MethodArgumentNotValidException {
		ImsResponse imsResponse = new ImsResponse();
		for(GuestUserData user:guestUserData) {
			DataBinder dataBinder = new DataBinder(user);
		      dataBinder.addValidators(new GuestUserDataValidator());
		      dataBinder.validate();
		      if(dataBinder.getBindingResult().hasErrors())
		    	  throw new MethodArgumentNotValidException(null, dataBinder.getBindingResult());
		}
		
		List<ImsUser> imsUsers = new ArrayList<>();
		List<AnnexUsers> annexUsers = new ArrayList<>();
		HashMap<String,String> importResult = new HashMap<String,String>();
		AtomicReference<UserType> userType = new AtomicReference<>();
		userType.set(null);
		if(guestUserData != null && !guestUserData.isEmpty())
			userType.set(getUserType(ImsUserTypeEnum.Annexure_D));

		List<AnnexUsers> annexUsersList = annexUsersRepository.findByDeleted(false);
		annexUsersList = annexUsersList.stream().filter(user -> user.getIdproof() != null).collect(Collectors.toList());
		List<String> idProofNos = annexUsersList.stream().map(user -> user.getIdproof()).collect(Collectors.toList());
		
		guestUserData.forEach(user -> {
			ImsUser imsUser = new ImsUser();
			AnnexUsers annexUser = new AnnexUsers();
			if(idProofNos.contains(user.getGovtIdNumber())) {
				importResult.put(user.getGovtIdNumber(), "User with Government Id Number "+ user.getGovtIdNumber() + " already exists!" );
			}else {
				idProofNos.add(user.getGovtIdNumber());
				if (user.getDepartmentId() > 0) {
					imsUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
					annexUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
				}
				if (user.getSalutationId() > 0) {
					imsUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
					annexUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
				}
				if (user.getName() != null && !user.getName().isEmpty()) {
					imsUser.setName(user.getName());
					annexUser.setName(user.getName());
				}
				if (user.getMobileNo() != null && !user.getMobileNo().isEmpty()) {
					imsUser.setMobileNo(user.getMobileNo());
					annexUser.setMobileNo(user.getMobileNo());
				}
				if (user.getEmail() != null && !user.getEmail().isEmpty()) {
					imsUser.setEmail(user.getEmail());
					annexUser.setEmail(user.getEmail());
				}
				if (user.getDateOfBirth() != null) {
					imsUser.setDateOfBirth(user.getDateOfBirth());
					annexUser.setDob(user.getDateOfBirth());
				}
				if (user.getResidentialAddress() != null && !user.getResidentialAddress().isEmpty()) {
					imsUser.setResidentialAddress(user.getResidentialAddress());
					annexUser.setAddress(user.getResidentialAddress());
				}
				if (user.getRemarks() != null && !user.getRemarks().isEmpty()) {
					imsUser.setRemarks(user.getRemarks());
					annexUser.setRemarks(user.getRemarks());
				}
				if (user.getNationality() != null && !user.getNationality().isEmpty()) {
					imsUser.setNationality(user.getNationality());
					annexUser.setNationality(user.getNationality());
				}
				if (user.getGovtIdNumber() != null && !user.getGovtIdNumber().isEmpty()) {
					imsUser.setIdProofNo(user.getGovtIdNumber());
					annexUser.setIdproof(user.getGovtIdNumber());
				}
				if (user.getRecommendingOfficerName() != null && !user.getRecommendingOfficerName().isEmpty()) {
					imsUser.setRecommendingOfficerName(user.getRecommendingOfficerName());
					annexUser.setRecommendingOfficerName(user.getRecommendingOfficerName());
				}
				if (user.getRecommendingOfficerDesignation() != null
						&& !user.getRecommendingOfficerDesignation().isEmpty()) {
					imsUser.setRecommendingOfficerDesignation(user.getRecommendingOfficerDesignation());
					annexUser.setRecommendingOfficerDesignation(user.getRecommendingOfficerDesignation());
				}
				imsUser.setUserType(userType.get());
				imsUser.setRole(roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get());
				imsUsers.add(imsUser);
				annexUser.setDeleted(false);
				annexUser.setUserType(userType.get());
				annexUsers.add(annexUser);
			}
		
		});

		Map<String, ImsUser> uniqueUsers = new HashMap<>();
		List<String> uniqueUsersMobiles = new ArrayList<>();
		for (ImsUser imsUser : imsUsers) {
			if (uniqueUsers.get(imsUser.getMobileNo()) == null) {
				uniqueUsersMobiles.add(imsUser.getMobileNo());
				uniqueUsers.put(imsUser.getMobileNo(), imsUser);
			}
		}
		List<ImsUser> listOfUniqueUsers = uniqueUsers.values().stream().collect(Collectors.toList());

		List<ImsUser> existingImsUsers = imsUserRepository.getByMobileNoInAndDeleted(uniqueUsersMobiles, false);
		List<String> existingImsUserMobiles = Optional.ofNullable(existingImsUsers)
	            .orElseGet(Collections::emptyList).stream()
				.filter(imsUser -> imsUser.getMobileNo() != null)
				.map(imsUser -> imsUser.getMobileNo())
				.collect(Collectors.toList());
		List<ImsUser> nonExistingUsers = listOfUniqueUsers.stream()
				.filter(imsUser -> !existingImsUserMobiles.contains(imsUser.getMobileNo()))
				.collect(Collectors.toList());
		
		if(nonExistingUsers.size() > 50) {
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers.subList(0, 50)));
			saveImsUsers(nonExistingUsers.subList(50, nonExistingUsers.size()));
		} else
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers));
		
		if(annexUsers.size() > 50) {
			annexUsersRepository.saveAll(annexUsers.subList(0, 50));
			saveAnnexUsers(annexUsers.subList(50, annexUsers.size()));
		} else
			annexUsersRepository.saveAll(annexUsers);
		
		log.saveLog(null, imsUsers.size() + " New Annex D users were imported and saved successfully!", "USER_CREATION",
				LogLevelEnum.INFO);
		
		if(importResult.size() > 0) {
			imsResponse.setData(importResult);
			imsResponse.setSuccess(false);
			imsResponse.setMessage("Users with duplicate Government IDs found. And the users with unique Government IDs were saved successfully!");
		}else {
			imsResponse.setSuccess(true);
			imsResponse.setMessage("Annex D users were imported and saved successfully!");
		}
		return imsResponse;

	}

	@PostMapping("/importAnnexEUsers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse importAnnexEUsers(@RequestBody List<DelegatesUserData> delegatesUserData) throws MethodArgumentNotValidException {
		ImsResponse imsResponse = new ImsResponse();
		for(DelegatesUserData user:delegatesUserData) {
			DataBinder dataBinder = new DataBinder(user);
		      dataBinder.addValidators(new DelegatesUserDataValidator());
		      dataBinder.validate();
		      if(dataBinder.getBindingResult().hasErrors())
		    	  throw new MethodArgumentNotValidException(null, dataBinder.getBindingResult());
		}
		
		List<ImsUser> imsUsers = new ArrayList<>();
		List<AnnexUsers> annexUsers = new ArrayList<>();
		HashMap<String,String> importResult = new HashMap<String,String>();
		AtomicReference<UserType> userType = new AtomicReference<>();
		userType.set(null);
		if(delegatesUserData != null && !delegatesUserData.isEmpty())
			userType.set(getUserType(ImsUserTypeEnum.Annexure_E));
		List<AnnexUsers> annexUsersList = annexUsersRepository.findByDeleted(false);
		annexUsersList = annexUsersList.stream().filter(user -> user.getEmpNo() != null).collect(Collectors.toList());
		List<String> empIds = annexUsersList.stream().map(user -> user.getEmpNo()).collect(Collectors.toList());
		
		delegatesUserData.forEach(user -> {
			ImsUser imsUser = new ImsUser();
			AnnexUsers annexUser = new AnnexUsers();
			if(empIds.contains(user.getEmpId())) {
				importResult.put(user.getEmpId(), "User with Emp ID "+ user.getEmpId() + " already exists!" );
			}else {
				empIds.add(user.getEmpId());
				if (user.getEmpId() != null && !user.getEmpId().isEmpty()) {
					imsUser.setEmpNo(user.getEmpId());
					annexUser.setEmpNo(user.getEmpId());
				}
				if (user.getDepartmentId() > 0) {
					imsUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
					annexUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
				}
				if (user.getSalutationId() > 0) {
					imsUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
					annexUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
				}
				if (user.getName() != null && !user.getName().isEmpty()) {
					imsUser.setName(user.getName());
					annexUser.setName(user.getName());
				}
				if (user.getMobileNo() != null && !user.getMobileNo().isEmpty()) {
					imsUser.setMobileNo(user.getMobileNo());
					annexUser.setMobileNo(user.getMobileNo());
				}
				if (user.getEmail() != null && !user.getEmail().isEmpty()) {
					imsUser.setEmail(user.getEmail());
					annexUser.setEmail(user.getEmail());
				}
				if (user.getDateOfBirth() != null) {
					imsUser.setDateOfBirth(user.getDateOfBirth());
					annexUser.setDob(user.getDateOfBirth());
				}
				if (user.getMaritalStatusId() > 0) {
					imsUser.setMaritalStatus(maritalStatusRepository.findById(user.getMaritalStatusId()).get());
					annexUser.setMaritalStatus(maritalStatusRepository.findById(user.getMaritalStatusId()).get());
				}
				if (user.getOfficialAddress() != null && !user.getOfficialAddress().isEmpty()) {
					imsUser.setOfficeAddress(user.getOfficialAddress());
					annexUser.setOfficeAddress(user.getOfficialAddress());
				}
				if (user.getRemarks() != null && !user.getRemarks().isEmpty()) {
					imsUser.setRemarks(user.getRemarks());
					annexUser.setRemarks(user.getRemarks());
				}
				if (user.getCountryOrOrganization() != null && !user.getCountryOrOrganization().isEmpty()) {
					imsUser.setNationality(user.getCountryOrOrganization());
					annexUser.setNationality(user.getCountryOrOrganization());
				}
				if (user.getPositionInMissionOrConsulate() != null && !user.getPositionInMissionOrConsulate().isEmpty()) {
					imsUser.setPositionMissionConsulate(user.getPositionInMissionOrConsulate());
					annexUser.setPositionMissionConsulate(user.getPositionInMissionOrConsulate());
				}
				imsUser.setUserType(userType.get());
				imsUser.setRole(roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get());
				imsUsers.add(imsUser);
				annexUser.setDeleted(false);
				annexUser.setUserType(userType.get());
				annexUsers.add(annexUser);
			}
		
		});

		Map<String, ImsUser> uniqueUsers = new HashMap<>();
		List<String> uniqueUsersMobiles = new ArrayList<>();
		for (ImsUser imsUser : imsUsers) {
			if (uniqueUsers.get(imsUser.getMobileNo()) == null) {
				uniqueUsersMobiles.add(imsUser.getMobileNo());
				uniqueUsers.put(imsUser.getMobileNo(), imsUser);
			}
		}
		List<ImsUser> listOfUniqueUsers = uniqueUsers.values().stream().collect(Collectors.toList());

		List<ImsUser> existingImsUsers = imsUserRepository.getByMobileNoInAndDeleted(uniqueUsersMobiles, false);
		List<String> existingImsUserMobiles = Optional.ofNullable(existingImsUsers)
	            .orElseGet(Collections::emptyList).stream()
				.filter(imsUser -> imsUser.getMobileNo() != null)
				.map(imsUser -> imsUser.getMobileNo())
				.collect(Collectors.toList());
		List<ImsUser> nonExistingUsers = listOfUniqueUsers.stream()
				.filter(imsUser -> !existingImsUserMobiles.contains(imsUser.getMobileNo()))
				.collect(Collectors.toList());
		
		if(nonExistingUsers.size() > 50) {
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers.subList(0, 50)));
			saveImsUsers(nonExistingUsers.subList(50, nonExistingUsers.size()));
		} else
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers));
		
		if(annexUsers.size() > 50) {
			annexUsersRepository.saveAll(annexUsers.subList(0, 50));
			saveAnnexUsers(annexUsers.subList(50, annexUsers.size()));
		} else
			annexUsersRepository.saveAll(annexUsers);
		
		
		if(importResult.size() > 0) {
			imsResponse.setData(importResult);
			imsResponse.setSuccess(false);
			imsResponse.setMessage("Users with duplicate Emp Ids found. And the users with unique Emp Ids were saved successfully!");
			log.saveLog(null, "Users with duplicate Emp Ids found. And the users with unique Emp Ids were saved successfully!", "USER_CREATION",
					LogLevelEnum.WARN);
		}else {
			imsResponse.setSuccess(true);
			imsResponse.setMessage("Annex E users were imported and saved successfully!");
			log.saveLog(null, "Annex E users were imported and saved successfully!", "USER_CREATION",
					LogLevelEnum.INFO);
		}
		return imsResponse;

	}

	@PostMapping("/importAnnexFUsers")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER')")
	public ImsResponse importAnnexFUsers(@RequestBody List<MlaMpUserData> mlaMpUserData) throws MethodArgumentNotValidException {
		ImsResponse imsResponse = new ImsResponse();
		for(MlaMpUserData user:mlaMpUserData) {
			DataBinder dataBinder = new DataBinder(user);
		      dataBinder.addValidators(new MlaMpUserDataValidator());
		      dataBinder.validate();
		      if(dataBinder.getBindingResult().hasErrors())
		    	  throw new MethodArgumentNotValidException(null, dataBinder.getBindingResult());
		}
		
		List<ImsUser> imsUsers = new ArrayList<>();
		List<AnnexUsers> annexUsers = new ArrayList<>();
		HashMap<String,String> importResult = new HashMap<String,String>();
		AtomicReference<UserType> userType = new AtomicReference<>();
		userType.set(null);
		if(mlaMpUserData != null && !mlaMpUserData.isEmpty())
			userType.set(getUserType(ImsUserTypeEnum.Annexure_F));
		
		List<AnnexUsers> annexUsersList = annexUsersRepository.findByDeleted(false);
		annexUsersList = annexUsersList.stream().filter(user -> user.getEmpNo() != null).collect(Collectors.toList());
		List<String> empIds = annexUsersList.stream().map(user -> user.getEmpNo()).collect(Collectors.toList());
		
		mlaMpUserData.forEach(user -> {
			ImsUser imsUser = new ImsUser();
			AnnexUsers annexUser = new AnnexUsers();
			if(empIds.contains(user.getEmpId())) {
				importResult.put(user.getEmpId(), "User with Emp ID "+ user.getEmpId() + " already exists!" );
			}else {
				empIds.add(user.getEmpId());
				if (user.getEmpId() != null && !user.getEmpId().isEmpty()) {
					imsUser.setEmpNo(user.getEmpId());
					annexUser.setEmpNo(user.getEmpId());
				}
				if (user.getDepartmentId() > 0) {
					imsUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
					annexUser.setDepartment(departmentRepository.findById(user.getDepartmentId()).get());
				}
				if (user.getSalutationId() > 0) {
					imsUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
					annexUser.setSalutation(salutationRepository.findById(user.getSalutationId()).get());
				}
				if (user.getName() != null && !user.getName().isEmpty()) {
					imsUser.setName(user.getName());
					annexUser.setName(user.getName());
				}
				if (user.getMobileNo() != null && !user.getMobileNo().isEmpty()) {
					imsUser.setMobileNo(user.getMobileNo());
					annexUser.setMobileNo(user.getMobileNo());
				}
				if (user.getDateOfBirth() != null) {
					imsUser.setDateOfBirth(user.getDateOfBirth());
					annexUser.setDob(user.getDateOfBirth());
				}
				if (user.getEmail() != null && !user.getEmail().isEmpty()) {
					imsUser.setEmail(user.getEmail());
					annexUser.setEmail(user.getEmail());
				}
				if (user.getMaritalStatusId() > 0) {
					imsUser.setMaritalStatus(maritalStatusRepository.findById(user.getMaritalStatusId()).get());
					annexUser.setMaritalStatus(maritalStatusRepository.findById(user.getMaritalStatusId()).get());
				}
				if (user.getAddress() != null && !user.getAddress().isEmpty()) {
					imsUser.setOfficeAddress(user.getAddress());
					annexUser.setOfficeAddress(user.getAddress());
				}
				if (user.getRemarks() != null && !user.getRemarks().isEmpty()) {
					imsUser.setRemarks(user.getRemarks());
					annexUser.setRemarks(user.getRemarks());
				}
				if (user.getConstituencyOrState() != null && !user.getConstituencyOrState().isEmpty()) {
					imsUser.setConstituency(user.getConstituencyOrState());
					annexUser.setConstituency(user.getConstituencyOrState());
				}
				imsUser.setUserType(userType.get());
				imsUser.setRole(roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get());
				imsUsers.add(imsUser);
				annexUser.setDeleted(false);
				annexUser.setUserType(userType.get());
				annexUsers.add(annexUser);
			}
		
		});

		Map<String, ImsUser> uniqueUsers = new HashMap<>();
		List<String> uniqueUsersMobiles = new ArrayList<>();
		for (ImsUser imsUser : imsUsers) {
			if (uniqueUsers.get(imsUser.getMobileNo()) == null) {
				uniqueUsersMobiles.add(imsUser.getMobileNo());
				uniqueUsers.put(imsUser.getMobileNo(), imsUser);
			}
		}
		List<ImsUser> listOfUniqueUsers = uniqueUsers.values().stream().collect(Collectors.toList());

		List<ImsUser> existingImsUsers = imsUserRepository.getByMobileNoInAndDeleted(uniqueUsersMobiles, false);
		List<String> existingImsUserMobiles = Optional.ofNullable(existingImsUsers)
	            .orElseGet(Collections::emptyList).stream()
				.filter(imsUser -> imsUser.getMobileNo() != null)
				.map(imsUser -> imsUser.getMobileNo())
				.collect(Collectors.toList());
		List<ImsUser> nonExistingUsers = listOfUniqueUsers.stream()
				.filter(imsUser -> !existingImsUserMobiles.contains(imsUser.getMobileNo()))
				.collect(Collectors.toList());
		
		if(nonExistingUsers.size() > 50) {
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers.subList(0, 50)));
			saveImsUsers(nonExistingUsers.subList(50, nonExistingUsers.size()));
		} else
			imsUserRepository.saveAll(populateImsUsers(nonExistingUsers));
		
		if(annexUsers.size() > 50) {
			annexUsersRepository.saveAll(annexUsers.subList(0, 50));
			saveAnnexUsers(annexUsers.subList(50, annexUsers.size()));
		} else
			annexUsersRepository.saveAll(annexUsers);
		
		if(importResult.size() > 0) {
			imsResponse.setData(importResult);
			imsResponse.setSuccess(false);
			imsResponse.setMessage("Users with duplicate Emp Ids found. And the users with unique Emp Ids were saved successfully!");
			log.saveLog(null, "Users with duplicate Emp Ids found. And the users with unique Emp Ids were saved successfully!", "USER_CREATION",
					LogLevelEnum.WARN);
		}else {
			imsResponse.setSuccess(true);
			imsResponse.setMessage("Annex F users were imported and saved successfully!");
			log.saveLog(null, "Annex F users were imported and saved successfully!", "USER_CREATION",
					LogLevelEnum.INFO);
		}
		return imsResponse;
	}
	
	private void saveImsUsers(List<ImsUser> listOfImsUsers) {
		TimerTask task = new TimerTask() {
			public void run() {
				imsUserRepository.saveAll(populateImsUsers(listOfImsUsers));
			}
		};
		Timer timer = new Timer("Save ImsUsers");

		long delay = 10000L;
		timer.schedule(task, delay);
	}
	
	private void saveAnnexUsers(List<AnnexUsers> annexUsers) {
		TimerTask task = new TimerTask() {
			public void run() {
				annexUsersRepository.saveAll(annexUsers);
			}
		};
		Timer timer = new Timer("Save AnnexUsers");

		long delay = 10000L;
		timer.schedule(task, delay);
	}

//	@PostMapping("/importAnnexAUsers")
//	public void importAnnexAUsers(@RequestParam("file") MultipartFile file) {
//
//		try {
//			List<ImsUser> imsUsers = excelTemplateParser.parseTemplateA(file.getInputStream());
//			imsUserRepository.saveAll(populateImsUsers(imsUsers, ImsUserTypeEnum.Annexure_A));
//			log.saveLog(null, imsUsers.size() + " New Annex A users were imported and saved successfully!", "USER_CREATION", LogLevelEnum.INFO);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			log.saveLog(null, "An error occurred while importing Annex A Users", "USER_CREATION", LogLevelEnum.ERROR);
//			e.printStackTrace();
//		}
//	}

	private List<ImsUser> populateImsUsers(List<ImsUser> imsUsers) {
		Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
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
				imsUser.setPassword("$2a$10$j7Pd/0bsnKt70zzTfLJtU.8OyLqLvq.Fczve.Pd5xYlmFPwsMdxa2");
				imsUser.setRole(role);
//				imsUserRepository.save(imsUser);
			} else {
				throw new DuplicateUserException("User Already Exists!");
			}
		});

		return imsUsers;
	}
	
	private UserType getUserType(ImsUserTypeEnum imsUserTypeEnum) {
		if(imsUserTypeEnum != null) {
			UserType userType = userTypeRepository.findById(imsUserTypeEnum.type).get();
			
			return userType;
		}
		
		return null;
	}

	private int getPayLevelId(String payLevelId) {
		if (payLevelId.equals("11")) {
			return 1;
		} else if (payLevelId.equals("12")) {
			return 2;
		} else if (payLevelId.equals("13")) {
			return 3;
		} else if (payLevelId.equals("13A")) {
			return 4;
		} else if (payLevelId.equals("14")) {
			return 5;
		} else if (payLevelId.equals("15")) {
			return 6;
		} else if (payLevelId.equals("16")) {
			return 7;
		} else if (payLevelId.equals("17")) {
			return 8;
		} else if (payLevelId.equals("18")) {
			return 9;
		} else if (payLevelId.equals("Others")) {
			return 10;
		}
		return 10;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, Object> handleEntityValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, Object> errors = new HashMap<>();
		Map<String, String> validationErrors = new HashMap<>();
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("ValidationMessages");
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = messageSource.getMessage(error, Locale.US);
			validationErrors.put(fieldName, errorMessage);
		});
		errors.put("message", validationErrors);
		errors.put("success", false);
		return errors;
	}

}
