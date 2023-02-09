package in.co.bel.ims.initial.service.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import in.co.bel.ims.initial.entity.Enclosure;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.ImsUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationOfficerResponse {
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	ImsUser imsUser;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	Event event;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	Map<Integer, Enclosure> enclosuresMap;

}
