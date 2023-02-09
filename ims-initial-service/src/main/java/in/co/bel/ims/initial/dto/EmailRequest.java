package in.co.bel.ims.initial.dto;

import java.util.List;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
	@Size(max=200, message="Name should not be more than 200 characters")
	@Pattern(regexp = "[a-zA-Z0-9\\s_@.#+\\-,]*", message = "Must not contain special characters")
	private String eventName;
	
	@Size(min=1)
	private List<@Pattern(regexp = "[a-zA-Z0-9\\s_@.#+\\-,]*", message = "Must not contain special characters") String> recipients;
}