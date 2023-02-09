package in.co.bel.ims.initial.service.exception;

public class PassCountExceededException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PassCountExceededException(String message) {
		super(message);
	}

}
