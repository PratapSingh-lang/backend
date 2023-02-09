package in.co.bel.ims.initial.service.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import in.co.bel.ims.initial.entity.Department;
import in.co.bel.ims.initial.entity.EnclosureGroup;
import in.co.bel.ims.initial.entity.GuestType;
import in.co.bel.ims.initial.entity.IdentityProof;
import in.co.bel.ims.initial.entity.MaritalStatus;
import in.co.bel.ims.initial.entity.PayLevel;
import in.co.bel.ims.initial.entity.Precedence;
import in.co.bel.ims.initial.entity.Role;
import in.co.bel.ims.initial.entity.Salutation;
import in.co.bel.ims.initial.entity.UserType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserData  {

	private int id;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private Department department;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private EnclosureGroup enclosureGroup;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private GuestType guestType;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private IdentityProof identityProof;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private MaritalStatus maritalStatus;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private PayLevel payLevel;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private Precedence precedence;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private Role role;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private Salutation salutation;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private UserType userType;
	private String name;
	private String empNo;
	private String mobileNo;
	private String email;
	private String designation;
	private String office;
	private String constituency;
	private String remarks;
	private String equivalentStatus;
	private String basicPay;
	private String nationality;
	private String idProofNo;
	private String positionMissionConsulate;
	private String officeAddress;
	private String residentialAddress;
	private Date doj;
	private String recommendingOfficerName;
	private String recommendingOfficerDesignation;
	private Date dateOfBirth;
}
