package in.co.bel.ims.initial.infra.dto.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import in.co.bel.ims.initial.infra.dto.DelegatesUserData;

public class DelegatesUserDataValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return DelegatesUserData.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		DelegatesUserData delegatesUserData = (DelegatesUserData) target;
		if (ValidationUtil.checkLength(delegatesUserData.getEmpId(), 50
				)) {
            errors.rejectValue("empId", "empId.size");
        }
		else if (ValidationUtil.checkPattern(delegatesUserData.getEmpId(), "[a-zA-Z0-9\\s/-]*"
				)) {
            errors.rejectValue("empId", "empId.pattern");
        }
		
		if (ValidationUtil.checkLength(delegatesUserData.getCountryOrOrganization(), 200
				)) {
            errors.rejectValue("countryOrOrganization", "countryOrOrganization.size");
        }
		else if (ValidationUtil.checkPattern(delegatesUserData.getCountryOrOrganization(), "[A-za-z\\s]*"
				)) {
            errors.rejectValue("countryOrOrganization", "countryOrOrganization.pattern");
        }
		
		if (ValidationUtil.checkLength(delegatesUserData.getName(), 200
				)) {
            errors.rejectValue("name", "name.size");
        }
		else if (ValidationUtil.checkPattern(delegatesUserData.getName(), "[a-zA-Z,.\\s]*"
				)) {
            errors.rejectValue("name", "name.pattern");
        }
		
		if (ValidationUtil.checkLength(delegatesUserData.getPositionInMissionOrConsulate(), 200
				)) {
            errors.rejectValue("positionInMissionOrConsulate", "positionInMissionOrConsulate.size");
        }
		else if (ValidationUtil.checkPattern(delegatesUserData.getPositionInMissionOrConsulate(), "[a-zA-Z\\s.,&()/]*"
				)) {
            errors.rejectValue("positionInMissionOrConsulate", "positionInMissionOrConsulate.pattern");
        }
		
		if (ValidationUtil.checkBlank(delegatesUserData.getMobileNo())) {
            errors.rejectValue("mobileNo", "mobileNo.blank");
        }
		else if (ValidationUtil.checkLength(delegatesUserData.getMobileNo(), 10
				)) {
            errors.rejectValue("mobileNo", "mobileNo.size");
        }
		else if (ValidationUtil.checkPattern(delegatesUserData.getMobileNo(), "[0-9\\s]*"
				)) {
            errors.rejectValue("mobileNo", "mobileNo.pattern");
        }
		
		if (ValidationUtil.checkLength(delegatesUserData.getEmail(), 100
				)) {
            errors.rejectValue("email", "email.size");
        }
		else if (ValidationUtil.checkPattern(delegatesUserData.getEmail(), "^(?=.{1,100}@)[A-Za-z0-9_-]+([A-Za-z0-9._-]+)*@[^-][A-Za-z0-9-]+([A-Za-z0-9.-]+)*(.[A-Za-z]{2,})$"
				)) {
            errors.rejectValue("email", "email.pattern");
        }
		
		if (ValidationUtil.checkLength(delegatesUserData.getOfficialAddress(), 200
				)) {
            errors.rejectValue("officialAddress", "officialAddress.size");
        }
		else if (ValidationUtil.checkPattern(delegatesUserData.getOfficialAddress(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("officialAddress", "officialAddress.pattern");
        }
		
		if (ValidationUtil.checkLength(delegatesUserData.getRemarks(), 200
				)) {
            errors.rejectValue("remarks", "remarks.size");
        }
		else if (ValidationUtil.checkPattern(delegatesUserData.getRemarks(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("remarks", "remarks.pattern");
        }
	}
}
