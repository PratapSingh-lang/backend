package in.co.bel.ims.initial.infra.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AutonomousUserData {

	private String empId;
	private int salutationId;
	private int departmentId;
	private String name;
	private String designation;
	private int payLevelId;
	private String basicPay;
	private String mobileNo;
	private String email;
	private int maritalStatusId;
	private String officialAddress;
	private String residentialAddress;
	private int enclosureGroupId;
	private String remarks;
	private String equivalentStatus;
	private Date dateOfBirth;
}
