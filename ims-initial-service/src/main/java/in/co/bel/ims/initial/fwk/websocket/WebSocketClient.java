package in.co.bel.ims.initial.fwk.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import in.co.bel.ims.initial.entity.Notification;

@Component
public class WebSocketClient {

	@Autowired
	SimpMessagingTemplate template;

	public void sendNotification(String topic, Notification message) {
		template.convertAndSend(topic, message);
	}

}
