package in.co.bel.ims.initial.service.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PassDayLimitRequest {
	
	int event;
	LocalDate startDate;
	LocalDate endDate;

}
