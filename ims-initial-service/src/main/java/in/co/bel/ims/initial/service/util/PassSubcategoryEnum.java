package in.co.bel.ims.initial.service.util;

public enum PassSubcategoryEnum {

	VIP(1), NON_VIP(2), RED_TIP(3), BLUE_TIP(4), YELLOW_TIP(5), NO_TIP(6), GREEN_TIP(7), TYPE_I(8), TYPE_II(9),
	TYPE_III(10), INR20(11), INR50(12), INR100(13), INR500(14), GUEST_YELLOW_TIP(15), GUEST_NO_TIP(16);

	public final int type;

	private PassSubcategoryEnum(int type) {
	        this.type = type;
	    }

}
