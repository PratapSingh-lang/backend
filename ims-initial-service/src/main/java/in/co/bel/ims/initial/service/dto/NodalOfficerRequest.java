package in.co.bel.ims.initial.service.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodalOfficerRequest {

	int imsUserId;
	int eventId;
	List<Integer> departmentIds;
	Map<Integer, Integer> departmentMap;
	List<Integer> nodalOfficerIds;
}
