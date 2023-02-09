package in.co.bel.ims.initial.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PassCategoryDto {
	private int passCategoryId;
	private long totalInvitations;
	private List<PassSubcategoryDto> passSubcategories;
}
