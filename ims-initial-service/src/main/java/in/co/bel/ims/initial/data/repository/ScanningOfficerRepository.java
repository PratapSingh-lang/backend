package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.ScanningOfficer;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface ScanningOfficerRepository extends ImsJPATemplate<ScanningOfficer> {

	ScanningOfficer getByMobileNoAndDeleted(String mobileNo, boolean b);

	List<ScanningOfficer> getAllByUsher(boolean b);
   
}

