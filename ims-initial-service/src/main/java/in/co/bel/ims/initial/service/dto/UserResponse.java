package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponse {
	
	int id;
	String name;
	String mobileNo;
	String email;
	int role;
	boolean isLocked;
	String validationMsg;
	int ministryId;
	int status;
}
