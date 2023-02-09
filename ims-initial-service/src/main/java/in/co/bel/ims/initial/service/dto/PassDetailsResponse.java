package in.co.bel.ims.initial.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PassDetailsResponse {

	private String eventName;
	private String controlNo;
	private String inviteeName;
	private String passType;
	private String enclosureName;
	private boolean status;
	private String errorDesc;
	private int passId;
}
