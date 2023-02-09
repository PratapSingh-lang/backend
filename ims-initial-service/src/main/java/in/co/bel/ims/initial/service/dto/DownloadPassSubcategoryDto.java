package in.co.bel.ims.initial.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DownloadPassSubcategoryDto {
	private int passSubCategoryId;
	private String name;
	private long totalDownloads;
}
