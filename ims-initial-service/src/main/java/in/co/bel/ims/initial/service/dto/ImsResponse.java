package in.co.bel.ims.initial.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImsResponse {

	private boolean success;
	private String message;
	private String errorCode;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private Object data;
}
