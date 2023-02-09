package in.co.bel.ims.initial.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import in.co.bel.ims.initial.entity.CarPass;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface CarPassRepository extends ImsJPATemplate<CarPass> {

	List<CarPass> findByDepartmentId(int departmentId);

	List<CarPass> findByEventIdAndDeleted(int eventId, boolean b);
	
	CarPass findByControlNoAndDeleted(String controlNo, boolean b);

	@Query(value = "select last_value from public.car_pass_seq", nativeQuery = true)
	int getLastIdOfPass();

	CarPass findByIdAndDeleted(int id, boolean b);

	List<CarPass> findByEventIdAndDeletedAndDepartmentIdIn(int eventId, boolean b, List<Integer> departments);

	List<CarPass> findByEventIdAndDeletedAndCplId(int eventId, boolean b, int cplId);

	List<CarPass> findByEventIdAndDeletedAndCplIdAndDepartmentIdIn(int eventId, boolean b, int cplId,
			List<Integer> departments);

	List<CarPass> findByEventIdAndDeletedAndCplIdIn(int eventId, boolean b, List<Integer> cplIds);
   
}

