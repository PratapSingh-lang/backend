package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.UserIpaddress;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface UserIpaddressRepository extends ImsJPATemplate<UserIpaddress> {
	List<UserIpaddress> findByIpaddressAndDeleted(String ipaddress, boolean deleted);
}

