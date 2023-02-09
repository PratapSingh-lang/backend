package in.co.bel.ims.initial.service.dto;


import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketSummaryRequest {
	private LocalDate fromDate;
	private LocalDate toDate;
	private int eventId;
	private int enclosureGroupId;
	private int roleId;
	private int passSubcategoryId;
	
}
