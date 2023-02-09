package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsherResponse {
	
	private int id; 
	private String name; 
	private String mobileNo; 
	private int eventId; 
	private String eventName;
	private int enclosureId;
	private String enclosureName;

}
