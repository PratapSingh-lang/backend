package in.co.bel.ims.initial.service.dto;

import java.util.Date;

import in.co.bel.ims.initial.entity.Captcha;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PublicUserRequest {

	private String name;
	private String mobileNo;
	private Date dob;
	private String address;
	private String fatherOrHusbandName;
	private Captcha captcha;
	private String otp;
}
