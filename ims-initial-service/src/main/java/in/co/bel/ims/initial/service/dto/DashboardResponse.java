package in.co.bel.ims.initial.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {

	private int eventId;
	private long totalPasses;
	private long totalIssued;
	private long totalRejected;
	private long totalAllocated;
	private long totalAccepted;
	private long totalAvailable;
	private long totalAttended;
	private long totalDownloads;
	private long totalCapacity;
	private long totalCancelled;
	private long totalUsersUploaded;
	private List<PassCategoryDto> categoryWiseStatistics;
	
}
