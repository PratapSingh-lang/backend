package in.co.bel.ims.initial.data.repository;

import in.co.bel.ims.initial.entity.Captcha;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface CaptchaRepository extends ImsJPATemplate<Captcha> {

	Captcha findByIdAndValid(int id, boolean b);
   
}

