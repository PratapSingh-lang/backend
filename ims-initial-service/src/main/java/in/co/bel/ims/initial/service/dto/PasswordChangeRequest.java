package in.co.bel.ims.initial.service.dto;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {

	@Size(max=25, message="Mobile No. should not be more than 25 characters")
	private String mobileNo;
	@Size(max=200, message="New Password should not be more than 200 characters")
	private String oldPassword;
	@Size(max=200, message="Confirm Password should not be more than 200 characters")
	private String newPassword;
	private String otp;
	private String emailOtp;
	private String captcha;
}
