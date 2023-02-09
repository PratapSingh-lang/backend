package in.co.bel.ims.initial.service.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import in.co.bel.ims.initial.entity.Department;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.ImsUser;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NodalOfficerResponse {

	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private ImsUser imsUser;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private Event event;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	private Map<Integer, Department> nodalDepartmentMap;
}
