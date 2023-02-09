package in.co.bel.ims.initial.service.exception;

public class DuplicateUserException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DuplicateUserException(String message) {
		super(message);
	}

}
