package in.co.bel.ims.initial.infra.dto.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import in.co.bel.ims.initial.infra.dto.AutonomousUserData;

public class AutonomousUserDataValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return AutonomousUserData.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AutonomousUserData autonomousUserData = (AutonomousUserData) target;
		if (ValidationUtil.checkLength(autonomousUserData.getEmpId(), 50
				)) {
            errors.rejectValue("empId", "empId.size");
        }
		else if (ValidationUtil.checkPattern(autonomousUserData.getEmpId(), "[a-zA-Z0-9\\s/-]*"
				)) {
            errors.rejectValue("empId", "empId.pattern");
        }
		
		if (ValidationUtil.checkLength(autonomousUserData.getName(), 200
				)) {
            errors.rejectValue("name", "name.size");
        }
		else if (ValidationUtil.checkPattern(autonomousUserData.getName(), "[a-zA-Z,.\\s]*"
				)) {
            errors.rejectValue("name", "name.pattern");
        }
		
		if (ValidationUtil.checkLength(autonomousUserData.getDesignation(), 200
				)) {
            errors.rejectValue("designation", "designation.size");
        }
		else if (ValidationUtil.checkPattern(autonomousUserData.getDesignation(), "[a-zA-Z0-9\\s.,()/&]*"
				)) {
            errors.rejectValue("designation", "designation.pattern");
        }
		
		if (ValidationUtil.checkLength(autonomousUserData.getBasicPay(), 8
				)) {
            errors.rejectValue("basicPay", "basicPay.size");
        }
		else if (ValidationUtil.checkPattern(autonomousUserData.getBasicPay(), "[0-9\\s]*"
				)) {
            errors.rejectValue("basicPay", "basicPay.pattern");
        }
		
		if (ValidationUtil.checkBlank(autonomousUserData.getMobileNo())) {
            errors.rejectValue("mobileNo", "mobileNo.blank");
        }
		else if (ValidationUtil.checkLength(autonomousUserData.getMobileNo(), 10
				)) {
            errors.rejectValue("mobileNo", "mobileNo.size");
        }
		else if (ValidationUtil.checkPattern(autonomousUserData.getMobileNo(), "[0-9\\s]*"
				)) {
            errors.rejectValue("mobileNo", "mobileNo.pattern");
        }
		
		if (ValidationUtil.checkLength(autonomousUserData.getEmail(), 100
				)) {
            errors.rejectValue("email", "email.size");
        }
		else if (ValidationUtil.checkPattern(autonomousUserData.getEmail(), "^(?=.{1,100}@)[A-Za-z0-9_-]+([A-Za-z0-9._-]+)*@[^-][A-Za-z0-9-]+([A-Za-z0-9.-]+)*(.[A-Za-z]{2,})$"
				)) {
            errors.rejectValue("email", "email.pattern");
        }
		
		if (ValidationUtil.checkLength(autonomousUserData.getOfficialAddress(), 200
				)) {
            errors.rejectValue("officialAddress", "officialAddress.size");
        }
		else if (ValidationUtil.checkPattern(autonomousUserData.getOfficialAddress(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("officialAddress", "officialAddress.pattern");
        }
		
		if (ValidationUtil.checkLength(autonomousUserData.getResidentialAddress(), 200
				)) {
            errors.rejectValue("residentialAddress", "residentialAddress.size");
        }
		else if (ValidationUtil.checkPattern(autonomousUserData.getResidentialAddress(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("residentialAddress", "residentialAddress.pattern");
        }
		
		if (ValidationUtil.checkLength(autonomousUserData.getRemarks(), 200
				)) {
            errors.rejectValue("remarks", "remarks.size");
        }
		else if (ValidationUtil.checkPattern(autonomousUserData.getRemarks(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("remarks", "remarks.pattern");
        }
		
		if (ValidationUtil.checkLength(autonomousUserData.getEquivalentStatus(), 200
				)) {
            errors.rejectValue("equivalentStatus", "equivalentStatus.size");
        }
		else if (ValidationUtil.checkPattern(autonomousUserData.getEquivalentStatus(), "[a-zA-Z0-9\\s.&()]*"
				)) {
            errors.rejectValue("equivalentStatus", "equivalentStatus.pattern");
        }
	}
}
