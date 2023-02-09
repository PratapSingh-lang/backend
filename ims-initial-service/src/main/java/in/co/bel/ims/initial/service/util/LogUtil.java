package in.co.bel.ims.initial.service.util;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import in.co.bel.ims.initial.data.repository.AuditTrailRepository;
import in.co.bel.ims.initial.entity.AuditTrail;
import in.co.bel.ims.initial.entity.ImsUser;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@Service
public class LogUtil {
	
	@Autowired
	AuditTrailRepository auditTrailRepository;
	@Autowired 
	Environment env;
	
	public void saveLog(ImsUser imsUser, String event, String event_category, LogLevelEnum logLevel) {
		AuditTrail log = new AuditTrail();
		if(imsUser != null) {
			log.setUsername(imsUser.getName());
			log.setMobileNo(imsUser.getMobileNo());
		}else {
			log.setUsername("ANNONYMOUS");
		}
		log.setEventCategory(event_category);
		log.setEvent(event);
		log.setLevel(logLevel.name());
		log.setDeleted(false);
		log.setCreatedTime(LocalDateTime.now());
		try(final DatagramSocket socket = new DatagramSocket()){
			  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			  log.setClientIp(socket.getLocalAddress().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String logLevelLimit = env.getProperty("spring.log.logLevel");
		if(logLevel.type >= LogLevelEnum.valueOf(logLevelLimit).type) {
			auditTrailRepository.save(log);
		}
		
	}
}