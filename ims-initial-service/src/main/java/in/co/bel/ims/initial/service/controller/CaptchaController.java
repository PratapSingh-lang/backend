package in.co.bel.ims.initial.service.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.apiclub.captcha.Captcha;
import in.co.bel.ims.initial.data.repository.CaptchaRepository;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.ImsCipherUtil;

@RestController
@CrossOrigin
@RequestMapping("/app/authentication")
public class CaptchaController extends ImsServiceTemplate<in.co.bel.ims.initial.entity.Captcha, CaptchaRepository>{
	
	@Autowired
	private CaptchaRepository captchaRepository;
	
	@GetMapping("/getCaptcha")
	private Map<String, String> getCaptcha() {
		Captcha captcha = ImsCipherUtil.createCaptcha(180, 60);
		in.co.bel.ims.initial.entity.Captcha imageCaptcha = new in.co.bel.ims.initial.entity.Captcha(); 
		String test = captcha.getAnswer().toString();
		String numOne = test.substring(0, test.indexOf(" "));
		String numTwo = test.substring(test.lastIndexOf(" ") + 1, test.length());
		String operator = test.substring(test.indexOf(" ") + 1, test.lastIndexOf(" "));
		imageCaptcha.setValue(calculateCaptchaValue(Integer.parseInt(numOne), Integer.parseInt(numTwo), operator));
		imageCaptcha.setValid(true);
		in.co.bel.ims.initial.entity.Captcha realCaptcha = captchaRepository.save(imageCaptcha);
		Map<String, String> result = new HashMap<>();
		result.put("image",ImsCipherUtil.encodeCaptcha(captcha));
		result.put("captchaId", String.valueOf(realCaptcha.getId()).toString());
		invalidateCaptcha(realCaptcha.getId());
		return result;
	}
	
	private String calculateCaptchaValue(int numOne, int numTwo, String operator) {
		int result = -1;
		switch (operator) {
	      case "+":
	    	  result = numOne + numTwo;
	        break;
	      case "-":
	    	  result = numOne - numTwo;
	        break;
	      default:
	        result = -1;
	        break;
	    }
		return Integer.toString(result);
	}

	private void invalidateCaptcha(int id) {

		TimerTask task = new TimerTask() {
			public void run() {
				in.co.bel.ims.initial.entity.Captcha captcha = captchaRepository.findById(id).get();
				captcha.setValid(false);
				captchaRepository.save(captcha);
			}
		};
		Timer timer = new Timer("Invalidate Captcha");

		long delay = 600000L;
		timer.schedule(task, delay);

	}
	
}
