package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaginationResponse {
	Object recordsList;
	long totalRecords;
}
