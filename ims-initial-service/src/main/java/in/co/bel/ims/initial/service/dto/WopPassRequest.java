package in.co.bel.ims.initial.service.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WopPassRequest {
	private int eventId;
	private Map<Integer, Integer> precendenceToEnclosure;
}
