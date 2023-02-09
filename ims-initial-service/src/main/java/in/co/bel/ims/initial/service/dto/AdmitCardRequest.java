package in.co.bel.ims.initial.service.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdmitCardRequest {

	int eventId;
	private Map<Integer, Integer> noOfAdmitCardsToEnclosure;
	int passCategory;
	int passSubcategory;
}
