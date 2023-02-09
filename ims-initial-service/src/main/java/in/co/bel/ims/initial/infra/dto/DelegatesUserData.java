package in.co.bel.ims.initial.infra.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DelegatesUserData {

	private String empId;
	private int departmentId;
	private String countryOrOrganization;
	private int salutationId;
	private String name;
	private String positionInMissionOrConsulate;
	private String mobileNo;
	private String email;
	private int maritalStatusId;
	private String officialAddress;
	private String remarks;
	private Date dateOfBirth;
}
