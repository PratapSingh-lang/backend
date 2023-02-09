package in.co.bel.ims.initial.service.dto;

import javax.validation.constraints.Size;

import in.co.bel.ims.initial.entity.Captcha;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequest {

	@Size(max=25, message="Mobile No. should not be more than 25 characters")
	private String mobileNo;
	@Size(max=100, message="Email should not be more than 100 characters")
	private String email;
	private String password_hash;
	@Size(max=200, message="Password should not be more than 200 characters")
	private String password;
	private Captcha captcha;
	private String clientAddress;
}
