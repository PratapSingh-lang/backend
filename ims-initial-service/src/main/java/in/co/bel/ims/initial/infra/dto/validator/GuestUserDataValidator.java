package in.co.bel.ims.initial.infra.dto.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import in.co.bel.ims.initial.infra.dto.GuestUserData;

public class GuestUserDataValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return GuestUserData.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		GuestUserData guestUserData = (GuestUserData) target;
		if (ValidationUtil.checkLength(guestUserData.getName(), 200
				)) {
            errors.rejectValue("name", "name.size");
        }
		else if (ValidationUtil.checkPattern(guestUserData.getName(), "[a-zA-Z,.\\s]*"
				)) {
            errors.rejectValue("name", "name.pattern");
        }
		
		
		if (ValidationUtil.checkBlank(guestUserData.getMobileNo())) {
            errors.rejectValue("mobileNo", "mobileNo.blank");
        }
		else if (ValidationUtil.checkLength(guestUserData.getMobileNo(), 10
				)) {
            errors.rejectValue("mobileNo", "mobileNo.size");
        }
		else if (ValidationUtil.checkPattern(guestUserData.getMobileNo(), "[0-9\\s]*"
				)) {
            errors.rejectValue("mobileNo", "mobileNo.pattern");
        }
		
		if (ValidationUtil.checkLength(guestUserData.getEmail(), 100
				)) {
            errors.rejectValue("email", "email.size");
        }
		else if (ValidationUtil.checkPattern(guestUserData.getEmail(), "^(?=.{1,100}@)[A-Za-z0-9_-]+([A-Za-z0-9._-]+)*@[^-][A-Za-z0-9-]+([A-Za-z0-9.-]+)*(.[A-Za-z]{2,})$"
				)) {
            errors.rejectValue("email", "email.pattern");
        }
		
		if (ValidationUtil.checkLength(guestUserData.getNationality(), 200
				)) {
            errors.rejectValue("nationality", "nationality.size");
        }
		else if (ValidationUtil.checkPattern(guestUserData.getNationality(), "[A-za-z\\s]*"
				)) {
            errors.rejectValue("nationality", "nationality.pattern");
        }
		
		if (ValidationUtil.checkLength(guestUserData.getResidentialAddress(), 200
				)) {
            errors.rejectValue("residentialAddress", "residentialAddress.size");
        }
		else if (ValidationUtil.checkPattern(guestUserData.getResidentialAddress(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("residentialAddress", "residentialAddress.pattern");
        }
		
		if (ValidationUtil.checkLength(guestUserData.getGovtIdNumber(), 200
				)) {
            errors.rejectValue("govtIdNumber", "govtIdNumber.size");
        }
		else if (ValidationUtil.checkPattern(guestUserData.getGovtIdNumber(), "[a-zA-Z0-9\\s/-]*"
				)) {
            errors.rejectValue("govtIdNumber", "govtIdNumber.pattern");
        }
		
		if (ValidationUtil.checkLength(guestUserData.getRemarks(), 200
				)) {
            errors.rejectValue("remarks", "remarks.size");
        }
		else if (ValidationUtil.checkPattern(guestUserData.getRemarks(), "[a-zA-Z0-9\\s.'@%&/,()#-]*"
				)) {
            errors.rejectValue("remarks", "remarks.pattern");
        }
		
		if (ValidationUtil.checkLength(guestUserData.getRecommendingOfficerName(), 200
				)) {
            errors.rejectValue("recommendingOfficerName", "recommendingOfficerName.size");
        }
		else if (ValidationUtil.checkPattern(guestUserData.getRecommendingOfficerName(), "[a-zA-Z\\s]*"
				)) {
            errors.rejectValue("recommendingOfficerName", "recommendingOfficerName.pattern");
        }
		
		if (ValidationUtil.checkLength(guestUserData.getRecommendingOfficerDesignation(), 200
				)) {
            errors.rejectValue("recommendingOfficerDesignation", "recommendingOfficerDesignation.size");
        }
		else if (ValidationUtil.checkPattern(guestUserData.getRecommendingOfficerDesignation(), "[a-zA-Z\\s.,&()/]*"
				)) {
            errors.rejectValue("recommendingOfficerDesignation", "recommendingOfficerDesignation.pattern");
        }
	}
}
