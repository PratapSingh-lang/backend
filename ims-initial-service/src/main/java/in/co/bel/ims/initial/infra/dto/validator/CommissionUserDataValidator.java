package in.co.bel.ims.initial.infra.dto.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import in.co.bel.ims.initial.infra.dto.CommissionUserData;

public class CommissionUserDataValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return CommissionUserData.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CommissionUserData commissionUserData = (CommissionUserData) target;
		if (ValidationUtil.checkLength(commissionUserData.getEmpId(), 50
				)) {
            errors.rejectValue("empId", "empId.size");
        }
		else if (ValidationUtil.checkPattern(commissionUserData.getEmpId(), "[a-zA-Z0-9\\s/-]*"
				)) {
            errors.rejectValue("empId", "empId.pattern");
        }
		
		if (ValidationUtil.checkLength(commissionUserData.getName(), 200
				)) {
            errors.rejectValue("name", "name.size");
        }
		else if (ValidationUtil.checkPattern(commissionUserData.getName(), "[a-zA-Z,.\\s]*"
				)) {
            errors.rejectValue("name", "name.pattern");
        }
		
		if (ValidationUtil.checkLength(commissionUserData.getDesignation(), 200
				)) {
            errors.rejectValue("designation", "designation.size");
        }
		else if (ValidationUtil.checkPattern(commissionUserData.getDesignation(), "[a-zA-Z0-9\\s.,()/&]*"
				)) {
            errors.rejectValue("designation", "designation.pattern");
        }
		
		if (ValidationUtil.checkLength(commissionUserData.getBasicPay(), 8
				)) {
            errors.rejectValue("basicPay", "basicPay.size");
        }
		else if (ValidationUtil.checkPattern(commissionUserData.getBasicPay(), "[0-9\\s]*"
				)) {
            errors.rejectValue("basicPay", "basicPay.pattern");
        }
		
		if (ValidationUtil.checkBlank(commissionUserData.getMobileNo())) {
            errors.rejectValue("mobileNo", "mobileNo.blank");
        }
		else if (ValidationUtil.checkLength(commissionUserData.getMobileNo(), 10
				)) {
            errors.rejectValue("mobileNo", "mobileNo.size");
        }
		else if (ValidationUtil.checkPattern(commissionUserData.getMobileNo(), "[0-9\\s]*"
				)) {
            errors.rejectValue("mobileNo", "mobileNo.pattern");
        }
		
		if (ValidationUtil.checkLength(commissionUserData.getEmail(), 100
				)) {
            errors.rejectValue("email", "email.size");
        }
		else if (ValidationUtil.checkPattern(commissionUserData.getEmail(), "^(?=.{1,100}@)[A-Za-z0-9_-]+([A-Za-z0-9._-]+)*@[^-][A-Za-z0-9-]+([A-Za-z0-9.-]+)*(.[A-Za-z]{2,})$"
				)) {
            errors.rejectValue("email", "email.pattern");
        }
		
		if (ValidationUtil.checkLength(commissionUserData.getOfficialAddress(), 200
				)) {
            errors.rejectValue("officialAddress", "officialAddress.size");
        }
		else if (ValidationUtil.checkPattern(commissionUserData.getOfficialAddress(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("officialAddress", "officialAddress.pattern");
        }
		
		if (ValidationUtil.checkLength(commissionUserData.getResidentialAddress(), 200
				)) {
            errors.rejectValue("residentialAddress", "residentialAddress.size");
        }
		else if (ValidationUtil.checkPattern(commissionUserData.getResidentialAddress(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("residentialAddress", "residentialAddress.pattern");
        }
		
		if (ValidationUtil.checkLength(commissionUserData.getRemarks(), 200
				)) {
            errors.rejectValue("remarks", "remarks.size");
        }
		else if (ValidationUtil.checkPattern(commissionUserData.getRemarks(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("remarks", "remarks.pattern");
        }
		
		if (ValidationUtil.checkLength(commissionUserData.getEquivalentStatus(), 200
				)) {
            errors.rejectValue("equivalentStatus", "equivalentStatus.size");
        }
		else if (ValidationUtil.checkPattern(commissionUserData.getEquivalentStatus(), "[a-zA-Z0-9\\s.&()]*"
				)) {
            errors.rejectValue("equivalentStatus", "equivalentStatus.pattern");
        }
	}
}
