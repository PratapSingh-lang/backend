package in.co.bel.ims.initial.service.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import in.co.bel.ims.initial.data.repository.CarPassRepository;
import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.data.repository.PassStatusRepository;
import in.co.bel.ims.initial.data.repository.PgTransactionsRepository;
import in.co.bel.ims.initial.entity.CarPass;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.entity.PassStatus;
import in.co.bel.ims.initial.entity.PgTransactions;
import in.co.bel.ims.initial.security.jwt.JwtUtils;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.dto.ImsSMS;
import in.co.bel.ims.initial.service.dto.PaidPassRequest;
import in.co.bel.ims.initial.service.dto.PaymentPayloadRequest;
import in.co.bel.ims.initial.service.dto.PaymentPayloadResponse;
import in.co.bel.ims.initial.service.dto.PgTransactionsResponse;
import in.co.bel.ims.initial.service.dto.PgTxnVerifiedResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.ImsCipherUtil;
import in.co.bel.ims.initial.service.util.PassStatusEnum;

@RestController
@CrossOrigin
@RequestMapping("/app/paymentManager")
public class PgTransactionsController extends ImsServiceTemplate<PgTransactions, PgTransactionsRepository> {

	@Autowired
	private PgTransactionsRepository pgTransactionsRepository;
	@Autowired
	private ImsUserRepository imsUserRepository;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	PassRepository passRepository;
	@Autowired
	PassStatusRepository passStatusRepository;
	@Autowired
	CarPassRepository carPassRepository;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	ImsEmailService emailService;
	@Autowired
	ImsSmsSender imsSmsSender;

	@Value("${PAYU_VERIFY_TXN_URL}")
	private String verifyTxnURL;
	@Value("${PAYU_TXN_KEY}")
	private String txnKey;
	@Value("${PAYU_TXN_SALT}")
	private String txnSalt;
	@Value("${PAYU_TXN_COMMAND}")
	private String txnCommand;
	@Value("${PAYU_TXN_HASH_FORMAT}")
	private String txnHashFormat;
	@Value("${payment.furl}")
	private String furl;
	@Value("${payment.surl}")
	private String surl;
	@Value("${payment.redirect.surl}")
	private String redirectSuccessURL;
	@Value("${payment.redirect.furl}")
	private String redirectFailureURL;
	@Value("${jwtSecret}")
	private String jwtSecret;
	@Value("${jwtExpirationMs}")
	private int jwtExpirationMs;
	@Value("${payment.ui.form.url}")
	private String redirectFormUrl;
	@Value("${smsEnabled}")
	private boolean smsEnabled;
	@Value("${EVENT_TKT_IMS}")
	private String passAccept;
	@Value("${EVENT_TKT_CNL_IMS}")
	private String passReject;
	
	private final String PIPE = "|";
	private final String PIPES = "||||||||||||";
	private final String PRODUCT_NAME = "Amantran";
	private final String STATUS_REQ_PARAM = "status";
	private final String TXNID_REQ_PARAM = "txnid";
	private final String AMOUNT_REQ_PARAM = "amount";
	private final String ERR_MSG_REQ_PARAM = "error_Message";
	private final String MODE_REQ_PARAM = "mode";
	private final int INR20 = 11;
	private final int INR50 = 12;
	private final int INR100 = 13;
	private final int INR500 = 14;

	private final String SMS_TICKET_TEMPLATE = "/templates/sms_Ticket_Template.txt";
	private final String SMS_TICKET_CANCEL_TEMPLATE = "/templates/sms_Ticket_Cancel_Template.txt";

	private final Gson gson = new GsonBuilder().create();

