package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarPassEligibilityRequest {
	private int imsUserId;
	private int eventId;
	private int enclosureGroupId;
}
