package in.co.bel.ims.initial.service.dto;

import in.co.bel.ims.initial.entity.EnclosureGroup;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CplAvailability {

	private int cplId;
	private String cplName;
    private EnclosureGroup enclosureGroup;
	private Integer maxCapacity;
	private long totalIssued;
	private long totalAllocated;
	private long totalAccepted;
	private long totalRejected;
	private long totalAvailable;
	
}