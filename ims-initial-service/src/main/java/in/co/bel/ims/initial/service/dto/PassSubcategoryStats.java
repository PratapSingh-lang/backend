package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassSubcategoryStats {

	private int passCategoryId;
	private String passCategoryName;
	private int passSubcategoryId;
	private String passSubcategoryName;
	private long totalIssued;
	private long totalAllocated;
	private long totalAccepted;
	private long totalRejected;
	private long totalAvailable;
	private long totalAttend;
}
