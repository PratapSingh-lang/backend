package in.co.bel.ims.initial.service.dto;

import in.co.bel.ims.initial.entity.Pass;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TicketsResponse {
	private Pass pass;
	private String txnId;
}
