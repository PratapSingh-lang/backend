package in.co.bel.ims.initial.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentPayloadRequest {
	private int imsUserId;
	private int passCategoryId;
	private List<Integer> passSubcategoryId;
	private double amount;
}
