package in.co.bel.ims.initial.data.repository;

import java.time.LocalDateTime;
import java.util.List;

import in.co.bel.ims.initial.entity.PgTransactions;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface PgTransactionsRepository extends ImsJPATemplate<PgTransactions>{

	List<PgTransactions> findByTransactionId(String transactionId);

	PgTransactions findByPassId(int id);

	List<PgTransactions> findByPassPassCategoryIdAndPassPassStatusId(int categoryId, int statusId);

	List<PgTransactions> findAllByPassEventIdAndPassCreatedTimestampBetweenAndPassPassSubcategoryIdAndPassImsUserByImsUserIdRoleId(
			int eventId, LocalDateTime fromDateTime, LocalDateTime toDateTime, int passSubcategoryId, int roleId);

	List<PgTransactions> findAllByPassEventId(int eventId);

	List<PgTransactions> findAllByPassEventIdAndPassPassSubcategoryIdAndPassImsUserByImsUserIdRoleIdAndPassCreatedTimestampBetween(
			int eventId, int passSubcategory, int mode, LocalDateTime fromeDateTime, LocalDateTime toDateTime);
}
