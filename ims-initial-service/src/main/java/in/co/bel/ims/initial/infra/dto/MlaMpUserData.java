package in.co.bel.ims.initial.infra.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MlaMpUserData {
	
	private String empId;
	private String constituencyOrState;
	private int departmentId;
	private int salutationId;
	private String name;
	private String mobileNo;
	private String email;
	private int maritalStatusId;
	private String address;
	private String remarks;
	private Date dateOfBirth;
}
