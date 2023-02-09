package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestPassRequest {
	private int departmentId;
	private int imsUserId;
	private int enclosureId;
	private int cplId;
	private int eventId;
	private String displayName;
	private int annexUserId;
	private int passSubcategoryId;
}
