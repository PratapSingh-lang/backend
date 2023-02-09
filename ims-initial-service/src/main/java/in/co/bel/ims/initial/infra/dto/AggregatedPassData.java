package in.co.bel.ims.initial.infra.dto;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AggregatedPassData {
	private int Id;
	private String passHolderName;
	private String controlNo;
	private String controlHash;
	private Date dob;
	private int eventId;
	private String eventName;
	private int enclosureId;
	private String enclosureName;
	private int passStatusId;
	private String passStatusName;
	private int passCategoryId;
	private String passCategoryName;
	private int passSubCategoryId;
	private String passSubCategoryName;
	private LocalDateTime modifiedTimestamp;
	private String remarks;
	private int salutationId;
	private String salutationName;
	private int maritalStatusId;
	private String maritalStatusName;
	private boolean synced;
}
