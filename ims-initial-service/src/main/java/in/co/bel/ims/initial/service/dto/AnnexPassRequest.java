package in.co.bel.ims.initial.service.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnexPassRequest {
	
	private int departmentId;
	private int eventId;
	private List<AnnexUserRequest> annexUsers = new ArrayList<>(0);
}
