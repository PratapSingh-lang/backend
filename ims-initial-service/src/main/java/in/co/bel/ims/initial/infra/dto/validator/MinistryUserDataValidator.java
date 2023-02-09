package in.co.bel.ims.initial.infra.dto.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import in.co.bel.ims.initial.infra.dto.MinistryUserData;

public class MinistryUserDataValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return MinistryUserData.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		MinistryUserData ministryUserData = (MinistryUserData) target;
		if (ValidationUtil.checkLength(ministryUserData.getEmpId(), 50
				)) {
            errors.rejectValue("empId", "empId.size");
        }
		else if (ValidationUtil.checkPattern(ministryUserData.getEmpId(), "[a-zA-Z0-9\\s/-]*"
				)) {
            errors.rejectValue("empId", "empId.pattern");
        }
		
		if (ValidationUtil.checkLength(ministryUserData.getName(), 200
				)) {
            errors.rejectValue("name", "name.size");
        }
		else if (ValidationUtil.checkPattern(ministryUserData.getName(), "[a-zA-Z,.\\s]*"
				)) {
            errors.rejectValue("name", "name.pattern");
        }
		
		if (ValidationUtil.checkLength(ministryUserData.getDesignation(), 200
				)) {
            errors.rejectValue("designation", "designation.size");
        }
		else if (ValidationUtil.checkPattern(ministryUserData.getDesignation(), "[a-zA-Z0-9\\s.,()/&]*"
				)) {
            errors.rejectValue("designation", "designation.pattern");
        }
		
		if (ValidationUtil.checkLength(ministryUserData.getBasicPay(), 8
				)) {
            errors.rejectValue("basicPay", "basicPay.size");
        }
		else if (ValidationUtil.checkPattern(ministryUserData.getBasicPay(), "[0-9\\s]*"
				)) {
            errors.rejectValue("basicPay", "basicPay.pattern");
        }
		
		if (ValidationUtil.checkBlank(ministryUserData.getMobileNo())) {
            errors.rejectValue("mobileNo", "mobileNo.blank");
        }
		else if (ValidationUtil.checkLength(ministryUserData.getMobileNo(), 10
				)) {
            errors.rejectValue("mobileNo", "mobileNo.size");
        }
		else if (ValidationUtil.checkPattern(ministryUserData.getMobileNo(), "[0-9\\s]*"
				)) {
            errors.rejectValue("mobileNo", "mobileNo.pattern");
        }
		
		if (ValidationUtil.checkLength(ministryUserData.getEmail(), 100
				)) {
            errors.rejectValue("email", "email.size");
        }
		else if (ValidationUtil.checkPattern(ministryUserData.getEmail(), "^(?=.{1,100}@)[A-Za-z0-9_-]+([A-Za-z0-9._-]+)*@[^-][A-Za-z0-9-]+([A-Za-z0-9.-]+)*(.[A-Za-z]{2,})$"
				)) {
            errors.rejectValue("email", "email.pattern");
        }
		
		if (ValidationUtil.checkLength(ministryUserData.getOfficialAddress(), 200
				)) {
            errors.rejectValue("officialAddress", "officialAddress.size");
        }
		else if (ValidationUtil.checkPattern(ministryUserData.getOfficialAddress(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("officialAddress", "officialAddress.pattern");
        }
		
		if (ValidationUtil.checkLength(ministryUserData.getResidentialAddress(), 200
				)) {
            errors.rejectValue("residentialAddress", "residentialAddress.size");
        }
		else if (ValidationUtil.checkPattern(ministryUserData.getResidentialAddress(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("residentialAddress", "residentialAddress.pattern");
        }
		
		if (ValidationUtil.checkLength(ministryUserData.getRemarks(), 200
				)) {
            errors.rejectValue("remarks", "remarks.size");
        }
		else if (ValidationUtil.checkPattern(ministryUserData.getRemarks(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("remarks", "remarks.pattern");
        }
	}
}
