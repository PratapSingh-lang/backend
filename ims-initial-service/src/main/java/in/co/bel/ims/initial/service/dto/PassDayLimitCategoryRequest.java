package in.co.bel.ims.initial.service.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import in.co.bel.ims.initial.entity.PassDayLimitCategory;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PassDayLimitCategoryRequest {
	@NotNull
	@Valid
	PassDayLimitCategory passDayLimitCategory;
	@NotNull
	@Valid
	PassDayLimitRequest passDayLimit;

}
