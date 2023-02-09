package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.Notification;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface NotificationRepository extends ImsJPATemplate<Notification> {

	List<Notification> findByImsUserId(int userId);
   
}