	@PostMapping("/generatePaymentRequestPayload")
	@PreAuthorize("hasRole('INVITEE') or hasRole('COUNTEREMP') or hasRole('SUPERADMIN') or hasRole('CITIZEN')")
	public ImsResponse generatePaymentRequestPayload(@RequestBody PaymentPayloadRequest paymentPayloadRequest, @RequestHeader (name="Authorization") String token) {
		double amount = 0;
		List<Integer> passSubCategories = paymentPayloadRequest.getPassSubcategoryId();
		ImsResponse imsResponse = new ImsResponse();
		
		for(int passSubCategory : passSubCategories) {
			switch (passSubCategory) {
			case INR20:
				amount += 20;
				break;
			case INR50:
				amount += 50;
				break;
			case INR100:
				amount += 100;
				break;
			case INR500:
				amount += 500;
				break;
			default:
				imsResponse.setSuccess(false);
				imsResponse.setMessage("Invalid Pass Type!");
				return imsResponse;

			}
		}
		
		
		PaymentPayloadResponse paymentPayloadResponse = new PaymentPayloadResponse();
		
		String mobileNo = jwtUtils.getUserNameFromJwtToken(token);
		ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
		
		String txnId = ImsCipherUtil.generateTxnId();
		// "key|txnId|10.00|product name|First Name|mail id|phone|furl|surl||||||||salt"
		String dataToHash = txnKey + PIPE + txnId + PIPE + Math.round(amount) + PIPE
				+ PRODUCT_NAME + PIPE + imsUser.getName() + PIPES + txnSalt;
		System.out.println("PgTransactionsController.generatePaymentRequestPayload() " + dataToHash);
		String hashedData = ImsCipherUtil.generateHash(dataToHash);
		paymentPayloadResponse.setAmount(amount);
		paymentPayloadResponse.setEmail(imsUser.getEmail());
		paymentPayloadResponse.setFirstname(imsUser.getName());
		paymentPayloadResponse.setFurl(furl);
		paymentPayloadResponse.setSurl(surl);
		paymentPayloadResponse.setHash(hashedData);
		paymentPayloadResponse.setKey(txnKey);
		paymentPayloadResponse.setPhone(imsUser.getMobileNo());
		paymentPayloadResponse.setProductinfo(PRODUCT_NAME);
		paymentPayloadResponse.setTxnid(txnId);
		paymentPayloadResponse.setRedirectFormUrl(redirectFormUrl);
		imsResponse.setData(paymentPayloadResponse);
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	public int saveTransaction(PaidPassRequest pgTransactionsRequest) {
		String dataToHash = MessageFormat.format(txnHashFormat, pgTransactionsRequest.getKey(),
				pgTransactionsRequest.getTxnid(), pgTransactionsRequest.getAmount(),
				pgTransactionsRequest.getProductinfo(), pgTransactionsRequest.getFirstname(),
				pgTransactionsRequest.getEmail(), txnSalt);

		String hashedData = ImsCipherUtil.generateHash(dataToHash);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("key", pgTransactionsRequest.getKey());
		requestBody.add("command", txnCommand);
		requestBody.add("var1", pgTransactionsRequest.getTxnid());
		requestBody.add("hash", hashedData);

		@SuppressWarnings("rawtypes")
		HttpEntity<MultiValueMap> entity = new HttpEntity<MultiValueMap>(requestBody, headers);

		PgTransactionsResponse pgTransactionsResponse = restTemplate
				.exchange(verifyTxnURL, HttpMethod.POST, entity, PgTransactionsResponse.class).getBody();
		PgTransactions pgTransactions = new PgTransactions();
		if (pgTransactionsResponse != null) {
			pgTransactions.setTransactionId(pgTransactionsResponse.getTxnid());
			pgTransactions.setMode(pgTransactionsResponse.getMode());
			if (pgTransactionsResponse.getStatus().equals("success"))
				pgTransactions.setStatus(1);
			else
				pgTransactions.setStatus(0);

		}
		pgTransactionsRepository.save(pgTransactions);

		return pgTransactions.getStatus();
	}
	
	@RequestMapping(value = "/handlePaymentSuccess", method = RequestMethod.POST)
	public void handlePaymentSuccess(HttpServletRequest request, HttpServletResponse response,  HttpSession session) {
		String transactionId = request.getParameter(TXNID_REQ_PARAM);
		/*Integer status = request.getParameter(STATUS_REQ_PARAM).equalsIgnoreCase("success") ? 1 : 0;
		String mode = request.getParameter(MODE_REQ_PARAM);
		Double amount = Double.parseDouble(request.getParameter(AMOUNT_REQ_PARAM));
		PassStatus passStatus = passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get();
		List<PgTransactions>  pgTransactions = pgTransactionsRepository.findByTransactionId(transactionId);
		pgTransactions.forEach( pgTransaction -> {
			Pass pass = pgTransaction.getPass();
			pass.setPassStatus(passStatus);
			CarPass carPass = pass.getCarPass();
			if(carPass != null) {
				carPass.setPassStatus(passStatus);
				carPassRepository.save(carPass);
			}
			passRepository.save(pass);
			pgTransaction.setAmount(amount);
			pgTransaction.setMode(mode);
			pgTransaction.setStatus(status);

			if (smsEnabled) {
				ImsSMS sms = new ImsSMS();
				sms.setMobileNo(pass.getImsUserByImsUserId().getMobileNo());
				
				try{
					InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_TICKET_TEMPLATE);
					String invitationMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
					inputStream.close();
	
					LocalDateTime eventDateTime = pass.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					String time = DateTimeFormatter.ofPattern("hh:mm a").format(eventDateTime);
					String date = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(eventDateTime);

					invitationMailText = invitationMailText.replace("<<EVENT_NAME>>", pass.getEvent().getName());
					invitationMailText = invitationMailText.replace("<<EVENT_TIME>>", time);
					invitationMailText = invitationMailText.replace("<<EVENT_DAY_AND_DATE>>", date);
					invitationMailText = invitationMailText.replace("<<EVENT_VENUE>>", pass.getEvent().getVenue().getName());

					sms.setMessage(invitationMailText);
				} catch(IOException ioe) {
					System.out.println("Error while reading OTP templates : "+ioe.getMessage());
				}
				
				sms.setTemplateId(passAccept);
				imsSmsSender.send(sms);

				LocalDateTime ldt = pass.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				emailService.sendEventTicket(pass.getImsUserByImsUserId().getEmail(), "", pass.getEvent().getName(), ldt, pass.getEvent().getVenue().getName());
			}
		});
		
		pgTransactionsRepository.saveAll(pgTransactions);*/

		String dataToHash = txnKey + PIPE + txnCommand + PIPE + transactionId + PIPE + txnSalt;
		String hashedData = ImsCipherUtil.generateHash(dataToHash);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("key", txnKey);
		requestBody.add("command", txnCommand);
		requestBody.add("var1", transactionId);
		requestBody.add("hash", hashedData);

		@SuppressWarnings("rawtypes")
		HttpEntity<MultiValueMap> entity = new HttpEntity<MultiValueMap>(requestBody, headers);

		String responseStr = restTemplate.exchange(verifyTxnURL, HttpMethod.POST, entity, String.class).getBody();
		try {
			PgTxnVerifiedResponse pgTransactionsResponse = gson.fromJson(responseStr, PgTxnVerifiedResponse.class);
			if (pgTransactionsResponse != null) {
				System.out.println("Transaction Status : "+pgTransactionsResponse.getStatus());
				if (pgTransactionsResponse.getStatus().equals(1)) {
					PassStatus passStatusAllotted = passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get();
					PassStatus passStatusCancelled = passStatusRepository.findById(PassStatusEnum.CANCELLED.type).get();

					pgTransactionsResponse.getTransaction_details().values().forEach(txnDtl -> {
						Integer txnStatus = txnDtl.getStatus().equalsIgnoreCase("success") ? 1 : 0;
						List<PgTransactions> pgTransactions = pgTransactionsRepository.findByTransactionId(txnDtl.getTxnid());
						pgTransactions.stream().forEach(pgTransaction -> {
							pgTransaction.setAmount(Double.parseDouble(txnDtl.getTransaction_amount()));
							pgTransaction.setMode(txnDtl.getMode());
							pgTransaction.setStatus(txnStatus);

							Pass passDataToUpdate = passRepository.findById(pgTransaction.getPass().getId()).get();
							if(0 == txnStatus) {
								passDataToUpdate.setPassStatus(passStatusCancelled);
								CarPass carPass = passDataToUpdate.getCarPass();
								if(carPass != null) {
									carPass.setPassStatus(passStatusCancelled);
									carPassRepository.save(carPass);
								}
								pgTransaction.setStatus(PassStatusEnum.CANCELLED.type);
								passDataToUpdate.setPassStatus(passStatusCancelled);
								passRepository.save(passDataToUpdate);
							} else {
								passDataToUpdate.setPassStatus(passStatusAllotted);
								CarPass carPass = passDataToUpdate.getCarPass();
								if(carPass != null) {
									carPass.setPassStatus(passStatusAllotted);
									carPassRepository.save(carPass);
								}
								pgTransaction.setStatus(PassStatusEnum.ALLOCATED.type);
								passDataToUpdate.setPassStatus(passStatusAllotted);
								passRepository.save(passDataToUpdate);

								if (smsEnabled) {
									ImsSMS sms = new ImsSMS();
									sms.setMobileNo(passDataToUpdate.getImsUserByImsUserId().getMobileNo());
									
									try{
										InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_TICKET_TEMPLATE);
										String invitationMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
										inputStream.close();
						
										//LocalDateTime eventDateTime = passDataToUpdate.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
										//String time = DateTimeFormatter.ofPattern("hh:mm a").format(eventDateTime);
										//String date = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(eventDateTime);
										SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

										invitationMailText = invitationMailText.replace("<<EVENT_NAME>>", passDataToUpdate.getEvent().getName());
										//invitationMailText = invitationMailText.replace("<<EVENT_TIME>>", time);
										invitationMailText = invitationMailText.replace("<<EVENT_DAY_AND_DATE>>", newFormat.format(passDataToUpdate.getEvent().getDate()));
										invitationMailText = invitationMailText.replace("<<EVENT_VENUE>>", passDataToUpdate.getEvent().getVenue().getName());

										sms.setMessage(invitationMailText);
									} catch(IOException ioe) {
										System.out.println("Error while reading OTP templates : "+ioe.getMessage());
									}
									
									sms.setTemplateId(passAccept);
									imsSmsSender.send(sms);

//									emailService.sendEventTicket(passDataToUpdate.getImsUserByImsUserId().getEmail(),
//											"IMS Portal - Event Ticket", passDataToUpdate.getEvent().getName(),
//											passDataToUpdate.getEvent().getDate(),
//											passDataToUpdate.getEvent().getVenue().getName());
								}
							}
						});
						pgTransactionsRepository.saveAll(pgTransactions);
					});
				}
			}
		} catch(JsonSyntaxException jpe) {
			System.out.println("Invalid response from payment gateway please check i/p Data");
		}

//		
//		// Start - Create new JWT token
//		ImsUser imsUser= pgTransactions.stream().findAny().get().getPass().getImsUserByImsUserId();
//		String username = imsUser.getMobileNo();
//		List<GrantedAuthority> authorities = new ArrayList<>();
//		authorities.add(new SimpleGrantedAuthority(imsUser.getRole().getName()));
//		
//		String token = "Bearer "+Jwts.builder().setSubject(username).setIssuedAt(new Date()).claim("role", authorities)
//		.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
//		.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
//		response.setHeader("Authorization", token);
//		session.setAttribute("Authorization", token);
//		Cookie cookie = new Cookie("Authorization", "token");
//	    // add cookie in server response
//	    response.addCookie(cookie);
//		
//		
//		// End - Create new JWT token
		
		response.setHeader("Location", redirectSuccessURL);
		
		response.setStatus(302);
	}
	
