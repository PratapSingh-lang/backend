package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImsSMS {

	private String message;
	private String templateId;
	private String mobileNo;

}
