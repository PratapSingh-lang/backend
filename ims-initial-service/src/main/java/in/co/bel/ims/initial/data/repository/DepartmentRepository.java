package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.Department;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface DepartmentRepository extends ImsJPATemplate<Department> {

	List<Department> findByOrganizationIdAndDeleted(int id, boolean deleted);

	Department findByName(String name);
   
}

