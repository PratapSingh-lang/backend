package in.co.bel.ims.initial.service.dto;

import in.co.bel.ims.initial.entity.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventOverview {

	private Event event;
	private long totalIssued;
	private long totalAllocated;
	private long totalAccepted;
	private long totalRejected;
	private long totalAvailable;
	
}