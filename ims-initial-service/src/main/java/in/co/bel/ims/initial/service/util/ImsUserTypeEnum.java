package in.co.bel.ims.initial.service.util;

public enum ImsUserTypeEnum {

	WoPUser(1), Annexure_A(2), Annexure_B(3), Annexure_C(4), Annexure_D(5), Annexure_E(6), Annexure_F(7), PublicUser(8), Organizer(9);

	public final int type;

	private ImsUserTypeEnum(int type) {
		this.type = type;
	}

}
