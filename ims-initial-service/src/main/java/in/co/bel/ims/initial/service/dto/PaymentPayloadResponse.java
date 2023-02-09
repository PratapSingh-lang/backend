package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentPayloadResponse {
	private String key;
	private String txnid;
	private String productinfo;
	private double amount;
	private String email;
	private String firstname;
	private String surl;
	private String furl;
	private String phone;
	private String hash;
	private String redirectFormUrl;
}
