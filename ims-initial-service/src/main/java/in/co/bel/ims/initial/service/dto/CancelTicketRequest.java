package in.co.bel.ims.initial.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelTicketRequest {
	@NotBlank
	private String controlNo;
	
	@NotBlank
	@Size(max=200, message="Remarks should not be more than 200 characters")
	@Pattern(regexp = "[a-zA-Z0-9\\s.'-@%&/,()#]*", message = "Must not contain special characters")
	private String remarks;
	
}
