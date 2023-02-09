package in.co.bel.ims.initial.service.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationOfficerRequest {

	int imsUserId;
	int eventId;
	List<Integer> enclosureIds;
	Map<Integer, Integer> enclosuresMap;
	List<Integer> invOffcrIds;
}
