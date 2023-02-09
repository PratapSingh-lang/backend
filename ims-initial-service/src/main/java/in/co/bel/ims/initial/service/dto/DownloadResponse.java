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
public class DownloadResponse {

	private int eventId;
	private long totalPasses;
	private long totalIssued;
	private long totalRejected;
	private long totalAccepted;
	private long totalAvailable;
	private long totalAttended;
	private long totalDownloads;
	private List<DownloadPassCategoryDto> downloadStatistics;
	
}
