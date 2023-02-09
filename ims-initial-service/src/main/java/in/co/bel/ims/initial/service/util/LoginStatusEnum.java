package in.co.bel.ims.initial.service.util;

public enum LoginStatusEnum {
	
	FIRST_LOGIN(0), ACTIVE(1), INACTIVE(2);
	
	public final int status;
	
	private LoginStatusEnum(int status) {
		this.status = status;
	}

}
