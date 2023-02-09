package in.co.bel.ims.initial.service.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import in.co.bel.ims.initial.entity.Enclosure;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.PassSubcategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnclosurePassTypeMappingResponse {
	
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	Enclosure enclosure;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	Map<Integer, Event> eventMap;
//	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
//	Map<Integer, PassCategory> passCategoryMap;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	Map<Integer, PassSubcategory> passSubcategoryMap;

}