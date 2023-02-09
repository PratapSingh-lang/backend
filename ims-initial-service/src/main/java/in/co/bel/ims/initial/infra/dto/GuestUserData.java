package in.co.bel.ims.initial.infra.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestUserData {

	private int salutationId;
	private int departmentId;
	private String name;
	private String mobileNo;
	private String email;
	private String nationality;
	private String residentialAddress;
	private String govtIdNumber;
	private Date dateOfBirth;
	private String remarks;
	private String recommendingOfficerName;
	private String recommendingOfficerDesignation;
}
