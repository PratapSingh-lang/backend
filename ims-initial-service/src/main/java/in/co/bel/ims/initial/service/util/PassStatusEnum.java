package in.co.bel.ims.initial.service.util;

public enum PassStatusEnum {

	INITIATED(1), ALLOCATED(2), RSVP_ACCEPTED(3), RSVP_REGRETTED(4), OUT(5), IN(6), ATTENDED(7), PENDING_APPROVAL(8), DENIED(9), CANCELLED(10);

	public final int type;

	private PassStatusEnum(int type) {
		this.type = type;
	}

}
