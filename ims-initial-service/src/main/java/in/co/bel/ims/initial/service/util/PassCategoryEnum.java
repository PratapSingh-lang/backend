package in.co.bel.ims.initial.service.util;

public enum PassCategoryEnum {

	ADMITCARD(1), INVITATION(2), CPL(3), PAIDTICKET(4), GUESTPASS(5);

	public final int type;

	private PassCategoryEnum(int type) {
		this.type = type;
	}

}
