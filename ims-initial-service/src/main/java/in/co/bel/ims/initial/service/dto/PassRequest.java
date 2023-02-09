package in.co.bel.ims.initial.service.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PassRequest {

	private int eventId;
	private int departmentId;
	private int noOfPasses;
	private int enclosureId;
	private int passCategory;
	private int passSubcategory;
	private Map<Integer, Integer> noOfPassesInEnclosure;
	private Map<Integer, Integer> noOfCarPassesInCPL;
	private List<Integer> passIds;
}
