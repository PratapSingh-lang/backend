package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserValidationRequest {
	private String mobileNo;
	private boolean isDuplicate;
}
