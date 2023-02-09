package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
	private String username;
	private String password;
	private String emailOtp;
}
