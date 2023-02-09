package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnexUserRequest {
	private int id;
	private int enclosureId;
	private int cplId;
	private int passSubcategoryId;
}
