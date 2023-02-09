package in.co.bel.ims.initial.infra.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poiji.bind.Poiji;

import in.co.bel.ims.initial.data.repository.DepartmentRepository;
import in.co.bel.ims.initial.data.repository.EnclosureGroupRepository;
import in.co.bel.ims.initial.data.repository.IdentityProofRepository;
import in.co.bel.ims.initial.data.repository.MaritalStatusRepository;
import in.co.bel.ims.initial.data.repository.OrganizationRepository;
import in.co.bel.ims.initial.data.repository.PayLevelRepository;
import in.co.bel.ims.initial.data.repository.PrecedenceRepository;
import in.co.bel.ims.initial.data.repository.RoleRepository;
import in.co.bel.ims.initial.data.repository.SalutationRepository;
import in.co.bel.ims.initial.data.repository.UserTypeRepository;
import in.co.bel.ims.initial.entity.Department;
import in.co.bel.ims.initial.entity.ExcelUser;
import in.co.bel.ims.initial.entity.IdentityProof;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.PayLevel;
import in.co.bel.ims.initial.entity.Precedence;
import in.co.bel.ims.initial.entity.Role;
import in.co.bel.ims.initial.entity.Salutation;
import in.co.bel.ims.initial.entity.UserType;
import in.co.bel.ims.initial.service.util.ImsUserTypeEnum;
import in.co.bel.ims.initial.service.util.RoleEnum;

@Service
public class ImportExcelData {
	@Autowired
	OrganizationRepository organizationRepository;
	
	@Autowired
	DepartmentRepository departmentRepository;
	
	@Autowired
	SalutationRepository salutationRepository;
	
	@Autowired
	PayLevelRepository payLevelRepository;
	
	@Autowired
	MaritalStatusRepository maritalStatusRepository;
	@Autowired
	PrecedenceRepository precedenceRepository;
	@Autowired
	EnclosureGroupRepository enclosureGroupRepository;
	@Autowired
	UserTypeRepository userTypeRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	IdentityProofRepository identityProofRepository;
	
	// Appendix A - Ministry Users excel template parsing.
	public List<ImsUser> importTemplateA(InputStream fileStream) throws IOException {
		try {
			File file = new File("src/main/resources/templates/Template F- MP.xlsx");			
			FileUtils.copyInputStreamToFile(fileStream, file);
			List<ExcelUser> excelUsers = Poiji.fromExcel(file, ExcelUser.class);
			List<ImsUser> imsUserList = new ArrayList<>();
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_A.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			excelUsers.forEach(excelUser -> {
					ImsUser imsUser = new ImsUser();
					imsUser.setUserType(userType);
					imsUser.setRole(role);
					Department department = departmentRepository.findByName(excelUser.getDepartment());
					imsUser.setDepartment(department);
					imsUser.setEmpNo(excelUser.getEmpId());
					Salutation salutation = salutationRepository.findByName(excelUser.getSalutation());
					imsUser.setSalutation(salutation);
					imsUser.setName(excelUser.getName());
					imsUser.setDesignation(excelUser.getDesignation());
					imsUser.setEmail(excelUser.getEmail());
					PayLevel payLevel = payLevelRepository.findByName(excelUser.getPayLevel());
					imsUser.setPayLevel(payLevel);
					imsUser.setBasicPay(excelUser.getBasicPay());
					imsUser.setMobileNo(excelUser.getMobileNo());
					imsUser.setOfficeAddress(excelUser.getOfficeAddress());
					imsUser.setResidentialAddress(excelUser.getResidentialAddress());
					imsUser.setMaritalStatus(maritalStatusRepository.findByStatus(excelUser.getMaritalStatus()));
					imsUser.setEnclosureGroup(enclosureGroupRepository.findById(4).get());
					imsUser.setRemarks(excelUser.getRemarks());
					imsUserList.add(imsUser);
			});
			return imsUserList;
		}catch (IOException e) {
		    // TODO throw or handle the exception however you need to
		}
		return null;
	}
	
	
	// Appendix B - Autonomous/PSU Users excel template parsing.
	public List<ImsUser> importTemplateB(InputStream fileStream) throws IOException {
		try {
			File file = new File("src/main/resources/templates/Template F- MP.xlsx");			
			FileUtils.copyInputStreamToFile(fileStream, file);
			List<ExcelUser> excelUsers = Poiji.fromExcel(file, ExcelUser.class);
			List<ImsUser> imsUserList = new ArrayList<>();
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			excelUsers.forEach(excelUser -> {
					ImsUser imsUser = new ImsUser();
					imsUser.setUserType(userType);
					imsUser.setRole(role);
					Department department = departmentRepository.findByName(excelUser.getDepartment());
					imsUser.setDepartment(department);
					imsUser.setEmpNo(excelUser.getEmpId());
					Salutation salutation = salutationRepository.findByName(excelUser.getSalutation());
					imsUser.setSalutation(salutation);
					imsUser.setName(excelUser.getName());
					imsUser.setDesignation(excelUser.getDesignation());
					imsUser.setEquivalentStatus(excelUser.getEquivalentStatus());
					imsUser.setEmail(excelUser.getEmail());
					PayLevel payLevel = payLevelRepository.findByName(excelUser.getPayLevel());
					imsUser.setPayLevel(payLevel);
					imsUser.setBasicPay(excelUser.getBasicPay());
					imsUser.setMobileNo(excelUser.getMobileNo());
					imsUser.setOfficeAddress(excelUser.getOfficeAddress());
					imsUser.setResidentialAddress(excelUser.getResidentialAddress());
					imsUser.setMaritalStatus(maritalStatusRepository.findByStatus(excelUser.getMaritalStatus()));
					imsUser.setEnclosureGroup(enclosureGroupRepository.findById(4).get());
					imsUser.setRemarks(excelUser.getRemarks());
					imsUserList.add(imsUser);
			});
			return imsUserList;
		}catch (IOException e) {
		    // TODO throw or handle the exception however you need to
		}
		return null;
	}
	
