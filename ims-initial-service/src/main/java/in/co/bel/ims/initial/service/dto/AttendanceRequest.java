package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceRequest {
	
	private int eventId;
	private String controlNo;
	private String inviteeId;
	private String qrCode;

}
