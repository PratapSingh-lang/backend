package in.co.bel.ims.initial.infra.dto.validator;

import java.util.regex.Pattern;

public class ValidationUtil {
	public static boolean checkBlank(String input) {
		return (input == null || input.trim().length() == 0);
    }
	
	public static boolean checkLength(String input, int length) {
		if(input != null && !input.isEmpty())
			return (input.trim().length() > length);
		
		return false;
    }
	
	public static boolean checkPattern(String input, String regex) {
		if(input != null && !input.isEmpty())
			return !Pattern.matches(regex, input);
		
		return false;
    }
}
