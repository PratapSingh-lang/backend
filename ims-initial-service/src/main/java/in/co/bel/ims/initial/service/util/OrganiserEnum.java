package in.co.bel.ims.initial.service.util;

public enum OrganiserEnum {

	YES('Y'), NO('N');
	
	public final char status;

	private OrganiserEnum(char status) {
		this.status = status;
	}
	
}
