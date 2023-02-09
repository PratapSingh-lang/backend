package in.co.bel.ims.initial.service.dto;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PgTxnVerifiedResponse {
	private Integer status;
	private String msg;
	private HashMap<String, PgTransactionsResponse> transaction_details;
	private String request_id;
	private String bank_ref_num;
}
