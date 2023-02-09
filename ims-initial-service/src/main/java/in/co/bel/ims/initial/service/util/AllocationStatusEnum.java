package in.co.bel.ims.initial.service.util;

public enum AllocationStatusEnum {

	INITIATED(1), ALLOCATED(2), ACCEPTED(3), REJECTED(4);

	public final int status;

	private AllocationStatusEnum(int status) {
		this.status = status;
	}
}
