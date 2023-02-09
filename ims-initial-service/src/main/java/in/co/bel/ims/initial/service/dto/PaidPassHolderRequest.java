package in.co.bel.ims.initial.service.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaidPassHolderRequest {
	
	private LocalDateTime startDate;
	private int eventId;
	private LocalDateTime endDate;
}
