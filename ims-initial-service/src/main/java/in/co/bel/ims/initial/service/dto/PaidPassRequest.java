package in.co.bel.ims.initial.service.dto;

import java.util.List;

import javax.validation.Valid;

import in.co.bel.ims.initial.entity.PaidPassHolders;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaidPassRequest {
	@Valid
    private List<@Valid PaidPassHolders> paidPassHoldersList;
	private String mihpayid;
	private String mode;
	private String status;
	private String unmappedstatus;
	private String key;
	private String txnid;
	private String amount;
	private String cardCategory;
	private String discount;
	private String net_amount_debit;
	private String addedon;
	private String productinfo;
	private String firstname;
	private String lastname;
	private String address1;
	private String address2;
	private String city ;
	private String state ;
	private String country;
	private String zipcode ;
	private String email;
	private String phone;
	private String hash;
	private String payment_source;
	private String PG_TYPE;
	private String bank_ref_num;
	private String bankcode;
	private String error;
	private String error_Message;
	private String name_on_card;
	private String cardnum;
	private String cardhash;
	private boolean carPassRequired;
}