	// Appendix C - Commission/Committees Users excel template parsing.
	public List<ImsUser> importTemplateC(InputStream fileStream) throws IOException {
		try {
			File file = new File("src/main/resources/templates/Template F- MP.xlsx");			
			FileUtils.copyInputStreamToFile(fileStream, file);
			List<ExcelUser> excelUsers = Poiji.fromExcel(file, ExcelUser.class);
			List<ImsUser> imsUserList = new ArrayList<>();
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			excelUsers.forEach(excelUser -> {
					ImsUser imsUser = new ImsUser();
					imsUser.setUserType(userType);
					imsUser.setRole(role);
					Department department = departmentRepository.findByName(excelUser.getDepartment());
					imsUser.setDepartment(department);
					imsUser.setEmpNo(excelUser.getEmpId());
					Salutation salutation = salutationRepository.findByName(excelUser.getSalutation());
					imsUser.setSalutation(salutation);
					imsUser.setName(excelUser.getName());
					imsUser.setDesignation(excelUser.getDesignation());
					imsUser.setPrecedence(new Precedence(Integer.valueOf(excelUser.getPrecedence())));
					imsUser.setEquivalentStatus(excelUser.getEquivalentStatus());
					imsUser.setEmail(excelUser.getEmail());
					PayLevel payLevel = payLevelRepository.findByName(excelUser.getPayLevel());
					imsUser.setPayLevel(payLevel);
					imsUser.setBasicPay(excelUser.getBasicPay());
					imsUser.setMobileNo(excelUser.getMobileNo());
					imsUser.setOfficeAddress(excelUser.getOfficeAddress());
					imsUser.setResidentialAddress(excelUser.getResidentialAddress());
					imsUser.setMaritalStatus(maritalStatusRepository.findByStatus(excelUser.getMaritalStatus()));
					imsUser.setEnclosureGroup(enclosureGroupRepository.findById(4).get());
					imsUser.setRemarks(excelUser.getRemarks());
					imsUserList.add(imsUser);
			});
			return imsUserList;
		}catch (IOException e) {
		    // TODO throw or handle the exception however you need to
		}
		return null;
	}
	

