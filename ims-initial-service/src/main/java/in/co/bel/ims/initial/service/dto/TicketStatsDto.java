package in.co.bel.ims.initial.service.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketStatsDto {
	private String eventName;
	private Map<String, Integer> statsData;
}
