package in.co.bel.ims.initial.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PassStatsRequest {
	private int eventId;
	private String mode;
	private List<Integer> passSubcategoryIds;
	private List<Integer> userRoleIds;
	private String fromDate;
	private String toDate;
}
