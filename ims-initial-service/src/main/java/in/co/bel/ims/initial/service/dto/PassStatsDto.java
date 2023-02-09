package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassStatsDto {
	private String passDate;
	private String paymentType;
	private String mode;
	private String ticketType;
	private Long count;
}
