package in.co.bel.ims.initial.service.util;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import in.co.bel.ims.initial.service.dto.ImsResponse;

@ControllerAdvice
public class FileSizeLimitExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(MultipartException.class)
	@ResponseBody
	public ImsResponse uploadedAFileTooLarge(FileSizeLimitExceededException e) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setSuccess(false);
		imsResponse.setMessage("Maximum file size limit (1MB) exceeded");
		
		return imsResponse;
	}
}
