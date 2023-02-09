package in.co.bel.ims.initial.service.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassSubcategoryByDayLimitRequest {
	private LocalDate forDate;
	private int eventId;
	private int roleId;
}
