package in.co.bel.ims.initial.service.util;

public enum MaritalStatusEnum {

	Single(1), Married(2);
	
	public final int value;

	private MaritalStatusEnum(int value) {
		this.value = value;
	}
}
