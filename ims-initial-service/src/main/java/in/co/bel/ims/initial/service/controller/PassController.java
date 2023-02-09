package in.co.bel.ims.initial.service.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import in.co.bel.ims.initial.data.repository.AnnexUsersRepository;
import in.co.bel.ims.initial.data.repository.CarPassRepository;
import in.co.bel.ims.initial.data.repository.CplRepository;
import in.co.bel.ims.initial.data.repository.DepartmentRepository;
import in.co.bel.ims.initial.data.repository.EnclosureCplMappingRepository;
import in.co.bel.ims.initial.data.repository.EnclosurePassTypeMappingRepository;
import in.co.bel.ims.initial.data.repository.EnclosureRepository;
import in.co.bel.ims.initial.data.repository.EventRepository;
import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.InvitationOfficerRepository;
import in.co.bel.ims.initial.data.repository.NodalOfficerRepository;
import in.co.bel.ims.initial.data.repository.NotificationRepository;
import in.co.bel.ims.initial.data.repository.PaidPassHoldersRepository;
import in.co.bel.ims.initial.data.repository.PassCategoryRepository;
import in.co.bel.ims.initial.data.repository.PassDayLimitCategoryRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.data.repository.PassStatusRepository;
import in.co.bel.ims.initial.data.repository.PassSubcategoryRepository;
import in.co.bel.ims.initial.data.repository.PgTransactionsRepository;
import in.co.bel.ims.initial.data.repository.UserIpaddressRepository;
import in.co.bel.ims.initial.entity.AnnexUsers;
import in.co.bel.ims.initial.entity.CarPass;
import in.co.bel.ims.initial.entity.Cpl;
import in.co.bel.ims.initial.entity.Department;
import in.co.bel.ims.initial.entity.Enclosure;
import in.co.bel.ims.initial.entity.EnclosureCplMapping;
import in.co.bel.ims.initial.entity.EnclosureGroup;
import in.co.bel.ims.initial.entity.EnclosurePassTypeMapping;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.InvitationOfficer;
import in.co.bel.ims.initial.entity.NodalOfficer;
import in.co.bel.ims.initial.entity.Notification;
import in.co.bel.ims.initial.entity.PaidPassHolders;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.entity.PassCategory;
import in.co.bel.ims.initial.entity.PassDayLimitCategory;
import in.co.bel.ims.initial.entity.PassStatus;
import in.co.bel.ims.initial.entity.PassSubcategory;
import in.co.bel.ims.initial.entity.PgTransactions;
import in.co.bel.ims.initial.entity.UserIpaddress;
import in.co.bel.ims.initial.fwk.websocket.WebSocketClient;
import in.co.bel.ims.initial.infra.dto.AggregatedPassData;
import in.co.bel.ims.initial.security.jwt.JwtUtils;
import in.co.bel.ims.initial.service.dto.AdmitCardRequest;
import in.co.bel.ims.initial.service.dto.AnnexPassRequest;
import in.co.bel.ims.initial.service.dto.CancelTicketRequest;
import in.co.bel.ims.initial.service.dto.ClientPassRequest;
import in.co.bel.ims.initial.service.dto.CplAvailability;
import in.co.bel.ims.initial.service.dto.GuestPassRequest;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.dto.ImsSMS;
import in.co.bel.ims.initial.service.dto.PaidPassRequest;
import in.co.bel.ims.initial.service.dto.PassRequest;
import in.co.bel.ims.initial.service.dto.PassStatsDto;
import in.co.bel.ims.initial.service.dto.PassStatsRequest;
import in.co.bel.ims.initial.service.dto.TicketsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.AllocationStatusEnum;
import in.co.bel.ims.initial.service.util.ExternalPassConfigEnum;
import in.co.bel.ims.initial.service.util.ExternalPassConfigReader;
import in.co.bel.ims.initial.service.util.ImsCipherUtil;
import in.co.bel.ims.initial.service.util.ImsPDFMerger;
import in.co.bel.ims.initial.service.util.ImsUserTypeEnum;
import in.co.bel.ims.initial.service.util.JsonStringReader;
import in.co.bel.ims.initial.service.util.LogLevelEnum;
import in.co.bel.ims.initial.service.util.LogUtil;
import in.co.bel.ims.initial.service.util.MaritalStatusEnum;
import in.co.bel.ims.initial.service.util.NotificationUtils;
import in.co.bel.ims.initial.service.util.PassCategoryEnum;
import in.co.bel.ims.initial.service.util.PassStatusEnum;
import in.co.bel.ims.initial.service.util.PassSubcategoryEnum;
import in.co.bel.ims.initial.service.util.RoleEnum;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@RestController
@CrossOrigin
@RequestMapping("/app/pass")
public class PassController extends ImsServiceTemplate<Pass, PassRepository> {

	@Autowired
	PassRepository passRepository;
	@Autowired
	EventRepository eventRepository;
	@Autowired
	ImsUserRepository imsUserRepository;
	@Autowired
	NotificationRepository notificationRepository;
	@Autowired
	WebSocketClient webSocketClient;
	@Autowired
	DepartmentRepository departmentRepository;
	@Autowired
	PassCategoryRepository passCategoryRepository;
	@Autowired
	PassSubcategoryRepository passSubcategoryRepository;
	@Autowired
	EnclosureRepository enclosureRepository;
	@Autowired
	CarPassRepository carPassRepository;
	@Autowired
	CplRepository cplRepository;
	@Autowired
	PassStatusRepository passStatusRepository;
	@Autowired
	NodalOfficerRepository nodalOfficerRepository;
	@Autowired
	InvitationOfficerRepository invitationOfficerRepository;
	@Autowired
	ImsSmsSender imsSmsSender;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	EnclosureCplMappingRepository enclosureCplMappingRepository;
	@Autowired
	EnclosurePassTypeMappingRepository enclosurePassTypeMappingRepository;
	@Autowired
	PgTransactionsRepository pgTransactionsRepository;
	@Autowired
	PaidPassHoldersRepository paidPassHoldersRepository;
	@Autowired
	UserIpaddressRepository userIpaddressRepository;
	@Autowired
	ExternalPassConfigReader externalPassConfigReader;
	@Autowired
	AnnexUsersRepository annexUsersRepository;
	@Autowired
	PassDayLimitCategoryRepository passDayLimitCategoryRepository;
	@Autowired
	ImsEmailService emailService;

	@Value("${EVENT_INVIT_IMS}")
	private String eventInvitationTemplate;
	@Value("${EVENT_TKT_IMS}")
	private String passAccept;
	@Value("${EVENT_TKT_CNL_IMS}")
	private String passReject;
	@Value("${smsEnabled}")
	private boolean smsEnabled;
	@Value("${applicationURL}")
	private String applicationURL;
	@Autowired
	LogUtil log;

	private final String SMS_TICKET_TEMPLATE = "/templates/sms_Ticket_Template.txt";
	private final String SMS_TICKET_CANCEL_TEMPLATE = "/templates/sms_Ticket_Cancel_Template.txt";
	private final String SMS_INVITE_TEMPLATE = "/templates/sms_Invite_Template.txt";

