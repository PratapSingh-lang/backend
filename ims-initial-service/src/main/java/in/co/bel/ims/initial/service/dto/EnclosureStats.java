package in.co.bel.ims.initial.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnclosureStats {

	private int enclosureId;
	private String name;
	private long enclosureCapacity;
	private int enclosureGroupId;
	private String enclosureGroupName;
	private long totalIssued;
	private long totalAllocated;
	private long totalAccepted;
	private long totalRejected;
	private long totalAvailable;
	private long totalAttend;
	private List<PassSubcategoryStats> passSubcategoryStats;
}