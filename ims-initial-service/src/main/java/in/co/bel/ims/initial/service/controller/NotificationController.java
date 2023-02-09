package in.co.bel.ims.initial.service.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.NotificationRepository;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.Notification;
import in.co.bel.ims.initial.security.jwt.JwtUtils;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;

@RestController
@CrossOrigin
@RequestMapping("/app/notification")
public class NotificationController extends ImsServiceTemplate<Notification, NotificationRepository> {

	@Autowired
	NotificationRepository notificationRepository;
	@Autowired
	ImsUserRepository imsUserRepository;
	@Autowired
	JwtUtils jwtUtils;

	@GetMapping("/getNotificationsByUserId/{userId}")
	public List<Notification> getNotificationsByUserId(@PathVariable int userId, @RequestHeader (name="Authorization") String token) {
		String mobileNo = jwtUtils.getUserNameFromJwtToken(token);
		ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
		return sanitizeUserData(notificationRepository.findByImsUserId(imsUser.getId()));

	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public Notification create(@Valid @RequestBody  Notification t) {
		// TODO Auto-generated method stub
		return sanitizeUserData(super.create(t));
	}

	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAll() {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((List<Notification>) super.getAll().getData()));
		return imsResponse;
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getById(@PathVariable int id) {

		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((Notification) super.getById(id).getData()));
		return imsResponse;
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse update(@Valid @RequestBody Notification t) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((Notification) super.update(t).getData()));
		return imsResponse;
	}

	private Notification sanitizeUserData(Notification notification) {
		ImsUser user = notification.getImsUser();
		user.setPassword(null);
		user.setPasswordHash(null);
		user.setPasswordSalt(null);
		user.setPasswordEmail(null);
		notification.setImsUser(user);
		return notification;
	}

	private List<Notification> sanitizeUserData(List<Notification> notifications) {
		List<Notification> modNotifications = new ArrayList<>();
		notifications.forEach(notification -> {
			modNotifications.add(sanitizeUserData(notification));
		});
		return modNotifications;
	}

}
