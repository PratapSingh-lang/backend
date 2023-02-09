package in.co.bel.ims.initial.service.util;

public enum RoleEnum {

	ROLE_SUPERADMIN(1), ROLE_NODALOFFICER(2), ROLE_SCANNINGOFFICER(3), ROLE_INVITEE(4), ROLE_INVITATIONADMIN(5), ROLE_HIGHEROFFICER(6), ROLE_COUNTEREMP(7), ROLE_CITIZEN(8);

	public final int role;

	private RoleEnum(int role) {
		this.role = role;
	}
}
