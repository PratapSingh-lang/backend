package in.co.bel.ims.initial.data.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import in.co.bel.ims.initial.entity.PaidPassHolders;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface PaidPassHoldersRepository extends ImsJPATemplate<PaidPassHolders> {

	//List<PaidPassHolders> findAllByStartDateLessThanEqualAndEndDateGreaterThanEqual(Date fromDate, Date toDate);
	
	@Query("from PaidPassHolders a where a.event.id = :eventId AND createdTimestamp BETWEEN :startDate AND :endDate")
	List<PaidPassHolders> getAllBetweenDates(@Param("startDate")LocalDateTime startDate, @Param("endDate")LocalDateTime endDate, @Param("eventId") int eventId);

	@Query("from PaidPassHolders a where a.event.id = :eventId AND a.passSubcategory.id = :passSubcategoryId AND a.enclosureGroup.id = :enclosureGroupId AND createdTimestamp BETWEEN :startDate AND :endDate")
	List<PaidPassHolders> getAllRecordsBetweenDates(@Param("startDate")LocalDateTime atStartOfDay, @Param("endDate")LocalDateTime atStartOfDay2,
			@Param("eventId")int eventId,@Param("enclosureGroupId") int enclosureGroupId, @Param("passSubcategoryId")int passSubcategoryId);

	@Transactional
	void deleteAllBymobileNoAndIdentityProofNumber(String mobileNo, String identityProofNumber);

	List<PaidPassHolders> findByImsUserId(int id);
}

