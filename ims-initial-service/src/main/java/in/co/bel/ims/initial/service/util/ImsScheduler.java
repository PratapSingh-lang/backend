package in.co.bel.ims.initial.service.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import in.co.bel.ims.initial.data.repository.PaidPassHoldersRepository;
import in.co.bel.ims.initial.data.repository.PassDayLimitCategoryRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.data.repository.PassStatusRepository;
import in.co.bel.ims.initial.data.repository.PgTransactionsRepository;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.entity.PassDayLimitCategory;
import in.co.bel.ims.initial.entity.PassStatus;
import in.co.bel.ims.initial.entity.PgTransactions;
import in.co.bel.ims.initial.service.dto.PgTxnVerifiedResponse;

@Configuration
@EnableScheduling
public class ImsScheduler {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PgTransactionsRepository pgTransactionsRepository;

	@Autowired
	PassStatusRepository passStatusRepository;

	@Autowired
	PassRepository passRepository;

	@Autowired
	PaidPassHoldersRepository paidPassHoldersRepository;
	@Autowired
	PassDayLimitCategoryRepository passDayLimitCategoryRepository;

	@Value("${PAYU_VERIFY_TXN_URL}")
	private String verifyTxnURL;
	@Value("${PAYU_TXN_KEY}")
	private String txnKey;
	@Value("${PAYU_TXN_SALT}")
	private String txnSalt;
	@Value("${PAYU_TXN_COMMAND}")
	private String txnCommand;

	private final String PIPE = "|";

	private final Gson gson = new GsonBuilder().create();

	@Scheduled(cron = "${TXN_VERIFY_SCHEDULED_CRON_EXP}")
	private void verifyTxs() {

		List<PgTransactions> listOfPendingApprovalPasses = pgTransactionsRepository.findByPassPassCategoryIdAndPassPassStatusId(PassCategoryEnum.PAIDTICKET.type, PassStatusEnum.PENDING_APPROVAL.type);
		List<String> txnIds = listOfPendingApprovalPasses.stream().filter(item -> null != item.getTransactionId()).map(pgtx -> pgtx.getTransactionId()).collect(Collectors.toList());
		System.out.println("No. Payment Txns to verify : "+txnIds.size());
		String var1 = StringUtils.join(txnIds, PIPE);

		String dataToHash = txnKey + PIPE + txnCommand + PIPE + var1 + PIPE + txnSalt;
		String hashedData = ImsCipherUtil.generateHash(dataToHash);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("key", txnKey);
		requestBody.add("command", txnCommand);
		requestBody.add("var1", var1);
		requestBody.add("hash", hashedData);

		@SuppressWarnings("rawtypes")
		HttpEntity<MultiValueMap> entity = new HttpEntity<MultiValueMap>(requestBody, headers);

		String responseStr = restTemplate.exchange(verifyTxnURL, HttpMethod.POST, entity, String.class).getBody();
		try {
			PgTxnVerifiedResponse pgTransactionsResponse = gson.fromJson(responseStr, PgTxnVerifiedResponse.class);
			if (pgTransactionsResponse != null) {
				System.out.println("Transaction Status : "+pgTransactionsResponse.getStatus());
				System.out.println("Transaction Msg : "+pgTransactionsResponse.getMsg());
				if (pgTransactionsResponse.getStatus().equals(1)) {
					pgTransactionsResponse.getTransaction_details().values().forEach(txnDtl -> {
						Integer status = txnDtl.getStatus().equalsIgnoreCase("success") ? 1 : 0;
						List<PgTransactions> pgTransactions = pgTransactionsRepository.findByTransactionId(txnDtl.getTxnid());
						pgTransactions.stream().forEach(en -> {
							en.setStatus(status);
							Pass passObj = en.getPass();
							Pass passDataToUpdate = passRepository.findByIdAndImsUserByImsUserIdRoleId(passObj.getId(),  RoleEnum.ROLE_CITIZEN.role);
							if (passDataToUpdate != null) {

								if (0 == status) {
									en.setErrorCode(txnDtl.getError_code());
									PassStatus passStatus = passStatusRepository.findById(PassStatusEnum.CANCELLED.type)
											.get();
									passDataToUpdate.setPassStatus(passStatus);
								} else {
									PassStatus passStatus = passStatusRepository.findById(PassStatusEnum.ALLOCATED.type)
											.get();
									passDataToUpdate.setPassStatus(passStatus);
								}
								System.out.println("Txn: " + txnDtl.getTxnid() + " -> " + txnDtl.getStatus() + " at "
										+ passDataToUpdate.getCreatedTimestamp());
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
	}

	@Scheduled(cron = "${TXN_VERIFY_SCHEDULED_CRON_EXP}")
	private void deleteInvalidPassHolders() {
		System.out.println("Deleting invalid pass holders data");
		paidPassHoldersRepository.deleteAllBymobileNoAndIdentityProofNumber(null, null);
	}
	
	@Scheduled(cron = "${TKT_RELEASE_CRON_EXP}")
	private void ticketsRelease() {
		System.out.println("Releasing Tickets now at "+LocalDateTime.now());
		List<PassDayLimitCategory> dayLimitCategories = passDayLimitCategoryRepository.findAllByRoleIdAndPassDayLimitDate(RoleEnum.ROLE_CITIZEN.role, LocalDate.now());
		for (PassDayLimitCategory passDayLimitCategory : dayLimitCategories) {
			passDayLimitCategory.setDeleted(false);
		}
		passDayLimitCategoryRepository.saveAll(dayLimitCategories);
		
	}
	
	
}