	@RequestMapping(value = "/handlePaymentFailure", method = RequestMethod.POST)
	public void handlePaymentFailure(HttpServletRequest request, HttpServletResponse response,  HttpSession session) {
		String transactionId = request.getParameter(TXNID_REQ_PARAM);
		/*Integer status = request.getParameter(STATUS_REQ_PARAM).equalsIgnoreCase("success") ? 1 : 0;
		String mode = request.getParameter(MODE_REQ_PARAM);
		Double amount = Double.parseDouble(request.getParameter(AMOUNT_REQ_PARAM));
		String errorCode = request.getParameter(ERR_MSG_REQ_PARAM);
		List<PgTransactions>  pgTransactions = pgTransactionsRepository.findByTransactionId(transactionId);
		pgTransactions.forEach( pgTransaction -> {
			pgTransaction.setAmount(amount);
			pgTransaction.setMode(mode);
			pgTransaction.setStatus(status);
			pgTransaction.setErrorCode(errorCode);
			Pass pass = pgTransaction.getPass();
			if (smsEnabled) {
				ImsSMS sms = new ImsSMS();
				sms.setMobileNo(pass.getImsUserByImsUserId().getMobileNo());
				
				try{
					InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_TICKET_CANCEL_TEMPLATE);
					String invitationMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
					inputStream.close();
	
					LocalDateTime eventDateTime = pass.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					String time = DateTimeFormatter.ofPattern("hh:mm a").format(eventDateTime);
					String date = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(eventDateTime);

					invitationMailText = invitationMailText.replace("<<EVENT_NAME>>", pass.getEvent().getName());
					invitationMailText = invitationMailText.replace("<<EVENT_TIME>>", time);
					invitationMailText = invitationMailText.replace("<<EVENT_DAY_AND_DATE>>", date);
					invitationMailText = invitationMailText.replace("<<EVENT_VENUE>>", pass.getEvent().getVenue().getName());

					sms.setMessage(invitationMailText);
				} catch(IOException ioe) {
					System.out.println("Error while reading OTP templates : "+ioe.getMessage());
				}
				
				sms.setTemplateId(passReject);
				imsSmsSender.send(sms);

				LocalDateTime ldt = pass.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				emailService.sendEventCancelTicket(pass.getImsUserByImsUserId().getEmail(), "", pass.getEvent().getName(), ldt, pass.getEvent().getVenue().getName());
			}
			//passRepository.delete(pass);
			
			if(pass != null) {
				pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.CANCELLED.type).get());
				passRepository.save(pass);
			}
		});
		
		pgTransactionsRepository.saveAll(pgTransactions);*/

		String dataToHash = txnKey + PIPE + txnCommand + PIPE + transactionId + PIPE + txnSalt;
		String hashedData = ImsCipherUtil.generateHash(dataToHash);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("key", txnKey);
		requestBody.add("command", txnCommand);
		requestBody.add("var1", transactionId);
		requestBody.add("hash", hashedData);

		@SuppressWarnings("rawtypes")
		HttpEntity<MultiValueMap> entity = new HttpEntity<MultiValueMap>(requestBody, headers);

		String responseStr = restTemplate.exchange(verifyTxnURL, HttpMethod.POST, entity, String.class).getBody();
		try {
			PgTxnVerifiedResponse pgTransactionsResponse = gson.fromJson(responseStr, PgTxnVerifiedResponse.class);

			if (pgTransactionsResponse != null) {
				System.out.println("Transaction Status : "+pgTransactionsResponse.getStatus());
				if (pgTransactionsResponse.getStatus().equals(1)) {
					PassStatus passStatusAllotted = passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get();
					PassStatus passStatusCancelled = passStatusRepository.findById(PassStatusEnum.CANCELLED.type).get();

					pgTransactionsResponse.getTransaction_details().values().forEach(txnDtl -> {
						Integer txnStatus = txnDtl.getStatus().equalsIgnoreCase("success") ? 1 : 0;
						List<PgTransactions> pgTransactions = pgTransactionsRepository.findByTransactionId(txnDtl.getTxnid());
						pgTransactions.stream().forEach(pgTransaction -> {
							pgTransaction.setAmount(Double.parseDouble(txnDtl.getTransaction_amount()));
							pgTransaction.setMode(txnDtl.getMode());
							pgTransaction.setStatus(txnStatus);

							Pass passDataToUpdate = passRepository.findById(pgTransaction.getPass().getId()).get();
							if(0 == txnStatus) {
								passDataToUpdate.setPassStatus(passStatusCancelled);
								CarPass carPass = passDataToUpdate.getCarPass();
								if(carPass != null) {
									carPass.setPassStatus(passStatusCancelled);
									carPassRepository.save(carPass);
								}
								pgTransaction.setStatus(PassStatusEnum.CANCELLED.type);
								passDataToUpdate.setPassStatus(passStatusCancelled);
								passRepository.save(passDataToUpdate);

								if (smsEnabled) {
									ImsSMS sms = new ImsSMS();
									sms.setMobileNo(passDataToUpdate.getImsUserByImsUserId().getMobileNo());
									
									try{
										InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_TICKET_CANCEL_TEMPLATE);
										String invitationMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
										inputStream.close();
						
										//LocalDateTime eventDateTime = passDataToUpdate.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
										//String time = DateTimeFormatter.ofPattern("hh:mm a").format(eventDateTime);
										//String date = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(eventDateTime);
										SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

										invitationMailText = invitationMailText.replace("<<EVENT_NAME>>", passDataToUpdate.getEvent().getName());
										//invitationMailText = invitationMailText.replace("<<EVENT_TIME>>", time);
										invitationMailText = invitationMailText.replace("<<EVENT_DAY_AND_DATE>>", newFormat.format(passDataToUpdate.getEvent().getDate()));
										invitationMailText = invitationMailText.replace("<<EVENT_VENUE>>", passDataToUpdate.getEvent().getVenue().getName());

										sms.setMessage(invitationMailText);
									} catch(IOException ioe) {
										System.out.println("Error while reading OTP templates : "+ioe.getMessage());
									}
									
									sms.setTemplateId(passReject);
									imsSmsSender.send(sms);

//									emailService.sendEventCancelTicket(
//											passDataToUpdate.getImsUserByImsUserId().getEmail(),
//											"IMS Portal - Event Ticket", passDataToUpdate.getEvent().getName(),
//											passDataToUpdate.getEvent().getDate(),
//											passDataToUpdate.getEvent().getVenue().getName());
								}
							} else {
								passDataToUpdate.setPassStatus(passStatusAllotted);
								CarPass carPass = passDataToUpdate.getCarPass();
								if(carPass != null) {
									carPass.setPassStatus(passStatusAllotted);
									carPassRepository.save(carPass);
								}
								pgTransaction.setStatus(PassStatusEnum.ALLOCATED.type);
								passDataToUpdate.setPassStatus(passStatusAllotted);
								passRepository.save(passDataToUpdate);
							}
						});
						pgTransactionsRepository.saveAll(pgTransactions);
					});
				}
			}
		} catch(JsonSyntaxException jpe) {
			System.out.println("Invalid response from payment gateway please check i/p Data");
		}

//		// Start - Create new JWT token
//				ImsUser imsUser= pgTransactions.stream().findAny().get().getPass().getImsUserByImsUserId();
//				String username = imsUser.getMobileNo();
//				List<GrantedAuthority> authorities = new ArrayList<>();
//				authorities.add(new SimpleGrantedAuthority(imsUser.getRole().getName()));
//				
//				String token = "Bearer "+Jwts.builder().setSubject(username).setIssuedAt(new Date()).claim("role", authorities)
//				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
//				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
//				response.setHeader("Authorization", token);
//				session.setAttribute("Authorization", token);
//				Cookie cookie = new Cookie("Authorization", "token");
//			    // add cookie in server response
//			    response.addCookie(cookie);
//				
//				// End - Create new JWT token
				
	    response.setHeader("Location", redirectFailureURL);
	    response.setStatus(302);
	}
	
}
