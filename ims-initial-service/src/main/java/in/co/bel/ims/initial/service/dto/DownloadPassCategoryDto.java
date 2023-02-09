package in.co.bel.ims.initial.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DownloadPassCategoryDto {
	private int passCategoryId;
	private long totalDownloads;
	private List<DownloadPassSubcategoryDto> passSubcategories;
}
