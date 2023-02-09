package in.co.bel.ims.initial.service.dto;

import in.co.bel.ims.initial.entity.Enclosure;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnclosureAvailability {

	private Enclosure enclosure;
	private long totalIssued;
	private long totalAllocated;
	private long totalAccepted;
	private long totalRejected;
	private long totalAvailable;
	
}