	// Appendix D - Guest Users excel template parsing.
	public List<ImsUser> importTemplateD(InputStream fileStream) throws IOException {
		try {
			File file = new File("src/main/resources/templates/Template F- MP.xlsx");			
			FileUtils.copyInputStreamToFile(fileStream, file);
			List<ExcelUser> excelUsers = Poiji.fromExcel(file, ExcelUser.class);
			List<ImsUser> imsUserList = new ArrayList<>();
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			excelUsers.forEach(excelUser -> {
					ImsUser imsUser = new ImsUser();
					imsUser.setUserType(userType);
					imsUser.setRole(role);
					Department department = departmentRepository.findByName(excelUser.getDepartment());
					imsUser.setDepartment(department);
					Salutation salutation = salutationRepository.findByName(excelUser.getSalutation());
					imsUser.setSalutation(salutation);
					imsUser.setName(excelUser.getName());
					imsUser.setEmail(excelUser.getEmail());
					imsUser.setMobileNo(excelUser.getMobileNo());
					imsUser.setNationality(excelUser.getNationality());
					imsUser.setIdProofNo(excelUser.getIdProofNo());
					IdentityProof identityProof = identityProofRepository.findByName(excelUser.getIdentityProof());
					imsUser.setIdentityProof(identityProof);
					imsUser.setResidentialAddress(excelUser.getResidentialAddress());
					imsUser.setRecommendingOfficerDesignation(excelUser.getRecommendingOfficerDesignation());
					imsUser.setRecommendingOfficerName(excelUser.getRecommendingOfficerName());
					imsUser.setRemarks(excelUser.getRemarks());
					imsUserList.add(imsUser);
			});
			return imsUserList;
		}catch (IOException e) {
		    // TODO throw or handle the exception however you need to
		}
		return null;
	}
	
	// Appendix E - Delegates/Consulate Users excel template parsing.
	public List<ImsUser> importTemplateE(InputStream fileStream) throws IOException {
		try {
			File file = new File("src/main/resources/templates/Template F- MP.xlsx");			
			FileUtils.copyInputStreamToFile(fileStream, file);
			List<ExcelUser> excelUsers = Poiji.fromExcel(file, ExcelUser.class);
			List<ImsUser> imsUserList = new ArrayList<>();
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			excelUsers.forEach(excelUser -> {
					ImsUser imsUser = new ImsUser();
					imsUser.setUserType(userType);
					imsUser.setRole(role);
					Department department = departmentRepository.findByName(excelUser.getDepartment());
					imsUser.setDepartment(department);
					imsUser.setEmpNo(excelUser.getEmpId());
					imsUser.setNationality(excelUser.getNationality());
					Salutation salutation = salutationRepository.findByName(excelUser.getSalutation());
					imsUser.setSalutation(salutation);
					imsUser.setName(excelUser.getName());
					imsUser.setPositionMissionConsulate(excelUser.getPositionMissionConsulate());
					imsUser.setEmail(excelUser.getEmail());
					imsUser.setMobileNo(excelUser.getMobileNo());
					imsUser.setOfficeAddress(excelUser.getOfficeAddress());
					imsUser.setMaritalStatus(maritalStatusRepository.findByStatus(excelUser.getMaritalStatus()));
					imsUser.setRemarks(excelUser.getRemarks());
					imsUserList.add(imsUser);
			});
			return imsUserList;
		}catch (IOException e) {
		    // TODO throw or handle the exception however you need to
		}
		return null;
	}
	
	// Appendix F - MLA/MP Users excel template parsing.
	public List<ImsUser> importTemplateF(InputStream fileStream) throws IOException {
		try {
			File file = new File("src/main/resources/templates/Template F- MP.xlsx");			
			FileUtils.copyInputStreamToFile(fileStream, file);
			List<ExcelUser> excelUsers = Poiji.fromExcel(file, ExcelUser.class);
			List<ImsUser> imsUserList = new ArrayList<>();
			UserType userType = userTypeRepository.findById(ImsUserTypeEnum.Annexure_B.type).get();
			Role role = roleRepository.findById(RoleEnum.ROLE_INVITEE.role).get();
			excelUsers.forEach(excelUser -> {
					ImsUser imsUser = new ImsUser();
					imsUser.setUserType(userType);
					imsUser.setRole(role);
					Department department = departmentRepository.findByName(excelUser.getDepartment());
					imsUser.setDepartment(department);
					imsUser.setConstituency(excelUser.getConstituency());
					imsUser.setEmpNo(excelUser.getEmpId());
					Salutation salutation = salutationRepository.findByName(excelUser.getSalutation());
					imsUser.setSalutation(salutation);
					imsUser.setName(excelUser.getName());
					imsUser.setEmail(excelUser.getEmail());
					imsUser.setMobileNo(excelUser.getMobileNo());
					imsUser.setOfficeAddress(excelUser.getOfficeAddress());
					imsUser.setMaritalStatus(maritalStatusRepository.findByStatus(excelUser.getMaritalStatus()));
					imsUser.setRemarks(excelUser.getRemarks());
					imsUserList.add(imsUser);
			});
			return imsUserList;
		}catch (IOException e) {
		    // TODO throw or handle the exception however you need to
		}
		return null;
	}
	
	
}
