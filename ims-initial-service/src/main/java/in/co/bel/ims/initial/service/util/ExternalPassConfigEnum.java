package in.co.bel.ims.initial.service.util;

public enum ExternalPassConfigEnum {
	PUBLIC_PASS_EVENT_NAME("eventName"),
	PUBLIC_PASS_IP_LIMIT("ipLimit"),
	PUBLIC_PASS_USER_LIMIT("userLimt");

    public final String key;

    ExternalPassConfigEnum(final String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
