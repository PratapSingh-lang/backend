package in.co.bel.ims.initial.service.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.dto.EmailRequest;

@CrossOrigin
@RestController
@RequestMapping("/app/email")
public class ImsEmailService {

	@Autowired
	private JavaMailSender emailSender;

	@Value("${email.attachment.path}")
	private String attachmentPath;

	private final String EMAIL_INVITE_TEMPLATE = "/templates/eMail_Invite_Template.txt";
	private final String EMAIL_TICKET_TEMPLATE = "/templates/eMail_Ticket_Template.txt";
	private final String EMAIL_TICKET_CANCEL_TEMPLATE = "/templates/eMail_Ticket_Cancel_Template.txt";
	private final String EMAIL_OTP_TEMPLATE = "/templates/eMail_OTP_Template.txt";
	private final String EMAIL_FORGOT_PWD_TEMPLATE = "/templates/eMail_Pwd_Forgot_Template.txt";
	
	@GetMapping("/send")
	public void sendSimpleMessage(@Valid @RequestBody EmailRequest emailRequest) {
		MimeMessage message = emailSender.createMimeMessage();

		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setFrom("mod-events@gov.in");
			helper.setTo(emailRequest.getRecipients().toArray(new String[0]));
			helper.setSubject(emailRequest.getEventName() + " Event Proforma");
			helper.setText("Sir/Madam, \nKindly find the attached Proforma for the event " + emailRequest.getEventName()
					+ "\n. Login to the portal https://nimantran.mod.gov.in for futher details. \nBest Regards, \nJS Ceremonials");

			File attachment = new File(attachmentPath);
			if (attachment.exists()) {
				helper.addAttachment("Proforma", attachment);
			}

			emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	public void sendPassword(String email, String password) {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setFrom("mod-events@gov.in");
			helper.setTo(email);
			helper.setSubject("IMS Portal Login");
			helper.setText(
					"Sir/Madam, \nWelcome to IMS portal! You are chosen as Nodal officer for MoD Events, Please login to IMS application with your mobile number and the password is "
							+ password
							+ "\n. Kindly change your password on your first login.\n\nBest Regards, \nJS Ceremonials");

			emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	public void sendPass(String email) {
		
		System.out.println("ImsEmailService.sendPass() "+email);
		if (email != null) {

			String content = "Dear Sir/Madam,<br><br>\r\n" + "\r\n"
					+ "The Ministry of Defence requests the pleasure of your presence at the Independence Day Flag Hoisting Ceremony at 7.00 am on Monday, the 15th August, 2022 at Red Fort, Delhi. <br><br>\r\n"
					+ "\r\n"
					+ "As part of Government of India’s initiative to digitalize the invitation process, an e-invitation is being issued to you as part of the pilot project for the above event. Please refer to the instructions as enclosed with the pass. <br><br>\r\n"
					+ "\r\n"
					+ "You are requested also to carry Physical Invitation card along with e-invitation card (either in soft copy or a printout) for safety purpose as the e-invitation is at pilot testing stage only. <br><br>\r\n"
					+ "\r\n"
					+ "You are requested to show your e-invitation card at the gate. Your card will be scanned through mobile app available with the security personnel at the gate. Only in the event of the scanning of e-invitation fails at the gate, physical card should be used. <br><br>\r\n"
					+ "\r\n"
					+ "You can download your E-invitation <a href=\"https://nimantran.mod.gov.in/\">here</a>. (Please refer to the instructions indicated below to download the pass)<br><br>\r\n"
					+ "\r\n" + "Regards<br>\r\n" + "Under Secretary (Ceremonial)/ MoD<br>\r\n"
					+ "Tel. No. 23016547, 23012904<br><br>\r\n" + "\r\n"
					+ "<b>Instructions to download E-pass:</b><br>\r\n" + "<ol>\r\n"
					+ "<li>	Login with your mobile number</li>\r\n" + "<li>	Enter OTP</li>\r\n"
					+ "<li>	Got to “My Invitations”</li>\r\n" + "<li>	Download your pass</li>\r\n" + "</ol>\r\n" + "";

			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper;
			try {
				helper = new MimeMessageHelper(message, true);
				helper.setFrom("mod-events@gov.in");
				helper.setTo(email);
				helper.setSubject("IMS Portal - Event Invitation");
				helper.setText(content, true);
				emailSender.send(message);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendOTP(String email, String otp) {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			System.out.println("Sending OTP through Mail");
			helper = new MimeMessageHelper(message, true);
			helper.setFrom("mod-events@gov.in");
			helper.setTo(email);
			helper.setSubject("IMS Portal Login");

			InputStream inputStream = (InputStream) getClass().getResourceAsStream(EMAIL_OTP_TEMPLATE);
			String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
			inputStream.close();

			otpMailText = otpMailText.replace("<<OTP>>", otp);
			otpMailText = otpMailText.replace("<<XX>>", "10");

			helper.setText(otpMailText);
			System.out.println(otpMailText);
			emailSender.send(message);
			System.out.println("Sent OTP via Mail");
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendOTPForForgotPwd(String email, String otp) {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			System.out.println("Sending OTP through Mail");
			helper = new MimeMessageHelper(message, true);
			helper.setFrom("mod-events@gov.in");
			helper.setTo(email);
			helper.setSubject("IMS Portal Login");
			
			InputStream inputStream = (InputStream) getClass().getResourceAsStream(EMAIL_FORGOT_PWD_TEMPLATE);
			String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
			inputStream.close();
			
			otpMailText = otpMailText.replace("<<OTP>>", otp);
			otpMailText = otpMailText.replace("<<VALIDITY_MIN>>", "10");
			
			System.out.println(otpMailText);
			helper.setText(otpMailText);
			emailSender.send(message);
			System.out.println("Sent OTP via Mail");
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void sendEventInvitation(String email, String subject, String eventName, Date eventDate, String eventVenue) {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper;
		try 
		{
			System.out.println("Sending Mail");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			helper = new MimeMessageHelper(message, true);
			helper.setFrom("mod-events@gov.in");
			helper.setTo(email);
			helper.setSubject(subject);

			InputStream inputStream = (InputStream) getClass().getResourceAsStream(EMAIL_INVITE_TEMPLATE);
			String invitationMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
			inputStream.close();
			System.out.println("Sending Mail "+invitationMailText);
			if(invitationMailText != null) {
			invitationMailText = invitationMailText.replace("<<EVENT_NAME>>", eventName);
			invitationMailText = invitationMailText.replace("<<EVENT_TIME>>", "");
			invitationMailText = invitationMailText.replace("<<EVENT_DAY_AND_DATE>>", dateFormatter.format(eventDate));
			invitationMailText = invitationMailText.replace("<<EVENT_VENUE>>", eventVenue);
			helper.setText(invitationMailText);

			System.out.println("Mail sent");
			emailSender.send(message);
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendEventTicket(String email, String subject, String eventName, Date eventDate, String eventVenue) {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper;
		try 
		{
			System.out.println("Sending Mail");
			//String time = DateTimeFormatter.ofPattern("hh:mm a").format(eventDateTime);
			//String date = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(eventDateTime);
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			
			helper = new MimeMessageHelper(message, true);
			helper.setFrom("mod-events@gov.in");
			helper.setTo(email);
			helper.setSubject(subject);
			
			InputStream inputStream = (InputStream) getClass().getResourceAsStream(EMAIL_TICKET_TEMPLATE);
			String invitationMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
			inputStream.close();
			
			invitationMailText = invitationMailText.replace("<<EVENT_NAME>>", eventName);
			invitationMailText = invitationMailText.replace("<<EVENT_TIME>>", "");
			invitationMailText = invitationMailText.replace("<<EVENT_DAY_AND_DATE>>", dateFormatter.format(eventDate));
			invitationMailText = invitationMailText.replace("<<EVENT_VENUE>>", eventVenue);
			helper.setText(invitationMailText);
			
			System.out.println("Mail sent");
			emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendEventCancelTicket(String email, String subject, String eventName, Date eventDate, String eventVenue) {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper;
		try 
		{
			System.out.println("Sending Mail");
			//String time = DateTimeFormatter.ofPattern("hh:mm a").format(eventDateTime);
			//String date = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(eventDateTime);
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			
			helper = new MimeMessageHelper(message, true);
			helper.setFrom("mod-events@gov.in");
			helper.setTo(email);
			helper.setSubject(subject);
			
			InputStream inputStream = (InputStream) getClass().getResourceAsStream(EMAIL_TICKET_CANCEL_TEMPLATE);
			String invitationMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
			inputStream.close();
			
			invitationMailText = invitationMailText.replace("<<EVENT_NAME>>", eventName);
			invitationMailText = invitationMailText.replace("<<EVENT_TIME>>", "");
			invitationMailText = invitationMailText.replace("<<EVENT_DAY_AND_DATE>>", dateFormatter.format(eventDate));
			invitationMailText = invitationMailText.replace("<<EVENT_VENUE>>", eventVenue);
			helper.setText(invitationMailText);
			
			System.out.println("Mail sent");
			emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	public String handleCustomExceptions(Exception ex) {
		ex.printStackTrace();
		return ex.getMessage();
	}

}