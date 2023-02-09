package in.co.bel.ims.initial.data.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.co.bel.ims.initial.entity.PassDayLimit;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface PassDayLimitRepository extends ImsJPATemplate<PassDayLimit> {

	@Query("from PassDayLimit a where a.event.id = :eventId AND a.date BETWEEN :fromDate AND :toDate")
	List<PassDayLimit> getAllBetweenDates(@Param("fromDate")LocalDate fromDate, @Param("toDate")LocalDate toDate, @Param("eventId")int eventId);

	List<PassDayLimit> findAllByDateAndDeleted(LocalDate now, boolean b);

}

