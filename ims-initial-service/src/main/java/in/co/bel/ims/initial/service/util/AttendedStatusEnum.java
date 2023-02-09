package in.co.bel.ims.initial.service.util;

public enum AttendedStatusEnum {

	ATTENDED(1), NOT_ATTENDED(2);
	
	public final int status;
	
	private AttendedStatusEnum(int status) {
        this.status = status;
    }
}