	@GetMapping("/getAllPassByCategory/{eventId}/{categoryId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllPassByCategory(@PathVariable("eventId") int eventId, @PathVariable int categoryId) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passes = passRepository.findByPassCategoryIdAndEventIdAndDeleted(categoryId, eventId, false);
		imsResponse.setData(sanitizeUserData(passes));
		imsResponse.setMessage("Retrieved Passes for the Pass Catgory!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getAllPassByCategory/{eventId}/{categoryId}/{organizationId}/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllPassByCategory(@PathVariable("eventId") int eventId, @PathVariable("categoryId") int categoryId,
			@PathVariable("organizationId") int organizationId, @PathVariable("departmentId") int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passes;
		if (departmentId == 0)
			passes = passRepository.findByPassCategoryIdAndEventIdAndDepartmentOrganizationIdAndDeleted(categoryId,
					eventId, organizationId, false);
		else
			passes = passRepository.findByPassCategoryIdAndEventIdAndDepartmentIdAndDeleted(categoryId, eventId,
					departmentId, false);
		imsResponse.setData(sanitizeUserData(passes));
		imsResponse.setMessage("Retrieved Passes for the Pass Catgory!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getAllPassBySubcategory/{eventId}/{categoryId}/{subcategoryId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllPassBySubcategory(@PathVariable("eventId") int eventId,
			@PathVariable("categoryId") int categoryId, @PathVariable("subcategoryId") int subcategoryId) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passes = passRepository.findByPassCategoryIdAndPassSubcategoryIdAndEventIdAndDeleted(categoryId,
				subcategoryId, eventId, false);
		imsResponse.setData(sanitizeUserData(passes));
		imsResponse.setMessage("Retrieved Passes for the Pass Subcatgory!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getAllPassByUserType/{eventId}/{userTypeId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllPassByUserType(@PathVariable("eventId") int eventId, @PathVariable int userTypeId) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passes = passRepository.findByImsUserByImsUserIdUserTypeIdAndEventIdAndDeleted(userTypeId, eventId,
				false);
		imsResponse.setData(sanitizeUserData(passes));
		imsResponse.setMessage("Retrieved Passes for the User Type!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@PutMapping("/updateAllPasses")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllPassByUserType(@RequestBody List<ClientPassRequest> clientPasses) {
		ImsResponse imsResponse = new ImsResponse();
		for (ClientPassRequest clientPass : clientPasses) {
			Pass pass = passRepository.findByControlNoAndDeleted(clientPass.getControlNo(), false);
			PassStatus passStatus = passStatusRepository.findById(clientPass.getPassStatusId()).get();
			if (pass != null && passStatus != null) {
				pass.setPassStatus(passStatus);
				passRepository.save(pass);
			}
		}
		System.out.println("***************Updated status of all passes at " + LocalDateTime.now());
		imsResponse.setMessage("Updated the pass data successfully");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getPassByInvitationAdmin/{eventId}/{invitationAdminId}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	ImsResponse getPassByInvitationAdmin(@PathVariable("eventId") int eventId,
			@PathVariable("invitationAdminId") int invitationAdminId) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passes = passRepository.findByImsUserByInvitationAdminIdIdAndEventIdAndDeleted(invitationAdminId,
				eventId, false);
		imsResponse.setData(sanitizeUserData(passes));
		imsResponse.setMessage("Retrieved Passes for the Invitation Admin!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getAllPassByNodalOfficer/{eventId}/{nodalOfficerId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER')")
	ImsResponse getPassByNodalOfficer(@PathVariable("eventId") int eventId,
			@PathVariable("nodalOfficerId") int nodalOfficerId) {
		ImsResponse imsResponse = new ImsResponse();
		try {
			List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByImsUserId(nodalOfficerId);
			List<Integer> departments = nodalOfficers.stream().map(nodalOfficer -> nodalOfficer.getDepartment().getId())
					.collect(Collectors.toList());
			List<Pass> passes = passRepository.findByEventIdAndDeletedAndDepartmentIdIn(eventId, false, departments);
			imsResponse.setData(sanitizeUserData(passes));
			imsResponse.setMessage("Retrieved Passes for the Invitation Admin!");
			imsResponse.setSuccess(true);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			imsResponse.setMessage("Passes not available for the department/event!");
			imsResponse.setSuccess(false);
		}
		return imsResponse;
	}

	@GetMapping("/getPassByInvitationAdmin/{eventId}/{invitationAdminId}/{filterDate}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	ImsResponse getPassByInvitationAdmin(@PathVariable("eventId") int eventId,
			@PathVariable("invitationAdminId") int invitationAdminId, @PathVariable("filterDate") String filterDate) {
		ImsResponse imsResponse = new ImsResponse();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		LocalDateTime fromDateTime = LocalDateTime.parse(filterDate + " 00:00:00.000", formatter);
		LocalDateTime toDateTime = LocalDateTime.parse(filterDate + " 23:59:59.999", formatter);
		List<Pass> passes = passRepository
				.findByImsUserByInvitationAdminIdIdAndEventIdAndCreatedTimestampBetweenAndDeleted(invitationAdminId,
						eventId, fromDateTime, toDateTime, false);
		imsResponse.setData(sanitizeUserData(passes));
		imsResponse.setMessage("Retrieved Passes for the Invitation Admin!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getAllPassByUser/{userId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER') or hasRole('INVITEE') or hasRole('CITIZEN') or hasRole('COUNTEREMP')")
	ImsResponse getAllPassByUser(@PathVariable int userId, @RequestHeader(name = "Authorization") String token) {
		String mobileNo = jwtUtils.getUserNameFromJwtToken(token);
		ImsResponse imsResponse = new ImsResponse();
		ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
		List<Pass> passes = passRepository.findByImsUserByImsUserIdIdAndPassStatusIdNotAndDeleted(imsUser.getId(),
				PassStatusEnum.PENDING_APPROVAL.type, false);
		imsResponse.setData(sanitizeUserData(passes));
		imsResponse.setMessage("Retrieved Passes for the User!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	//To get all the passes for an user along with the payment mode
	@GetMapping("/getPassesByUserId/{userId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER') or hasRole('INVITEE') or hasRole('CITIZEN') or hasRole('COUNTEREMP')")
	ImsResponse getPassesByUserId(@PathVariable int userId, @RequestHeader(name = "Authorization") String token) {
		String mobileNo = jwtUtils.getUserNameFromJwtToken(token);
		ImsResponse imsResponse = new ImsResponse();
		ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
		List<Pass> passes = passRepository.findByImsUserByImsUserIdIdAndPassStatusIdNotAndDeleted(imsUser.getId(),
				PassStatusEnum.PENDING_APPROVAL.type, false);
		List<TicketsResponse> ticketsResponses = new ArrayList<>();
		passes = sanitizeUserData(passes);
		for(Pass pass : passes) {
			TicketsResponse ticketsResponse = new TicketsResponse();
			ticketsResponse.setPass(pass);
			PgTransactions pgTransaction = pgTransactionsRepository.findByPassId(pass.getId());
			ticketsResponse.setTxnId(pgTransaction.getMode());
			ticketsResponses.add(ticketsResponse);
		}
		imsResponse.setData(ticketsResponses);
		imsResponse.setMessage("Retrieved Passes for the User!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	    //To get all the passes for an user along with the payment mode
		@GetMapping("/getPassesByUserIdForCurrentDate/{userId}/{date}")
		@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER') or hasRole('INVITEE') or hasRole('CITIZEN') or hasRole('COUNTEREMP')")
		ImsResponse getPassesByUserIdForCurrentDate(@PathVariable int userId, @PathVariable String date, @RequestHeader(name = "Authorization") String token) {
			String mobileNo = jwtUtils.getUserNameFromJwtToken(token);
			ImsResponse imsResponse = new ImsResponse();
			ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
			final LocalDateTime fromDateTime = LocalDateTime.parse(date + " 00:00:00.000", formatter);
			final LocalDateTime toDateTime = LocalDateTime.parse(date + " 23:59:59.999", formatter);
			List<Pass> passes = passRepository.findByImsUserByImsUserIdIdAndPassStatusIdNotAndCreatedTimestampBetweenAndAndDeleted(Sort.by(Order.desc("id")), imsUser.getId(),
					PassStatusEnum.PENDING_APPROVAL.type, fromDateTime, toDateTime, false);
			List<TicketsResponse> ticketsResponses = new ArrayList<>();
			passes = sanitizeUserData(passes);
			for(Pass pass : passes) {
				TicketsResponse ticketsResponse = new TicketsResponse();
				ticketsResponse.setPass(pass);
				PgTransactions pgTransaction = pgTransactionsRepository.findByPassId(pass.getId());
				ticketsResponse.setTxnId(pgTransaction.getMode());
				ticketsResponses.add(ticketsResponse);
			}
			imsResponse.setData(ticketsResponses);
			imsResponse.setMessage("Retrieved Passes for the User!");
			imsResponse.setSuccess(true);
			return imsResponse;
		}

	@GetMapping("/getAllPassByOrganization/{eventId}/{organizationId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	ImsResponse getAllPassByOrganization(@PathVariable("eventId") int eventId,
			@PathVariable("organizationId") int organizationId) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passes = passRepository.findByDepartmentOrganizationIdAndEventIdAndDeleted(organizationId, eventId,
				false);
		imsResponse.setData(sanitizeUserData(passes));
		imsResponse.setMessage("Retrieved Passes for the Organization!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getAllPassByDepartment/{eventId}/{departmentId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('HIGHEROFFICER')")
	ImsResponse getAllPassByDepartment(@PathVariable("eventId") int eventId,
			@PathVariable("departmentId") int departmentId) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passes = passRepository.findByDepartmentIdAndEventIdAndDeleted(departmentId, eventId, false);
		imsResponse.setData(sanitizeUserData(passes));
		imsResponse.setMessage("Retrieved Passes for the Department!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getAllPassByEventAndEnclosure/{eventId}/{enclosureId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	ResponseEntity<Object> getAllPassByEventAndEnclosure(@PathVariable("eventId") int eventId,
			@PathVariable("enclosureId") int enclosureId) {
		List<Pass> passes = passRepository.getAllPassesByEventAndEnclosure();
		passes = passes.stream()
				.filter(pass -> pass.getEvent() != null && pass.getEnclosure() != null
						&& pass.getEvent().getId() == eventId && pass.getEnclosure().getId() == enclosureId)
				.collect(Collectors.toList());
		List<AggregatedPassData> data = passes.stream().map(obj -> {
			AggregatedPassData apd = new AggregatedPassData();
			apd.setId(obj.getId());
			if(obj.getAnnexUsers() != null)
				apd.setPassHolderName(obj.getAnnexUsers().getName());
			else if(obj.getPaidPassHolders() != null)
				apd.setPassHolderName(obj.getPaidPassHolders().getName());
			apd.setControlNo(obj.getControlNo());
			apd.setControlHash(obj.getControlHash());
			if (obj.getPaidPassHolders() != null)
				apd.setDob(obj.getPaidPassHolders().getDob());
			else if(obj.getAnnexUsers() != null)
				apd.setDob(obj.getAnnexUsers().getDob());
			apd.setEventId(obj.getEvent().getId());
			apd.setEventName(obj.getEvent().getName());
			apd.setEnclosureId(obj.getEnclosure().getId());
			apd.setEnclosureName(obj.getEnclosure().getName());
			apd.setPassStatusId(obj.getPassStatus().getId());
			apd.setPassStatusName(obj.getPassStatus().getStatus());
			apd.setPassCategoryId(obj.getPassCategory().getId());
			apd.setPassCategoryName(obj.getPassCategory().getName());
			apd.setPassSubCategoryId(obj.getPassSubcategory().getId());
			apd.setPassSubCategoryName(obj.getPassSubcategory().getName());
			apd.setModifiedTimestamp(obj.getModifiedTimestamp());
			apd.setRemarks(obj.getRemarks());
			
			if (obj.getAnnexUsers() != null && obj.getAnnexUsers().getMaritalStatus() != null)
				apd.setMaritalStatusId(obj.getAnnexUsers().getMaritalStatus().getId());
			else if (obj.getImsUserByImsUserId() != null && obj.getImsUserByImsUserId().getMaritalStatus() != null)
				apd.setMaritalStatusId(obj.getImsUserByImsUserId().getMaritalStatus().getId());
			if (obj.getAnnexUsers() != null && obj.getAnnexUsers().getMaritalStatus() != null)
				apd.setMaritalStatusName(obj.getAnnexUsers().getMaritalStatus().getStatus());
			else if (obj.getImsUserByImsUserId() != null && obj.getImsUserByImsUserId().getMaritalStatus() != null)
				apd.setMaritalStatusName(obj.getImsUserByImsUserId().getMaritalStatus().getStatus());
			if (obj.getAnnexUsers() != null && obj.getAnnexUsers().getSalutation() != null)
				apd.setSalutationId(obj.getAnnexUsers().getSalutation().getId());
			else if (obj.getImsUserByImsUserId() != null && obj.getImsUserByImsUserId().getSalutation() != null)
				apd.setSalutationId(obj.getImsUserByImsUserId().getSalutation().getId());
			if (obj.getAnnexUsers() != null && obj.getAnnexUsers().getSalutation() != null)
				apd.setSalutationName(obj.getAnnexUsers().getSalutation().getName());
			else if (obj.getImsUserByImsUserId() != null && obj.getImsUserByImsUserId().getSalutation() != null)
				apd.setSalutationName(obj.getImsUserByImsUserId().getSalutation().getName());
			return apd;
		}).collect(Collectors.toList());

		try {
			CSVPrinter printer = new CSVPrinter(new FileWriter("enclosure.csv"), CSVFormat.DEFAULT);
			printer.printRecord("id", "pass_holder_name", "control_no", "control_hash", "dob", "event_id", "event_name",
					"enclosure_id", "enclosure_name", "pass_status_id", "pass_status_name", "pass_category_id",
					"pass_category_name", "pass_sub_category_id", "pass_sub_category_name", "modified_timestamp",
					"remarks", "salutation_id", "salutation_name", "marital_status_id", "marital_status_name",
					"synced");
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
			for (AggregatedPassData instance : data) {
				List<String> recordToInsert = new ArrayList<>();
				recordToInsert.add(instance.getId() + "");
				recordToInsert.add(instance.getPassHolderName());
				recordToInsert.add(instance.getControlNo());
				recordToInsert.add(instance.getControlHash());
				if (null != instance.getDob()) {
					recordToInsert.add(sdf.format(instance.getDob()));
				} else {
					recordToInsert.add(null);
				}
				recordToInsert.add(instance.getEventId() + "");
				recordToInsert.add(instance.getEventName());
				recordToInsert.add(instance.getEnclosureId() + "");
				recordToInsert.add(instance.getEnclosureName());
				recordToInsert.add(instance.getPassStatusId() + "");
				recordToInsert.add(instance.getPassStatusName());
				recordToInsert.add(instance.getPassCategoryId() + "");
				recordToInsert.add(instance.getPassCategoryName());
				recordToInsert.add(instance.getPassSubCategoryId() + "");
				recordToInsert.add(instance.getPassSubCategoryName());
				recordToInsert.add(null);
				recordToInsert.add(instance.getRemarks());
				recordToInsert.add(instance.getSalutationId() + "");
				recordToInsert.add(instance.getSalutationName());
				recordToInsert.add(instance.getMaritalStatusId() + "");
				recordToInsert.add(instance.getMaritalStatusName());
				recordToInsert.add(instance.isSynced() + "");
				printer.printRecord(recordToInsert);
			}
			printer.flush();
			printer.close();

			/*
			 * String outputFilePath = "csvFileWithHeaders.csv"; BufferedWriter
			 * bufferedWriter = Files.newBufferedWriter(Paths.get(outputFilePath)); try
			 * (CSVPrinter csvPrinter = CSVFormat.DEFAULT.withHeader("id",
			 * "pass_holder_name", "control_no",
			 * "control_hash","dob","event_id","event_name","enclosure_id","enclosure_name"
			 * ,"pass_status_id","pass_status_name","pass_category_id","pass_category_name",
			 * "pass_sub_category_id","pass_sub_category_name","modified_timestamp"
			 * ,"remarks","salutation_id","salutation_name","marital_status_id",
			 * "marital_status_name") .print(bufferedWriter)) { for (AggregatedPassData
			 * instance : data) { csvPrinter.printRecord(instance); } }
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}

		String desFileName = "enclosure.csv";
		Path path = Paths.get(desFileName);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		if (data.size() > 0) {
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path + "\"")
					.body(resource);
		} else {
			return ResponseEntity.badRequest().body("");
		}

	}

	@GetMapping("/getAllAnnexurePasses/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	ImsResponse getAllAnnexurePasses(@PathVariable("eventId") int eventId) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passes = passRepository.findByEventIdAndDeleted(eventId, false);
		List<Pass> filteredPasses = new ArrayList<>();
		for (Pass pass : passes) {
			if (pass.getImsUserByImsUserId() != null) {
				if (pass.getImsUserByImsUserId().getUserType().getId() != ImsUserTypeEnum.Organizer.type
						&& pass.getImsUserByImsUserId().getUserType().getId() != ImsUserTypeEnum.PublicUser.type
						&& pass.getImsUserByImsUserId().getUserType().getId() != ImsUserTypeEnum.WoPUser.type) {
					filteredPasses.add(pass);
				}
			}
		}
		imsResponse.setData(sanitizeUserData(filteredPasses));
		imsResponse.setMessage("Retrieved Passes for the Event!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@PostMapping("/assignWopPass")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse assignWopPass(@RequestBody List<Pass> passes,
			@RequestHeader(name = "Authorization") String token) {
		int passSeq = passRepository.getLastIdOfPass();
		int carPassSeq = carPassRepository.getLastIdOfPass();
		AtomicReference<Integer> passSeqAI = new AtomicReference<Integer>(passSeq);
		AtomicReference<Integer> carPassSeqAI = new AtomicReference<Integer>(carPassSeq);
		String loggedInRole = jwtUtils.getRoleFromJwtToken(token);
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> uniquePasses = new ArrayList<>();
		HashMap<Integer, Boolean> assignResult = new HashMap<Integer, Boolean>();

		passes.forEach(pass -> {
			if (pass.getImsUserByImsUserId() == null && pass.getAnnexUsers() != null) {
				AnnexUsers annexUser = annexUsersRepository.findByIdAndDeleted(pass.getAnnexUsers().getId(), false);
				ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(annexUser.getMobileNo(), false);
				pass.setImsUserByImsUserId(imsUser);
			}
			if (pass.getImsUserByImsUserId() != null && pass.getAnnexUsers() != null) {
				Pass dbPass = passRepository.findByImsUserByImsUserIdIdAndEventIdAndDeleted(
						pass.getImsUserByImsUserId().getId(), pass.getEvent().getId(), false);
				if (dbPass == null) {
					Event event = eventRepository.findById(pass.getEvent().getId()).get();
					if (loggedInRole != null && loggedInRole.equalsIgnoreCase(RoleEnum.ROLE_SUPERADMIN.name())) {
						pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
						if (smsEnabled) {
							ImsSMS sms = new ImsSMS();
							ImsUser imsUser = imsUserRepository.findByIdAndDeleted(pass.getImsUserByImsUserId().getId(), false);
							sms.setMobileNo(imsUser.getMobileNo());
							try {
								InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_INVITE_TEMPLATE);
								String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
								inputStream.close();
								SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
								otpMailText = otpMailText.replace("<<EVENT_NAME>>", event.getName());
								otpMailText = otpMailText.replace("<<EVENT_DAY_AND_DATE>>",
										newFormat.format(event.getDate()));
								otpMailText = otpMailText.replace("<<EVENT_VENUE>>", event.getVenue().getName());
								otpMailText = otpMailText.replace("<<EVENT_TIME>>", "");
								sms.setMessage(otpMailText);
							} catch (IOException ioe) {
								System.out.println("Error while reading OTP templates : " + ioe.getMessage());
							}
							sms.setTemplateId(eventInvitationTemplate);
							imsSmsSender.send(sms);
							emailService.sendEventInvitation(imsUser.getEmail(),
									"IMS Portal - Event Invitation ", event.getName(), event.getDate(),
									event.getVenue().getName());
						}
					} else
						pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.PENDING_APPROVAL.type).get());
					passSeqAI.set(passSeqAI.get() + 1);
					String controlNo = generateControlNo(pass, passSeqAI.get());
					String salt = ImsCipherUtil.generateSalt();
					pass.setControlNo(controlNo);
					// Car Pass - Start
					if (pass.getCarPass() != null && pass.getCarPass().getCpl() != null) {
						CarPass carPass = new CarPass();
						String carSalt = ImsCipherUtil.generateSalt();
						carPass.setControlSalt(carSalt);
						carPass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
						carPass.setCpl(cplRepository.findById(pass.getCarPass().getCpl().getId()).get());
						carPass.setPassCategory(passCategoryRepository.findById(PassCategoryEnum.CPL.type).get());
						carPass.setEvent(event);
						carPassSeqAI.set(carPassSeqAI.get() + 1);
						String carControlNo = generateCPLControlNo(carPass, carPassSeqAI.get());
						carPass.setControlNo(carControlNo);
						carPass.setControlHash(ImsCipherUtil.generateHash(carControlNo, carSalt));
						pass.setCarPass(carPassRepository.saveAndFlush(carPass));
					}
					// Car Pass - End
					pass.setControlHash(ImsCipherUtil.generateHash(controlNo, salt));
					pass.setControlSalt(salt);
					pass.setCreatedTimestamp(LocalDateTime.now());
					uniquePasses.add(pass);
					assignResult.put(pass.getImsUserByImsUserId().getId(), true);
				} else {
					assignResult.put(dbPass.getImsUserByImsUserId().getId(), false);
				}
			}
		});
		super.createAll(uniquePasses);
		imsResponse.setData(new Pass());
		imsResponse.setData(assignResult);
		if (uniquePasses.size() != passes.size()) {
			imsResponse.setMessage("WoP Pass already exists for few users");
			System.out.println("***************WoP Pass already exists for few users******************");
			log.saveLog(null, "WoP Pass already exists for few users", "ASSIGN_PASS", LogLevelEnum.INFO);
			imsResponse.setSuccess(false);
		} else {
			imsResponse.setMessage("Assigned WoP Pass!");
			System.out.println("***************Assigned WoP Pass ******************");
			log.saveLog(null, "Assigned WoP Pass!", "ASSIGN_PASS", LogLevelEnum.INFO);
			imsResponse.setSuccess(true);
		}

		return imsResponse;
	}

	@PostMapping("/assignOrganizerPass")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse assignOrganizerPass(@RequestBody List<Pass> passes,
			@RequestHeader(name = "Authorization") String token) {
		int passSeq = passRepository.getLastIdOfPass();
		int carPassSeq = carPassRepository.getLastIdOfPass();
		AtomicReference<Integer> passSeqAI = new AtomicReference<Integer>(passSeq);
		AtomicReference<Integer> carPassSeqAI = new AtomicReference<Integer>(carPassSeq);
		String loggedInRole = jwtUtils.getRoleFromJwtToken(token);
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> uniquePasses = new ArrayList<>();
		HashMap<Integer, Boolean> assignResult = new HashMap<Integer, Boolean>();
		passes.forEach(pass -> {

			if (pass.getImsUserByImsUserId() == null && pass.getAnnexUsers() != null) {
				AnnexUsers annexUser = annexUsersRepository.findByIdAndDeleted(pass.getAnnexUsers().getId(), false);
				ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(annexUser.getMobileNo(), false);
				pass.setImsUserByImsUserId(imsUser);
			}
			if (pass.getImsUserByImsUserId() != null && pass.getAnnexUsers() != null) {
				Pass dbPass = passRepository.findByImsUserByImsUserIdIdAndEventIdAndDeleted(
						pass.getImsUserByImsUserId().getId(), pass.getEvent().getId(), false);
				if (dbPass == null) {
					Event event = eventRepository.findById(pass.getEvent().getId()).get();
					if (loggedInRole != null && loggedInRole.equalsIgnoreCase(RoleEnum.ROLE_SUPERADMIN.name())) {
						pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
						if (smsEnabled) {
							ImsSMS sms = new ImsSMS();
							ImsUser imsUser = imsUserRepository.findByIdAndDeleted(pass.getImsUserByImsUserId().getId(), false);
							sms.setMobileNo(imsUser.getMobileNo());
							try {
								InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_INVITE_TEMPLATE);
								String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
								inputStream.close();
								SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
								otpMailText = otpMailText.replace("<<EVENT_NAME>>", event.getName());
								otpMailText = otpMailText.replace("<<EVENT_DAY_AND_DATE>>",
										newFormat.format(event.getDate()));
								otpMailText = otpMailText.replace("<<EVENT_VENUE>>", event.getVenue().getName());
								otpMailText = otpMailText.replace("<<EVENT_TIME>>", "");
								sms.setMessage(otpMailText);
							} catch (IOException ioe) {
								System.out.println("Error while reading OTP templates : " + ioe.getMessage());
							}
							sms.setTemplateId(eventInvitationTemplate);
							imsSmsSender.send(sms);
							emailService.sendEventInvitation(imsUser.getEmail(),
									"IMS Portal - Event Invitation ", event.getName(), event.getDate(),
									event.getVenue().getName());
						}
					} else
						pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.PENDING_APPROVAL.type).get());
					passSeqAI.set(passSeqAI.get() + 1);
					String controlNo = generateControlNo(pass, passSeqAI.get());
					String salt = ImsCipherUtil.generateSalt();
					pass.setControlNo(controlNo);
					// Car Pass - Start
					if (pass.getCarPass() != null && pass.getCarPass().getCpl() != null) {
						CarPass carPass = new CarPass();
						String carSalt = ImsCipherUtil.generateSalt();
						carPass.setControlSalt(carSalt);
						carPass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
						carPass.setCpl(cplRepository.findById(pass.getCarPass().getCpl().getId()).get());
						carPass.setPassCategory(passCategoryRepository.findById(PassCategoryEnum.CPL.type).get());
						carPass.setEvent(event);
						carPassSeqAI.set(carPassSeqAI.get() + 1);
						String carControlNo = generateCPLControlNo(carPass, carPassSeqAI.get());
						carPass.setControlNo(carControlNo);
						carPass.setControlHash(ImsCipherUtil.generateHash(carControlNo, carSalt));
						pass.setCarPass(carPassRepository.saveAndFlush(carPass));
					}
					// Car Pass - End
					pass.setControlHash(ImsCipherUtil.generateHash(controlNo, salt));
					pass.setControlSalt(salt);
					pass.setCreatedTimestamp(LocalDateTime.now());
					uniquePasses.add(pass);
					assignResult.put(pass.getImsUserByImsUserId().getId(), true);
				} else {
					assignResult.put(dbPass.getImsUserByImsUserId().getId(), false);
				}
			}

		});
		super.createAll(uniquePasses);
		imsResponse.setData(new Pass());
		imsResponse.setData(assignResult);
		if (uniquePasses.size() != passes.size()) {
			imsResponse.setMessage("Organizer Pass already exists for few users");
			System.out.println("***************Organizer Pass already exists for few users ******************");
			log.saveLog(null, "Organizer Pass already exists for few users", "ASSIGN_PASS", LogLevelEnum.INFO);
			imsResponse.setSuccess(false);
		} else {
			imsResponse.setMessage("Assigned Pass to the Organizer!");
			System.out.println("***************Assigned Pass to the Organizer******************");
			log.saveLog(null, "Assigned Pass to the Organizer!", "ASSIGN_PASS", LogLevelEnum.INFO);
			imsResponse.setSuccess(true);
		}

		return imsResponse;
	}

	@PostMapping("/assignIndividualPass")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse assignIndividualPass(@RequestBody List<Pass> passes,
			@RequestHeader(name = "Authorization") String token) {
		int passSeq = passRepository.getLastIdOfPass();
		int carPassSeq = carPassRepository.getLastIdOfPass();
		AtomicReference<Integer> passSeqAI = new AtomicReference<Integer>(passSeq);
		AtomicReference<Integer> carPassSeqAI = new AtomicReference<Integer>(carPassSeq);
		String loggedInRole = jwtUtils.getRoleFromJwtToken(token);
		ImsResponse imsResponse = new ImsResponse();
		
		passes.forEach(pass -> {
			if (pass.getImsUserByImsUserId() == null && pass.getAnnexUsers() != null) {
				AnnexUsers annexUser = annexUsersRepository.findByIdAndDeleted(pass.getAnnexUsers().getId(), false);
				ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(annexUser.getMobileNo(), false);
				pass.setImsUserByImsUserId(imsUser);
			}
			Event event = eventRepository.findById(pass.getEvent().getId()).get();
			if (loggedInRole != null && loggedInRole.equalsIgnoreCase(RoleEnum.ROLE_INVITATIONADMIN.name()))
				pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.PENDING_APPROVAL.type).get());
			else {
				pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
				if (smsEnabled) {
					ImsSMS sms = new ImsSMS();
					ImsUser imsUser = imsUserRepository.findByIdAndDeleted(pass.getImsUserByImsUserId().getId(), false);
					sms.setMobileNo(imsUser.getMobileNo());
					try {
						InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_INVITE_TEMPLATE);
						String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
						inputStream.close();
						SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
						otpMailText = otpMailText.replace("<<EVENT_NAME>>", event.getName());
						otpMailText = otpMailText.replace("<<EVENT_DAY_AND_DATE>>",
								newFormat.format(event.getDate()));
						otpMailText = otpMailText.replace("<<EVENT_VENUE>>", event.getVenue().getName());
						otpMailText = otpMailText.replace("<<EVENT_TIME>>", "");
						sms.setMessage(otpMailText);
					} catch (IOException ioe) {
						System.out.println("Error while reading OTP templates : " + ioe.getMessage());
					}
					sms.setTemplateId(eventInvitationTemplate);
					imsSmsSender.send(sms);
					emailService.sendEventInvitation(imsUser.getEmail(),
							"IMS Portal - Event Invitation ", event.getName(), event.getDate(),
							event.getVenue().getName());
				}
			}
			passSeqAI.set(passSeqAI.get() + 1);
			String controlNo = generateControlNo(pass, passSeqAI.get());
			String salt = ImsCipherUtil.generateSalt();
			pass.setControlNo(controlNo);

			PassSubcategory passSubcategory = passSubcategoryRepository.findById(pass.getPassSubcategory().getId())
					.get();
			pass.setPassCategory(passSubcategory.getPassCategory());
			

			// Car Pass - Start
			if (pass.getCarPass() != null && pass.getCarPass().getCpl() != null) {
				CarPass carPass = new CarPass();
				String carSalt = ImsCipherUtil.generateSalt();
				carPass.setControlSalt(carSalt);
				carPass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
				carPass.setCpl(cplRepository.findById(pass.getCarPass().getCpl().getId()).get());
				carPass.setPassCategory(passCategoryRepository.findById(PassCategoryEnum.CPL.type).get());
				carPass.setEvent(event);
				carPassSeqAI.set(carPassSeqAI.get() + 1);
				String carControlNo = generateCPLControlNo(carPass, carPassSeqAI.get());
				carPass.setControlNo(carControlNo);
				carPass.setControlHash(ImsCipherUtil.generateHash(carControlNo, carSalt));
				pass.setCarPass(carPassRepository.save(carPass));
			}
			// Car Pass - End
			pass.setControlHash(ImsCipherUtil.generateHash(controlNo, salt));
			pass.setControlSalt(salt);
			pass.setCreatedTimestamp(LocalDateTime.now());
		});
		super.createAll(passes);
		imsResponse.setData(new Pass());

		imsResponse.setMessage("Assigned Pass to the User!");
		System.out.println("***************Pass with the control no has been assigned to the User ******************");
		log.saveLog(null, "Pass with the control no has been assigned to the User", "ASSIGN_PASS", LogLevelEnum.INFO);
		return imsResponse;
	}

	@PostMapping("/assignBulkPass")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse assignBulkPass(@RequestBody PassRequest passRequest) {
		ImsResponse imsResponse = new ImsResponse();
		if (passRequest != null && passRequest.getNoOfPassesInEnclosure() != null
				&& !passRequest.getNoOfPassesInEnclosure().isEmpty()) {
			int requsetedBulkPassCount = passRequest.getNoOfPassesInEnclosure().values().stream()
					.mapToInt(Integer::intValue).sum();
			if (requsetedBulkPassCount > 500) {
				imsResponse.setMessage("Maximum 500 bulk passes allowed!");
				imsResponse.setSuccess(false);
				return imsResponse;
			}

			if (passRequest.getNoOfCarPassesInCPL() != null && !passRequest.getNoOfCarPassesInCPL().isEmpty()) {
				int requsetedCPLPassCount = passRequest.getNoOfCarPassesInCPL().values().stream()
						.mapToInt(Integer::intValue).sum();
				if (requsetedCPLPassCount > 500) {
					imsResponse.setMessage("Maximum 500 CPL passes allowed!");
					imsResponse.setSuccess(false);
					return imsResponse;
				}
			}
		}

		AtomicReference<String> eventName = new AtomicReference<>();
		AtomicReference<String> venueName = new AtomicReference<>();
		AtomicReference<Integer> bulkPassCount = new AtomicReference<>();
		AtomicReference<Date> date = new AtomicReference<>();

		Event event = eventRepository.findById(passRequest.getEventId()).get();
		Department department = departmentRepository.findById(passRequest.getDepartmentId()).get();
		PassCategory passCategory = passCategoryRepository.findById(passRequest.getPassCategory()).get();
		PassSubcategory passSubcategory = passSubcategoryRepository.findById(passRequest.getPassSubcategory()).get();
		PassStatus initiatedPassStatus = passStatusRepository.findById(PassStatusEnum.INITIATED.type).get();
		PassCategory cplPassCategory = passCategoryRepository.findById(PassCategoryEnum.CPL.type).get();
		passRequest.getNoOfPassesInEnclosure().forEach((enclosurId, passCount) -> {
			Enclosure enclosure = enclosureRepository.findById(enclosurId).get();
			bulkPassCount.set(passCount);
			List<Pass> passList = Stream.generate(Pass::new).limit(passCount).collect(Collectors.toList());
			passList.forEach(pass -> {
				pass.setEvent(event);
				eventName.set(event.getName());
				venueName.set(event.getVenue().getName());
				date.set(event.getDate());
				pass.setDepartment(department);
				pass.setPassCategory(passCategory);
				pass.setPassSubcategory(passSubcategory);
				pass.setPassSubcategory(new PassSubcategory(passRequest.getPassSubcategory()));
				pass.setPassStatus(initiatedPassStatus);
				pass.setEnclosure(enclosure);
				pass.setCreatedTimestamp(LocalDateTime.now());
			});
			super.createAll(passList);
			imsResponse.setData(new Pass());
		});

		if (passRequest.getNoOfCarPassesInCPL() != null) {
			passRequest.getNoOfCarPassesInCPL().forEach((cplId, passCount) -> {
				List<CarPass> carPassList = Stream.generate(CarPass::new).limit(passCount).collect(Collectors.toList());
				Cpl cpl = cplRepository.findById(cplId).get();
				carPassList.forEach(pass -> {
					pass.setCpl(cpl);
					pass.setEvent(event);
					pass.setPassCategory(cplPassCategory);
					pass.setDepartment(department);
					pass.setPassStatus(initiatedPassStatus);
					pass.setEvent(event);
					pass.setCreatedTimestamp(LocalDateTime.now());
				});
				imsResponse.setData(carPassRepository.saveAll(carPassList));
			});
		}

		List<ImsUser> userList = imsUserRepository.findAllByRoleIdAndDepartmentId(RoleEnum.ROLE_NODALOFFICER.role,
				passRequest.getDepartmentId());
		if (userList != null) {
			Notification notification = new Notification();
			notification.setMessage("Bulk Pass Allocated for the event " + eventName.get() + " for the date "
					+ date.get() + " with Venue  " + venueName.get() + " with count " + bulkPassCount.get());
			notification.setTitle("Pass Allocated");
			notification.setDate(new Date());
			notification.setRead(false);
			userList.forEach(imsUser -> {
				notification.setImsUser(imsUser);
				notificationRepository.save(notification);
				webSocketClient.sendNotification(NotificationUtils.WS_TOPIC + imsUser.getMobileNo(), notification);
				log.saveLog(null,
						"A notification has been sent to the user mobile " + imsUser.getMobileNo()
								+ " regarding bulk pass allocation for the event " + eventName.get() + " with count "
								+ bulkPassCount.get(),
						"NOTIFICATION", LogLevelEnum.INFO);
			});

		}
		System.out.println("**************Bulk Pass Allocated for the event " + eventName.get() + " for the date " + date.get() + " with Venue  " + venueName.get() + " with count " + bulkPassCount.get() + "at " + LocalDateTime.now() + "******************");
		log.saveLog(
				null, "Bulk Pass Allocated for the event " + eventName.get() + " for the date " + date.get()
						+ " with Venue  " + venueName.get() + " with count " + bulkPassCount.get(),
				"ASSIGN_PASS", LogLevelEnum.INFO);
		imsResponse.setMessage("Allocated Bulk Passes!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@PostMapping("/approvePasses")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	ImsResponse approvePasses(@RequestBody PassRequest passRequest) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passList = passRepository.findAllById(passRequest.getPassIds());
		PassStatus passStatus = passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get();
		Event event = eventRepository.findById(passList.stream().map(pass -> pass.getEvent().getId()).findAny().get()).get();
		passList.forEach(pass -> {
			pass.setPassStatus(passStatus);
			
			if (pass.getImsUserByImsUserId() == null && pass.getAnnexUsers() != null) {
				AnnexUsers annexUser = annexUsersRepository.findByIdAndDeleted(pass.getAnnexUsers().getId(), false);
				ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(annexUser.getMobileNo(), false);
				pass.setImsUserByImsUserId(imsUser);
			}
			
			if (smsEnabled) {
				ImsSMS sms = new ImsSMS();
				ImsUser imsUser = imsUserRepository.findByIdAndDeleted(pass.getImsUserByImsUserId().getId(), false);
				sms.setMobileNo(imsUser.getMobileNo());
				try {
					InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_INVITE_TEMPLATE);
					String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
					inputStream.close();
					SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
					otpMailText = otpMailText.replace("<<EVENT_NAME>>", event.getName());
					otpMailText = otpMailText.replace("<<EVENT_DAY_AND_DATE>>",
							newFormat.format(event.getDate()));
					otpMailText = otpMailText.replace("<<EVENT_VENUE>>", event.getVenue().getName());
					otpMailText = otpMailText.replace("<<EVENT_TIME>>", "");
					sms.setMessage(otpMailText);
				} catch (IOException ioe) {
					System.out.println("Error while reading OTP templates : " + ioe.getMessage());
				}
				sms.setTemplateId(eventInvitationTemplate);
				imsSmsSender.send(sms);
				emailService.sendEventInvitation(imsUser.getEmail(),
						"IMS Portal - Event Invitation ", event.getName(), event.getDate(),
						event.getVenue().getName());
			}
			System.out.println("***************Successfully Approved Pass with the control no " + pass.getControlNo() + "******************");
			log.saveLog(null, "Successfully Approved Pass with the control no " + pass.getControlNo(), "ASSIGN_PASS",
					LogLevelEnum.INFO);
			// Send SMS or mail to the end-user
		});
		super.createAll(passList);
		imsResponse.setMessage("Successfully Approved Passes!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@PostMapping("/assignGuestPass")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse assignGuestPass(@RequestBody List<GuestPassRequest> guestPassRequests) {
		ImsResponse imsResponse = new ImsResponse();
		try {
			List<Pass> passes = new ArrayList<>();
			Event event = eventRepository.findById(
					guestPassRequests.stream().map(guestPassRequest -> guestPassRequest.getEventId()).findAny().get())
					.get();
			guestPassRequests.forEach(guestPassRequest -> {
				AnnexUsers annexdUser = annexUsersRepository.findById(guestPassRequest.getAnnexUserId()).get();
				ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(annexdUser.getMobileNo(), false);
				Pass pass = passRepository.findByDepartmentIdAndDeleted(guestPassRequest.getDepartmentId(), false)
						.stream()
						.filter(guestPass -> (guestPass.getPassStatus().getId() == PassStatusEnum.INITIATED.type)
								&& (guestPass.getEnclosure().getId() == guestPassRequest.getEnclosureId())
								&& (guestPass.getPassSubcategory().getId() == guestPassRequest.getPassSubcategoryId())
								&& (guestPassRequest.getEventId() == guestPass.getEvent().getId()))
						.findAny().get();
				if (guestPassRequest.getCplId() != 0) {
					CarPass carPass = carPassRepository.findByDepartmentId(guestPassRequest.getDepartmentId()).stream()
							.filter(guestCarPass -> (guestCarPass.getPassStatus()
									.getId() == PassStatusEnum.INITIATED.type)
									&& (guestCarPass.getCpl().getId() == guestPassRequest.getCplId())
									&& (guestPassRequest.getEventId() == guestCarPass.getEvent().getId()))
							.findAny().get();
					carPass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
					String carSalt = ImsCipherUtil.generateSalt();
					carPass.setControlSalt(carSalt);
					String carControlNo = generateCPLControlNo(carPass, carPass.getId());
					carPass.setControlNo(carControlNo);
					carPass.setControlHash(ImsCipherUtil.generateHash(carControlNo, carSalt));
					carPassRepository.save(carPass);
					pass.setCarPass(carPass);
				}
				pass.setPassStatus(new PassStatus(AllocationStatusEnum.ALLOCATED.status));
				String controlNo = generateControlNo(pass, pass.getId());
				String salt = ImsCipherUtil.generateSalt();
				pass.setControlNo(controlNo);
				pass.setPassHolderName(guestPassRequest.getDisplayName());
				pass.setPassSubcategory(
						passSubcategoryRepository.findByIdAndDeleted(guestPassRequest.getPassSubcategoryId(), false));
				pass.setControlHash(ImsCipherUtil.generateHash(controlNo, salt));
				pass.setImsUserByImsUserId(imsUser);
				pass.setAnnexUsers(annexdUser);
				pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
				pass.setControlSalt(salt);
				pass.setCreatedTimestamp(LocalDateTime.now());
				passes.add(pass);
				if (smsEnabled) {
					ImsSMS sms = new ImsSMS();
					sms.setMobileNo(pass.getImsUserByImsUserId().getMobileNo());
					try {
						InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_INVITE_TEMPLATE);
						String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
						inputStream.close();
						SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
						otpMailText = otpMailText.replace("<<EVENT_NAME>>", event.getName());
						otpMailText = otpMailText.replace("<<EVENT_DAY_AND_DATE>>", newFormat.format(event.getDate()));
						otpMailText = otpMailText.replace("<<EVENT_VENUE>>", event.getVenue().getName());
						otpMailText = otpMailText.replace("<<EVENT_TIME>>", "");
						sms.setMessage(otpMailText);
					} catch (IOException ioe) {
						System.out.println("Error while reading OTP templates : " + ioe.getMessage());
					}
					sms.setTemplateId(eventInvitationTemplate);
					imsSmsSender.send(sms);
					emailService.sendEventInvitation(imsUser.getEmail(),
							"IMS Portal - Event Invitation ", event.getName(), event.getDate(),
							event.getVenue().getName());
				}

			});
			super.createAll(passes);
			imsResponse.setData(new Pass());
			log.saveLog(null, "Pass with the control no has been assigned to the Guest", "ASSIGN_PASS",
					LogLevelEnum.INFO);
			System.out.println("***************Assigned Pass to the Guest! ******************");
			imsResponse.setMessage("Assigned Pass to the Guest!");
			imsResponse.setSuccess(true);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			imsResponse.setSuccess(false);
			System.out.println("***************Sufficient passes are not available to assign, Please contact System Admin/ Invitation admin! ******************");
			imsResponse.setMessage(
					"Sufficient passes are not available to assign, Please contact System Admin/ Invitation admin!");
		}
		return imsResponse;
	}

	@PostMapping("/assignBulkGuestPass")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse assignBulkGuestPass(@RequestBody AnnexPassRequest passRequest) {

		List<GuestPassRequest> guestPassRequests = new ArrayList<>();
		passRequest.getAnnexUsers().forEach(annexUser -> {
			GuestPassRequest guestPassRequest = new GuestPassRequest();
			AnnexUsers annexdUser = annexUsersRepository.findById(annexUser.getId()).get();
			String mobileNo = annexdUser.getMobileNo();
			ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);
			guestPassRequest.setAnnexUserId(annexdUser.getId());
			guestPassRequest.setImsUserId(imsUser.getId());
			guestPassRequest.setCplId(annexUser.getCplId());
			guestPassRequest.setEnclosureId(annexUser.getEnclosureId());
			guestPassRequest.setPassSubcategoryId(annexUser.getPassSubcategoryId());
			guestPassRequest.setEventId(passRequest.getEventId());
			guestPassRequest.setDepartmentId(passRequest.getDepartmentId());
			guestPassRequest.setDisplayName(annexdUser.getName());
			guestPassRequests.add(guestPassRequest);
		});
		ImsResponse imsResponse = new ImsResponse();
		Event event = eventRepository.findById(
				guestPassRequests.stream().map(guestPassRequest -> guestPassRequest.getEventId()).findAny().get())
				.get();
		try {
			List<Pass> passes = new ArrayList<>();
			guestPassRequests.forEach(guestPassRequest -> {

				ImsUser guestUser = imsUserRepository.findByIdAndDeleted(guestPassRequest.getImsUserId(), false);
				AnnexUsers annexUser = annexUsersRepository.findByIdAndDeleted(guestPassRequest.getAnnexUserId(),
						false);
				Pass pass = passRepository.findByDepartmentIdAndDeleted(guestPassRequest.getDepartmentId(), false)
						.stream()
						.filter(guestPass -> (guestPass.getPassStatus().getId() == PassStatusEnum.INITIATED.type)
								&& (guestPass.getEnclosure().getId() == guestPassRequest.getEnclosureId())
								&& (guestPassRequest.getEventId() == guestPass.getEvent().getId()))
						.findAny().get();
				if (guestPassRequest.getCplId() != 0) {
					CarPass carPass = carPassRepository.findByDepartmentId(guestPassRequest.getDepartmentId()).stream()
							.filter(guestCarPass -> (guestCarPass.getPassStatus()
									.getId() == PassStatusEnum.INITIATED.type)
									&& (guestCarPass.getCpl().getId() == guestPassRequest.getCplId())
									&& (guestPassRequest.getEventId() == guestCarPass.getEvent().getId()))
							.findAny().get();
					carPass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
					String carSalt = ImsCipherUtil.generateSalt();
					carPass.setControlSalt(carSalt);
					String carControlNo = generateCPLControlNo(carPass, carPass.getId());
					carPass.setControlNo(carControlNo);
					carPass.setControlHash(ImsCipherUtil.generateHash(carControlNo, carSalt));
					carPassRepository.save(carPass);
					pass.setCarPass(carPass);
				}
				pass.setPassStatus(new PassStatus(AllocationStatusEnum.ALLOCATED.status));
				String controlNo = generateControlNo(pass, pass.getId());
				String salt = ImsCipherUtil.generateSalt();
				pass.setControlNo(controlNo);
				pass.setPassHolderName(guestPassRequest.getDisplayName());
				pass.setControlHash(ImsCipherUtil.generateHash(controlNo, salt));
				pass.setImsUserByImsUserId(guestUser);
				pass.setPassSubcategory(
						passSubcategoryRepository.findByIdAndDeleted(guestPassRequest.getPassSubcategoryId(), false));
				pass.setAnnexUsers(annexUser);
				pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
				pass.setControlSalt(salt);
				pass.setCreatedTimestamp(LocalDateTime.now());
				passes.add(pass);

				if (smsEnabled) {
					ImsSMS sms = new ImsSMS();
					sms.setMobileNo(pass.getImsUserByImsUserId().getMobileNo());
					try {
						InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_INVITE_TEMPLATE);
						String otpMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
						inputStream.close();
						SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
						otpMailText = otpMailText.replace("<<EVENT_NAME>>", event.getName());
						otpMailText = otpMailText.replace("<<EVENT_DAY_AND_DATE>>", newFormat.format(event.getDate()));
						otpMailText = otpMailText.replace("<<EVENT_VENUE>>", event.getVenue().getName());
						otpMailText = otpMailText.replace("<<EVENT_TIME>>", "");
						sms.setMessage(otpMailText);
					} catch (IOException ioe) {
						System.out.println("Error while reading OTP templates : " + ioe.getMessage());
					}
					sms.setTemplateId(eventInvitationTemplate);
					imsSmsSender.send(sms);
					emailService.sendEventInvitation(pass.getImsUserByImsUserId().getEmail(),
							"IMS Portal - Event Invitation ", event.getName(), event.getDate(),
							event.getVenue().getName());
				}
			});
			super.createAll(passes);
			imsResponse.setData(new Pass());
			System.out.println("***************Bulk Passes assigned to the Guests! ******************");
			log.saveLog(null, "Pass with the control no has been assigned to the Guest", "ASSIGN_PASS",
					LogLevelEnum.INFO);
			imsResponse.setMessage("Assigned Pass to the Guest!");
			imsResponse.setSuccess(true);
		} catch (NoSuchElementException e) {
			imsResponse.setSuccess(false);
			System.out.println("***************Sufficient passes are not available to assign, Please contact System Admin/ Invitation admin! ******************");
			imsResponse.setMessage(
					"Sufficient passes are not available to assign, Please contact System Admin/ Invitation admin!");
		}
		return imsResponse;
	}

	@PostMapping("/allotAdmitCard")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse allotAdmitCard(@RequestBody AdmitCardRequest admitCardRequest) {
		int passSeq = passRepository.getLastIdOfPass();
		AtomicReference<Integer> passSeqAI = new AtomicReference<Integer>(passSeq);
		ImsResponse imsResponse = new ImsResponse();
		PassCategory passCategory = passCategoryRepository.findById(admitCardRequest.getPassCategory()).get();
		PassSubcategory passSubcategory = passSubcategoryRepository.findById(admitCardRequest.getPassSubcategory())
				.get();
		admitCardRequest.getNoOfAdmitCardsToEnclosure().forEach((enclosurId, admiCardCount) -> {
			List<Pass> passList = Stream.generate(Pass::new).limit(admiCardCount).collect(Collectors.toList());
			Enclosure enclosure = enclosureRepository.findById(enclosurId).get();
			Event event = eventRepository.findById(admitCardRequest.getEventId()).get();
			passList.forEach(pass -> {
				passSeqAI.set(passSeqAI.get() + 1);
				String controlNo = generateControlNoForAdmitCard(pass, passSeqAI.get(), event, enclosure);
				String salt = ImsCipherUtil.generateSalt();
				pass.setControlNo(controlNo);
				pass.setEvent(event);
				pass.setEnclosure(enclosure);
				pass.setPassCategory(passCategory);
				pass.setPassSubcategory(passSubcategory);
				pass.setControlHash(ImsCipherUtil.generateHash(controlNo, salt));
				pass.setControlSalt(salt);
				pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
				pass.setCreatedTimestamp(LocalDateTime.now());
			});

			super.createAll(passList);
			imsResponse.setData(new Pass());
		});
		log.saveLog(null, "Generated Admit Cards", "ASSIGN_PASS", LogLevelEnum.INFO);
		System.out.println("**************Alloted Admit Cards! ******************");
		imsResponse.setMessage("Generated Admit Cards!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/downloadPass/{passCategoryId}/{controlNo}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('HIGHEROFFICER') or hasRole('INVITATIONADMIN') or hasRole('INVITEE')  or hasRole('CITIZEN') or hasRole('COUNTEREMP')")
	public ResponseEntity<Resource> generatePass(@PathVariable int passCategoryId, @PathVariable String controlNo,
			@RequestHeader(name = "Authorization") String token) {

		Pass passCancelled = passRepository.findByControlNoAndDeleted(controlNo, false);
		if(passCancelled != null && passCancelled.getPassStatus().getId() == PassStatusEnum.CANCELLED.type) {
			return null;
		}
		String role = jwtUtils.getRoleFromJwtToken(token);
		if (role != null && (role.contains("CITIZEN") || role.contains("INVITEE"))) {
			String mobileNo = jwtUtils.getUserNameFromJwtToken(token);
			List<Pass> passList;

			if (role.contains("CITIZEN"))
				passList = passRepository.findByImsUserByImsUserIdMobileNoAndPassStatusId(mobileNo,
						PassStatusEnum.ALLOCATED.type);
			else
				passList = passRepository.findByImsUserByImsUserIdMobileNo(mobileNo);

			if (passList != null && !passList.isEmpty()) {

				long passFoundCount = passList.stream()
						.filter(pass -> (pass.getControlNo() != null && pass.getControlNo().equals(controlNo))).count();
				long carPassFoundCount = passList.stream()
						.filter(pass -> pass.getCarPass() != null && pass.getCarPass().getControlNo().equals(controlNo))
						.count();
				if ((passFoundCount + carPassFoundCount) <= 0)
					return null;
			} else {
				return null;
			}

		} else if(role != null && role.contains("NODALOFFICER")){
			String mobileNo = jwtUtils.getUserNameFromJwtToken(token);
			ImsUser imsUser = imsUserRepository.findByMobileNo(mobileNo);
			if(imsUser!=null) {
				List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByImsUserId(imsUser.getId());
				List<Integer> departments = nodalOfficers.stream().map(nodalOfficer -> nodalOfficer.getDepartment().getId())
						.collect(Collectors.toList());
				Pass pass = passRepository.findByControlNoAndDeleted(controlNo, false);
				if(null != pass && !departments.contains(pass.getDepartment().getId())) {
					return null;
				}
			}else
				return null;
        }
		JasperReport jasperReport = null;
		Path path = null;
		Resource resource = null;
		if (passCategoryId != PassCategoryEnum.CPL.type) {
			Pass pass = passRepository.findByControlNoAndDeleted(controlNo, false);

			Date eventDate = pass.getEvent().getDate();
			Calendar eventCalendar = Calendar.getInstance();
			eventCalendar.setTime(eventDate);
			InputStream sourceFileName = getPassTemplateSourceFile(pass);
			String fileName = controlNo + ".pdf";
			path = Paths.get(fileName);
			// Resource resource = null;
			try {
				jasperReport = JasperCompileManager.compileReport(sourceFileName);
				Map<String, Object> params = new HashMap<String, Object>();
				if (pass.getPassCategory().getId() == PassCategoryEnum.PAIDTICKET.type) {
					params.put("name", pass.getPaidPassHolders().getName());
				} else if (pass.getPassCategory().getId() == PassCategoryEnum.GUESTPASS.type) {
					params.put("name",
							((pass.getAnnexUsers().getSalutation() != null)
									? pass.getAnnexUsers().getSalutation().getName()
									: "") + StringUtils.SPACE
									+ ((pass.getPassHolderName() != null) ? pass.getPassHolderName()
											: pass.getAnnexUsers().getName()));
				} else {

					if (pass.getImsUserByImsUserId().getUserType().getId() == ImsUserTypeEnum.Organizer.type) {
						

						if (pass.getImsUserByImsUserId().getMaritalStatus() != null && pass.getImsUserByImsUserId()
								.getMaritalStatus().getId() == MaritalStatusEnum.Married.value)
							params.put("name",
									((pass.getImsUserByImsUserId().getSalutation() != null)
											? pass.getImsUserByImsUserId().getSalutation().getName()
											: "") + StringUtils.SPACE
											+ ((pass.getPassHolderName() != null) ? pass.getPassHolderName()
													: pass.getImsUserByImsUserId().getName())
											+ " & Spouse");
						else
							params.put("name",
									((pass.getImsUserByImsUserId().getSalutation() != null)
											? pass.getImsUserByImsUserId().getSalutation().getName()
											: "") + StringUtils.SPACE
											+ ((pass.getPassHolderName() != null) ? pass.getPassHolderName()
													: pass.getImsUserByImsUserId().getName()));

					}
					if (pass.getImsUserByImsUserId().getUserType().getId() == ImsUserTypeEnum.WoPUser.type) {
						int maritalStatus = 1;
						if(pass.getAnnexUsers() != null && pass.getAnnexUsers().getMaritalStatus() != null)
							maritalStatus = pass.getAnnexUsers().getMaritalStatus().getId();
						else if(pass.getImsUserByImsUserId() != null && pass.getImsUserByImsUserId().getMaritalStatus() != null)
							maritalStatus = pass.getImsUserByImsUserId().getMaritalStatus().getId();

						if (maritalStatus == MaritalStatusEnum.Married.value)
							params.put("name",
									((pass.getAnnexUsers().getSalutation() != null)
											? pass.getAnnexUsers().getSalutation().getName()
											: "") + StringUtils.SPACE
											+ ((pass.getPassHolderName() != null) ? pass.getPassHolderName()
													: pass.getAnnexUsers().getName())
											+ " & Spouse");
						else
							params.put("name",
									((pass.getAnnexUsers().getSalutation() != null)
											? pass.getAnnexUsers().getSalutation().getName()
											: "") + StringUtils.SPACE
											+ ((pass.getPassHolderName() != null) ? pass.getPassHolderName()
													: pass.getAnnexUsers().getName()));

					} else {
						int maritalStatus = 1;
						if(pass.getAnnexUsers() != null && pass.getAnnexUsers().getMaritalStatus() != null)
							maritalStatus = pass.getAnnexUsers().getMaritalStatus().getId();
						else if(pass.getImsUserByImsUserId() != null && pass.getImsUserByImsUserId().getMaritalStatus() != null)
							maritalStatus = pass.getImsUserByImsUserId().getMaritalStatus().getId();

						if (maritalStatus == MaritalStatusEnum.Married.value)
							params.put("name",
									((pass.getAnnexUsers().getSalutation() != null)
											? pass.getAnnexUsers().getSalutation().getName()
											: "") + StringUtils.SPACE
											+ ((pass.getPassHolderName() != null) ? pass.getPassHolderName()
													: pass.getAnnexUsers().getName())
											+ " & Spouse");
						else
							params.put("name",
									((pass.getAnnexUsers().getSalutation() != null)
											? pass.getAnnexUsers().getSalutation().getName()
											: "") + StringUtils.SPACE
											+ ((pass.getPassHolderName() != null) ? pass.getPassHolderName()
													: pass.getAnnexUsers().getName()));
					}
				}
				params.put("venue", pass.getEnclosure().getEnclosureGroup().getVenue().getName());
				params.put("day", eventCalendar.get(Calendar.DAY_OF_MONTH));
				params.put("year", eventCalendar.get(Calendar.YEAR));
				params.put("month", Month.of(eventCalendar.get(Calendar.MONTH) + 1).name());
				params.put("eventName", pass.getEvent().getName());
				params.put("enclosureGroup", pass.getEnclosure().getEnclosureGroup().getName());
				String encName = pass.getEnclosure().getName();
				String encNameSplit[] = encName.contains("-") ? encName.split("-") : encName.split(" ");
				params.put("bridge", "");
				params.put("gate", "");
				params.put("blockNo", "");
				if (pass.getEvent().getEventCode() != null && pass.getEvent().getEventCode().startsWith("BR")) {
					if (encNameSplit.length == 3) {
						params.put("bridge", encNameSplit[1]);
						params.put("blockNo", encNameSplit[1]);
						params.put("gate", encNameSplit[2]);
					} else if (encNameSplit.length == 2) {
						params.put("blockNo", encNameSplit[0]);
						params.put("gate", encNameSplit[1]);
					}
				} else {
					params.put("enclosureNo", encNameSplit[0]);
					if (encNameSplit.length == 3) {
						params.put("bridge", encNameSplit[1]);
						params.put("blockNo", encNameSplit[1]);
						params.put("gate", encNameSplit[2]);
					} else if (encNameSplit.length == 2) {
						params.put("blockNo", encNameSplit[1]);
					}
				}
				params.put("controlNo", pass.getControlNo());
				params.put("qrCode", new ByteArrayInputStream(getQRCodeImage(pass.getControlHash(), 250, 250)));
				params.put("qrCode1", new ByteArrayInputStream(getQRCodeImage(pass.getControlHash(), 250, 250)));
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
				JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);

//					ImsPDFMerger.mergePDF(new File(fileName), getClass().getResourceAsStream("/reports/Invite_Back.pdf"),
//							getClass().getResourceAsStream("/reports/Instruction_Slip.pdf"), fileName);

				resource = new UrlResource(path.toUri());

				System.out.println("***************Pass with the control no " + pass.getControlNo() + " downloaded successfully ******************");
				log.saveLog(null, "Pass with the control no " + pass.getControlNo() + " downloaded successfully",
						"DOWNLOAD", LogLevelEnum.INFO);
			} catch (JRException | MalformedURLException e) {
				// TODO Auto-generated catch block
				log.saveLog(null,
						"An error occurred while downloading the Pass with the control no " + pass.getControlNo(),
						"DOWNLOAD", LogLevelEnum.ERROR);
				System.out.println("***************An error occurred while downloading the Pass with the control no " + pass.getControlNo() + "******************");
				e.printStackTrace();
			}

			pass.setDownloaded(true);
			passRepository.save(pass);

		} else {
			CarPass carPass = carPassRepository.findByControlNoAndDeleted(controlNo, false);
			Pass pass = passRepository.findByCarPassIdAndDeleted(carPass.getId(), false);

			Date eventDate = carPass.getEvent().getDate();
			Event carPassEvent = eventRepository.findById(carPass.getEvent().getId()).get();
			Calendar eventCalendar = Calendar.getInstance();
			eventCalendar.setTime(eventDate);
			InputStream sourceFileName = getCarPassTemplateSourceFile(carPass, carPassEvent.getEventCode());
			String fileName = controlNo + ".pdf";
			path = Paths.get(fileName);
			// Resource resource = null;
			try {
				jasperReport = JasperCompileManager.compileReport(sourceFileName);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("vehicleNo", carPass.getVehNo());
				params.put("cplNo", carPass.getControlNo());
				params.put("passNo", " ");
				if (pass != null)
					params.put("passNo", pass.getControlNo());
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
				JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
				resource = new UrlResource(path.toUri());
				System.out.println("***************Car Pass downloaded successfully ******************");
				log.saveLog(null, "Pass with the control no " + carPass.getControlNo() + " downloaded successfully",
						"DOWNLOAD", LogLevelEnum.INFO);
			} catch (JRException | MalformedURLException e) {
				// TODO Auto-generated catch block
				log.saveLog(null,
						"An error occurred while downloading the Pass with the control no " + carPass.getControlNo(),
						"DOWNLOAD", LogLevelEnum.ERROR);
				System.out.println("***************An error occurred while downloading the Car Pass with the control no " + pass.getControlNo() + "******************");
				e.printStackTrace();
			}

		}

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path + "\"")
				.contentType(MediaType.APPLICATION_PDF).body(resource);

	}

	@GetMapping("/downloadAdmitCardsEnclosureWise/{eventId}/{enclosureId}/{subcategoryId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ResponseEntity<Object> downloadAdmitCardsEnclosureWise(@PathVariable int eventId,
			@PathVariable int enclosureId, @PathVariable int subcategoryId) throws FileNotFoundException, IOException {
		JasperReport jasperReport = null;
		Path path = null;
		Resource resource = null;
		List<Pass> passes = passRepository.findByEventIdAndEnclosureIdAndPassCategoryIdAndPassSubcategoryIdAndDeleted(
				eventId, enclosureId, PassCategoryEnum.ADMITCARD.type, subcategoryId, false);
		int i = 0;
		for (Pass pass : passes) {
			i++;
			if (pass.getControlNo() != null) {
				String controlNo = pass.getControlNo();
				Date eventDate = pass.getEvent().getDate();
				Calendar eventCalendar = Calendar.getInstance();
				eventCalendar.setTime(eventDate);
				InputStream sourceFileName = getPassTemplateSourceFile(pass);
				String fileName = controlNo + ".pdf";
				String mergedFileName = "AdmitCards.pdf";
				path = Paths.get(mergedFileName);
				try {
					jasperReport = JasperCompileManager.compileReport(sourceFileName);
					Map<String, Object> params = new HashMap<String, Object>();
//					if (pass.getPassCategory().getId() == PassCategoryEnum.PAIDTICKET.type) {
//						params.put("name", pass.getPaidPassHolders().getName());
//					} else {
//						if (pass.getImsUserByImsUserId().getMaritalStatus() != null && pass.getImsUserByImsUserId()
//								.getMaritalStatus().getId() == MaritalStatusEnum.Married.value)
//							params.put("name",
//									((pass.getImsUserByImsUserId().getSalutation() != null)
//											? pass.getImsUserByImsUserId().getSalutation().getName()
//											: "") + StringUtils.SPACE
//											+ ((pass.getPassHolderName() != null) ? pass.getPassHolderName()
//													: pass.getImsUserByImsUserId().getName())
//											+ " & Spouse");
//						else
//							params.put("name",
//									((pass.getImsUserByImsUserId().getSalutation() != null)
//											? pass.getImsUserByImsUserId().getSalutation().getName()
//											: "") + StringUtils.SPACE
//											+ ((pass.getPassHolderName() != null) ? pass.getPassHolderName()
//													: pass.getImsUserByImsUserId().getName()));
//					}
					params.put("venue", pass.getEnclosure().getEnclosureGroup().getVenue().getName());
					params.put("day", eventCalendar.get(Calendar.DAY_OF_MONTH));
					params.put("year", eventCalendar.get(Calendar.YEAR));
					params.put("month", Month.of(eventCalendar.get(Calendar.MONTH) + 1).name());
					params.put("eventName", pass.getEvent().getName());
					params.put("enclosureGroup", pass.getEnclosure().getEnclosureGroup().getName());
					String encName = pass.getEnclosure().getName();
					String encNameSplit[] = encName.contains("-") ? encName.split("-") : encName.split(" ");
					params.put("enclosureNo", encNameSplit[0]);
					params.put("bridge", "");
					params.put("gate", "");
					if (encNameSplit.length == 3) {
						params.put("bridge", encNameSplit[1]);
						params.put("blockNo", encNameSplit[1]);
						params.put("gate", encNameSplit[2]);
					} else if (encNameSplit.length == 2) {
						params.put("blockNo", encNameSplit[1]);
					}
					params.put("controlNo", pass.getControlNo());
					params.put("qrCode", new ByteArrayInputStream(getQRCodeImage(pass.getControlHash(), 250, 250)));
					params.put("qrCode1", new ByteArrayInputStream(getQRCodeImage(pass.getControlHash(), 250, 250)));
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params,
							new JREmptyDataSource());
					JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
					if (i == 1) {
						JasperExportManager.exportReportToPdfFile(jasperPrint, mergedFileName);
					} else {
						ImsPDFMerger.mergePDF(new File(mergedFileName), new File(fileName), mergedFileName);
					}

					resource = new UrlResource(path.toUri());

					log.saveLog(null, "Pass with the control no " + pass.getControlNo() + " downloaded successfully",
							"DOWNLOAD", LogLevelEnum.INFO);
				} catch (JRException | MalformedURLException e) {
					// TODO Auto-generated catch block
					log.saveLog(null,
							"An error occurred while downloading the Pass with the control no " + pass.getControlNo(),
							"DOWNLOAD", LogLevelEnum.ERROR);
					e.printStackTrace();
				}

				pass.setDownloaded(true);
				passRepository.save(pass);
				
			}
		}
		if (passes.size() > 0) {
			System.out.println("***************Successfully Downloaded Admit Cards for an enclosure ******************");
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path + "\"")
					.contentType(MediaType.APPLICATION_PDF).body(resource);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/downloadSamplePass/{eventCode}/{passCategory}/{passSubcategory}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ResponseEntity<Resource> downloadSamplePass(@PathVariable("eventCode") String eventCode,
			@PathVariable("passCategory") String passCategory,
			@PathVariable("passSubcategory") String passSubcategory) {

		InputStream in = getSamplePass(passCategory, passSubcategory, eventCode);
		System.out.println("***************Downloaded sample pass******************");
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Pass.pdf")
				.body(new InputStreamResource(in));
	}

	@PostMapping("/getPaidPass")
	@PreAuthorize("hasRole('CITIZEN') or hasRole('COUNTEREMP') or hasRole('SUPERADMIN') or hasRole('INVITEE')")
	public ImsResponse getPaidPass(@Valid @RequestBody PaidPassRequest paidPassRequest,
			HttpServletRequest httpServletRequest, @RequestHeader(name = "Authorization") String token) {

		AtomicReference<Boolean> isCarPassDuplicate = new AtomicReference<>(false);

		String mobileNo = jwtUtils.getUserNameFromJwtToken(token);
		ImsUser imsUser = imsUserRepository.getByMobileNoAndDeleted(mobileNo, false);

		int passSeq = passRepository.getLastIdOfPass();
		int carPassSeq = carPassRepository.getLastIdOfPass();

		AtomicReference<Integer> passSeqAI = new AtomicReference<Integer>(passSeq);
		AtomicReference<Integer> carPassSeqAI = new AtomicReference<Integer>(carPassSeq);

		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passes = new ArrayList<>();
		List<PaidPassHolders> paidPassHoldersList = paidPassRequest.getPaidPassHoldersList();
		List<PgTransactions> pgTransactionsList = new ArrayList<>();

		AtomicReference<Boolean> savePaidPass = new AtomicReference<>();
		List<UserIpaddress> userIpaddressesToSave = new ArrayList<>();

		Resource resource = externalPassConfigReader.getFile();
		String dataFromExternalFile = JsonStringReader.getFileContent(resource);

		String requestedIpAddress = httpServletRequest.getHeader("X-Forwarded-For");

		savePaidPass.set(true);
		paidPassHoldersList.forEach(paidPassHolder -> {

			PaidPassHolders paidPassHolderExisting = paidPassHoldersRepository.findById(paidPassHolder.getId()).get();
			paidPassHolder.setCreatedTimestamp(paidPassHolderExisting.getCreatedTimestamp());
			paidPassHolder.setIdentityProofDocument(paidPassHolderExisting.getIdentityProofDocument());

			if (savePaidPass.get()) {
				Event event = eventRepository.findById(paidPassHolder.getEvent().getId()).get();

				if (imsUser.getRole().getId() != RoleEnum.ROLE_COUNTEREMP.role) {

					Event currentEvent = paidPassHoldersList.get(0).getEvent();

					// Start Login User pass limit validation
					String ipLimit = JsonStringReader.getValue(dataFromExternalFile, event.getEventCode(),
							ExternalPassConfigEnum.PUBLIC_PASS_IP_LIMIT.key);
					String userLimt = JsonStringReader.getValue(dataFromExternalFile, event.getEventCode(),
							ExternalPassConfigEnum.PUBLIC_PASS_USER_LIMIT.key);
					if (dataFromExternalFile == null || dataFromExternalFile.isEmpty() || ipLimit == null
							|| ipLimit.isEmpty() || userLimt == null || userLimt.isEmpty()) {
						ipLimit = "0";
						userLimt = "0";
					}

					long paidPassCountOfUser = 0;
					if (!imsUser.getPassesForImsUserId().isEmpty())
						paidPassCountOfUser = imsUser.getPassesForImsUserId().stream()
								.filter(p -> p.getEvent().getId() == currentEvent.getId())
								.filter(p -> p.getPassCategory().getId() == PassCategoryEnum.PAIDTICKET.type)
								.filter(p -> p.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type).count();

					if (paidPassCountOfUser >= Long.parseLong(userLimt)) {
						savePaidPass.set(false);
						imsResponse.setSuccess(false);
						imsResponse.setMessage("Pass booking limit exceed for your login!");
						return;
					} else if (paidPassHoldersList.size() > (Long.parseLong(userLimt) - paidPassCountOfUser)) {
						long count = paidPassHoldersList.size() - (Long.parseLong(userLimt) - paidPassCountOfUser);
						savePaidPass.set(false);
						imsResponse.setSuccess(false);
						imsResponse.setMessage("Only " + count + " more pass(es) allowed for your login!");
						return;
					}
					// End Login User pass limit validation

					// Start User IP Address pass limit validation
					List<UserIpaddress> userIpaddresses = userIpaddressRepository
							.findByIpaddressAndDeleted(requestedIpAddress, false);
					// paidPassCountByIp is count of passes created from current ip address using
					// different ims user accounts
					AtomicReference<Long> paidPassCountByIp = new AtomicReference<>();
					paidPassCountByIp.set(0L);
					BinaryOperator<Long> add = (u, v) -> u + v;
					Map<Integer, ImsUser> nonDuplicateImsUserMap = new HashMap<>();
					for (UserIpaddress userIpaddress : userIpaddresses) {
						if (nonDuplicateImsUserMap.get(userIpaddress.getImsUser().getId()) == null)
							nonDuplicateImsUserMap.put(userIpaddress.getImsUser().getId(), userIpaddress.getImsUser());
					}
					nonDuplicateImsUserMap.keySet().forEach(k -> {
						for (Pass pass : nonDuplicateImsUserMap.get(k).getPassesForImsUserId()) {
							if (pass.getPassCategory().getId() == PassCategoryEnum.PAIDTICKET.type
									&& pass.getEvent().getId() == currentEvent.getId()
									&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type)
								paidPassCountByIp.accumulateAndGet(1L, add);
						}
					});

					if (paidPassCountByIp.get() >= Long.parseLong(ipLimit)) {
						imsResponse.setSuccess(false);
						imsResponse.setMessage("Pass booking limit exceed for your ip!");
						return;
					} else if (paidPassHoldersList.size() > (Long.parseLong(ipLimit) - paidPassCountByIp.get())) {
						long count = paidPassHoldersList.size() - (Long.parseLong(ipLimit) - paidPassCountByIp.get());

						imsResponse.setSuccess(false);
						imsResponse.setMessage("Only " + count + " more pass(es) allowed from your ip!");
						return;
					}
					// End User IP Address pass limit validation

				}

				// Start - Daily quota validation

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
				final LocalDateTime fromDateTime = LocalDateTime.parse(LocalDate.now() + " 00:00:00.000", formatter);
				final LocalDateTime toDateTime = LocalDateTime.parse(LocalDate.now() + " 23:59:59.999", formatter);

				PassDayLimitCategory passDayLimitCategories = passDayLimitCategoryRepository
						.findByPassDayLimitEventIdAndPassDayLimitDateAndRoleIdAndPassSubcategoryIdAndEnclosureGroupIdAndDeleted(
								paidPassHolder.getEvent().getId(), LocalDate.now(), imsUser.getRole().getId(),
								paidPassHolder.getPassSubcategory().getId(),
								paidPassHolder.getEnclosureGroup().getId(), false);

				int limit = (passDayLimitCategories == null) ? 0 : passDayLimitCategories.getPassLimit(); 

				int requestedCount = passRepository
						.findByPassCategoryIdAndPassSubcategoryIdAndEventIdAndCreatedTimestampBetweenAndEnclosureEnclosureGroupIdAndImsUserByImsUserIdRoleIdAndPassStatusIdNotAndDeleted(
								PassCategoryEnum.PAIDTICKET.type, paidPassHolder.getPassSubcategory().getId(),
								paidPassHolder.getEvent().getId(), fromDateTime, toDateTime,
								paidPassHolder.getEnclosureGroup().getId(), imsUser.getRole().getId(),
								PassStatusEnum.CANCELLED.type, false) 
						.size();
				PassSubcategory passSubcategory = passSubcategoryRepository
						.findById(paidPassHolder.getPassSubcategory().getId()).get();
				PassCategory passCategory = passCategoryRepository.findById(passSubcategory.getPassCategory().getId())
						.get();

				System.out.println(event.getEventCode()+"--------- Pass Subcategory -------- "+passSubcategory.getName()+"   -------- Day Limit Set By Admin --------- "+limit+"   ------------ Requested Count ---- "+requestedCount);

				if (limit == 0 || limit <= requestedCount || (paidPassHoldersList.size() > (limit - requestedCount))) {
					System.out.println(event.getEventCode()+" PassController.getPaidPass() --- Daily Quota Exceeded "+paidPassHoldersList.size());
					passes.forEach( pass -> {
						CarPass carPass = pass.getCarPass();
						if(carPass != null) {
							pass.setCarPass(null);
							carPass.setPassStatus(passStatusRepository.findById(PassStatusEnum.CANCELLED.type).get());
							System.out.println("--------- Deleted the car pass -------- ");

						}
						pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.CANCELLED.type).get());
						System.out.println("--------- Deleted the pass -------- ");
					});
					imsResponse.setSuccess(false);
					imsResponse.setMessage(
							"Daily quota exceeded for the selected Pass Category(s) "+passSubcategory.getName()+" and enclosure group ");
					return;
				}

				// End - Daily quota Validation

				UserIpaddress userIpaddress = new UserIpaddress();
				userIpaddress.setImsUser(imsUser);
				userIpaddress.setDeleted(false);
				userIpaddress.setCreatedTimestamp(new Date(System.currentTimeMillis()));
				userIpaddress.setIpaddress(requestedIpAddress);
				userIpaddressesToSave.add(userIpaddress);

				
				List<EnclosurePassTypeMapping> enclosurePassTypeMapping = enclosurePassTypeMappingRepository
						.findByPassSubcategoryIdAndEnclosureEnclosureGroupIdAndDeleted(
								paidPassHolder.getPassSubcategory().getId(), paidPassHolder.getEnclosureGroup().getId(),
								false);
				Set<Enclosure> enclosures = enclosurePassTypeMapping.stream().map( enclosurePassType -> enclosurePassType.getEnclosure()).collect(Collectors.toSet());
				
				Enclosure enclosureSelected = null;
				for (Enclosure enclosure : enclosures) {
					int enclosureMaxCapacity = enclosure.getMaxCapacity();
					int allocatedCapacity = passRepository.findAllByEnclosureIdAndEventIdAndPassStatusIdNotAndDeleted(enclosure.getId(), event.getId(), PassStatusEnum.CANCELLED.type ,false).size();
					if(allocatedCapacity < enclosureMaxCapacity) {
						enclosureSelected = enclosure;
						break;
					}
				}
				
				if(enclosureSelected == null) {
					imsResponse.setSuccess(false);
					imsResponse.setMessage("All the Enclosures capacity exceeded for the selecetd enclosure group and ticket category!");
					return;
				}
				List<EnclosureCplMapping> enclosureCplMapping = enclosureCplMappingRepository
						.findByEnclosureId(enclosureSelected.getId());
				Set<Cpl> cpls = null;
				Cpl cplSelected = null;
				if (enclosureCplMapping != null && !enclosureCplMapping.isEmpty()) {
					cpls = enclosureCplMapping.stream().map(enclosureCplMappin -> enclosureCplMappin.getCpl()).collect(Collectors.toSet());
					for (Cpl cpl : cpls) {
					int  allocatedCapacity = passRepository.findAllByCarPassCplIdAndDeleted(cpl.getId(), false).size();
						if(allocatedCapacity < cpl.getMaxCapacity()) {
							cplSelected = cpl;
							break;
						}
					}
				}
				
//				if(cplSelected == null) {
//					imsResponse.setSuccess(false);
//					imsResponse.setMessage("No Car parking slots avalable in this enclosure group!");
//					return;
//				}
				PgTransactions pgTransactions = new PgTransactions();

				Pass pass = new Pass();
				if (imsUser.getRole().getId() != RoleEnum.ROLE_COUNTEREMP.role)
					pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.PENDING_APPROVAL.type).get());
				else
					pass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());

				pass.setImsUserByImsUserId(imsUser);
				pass.setPassCategory(passCategory);
				pass.setPassSubcategory(passSubcategory);
				pass.setEvent(event);
				pass.setEnclosure(enclosureSelected);
				pass.setPaidPassHolders(paidPassHolder);
				pass.setCreatedTimestamp(LocalDateTime.now());
				pass = passRepository.save(pass);
				passSeqAI.set(passSeqAI.get() + 1);
				String controlNo = generateControlNo(pass, pass.getId());
				String salt = ImsCipherUtil.generateSalt();
				pass.setControlNo(controlNo);
				

				if (paidPassRequest.isCarPassRequired()
						&& paidPassHolder.getPassSubcategory().getId() == PassSubcategoryEnum.INR500.type
						&& !isCarPassDuplicate.get()) {
					// Car Pass - Start
					CarPass carPass = new CarPass();
					String carSalt = ImsCipherUtil.generateSalt();
					carPass.setControlSalt(carSalt);
					if (imsUser.getRole().getId() != RoleEnum.ROLE_COUNTEREMP.role)
						carPass.setPassStatus(
								passStatusRepository.findById(PassStatusEnum.PENDING_APPROVAL.type).get());
					else
						carPass.setPassStatus(passStatusRepository.findById(PassStatusEnum.ALLOCATED.type).get());
					carPass.setCpl(cplSelected);
					carPass.setPassCategory(passCategoryRepository.findById(PassCategoryEnum.CPL.type).get());
					carPass.setEvent(event);
					carPassSeqAI.set(carPassSeqAI.get() + 1);
					String carControlNo = generateCPLControlNo(carPass, carPassSeqAI.get());
					carPass.setControlNo(carControlNo);
					carPass.setControlHash(ImsCipherUtil.generateHash(carControlNo, carSalt));
					pass.setCarPass(carPassRepository.save(carPass));
					isCarPassDuplicate.set(true);
					// Car Pass - End
				}

				pass.setControlHash(ImsCipherUtil.generateHash(controlNo, salt));
				pass.setControlSalt(salt);
				passes.add(pass);
				pgTransactions.setTransactionId(paidPassRequest.getTxnid());
				pgTransactions.setMode(paidPassRequest.getMode());
				if(paidPassRequest.getAmount() != null) {
					pgTransactions.setAmount(Double.parseDouble(paidPassRequest.getAmount()));
				}
				pgTransactions.setPass(pass);
				
				pgTransactionsList.add(pgTransactions);
			}
			System.out.println("***************Ticket booked successfully for PaidPassHolder with the id " + paidPassHolder.getId() + "******************");
		});

		if (savePaidPass.get()) {
			paidPassHoldersRepository.saveAll(paidPassHoldersList);
			passRepository.saveAll(passes);
			imsResponse.setData(new Pass());
			pgTransactionsRepository.saveAll(pgTransactionsList);
			userIpaddressRepository.saveAll(userIpaddressesToSave);
			if (imsResponse.getMessage() == null || imsResponse.getMessage().isEmpty()) {
				imsResponse.setSuccess(true);
				imsResponse.setMessage("Pass Allocated Successfully!");
			}
		}

		return imsResponse;
	}

	@GetMapping("/reject/{passId}")
	@PreAuthorize("hasRole('INVITEE') or hasRole('NODALOFFICER') or hasRole('HIGHEROFFICER') or hasRole('INVITATIONADMIN')")
	public String reject(@PathVariable int passId) {
		Pass pass = passRepository.findById(passId).get();
		pass.setPassStatus(new PassStatus(AllocationStatusEnum.REJECTED.status));
		passRepository.save(pass);
		System.out.println("***************Rejected Pass with the id " + pass.getId() + "******************");

//		if (smsEnabled) {
//			String message = "Dear Sir/Madam, You have regretted our invitation for the " + pass.getEvent().getName()
//					+ ". From Ministry of Defence";
//			ImsSMS sms = new ImsSMS();
//			sms.setMobileNo(pass.getImsUserByImsUserId().getMobileNo());
//			sms.setMessage(message);
//
//			try {
//				InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_TICKET_CANCEL_TEMPLATE);
//				String invitationMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
//				inputStream.close();
//
//				LocalDateTime eventDateTime = pass.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault())
//						.toLocalDateTime();
//				String time = DateTimeFormatter.ofPattern("hh:mm a").format(eventDateTime);
//				String date = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(eventDateTime);
//
//				invitationMailText = invitationMailText.replace("<<EVENT_NAME>>", pass.getEvent().getName());
//				invitationMailText = invitationMailText.replace("<<EVENT_TIME>>", time);
//				invitationMailText = invitationMailText.replace("<<EVENT_DAY_AND_DATE>>", date);
//				invitationMailText = invitationMailText.replace("<<EVENT_VENUE>>",
//						pass.getEvent().getVenue().getName());
//
//				sms.setMessage(invitationMailText);
//			} catch (IOException ioe) {
//				System.out.println("Error while reading OTP templates : " + ioe.getMessage());
//			}
//			imsSmsSender.send(sms);
//
//			LocalDateTime ldt = pass.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//			emailService.sendEventCancelTicket(pass.getImsUserByImsUserId().getEmail(), "IMS Portal - Event Ticket", pass.getEvent().getName(),
//					ldt, pass.getEvent().getVenue().getName());
//			sms.setTemplateId(passReject);
//		}
		return "Rejected Invite!";
	}

	@GetMapping("/accept/{passId}")
	@PreAuthorize("hasRole('INVITEE') or hasRole('NODALOFFICER') or hasRole('HIGHEROFFICER') or hasRole('INVITATIONADMIN')")
	public String accept(@PathVariable int passId) {
		Pass pass = passRepository.findById(passId).get();
		pass.setPassStatus(new PassStatus(AllocationStatusEnum.ACCEPTED.status));
		Event event = pass.getEvent();
		passRepository.save(pass);
		System.out.println("***************Accepted Pass with the the id " + pass.getId() + "******************");
//		if (smsEnabled) {
//			String message = "Dear Sir/madam, You are cordially invited to the " + event.getName() + " on "
//					+ event.getDate() + " at " + event.getVenue().getName()
//					+ ". Kindly download your Invitation by logon to nimantran.mod.gov.in"
//					+ ". From Ministry of Defence";
//			ImsSMS sms = new ImsSMS();
//			sms.setMobileNo(pass.getImsUserByImsUserId().getMobileNo());
//			sms.setMessage(message);
//			try {
//				InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_INVITE_TEMPLATE);
//				String invitationMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
//				inputStream.close();
//
//				SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//				invitationMailText = invitationMailText.replace("<<EVENT_NAME>>", pass.getEvent().getName());
//				invitationMailText = invitationMailText.replace("<<EVENT_TIME>>", "");
//				invitationMailText = invitationMailText.replace("<<EVENT_DAY_AND_DATE>>",
//						newFormat.format(event.getDate()));
//				invitationMailText = invitationMailText.replace("<<EVENT_VENUE>>",
//						pass.getEvent().getVenue().getName());
//
//				sms.setMessage(invitationMailText);
//			} catch (IOException ioe) {
//				System.out.println("Error while reading OTP templates : " + ioe.getMessage());
//			}
//			sms.setTemplateId(eventInvitationTemplate);
//			imsSmsSender.send(sms);
//
//			emailService.sendEventInvitation(pass.getImsUserByImsUserId().getEmail(), "IMS Portal - Event Invitation", event.getName(),
//					event.getDate(), event.getVenue().getName());
//		}
		return "Accepted Invite!";
	}

	@GetMapping("/getCplWiseAvailability/{eventId}/{nodalOfficerId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public List<CplAvailability> getCplWiseAvailability(@PathVariable int eventId, @PathVariable int nodalOfficerId) {
		// TODO Auto-generated method stub
		ImsResponse imsResponse = new ImsResponse();
		List<CplAvailability> cplAvailabilityList = new ArrayList<>();
		List<CarPass> filteredCarPasses = new ArrayList<>();

		try {
			List<NodalOfficer> nodalOfficers = nodalOfficerRepository.findByImsUserId(nodalOfficerId);
			List<Integer> departments = nodalOfficers.stream().map(nodalOfficer -> nodalOfficer.getDepartment().getId())
					.collect(Collectors.toList());
			List<CarPass> carPasses = carPassRepository.findByEventIdAndDeletedAndDepartmentIdIn(eventId, false,
					departments);

			Set<CarPass> cplDistinct = carPasses.stream().filter(distinctByKey(pass -> pass.getCpl().getId()))
					.collect(Collectors.toSet());

			for (CarPass carPass : cplDistinct) {
				if (carPass.getCpl() != null && carPass.getCpl().isDeleted() != true) {
					CplAvailability cplAvailability = new CplAvailability();
					cplAvailability.setCplId(carPass.getCpl().getId());
					cplAvailability.setCplName(carPass.getCpl().getName());
					cplAvailability.setMaxCapacity(carPass.getCpl().getMaxCapacity());
					cplAvailability.setEnclosureGroup(carPass.getCpl().getEnclosureGroup());
					filteredCarPasses = carPasses.stream()
							.filter(pass -> pass.getEvent() != null && pass.getCpl() != null
									&& pass.getCpl().getId() == carPass.getCpl().getId()
									&& pass.getEvent().getId() == eventId)
							.collect(Collectors.toList());
					long totalAccepted = filteredCarPasses.stream()
							.filter(pass -> pass.getPassStatus() != null
									&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_ACCEPTED.type)
							.collect(Collectors.toList()).size();
					long totalRejected = filteredCarPasses.stream()
							.filter(pass -> pass.getPassStatus() != null
									&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_REGRETTED.type)
							.collect(Collectors.toList()).size();
					long totalAllocated = filteredCarPasses.stream()
							.filter(pass -> pass.getPassStatus() != null
									&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type)
							.collect(Collectors.toList()).size();
					long totalIssued = filteredCarPasses.size() - totalRejected;
					long totalAvailable = 0;
					if (carPass.getCpl().getMaxCapacity() != null) {
						totalAvailable = totalIssued - totalAllocated;
					}
					cplAvailability.setTotalAccepted(totalAccepted);
					cplAvailability.setTotalAllocated(totalAllocated);
					cplAvailability.setTotalIssued(totalIssued);
					cplAvailability.setTotalRejected(totalRejected);
					cplAvailability.setTotalAvailable(totalAvailable);
					cplAvailabilityList.add(cplAvailability);
				}
			}

		} catch (NoSuchElementException e) {
			e.printStackTrace();
			imsResponse.setMessage("Passes not available for the department/event!");
			imsResponse.setSuccess(false);
		}
		return cplAvailabilityList;
	}

	@GetMapping("/getCplWiseAvailabilityForIO/{eventId}/{invitationOfficerId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public List<CplAvailability> getCplWiseAvailabilityForIO(@PathVariable int eventId,
			@PathVariable int invitationOfficerId) {
		// TODO Auto-generated method stub
		ImsResponse imsResponse = new ImsResponse();
		List<CplAvailability> cplAvailabilityList = new ArrayList<>();
		List<CarPass> filteredCarPasses = new ArrayList<>();

		try {
			List<InvitationOfficer> invitationOfficers = invitationOfficerRepository
					.findByImsUserId(invitationOfficerId);
			Map<Integer, EnclosureGroup> uniqueEnclGrps = new HashMap<>();
			List<Integer> uniqueEnclGrpIds = new ArrayList<>();
			for (InvitationOfficer invitationOfficer : invitationOfficers) {
				if (uniqueEnclGrps.get(invitationOfficer.getEnclosure().getEnclosureGroup().getId()) == null) {
					uniqueEnclGrpIds.add(invitationOfficer.getEnclosure().getEnclosureGroup().getId());
					uniqueEnclGrps.put(invitationOfficer.getEnclosure().getEnclosureGroup().getId(),
							invitationOfficer.getEnclosure().getEnclosureGroup());
				}
			}
			List<Cpl> cplList = cplRepository.findByEnclosureGroupIdInAndDeleted(uniqueEnclGrpIds, false);
			List<Integer> cplIds = cplList.stream().map(cpl -> cpl.getId()).collect(Collectors.toList());
			List<CarPass> carPasses = carPassRepository.findByEventIdAndDeletedAndCplIdIn(eventId, false, cplIds);

			Set<CarPass> cplDistinct = carPasses.stream().filter(distinctByKey(pass -> pass.getCpl().getId()))
					.collect(Collectors.toSet());

			for (CarPass carPass : cplDistinct) {
				if (carPass.getCpl() != null && carPass.getCpl().isDeleted() != true) {
					CplAvailability cplAvailability = new CplAvailability();
					cplAvailability.setCplId(carPass.getCpl().getId());
					cplAvailability.setCplName(carPass.getCpl().getName());
					cplAvailability.setMaxCapacity(carPass.getCpl().getMaxCapacity());
					cplAvailability.setEnclosureGroup(carPass.getCpl().getEnclosureGroup());
					filteredCarPasses = carPasses.stream()
							.filter(pass -> pass.getEvent() != null && pass.getCpl() != null
									&& pass.getCpl().getId() == carPass.getCpl().getId()
									&& pass.getEvent().getId() == eventId)
							.collect(Collectors.toList());
					long totalAccepted = filteredCarPasses.stream()
							.filter(pass -> pass.getPassStatus() != null
									&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_ACCEPTED.type)
							.collect(Collectors.toList()).size();
					long totalRejected = filteredCarPasses.stream()
							.filter(pass -> pass.getPassStatus() != null
									&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_REGRETTED.type)
							.collect(Collectors.toList()).size();
					long totalAllocated = filteredCarPasses.stream()
							.filter(pass -> pass.getPassStatus() != null
									&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type)
							.collect(Collectors.toList()).size();
					long totalIssued = filteredCarPasses.size() - totalRejected;
					long totalAvailable = 0;
					if (carPass.getCpl().getMaxCapacity() != null) {
						totalAvailable = totalIssued - totalAllocated;
					}
					cplAvailability.setTotalAccepted(totalAccepted);
					cplAvailability.setTotalAllocated(totalAllocated);
					cplAvailability.setTotalIssued(totalIssued);
					cplAvailability.setTotalRejected(totalRejected);
					cplAvailability.setTotalAvailable(totalAvailable);
					cplAvailabilityList.add(cplAvailability);
				}
			}

		} catch (NoSuchElementException e) {
			e.printStackTrace();
			imsResponse.setMessage("Passes not available for the department/event!");
			imsResponse.setSuccess(false);
		}
		return cplAvailabilityList;
	}

	@GetMapping("/getCplWiseAvailabilityByEvent/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public List<CplAvailability> getCplWiseAvailabilityByEvent(@PathVariable int eventId) {
		// TODO Auto-generated method stub
		ImsResponse imsResponse = new ImsResponse();
		List<CplAvailability> cplAvailabilityList = new ArrayList<>();
		List<CarPass> filteredCarPasses = new ArrayList<>();

		try {

			List<CarPass> carPasses = carPassRepository.findByEventIdAndDeleted(eventId, false);

			Set<CarPass> cplDistinct = carPasses.stream().filter(distinctByKey(pass -> pass.getCpl().getId()))
					.collect(Collectors.toSet());

			for (CarPass carPass : cplDistinct) {
				if (carPass.getCpl() != null && carPass.getCpl().isDeleted() != true) {
					CplAvailability cplAvailability = new CplAvailability();
					cplAvailability.setCplId(carPass.getCpl().getId());
					cplAvailability.setCplName(carPass.getCpl().getName());
					cplAvailability.setMaxCapacity(carPass.getCpl().getMaxCapacity());
					cplAvailability.setEnclosureGroup(carPass.getCpl().getEnclosureGroup());
					filteredCarPasses = carPasses.stream()
							.filter(pass -> pass.getEvent() != null && pass.getCpl() != null
									&& pass.getCpl().getId() == carPass.getCpl().getId()
									&& pass.getEvent().getId() == eventId)
							.collect(Collectors.toList());
					long totalAccepted = filteredCarPasses.stream()
							.filter(pass -> pass.getPassStatus() != null
									&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_ACCEPTED.type)
							.collect(Collectors.toList()).size();
					long totalRejected = filteredCarPasses.stream()
							.filter(pass -> pass.getPassStatus() != null
									&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_REGRETTED.type)
							.collect(Collectors.toList()).size();
					long totalAllocated = filteredCarPasses.stream()
							.filter(pass -> pass.getPassStatus() != null
									&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type)
							.collect(Collectors.toList()).size();
					long totalIssued = filteredCarPasses.size() - totalRejected;
					long totalAvailable = 0;
					if (carPass.getCpl().getMaxCapacity() != null) {
						totalAvailable = totalIssued - totalAllocated;
					}
					cplAvailability.setTotalAccepted(totalAccepted);
					cplAvailability.setTotalAllocated(totalAllocated);
					cplAvailability.setTotalIssued(totalIssued);
					cplAvailability.setTotalRejected(totalRejected);
					cplAvailability.setTotalAvailable(totalAvailable);
					cplAvailabilityList.add(cplAvailability);
				}
			}

		} catch (NoSuchElementException e) {
			e.printStackTrace();
			imsResponse.setMessage("Passes not available for the department/event!");
			imsResponse.setSuccess(false);
		}
		return cplAvailabilityList;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private String generateControlNo(Pass pass, int passSeq) {

		Event event = eventRepository.findById(pass.getEvent().getId()).get();
		Enclosure enclosure = enclosureRepository.findById(pass.getEnclosure().getId()).get();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(event.getDate());
		int year = calendar.get(Calendar.YEAR);

//		Pattern: <YEAR>-<EVENT Code>-<Enclosure code>-<Sequence No> Sample: 2022-IDC-Enc1-0000001

		String controlNo = year + "-" + event.getEventCode() + "-" + enclosure.getName() + "-"
				+ String.format("%04d", passSeq);
		return controlNo;

	}

	private String generateControlNoForAdmitCard(Pass pass, int passSeq, Event event, Enclosure enclosure) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(event.getDate());
		int year = calendar.get(Calendar.YEAR);

//		Pattern: <YEAR>-<EVENT Code>-<Enclosure code>-<Sequence No> Sample: 2022-IDC-Enc1-0000001

		String controlNo = year + "-" + event.getEventCode() + "-" + enclosure.getName() + "-"
				+ String.format("%04d", passSeq);
		return controlNo;

	}

	private String generateCPLControlNo(CarPass pass, int carPassSeq) {
		Event event = eventRepository.findById(pass.getEvent().getId()).get();
		Cpl cpl = cplRepository.findById(pass.getCpl().getId()).get();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(event.getDate());
		int year = calendar.get(Calendar.YEAR);

//		Pattern: <YEAR>-<EVENT Code>-<Enclosure code>-<Sequence No> Sample: 2022-IDC-Enc1-0000001

		String controlNo = year + "-" + event.getEventCode() + "-" + cpl.getName() + "-"
				+ String.format("%04d", carPassSeq);
		return controlNo;
	}

	private byte[] getQRCodeImage(String text, int width, int height) {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix;
		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		try {
			bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
			MatrixToImageConfig con = new MatrixToImageConfig();
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
		} catch (WriterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pngOutputStream.toByteArray();

	}

	private InputStream getSamplePass(String passCategory, String passSubcategory, String eventCode) {
		if (passCategory.equalsIgnoreCase("" + PassCategoryEnum.ADMITCARD.type)) {
			if (passSubcategory.equalsIgnoreCase("" + PassSubcategoryEnum.VIP.type)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_AdmitCard_VIP.pdf");
			} else if (passSubcategory.equalsIgnoreCase("" + PassSubcategoryEnum.NON_VIP.type)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_AdmitCard_NonVIP.pdf");
			}
		} else if (passCategory.equalsIgnoreCase("" + PassCategoryEnum.INVITATION.type)) {
			if (passSubcategory.equalsIgnoreCase("" + PassSubcategoryEnum.RED_TIP.type)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_Invite_RedTip.pdf");
			} else if (passSubcategory.equalsIgnoreCase("" + PassSubcategoryEnum.YELLOW_TIP.type)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_Invite_YellowTip.pdf");
			} else if (passSubcategory.equalsIgnoreCase("" + PassSubcategoryEnum.BLUE_TIP.type)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_Invite_BlueTip.pdf");
			} else if (passSubcategory.equalsIgnoreCase("" + PassSubcategoryEnum.NO_TIP.type)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_Invite_NoTip.pdf");
			} else if (passSubcategory.equalsIgnoreCase("" + PassSubcategoryEnum.GREEN_TIP.type)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_Invite_GreenTip.pdf");
			} else {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_Invite_NoTip.pdf");
			}

		} else if (passCategory.equalsIgnoreCase("" + PassCategoryEnum.CPL.type)) {
			/*
			 * if (passSubcategory == PassSubcategoryEnum.TYPE_I.type) { return
			 * getClass().getResourceAsStream("/pass/samples/RDC_CPL_8.pdf"); } else if
			 * (passSubcategory == PassSubcategoryEnum.TYPE_II.type) { return
			 * getClass().getResourceAsStream("/pass/samples/RDC_CPL_1.pdf"); } else if
			 * (passSubcategory == PassSubcategoryEnum.TYPE_III.type) { return
			 * getClass().getResourceAsStream("/pass/samples/MD_CPL_1.pdf"); }
			 * 
			 * try { File searchFolder = new
			 * File(getClass().getResource("/pass/samples/").getFile()); List<File>
			 * filesToMerge = Files.walk(searchFolder.toPath()). filter(pth ->
			 * pth.getFileName().toString().startsWith(eventCode +"_"+
			 * PassCategoryEnum.CPL+"_")). map(p ->
			 * p.toFile()).collect(Collectors.toList()); String mergedFileName =
			 * eventCode+"_"+PassCategoryEnum.CPL+"_GROUP.pdf";
			 * ImsPDFMerger.mergePDF(filesToMerge, mergedFileName); return new
			 * FileInputStream(new File(mergedFileName)); } catch (IOException ioe) { return
			 * null; }
			 */
			return getClass().getResourceAsStream(
					"/pass/samples/" + eventCode + "_" + PassCategoryEnum.CPL + "_" + passSubcategory + ".pdf");

		} else if (passCategory.equalsIgnoreCase("" + PassCategoryEnum.PAIDTICKET.type)) {
			if (passSubcategory.equalsIgnoreCase("" + PassSubcategoryEnum.INR20.type)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_PCP_PaidTicket_20.pdf");
			} else if (passSubcategory.equalsIgnoreCase("" + PassSubcategoryEnum.INR50.type)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_PCP_PaidTicket_50.pdf");
			} else if (passSubcategory.equalsIgnoreCase("" + PassSubcategoryEnum.INR100.type)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_PCP_PaidTicket_100.pdf");
			} else if (passSubcategory.equalsIgnoreCase("" + PassSubcategoryEnum.INR500.type)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_PCP_PaidTicket_500.pdf");
			}

		} else if (passCategory.equalsIgnoreCase("" + PassCategoryEnum.GUESTPASS.type)) {
			if (passSubcategory.equalsIgnoreCase("" + 15)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_Invite_YellowTip.pdf");
			} else if (passSubcategory.equalsIgnoreCase("" + 16)) {
				return getClass().getResourceAsStream("/pass/samples/" + eventCode + "_Invite_NoTip.pdf");
			}
		}
		return null;

	}

	private InputStream getPassTemplateSourceFile(Pass pass) {

		String eventCode = pass.getEvent().getEventCode();
		Integer passCategory = pass.getPassCategory().getId();
		Integer passSubcategory = pass.getPassSubcategory().getId();
		if (passCategory == PassCategoryEnum.ADMITCARD.type) {
			if (passSubcategory == PassSubcategoryEnum.VIP.type) {
				return getClass().getResourceAsStream("/reports/" + eventCode + "_AdmitCard_VIP.jrxml");
			} else if (passSubcategory == PassSubcategoryEnum.NON_VIP.type) {
				return getClass().getResourceAsStream("/reports/" + eventCode + "_AdmitCard_NonVIP.jrxml");
			}

		} else if (passCategory == PassCategoryEnum.INVITATION.type) {

			if (passSubcategory == PassSubcategoryEnum.RED_TIP.type) {
				return getClass().getResourceAsStream("/reports/" + eventCode + "_Invite_RedTip.jrxml");
			} else if (passSubcategory == PassSubcategoryEnum.YELLOW_TIP.type) {
				return getClass().getResourceAsStream("/reports/" + eventCode + "_Invite_YellowTip.jrxml");
			} else if (passSubcategory == PassSubcategoryEnum.BLUE_TIP.type) {
				return getClass().getResourceAsStream("/reports/" + eventCode + "_Invite_BlueTip.jrxml");
			} else if (passSubcategory == PassSubcategoryEnum.NO_TIP.type) {
				return getClass().getResourceAsStream("/reports/" + eventCode + "_Invite_NoTip.jrxml");
			} else if (passSubcategory == PassSubcategoryEnum.GREEN_TIP.type) {
				return getClass().getResourceAsStream("/reports/" + eventCode + "_Invite_GreenTip.jrxml");
			}

		} else if (passCategory == PassCategoryEnum.CPL.type) {
			if (passSubcategory == PassSubcategoryEnum.TYPE_I.type) {
				return getClass().getResourceAsStream("/reports/RDC_Invite_Front.jrxml");
			} else if (passSubcategory == PassSubcategoryEnum.TYPE_II.type) {
				return getClass().getResourceAsStream("/reports/RDC_Invite_Front.jrxml");
			} else if (passSubcategory == PassSubcategoryEnum.TYPE_III.type) {
				return getClass().getResourceAsStream("/reports/RDC_Invite_Front.jrxml");
			}

		} else if (passCategory == PassCategoryEnum.PAIDTICKET.type) {
			if (passSubcategory == PassSubcategoryEnum.INR20.type) {
				return getClass().getResourceAsStream("/reports/" + eventCode + "_PCP_PaidTicket_20.jrxml");
			} else if (passSubcategory == PassSubcategoryEnum.INR50.type) {
				return getClass().getResourceAsStream("/reports/" + eventCode + "_PCP_PaidTicket_50.jrxml");
			} else if (passSubcategory == PassSubcategoryEnum.INR100.type) {
				return getClass().getResourceAsStream("/reports/" + eventCode + "_PCP_PaidTicket_100.jrxml");
			} else if (passSubcategory == PassSubcategoryEnum.INR500.type) {
				return getClass().getResourceAsStream("/reports/" + eventCode + "_PCP_PaidTicket_500.jrxml");
			}

		} else if (passCategory == PassCategoryEnum.GUESTPASS.type) {
			if (passSubcategory == PassSubcategoryEnum.GUEST_YELLOW_TIP.type) {
				return getClass().getResourceAsStream("/reports/"+ eventCode +"_Invite_YellowTip.jrxml");
			} else if (passSubcategory == PassSubcategoryEnum.GUEST_NO_TIP.type) {
				return getClass().getResourceAsStream("/reports/"+ eventCode +"_Invite_NoTip.jrxml");
			}
		}
		return null;

	}

	private InputStream getCarPassTemplateSourceFile(CarPass pass, String eventCode) {
//		String enclGroup = pass.getCpl().getEnclosureGroup().getName().toLowerCase();
		String enclId = pass.getCpl().getName();

		return getClass()
				.getResourceAsStream("/reports/" + eventCode + "_" + PassCategoryEnum.CPL + "_" + enclId + ".jrxml");
		/*
		 * if (enclGroup.contains("south")) { // if the enclosure is South of Kartavya -
		 * 1 switch (enclId) { case "1": return
		 * getClass().getResourceAsStream("/reports/CPL_SouthOfKartavya_1.jrxml"); case
		 * "2": return
		 * getClass().getResourceAsStream("/reports/CPL_SouthOfKartavya_2.jrxml"); case
		 * "5": return
		 * getClass().getResourceAsStream("/reports/CPL_SouthOfKartavya_5.jrxml"); case
		 * "6": return
		 * getClass().getResourceAsStream("/reports/CPL_SouthOfKartavya_6.jrxml"); case
		 * "7": return
		 * getClass().getResourceAsStream("/reports/CPL_SouthOfKartavya_7.jrxml");
		 * default: return
		 * getClass().getResourceAsStream("/reports/CPL_SouthOfKartavya_1.jrxml"); } }
		 * else if (enclGroup.contains("north")) { // if the enclosure is North of
		 * Kartavya - 8 switch (enclId) { case "8": return
		 * getClass().getResourceAsStream("/reports/CPL_NorthOfKartavya_8.jrxml"); case
		 * "9": return
		 * getClass().getResourceAsStream("/reports/CPL_NorthOfKartavya_9.jrxml"); case
		 * "10": return
		 * getClass().getResourceAsStream("/reports/CPL_NorthOfKartavya_10.jrxml"); case
		 * "11": return
		 * getClass().getResourceAsStream("/reports/CPL_NorthOfKartavya_11.jrxml");
		 * default: return
		 * getClass().getResourceAsStream("/reports/CPL_NorthOfKartavya_8.jrxml"); } }
		 * else if (enclGroup.contains("rajghat")) { // if the enclosure is Rajghat - 1
		 * switch (enclId) { case "1": return
		 * getClass().getResourceAsStream("/reports/CPL_Rajghat_1.jrxml"); case "2":
		 * return getClass().getResourceAsStream("/reports/CPL_Rajghat_2.jrxml");
		 * default: return
		 * getClass().getResourceAsStream("/reports/CPL_Rajghat_1.jrxml"); } } else if
		 * (enclGroup.contains("vinay")) { // if the enclosure is Rajghat - 1 switch
		 * (enclId) { case "12": return
		 * getClass().getResourceAsStream("/reports/CPL_VinayMarg_12.jrxml"); default:
		 * return getClass().getResourceAsStream("/reports/CPL_VinayMarg_12.jrxml"); } }
		 * else if (enclGroup.contains("jln")) { // if the enclosure is Rajghat - 1
		 * switch (enclId) { case "13": return
		 * getClass().getResourceAsStream("/reports/CPL_JlnStadium_13.jrxml"); default:
		 * return getClass().getResourceAsStream("/reports/CPL_JlnStadium_13.jrxml"); }
		 * } else if (enclGroup.contains("shivaji")) { // if the enclosure is Rajghat -
		 * 1 switch (enclId) { case "14": return
		 * getClass().getResourceAsStream("/reports/CPL_ShivajiStadium_14.jrxml");
		 * default: return
		 * getClass().getResourceAsStream("/reports/CPL_ShivajiStadium_14.jrxml"); } }
		 * return null;
		 */

	}

	@Override
	@PostMapping("/save")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public Pass create(@Valid @RequestBody Pass t) {
		// TODO Auto-generated method stub
		System.out.println("***************Saved the pass ******************");
		return sanitizeUserData(super.create(t));
	}

	@Override
	@GetMapping("/getById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getById(@PathVariable int id) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((Pass) super.getById(id).getData()));
		return imsResponse;
	}

	@PostMapping("/saveAll")
	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<Pass> createAll(@Valid @RequestBody List<Pass> t) {
		System.out.println("***************Saved all the passes ******************");
		return sanitizeUserData(super.createAll(t));
	}

	@Override
	@DeleteMapping("/deleteById/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		System.out.println("***************Deleted pass with the given ID " +id+" ******************");
		super.delete(id);
	}

	@PostMapping("/deleteAllById")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public void deleteAllById(@RequestBody PassRequest passRequest) {
		System.out.println("***************Deleted all the passes with the given IDs ******************");
		passRepository.deleteAllById(passRequest.getPassIds());
	}

	@GetMapping("/getAllControlNos/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllControlNo(@PathVariable int eventId) {
		List<Pass> paidTickets = passRepository
				.findByPassCategoryIdAndEventIdAndDeleted(PassCategoryEnum.PAIDTICKET.type, eventId, false);
		List<String> controlNos = Optional.ofNullable(paidTickets).orElseGet(Collections::emptyList).stream()
				.filter(pass -> pass.getControlNo() != null && pass.getPassStatus() != null
						&& pass.getPassStatus().getId() != PassStatusEnum.CANCELLED.type)
				.map(pass -> pass.getControlNo()).collect(Collectors.toList());

		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(controlNos);
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@Override
	@PutMapping("/update")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse update(@Valid @RequestBody Pass t) {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData(sanitizeUserData((Pass) super.update(t).getData()));
		System.out.println("***************Updated Pass with the id " + t.getId() + "******************");
		return imsResponse;
	}

	@PutMapping("/cancelTicket")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	ImsResponse cancelTicket(@Valid @RequestBody CancelTicketRequest cancelTicketRequest) {
		ImsResponse imsResponse = new ImsResponse();
		Pass pass = passRepository.findByControlNoAndDeleted(cancelTicketRequest.getControlNo(), false);
		Event event = eventRepository.findByIdAndDeleted(pass.getEvent().getId(), false);
		if (pass != null) {
			PassStatus passStatus = passStatusRepository.findById(PassStatusEnum.CANCELLED.type).get();
			pass.setPassStatus(passStatus);
			pass.setRemarks(cancelTicketRequest.getRemarks());

			if (smsEnabled) {
				ImsSMS sms = new ImsSMS();
				ImsUser imsUser = imsUserRepository.findByIdAndDeleted(pass.getImsUserByImsUserId().getId(), false);
				sms.setMobileNo(imsUser.getMobileNo());

				try {
					InputStream inputStream = (InputStream) getClass().getResourceAsStream(SMS_TICKET_CANCEL_TEMPLATE);
					String invitationMailText = IOUtils.toString(inputStream, Charset.defaultCharset());
					inputStream.close();

					//LocalDateTime eventDateTime = pass.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					//String time = DateTimeFormatter.ofPattern("hh:mm a").format(eventDateTime);
					//String date = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(eventDateTime);
					SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

					invitationMailText = invitationMailText.replace("<<EVENT_NAME>>", event.getName());
					//invitationMailText = invitationMailText.replace("<<EVENT_TIME>>", time);
					invitationMailText = invitationMailText.replace("<<EVENT_DAY_AND_DATE>>", newFormat.format(event.getDate()));
					invitationMailText = invitationMailText.replace("<<EVENT_VENUE>>",
							event.getVenue().getName());

					sms.setMessage(invitationMailText);
				} catch (IOException ioe) {
					System.out.println("Error while reading OTP templates : " + ioe.getMessage());
				}

				sms.setTemplateId(passReject);
				imsSmsSender.send(sms);

				
//				emailService.sendEventCancelTicket(imsUser.getEmail(), "IMS Portal - Event Ticket",
//						event.getName(), event.getDate(), event.getVenue().getName());
			}

			imsResponse.setData(sanitizeUserData(passRepository.save(pass)));
			System.out.println("***************Cancelled Pass with the id " + pass.getId() + "******************");
			imsResponse.setMessage("Cancelled given pass");
			imsResponse.setSuccess(true);
		} else {
			imsResponse.setMessage("Unable to Cancel given pass");
			imsResponse.setSuccess(false);
		}

		return imsResponse;
	}

	@GetMapping("/getAllTickets/{filterDate}/{eventId}/{passSubcategoryId}/{roleId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('COUNTEREMP')")
	ImsResponse getAllTickets(@PathVariable("filterDate") String filterDate, @PathVariable("eventId") int eventId,
			@PathVariable("passSubcategoryId") int passSubcategoryId, @PathVariable("roleId") int roleId) {
		ImsResponse imsResponse = new ImsResponse();
		List<TicketsResponse> ticketsResponses = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		LocalDateTime fromDateTime = LocalDateTime.parse(filterDate + " 00:00:00.000", formatter);
		LocalDateTime toDateTime = LocalDateTime.parse(filterDate + " 23:59:59.999", formatter);
		List<PgTransactions> pgTransactionsList = pgTransactionsRepository
				.findAllByPassEventIdAndPassCreatedTimestampBetweenAndPassPassSubcategoryIdAndPassImsUserByImsUserIdRoleId(
						eventId, fromDateTime, toDateTime, passSubcategoryId, roleId);

		pgTransactionsList.forEach(pgTransactions -> {
			TicketsResponse ticketsResponse = new TicketsResponse();
			if (pgTransactions.getPass() != null) {
				ticketsResponse.setPass(sanitizeUserData(pgTransactions.getPass()));
				ticketsResponse.setTxnId(pgTransactions.getTransactionId());
				ticketsResponses.add(ticketsResponse);
			}
		});
		imsResponse.setData(ticketsResponses);
		imsResponse.setMessage("Retrieved Tickets for the event!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getAllCancelledTickets/{eventId}/{date}/{passSubcategoryId}/{mode}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllCancelledTickets(@PathVariable int eventId, @PathVariable String date, @PathVariable int passSubcategoryId, @PathVariable int mode) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		final LocalDateTime fromeDateTime = LocalDateTime.parse(date + " 00:00:00.000", formatter);
		final LocalDateTime toDateTime = LocalDateTime.parse(date + " 23:59:59.000", formatter);
		
		ImsResponse imsResponse = new ImsResponse();
		List<TicketsResponse> ticketsResponses = new ArrayList<>();
		List<PgTransactions> pgTransactionsList = pgTransactionsRepository
				.findAllByPassEventIdAndPassPassSubcategoryIdAndPassImsUserByImsUserIdRoleIdAndPassCreatedTimestampBetween(
						eventId, passSubcategoryId, mode, fromeDateTime, toDateTime);

		pgTransactionsList.forEach(pgTransactions -> {
			Pass pass = pgTransactions.getPass();
			if (pass != null && pass.getPassStatus().getId() == PassStatusEnum.CANCELLED.type && pass.getRemarks() != null) {
				TicketsResponse ticketsResponse = new TicketsResponse();
				ticketsResponse.setPass(sanitizeUserData(pass));
				ticketsResponse.setTxnId(pgTransactions.getTransactionId());
				ticketsResponses.add(ticketsResponse);
			}
		});
		imsResponse.setData(ticketsResponses);
		imsResponse.setMessage("Retrieved Tickets for the event!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@GetMapping("/getUserLimit/{eventId}/{imsUserId}")
	@PreAuthorize("hasRole('CITIZEN')")
	public ImsResponse getUserLimit(@PathVariable int eventId, @PathVariable int imsUserId) {
		ImsResponse imsResponse = new ImsResponse();
		Event event = eventRepository.findByIdAndDeleted(eventId, false);
		ImsUser imsUser = imsUserRepository.findByIdAndDeleted(imsUserId, false);

		if (event != null && imsUser != null) {
			List<Pass> passes = passRepository.findAllByImsUserByImsUserIdIdAndEventIdAndPassStatusIdAndDeleted(imsUser.getId(),
					event.getId(), PassStatusEnum.ALLOCATED.type, false);

			Resource resource = externalPassConfigReader.getFile();
			String dataFromExternalFile = JsonStringReader.getFileContent(resource);
			String userLimt = JsonStringReader.getValue(dataFromExternalFile, event.getEventCode(),
					ExternalPassConfigEnum.PUBLIC_PASS_USER_LIMIT.key);
			if (dataFromExternalFile == null || dataFromExternalFile.isEmpty() || userLimt == null
					|| userLimt.isEmpty())
				userLimt = "0";

			long userLimitNo = Long.parseLong(userLimt);
			long remainingQuota = 0;
			if (passes.size() < userLimitNo)
				remainingQuota = userLimitNo - passes.size();

			if (remainingQuota == 0) {
				imsResponse.setSuccess(false);
				imsResponse.setMessage("Pass quota for user exceeded");
			} else {
				imsResponse.setSuccess(true);
				imsResponse.setMessage("User can book pass");
			}
			imsResponse.setData(remainingQuota);

			return imsResponse;
		}

		imsResponse.setSuccess(false);
		imsResponse.setMessage("Unable to process request");
		return imsResponse;
	}

	@Override
	public ImsResponse getAll() {
		ImsResponse imsResponse = new ImsResponse();
		imsResponse.setData("");
		return imsResponse;
	}

	private Pass sanitizeUserData(Pass pass) {
		ImsUser user = pass.getImsUserByImsUserId();
		ImsUser invAdmin = pass.getImsUserByInvitationAdminId();
		if (pass.getPaidPassHolders() != null) {
			pass.getPaidPassHolders().setIdentityProofDocument(null);
			pass.getPaidPassHolders().setIdentityProofDocument2(null);
		}
		if (user != null) {
			user.setPassword(null);
			user.setPasswordHash(null);
			user.setPasswordEmail(null);
			user.setPasswordSalt(null);
			pass.setImsUserByImsUserId(user);
		}

		if (invAdmin != null) {
			invAdmin.setPassword(null);
			invAdmin.setPasswordHash(null);
			invAdmin.setPasswordSalt(null);
			invAdmin.setPasswordEmail(null);
			pass.setImsUserByInvitationAdminId(invAdmin);
		}
		return pass;
	}

	private List<Pass> sanitizeUserData(List<Pass> passes) {
		List<Pass> mPasses = new ArrayList<>();
		passes.forEach(pass -> {
			mPasses.add(sanitizeUserData(pass));
		});
		return mPasses;
	}

	@GetMapping("/getSamplePassesMapping")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getSamplePassesMapping() {
		ImsResponse responseData = new ImsResponse();
		try {
			InputStream inputStream = (InputStream) getClass().getResourceAsStream("/pass/SamplePassMapper.json");
			String readFileToString = IOUtils.toString(inputStream, Charset.defaultCharset());
			responseData.setSuccess(true);
			responseData.setData(readFileToString);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseData;
	}

	@PostMapping("/getPaidPassesSummary")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getPaidPassesSummary(@RequestBody PassStatsRequest requestObj) {
		ImsResponse responseData = new ImsResponse(); 
		final DateTimeFormatter inputDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

		Event eventDtls = eventRepository.findByIdAndDeleted(requestObj.getEventId(), false);
		if(null != eventDtls) {
			LocalDateTime fromDateObj = eventDtls.getCreatedTimestamp();
			LocalDateTime toDateObj = LocalDateTime.now();
			if(null != requestObj.getFromDate()) {
				fromDateObj = LocalDateTime.parse(requestObj.getFromDate() + " 00:00:00.000", inputDateTimeFormatter);
			}

			if(null != requestObj.getToDate()) {
				toDateObj = LocalDateTime.parse(requestObj.getToDate() + " 23:59:59.999", inputDateTimeFormatter);
			}
			System.out.println("Getting Paid Passes Summary for Event " + eventDtls.getId()+", between "+fromDateObj+" <-> "+toDateObj);
			
			List<PassStatsDto> responsePassList = new ArrayList<>();
			List<Integer> roles = new ArrayList<>();
			roles.add(RoleEnum.ROLE_COUNTEREMP.role);
			roles.add(RoleEnum.ROLE_CITIZEN.role);

			List<Integer> subCategories = new ArrayList<>();
			subCategories.add(PassSubcategoryEnum.INR20.type);
			subCategories.add(PassSubcategoryEnum.INR50.type);
			subCategories.add(PassSubcategoryEnum.INR100.type);
			subCategories.add(PassSubcategoryEnum.INR500.type);

			if(null != requestObj.getUserRoleIds() && requestObj.getUserRoleIds().size()>0) {
				roles.clear();
				roles.addAll(requestObj.getUserRoleIds());
			}

			if(null != requestObj.getPassSubcategoryIds() && requestObj.getPassSubcategoryIds().size()>0) {
				subCategories.clear();
				subCategories.addAll(requestObj.getPassSubcategoryIds());
			}

			List<Tuple> paidTickets = passRepository.getAllPassStatsData(eventDtls.getId(), roles, subCategories, fromDateObj, toDateObj, false);
			for (Tuple tpl : paidTickets) {
				PassStatsDto temp = new PassStatsDto();
				temp.setPassDate(tpl.get("passDate", String.class));
				temp.setMode(tpl.get("mode", String.class));
				String paymentType = tpl.get("paymentType", String.class);
				temp.setPaymentType(paymentType);
				if(paymentType.equalsIgnoreCase("Online")) {
					temp.setMode("Digital");
				}
				temp.setCount(tpl.get("count", BigInteger.class).longValue());
				temp.setTicketType(tpl.get("ticketType", String.class));
				if(null != requestObj.getMode() && !requestObj.getMode().isEmpty()) {
					if(requestObj.getMode().equalsIgnoreCase(temp.getMode())) {
						responsePassList.add(temp);
					}
				} else {
					responsePassList.add(temp);
				}
			}
			responseData.setSuccess(true);
			responseData.setData(responsePassList);
		} else {
			responseData.setSuccess(false);
			responseData.setMessage("Event not found");
		}
		return responseData;
	}
}
