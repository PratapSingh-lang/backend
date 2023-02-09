package in.co.bel.ims.initial.service.util;

public enum LogLevelEnum {

	DEBUG(1), INFO(2), WARN(3), ERROR(4), FATAL(5), OFF(6), TRACE(7);

	public final int type;

	private LogLevelEnum(int type) {
		this.type = type;
	}

}