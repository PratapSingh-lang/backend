package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.InvitationOfficer;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface InvitationOfficerRepository extends ImsJPATemplate<InvitationOfficer> {

	List<InvitationOfficer> findByEventIdAndDeleted(int eventId, boolean deleted);

	List<InvitationOfficer> findByImsUserId(int imsUserId);

	List<InvitationOfficer> findByImsUserIdAndEventIdAndDeleted(int imsUserId, int eventId, boolean b);
   
}

