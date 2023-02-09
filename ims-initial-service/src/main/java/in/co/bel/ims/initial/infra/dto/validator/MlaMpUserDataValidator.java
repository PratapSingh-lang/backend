package in.co.bel.ims.initial.infra.dto.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import in.co.bel.ims.initial.infra.dto.MlaMpUserData;

public class MlaMpUserDataValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return MlaMpUserData.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		MlaMpUserData mlaMpUserData = (MlaMpUserData) target;
		if (ValidationUtil.checkLength(mlaMpUserData.getEmpId(), 50
				)) {
            errors.rejectValue("empId", "empId.size");
        }
		else if (ValidationUtil.checkPattern(mlaMpUserData.getEmpId(), "[a-zA-Z0-9\\s/-]*"
				)) {
            errors.rejectValue("empId", "empId.pattern");
        }
		
		if (ValidationUtil.checkLength(mlaMpUserData.getConstituencyOrState(), 200
				)) {
            errors.rejectValue("constituencyOrState", "constituencyOrState.size");
        }
		else if (ValidationUtil.checkPattern(mlaMpUserData.getConstituencyOrState(), "[a-zA-Z0-9\\s.,()&-]*"
				)) {
            errors.rejectValue("constituencyOrState", "constituencyOrState.pattern");
        }
		
		if (ValidationUtil.checkLength(mlaMpUserData.getName(), 200
				)) {
            errors.rejectValue("name", "name.size");
        }
		else if (ValidationUtil.checkPattern(mlaMpUserData.getName(), "[a-zA-Z,.\\s]*"
				)) {
            errors.rejectValue("name", "name.pattern");
        }
		
		if (ValidationUtil.checkBlank(mlaMpUserData.getMobileNo())) {
            errors.rejectValue("mobileNo", "mobileNo.blank");
        }
		else if (ValidationUtil.checkLength(mlaMpUserData.getMobileNo(), 10
				)) {
            errors.rejectValue("mobileNo", "mobileNo.size");
        }
		else if (ValidationUtil.checkPattern(mlaMpUserData.getMobileNo(), "[0-9\\s]*"
				)) {
            errors.rejectValue("mobileNo", "mobileNo.pattern");
        }
		
		if (ValidationUtil.checkLength(mlaMpUserData.getEmail(), 100
				)) {
            errors.rejectValue("email", "email.size");
        }
		else if (ValidationUtil.checkPattern(mlaMpUserData.getEmail(), "^(?=.{1,100}@)[A-Za-z0-9_-]+([A-Za-z0-9._-]+)*@[^-][A-Za-z0-9-]+([A-Za-z0-9.-]+)*(.[A-Za-z]{2,})$"
				)) {
            errors.rejectValue("email", "email.pattern");
        }
		
		if (ValidationUtil.checkLength(mlaMpUserData.getAddress(), 200
				)) {
            errors.rejectValue("address", "address.size");
        }
		else if (ValidationUtil.checkPattern(mlaMpUserData.getAddress(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("address", "address.pattern");
        }
		
		if (ValidationUtil.checkLength(mlaMpUserData.getRemarks(), 200
				)) {
            errors.rejectValue("remarks", "remarks.size");
        }
		else if (ValidationUtil.checkPattern(mlaMpUserData.getRemarks(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("remarks", "remarks.pattern");
        }
	}
}
