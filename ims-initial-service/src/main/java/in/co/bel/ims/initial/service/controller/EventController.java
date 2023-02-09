package in.co.bel.ims.initial.service.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.EventRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.service.dto.EventOverview;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.PassStatusEnum;

@RestController@CrossOrigin
@RequestMapping("/app/event")
public class EventController extends ImsServiceTemplate<Event, EventRepository>{

	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	PassRepository passRepository;
	
	
	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('NODALOFFICER') or hasRole('HIGHEROFFICER') or hasRole('CITIZEN') or hasRole('COUNTEREMP')")
	public ImsResponse getAll() {
		// TODO Auto-generated method stub
		return super.getAll();
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse getById(@PathVariable int id) {
		// TODO Auto-generated method stub
		return super.getById(id);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public Event create(@Valid @RequestBody Event t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public List<Event> createAll(@Valid @RequestBody  List<Event> t) {
		// TODO Auto-generated method stub
		return super.createAll(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN')")
	public ImsResponse update(@Valid @RequestBody Event t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}

	@GetMapping("/getEventOverview/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public EventOverview getEventOverview(@PathVariable int eventId) {
		EventOverview eventOverview = new EventOverview();
		Event event = eventRepository.findByIdAndDeleted(eventId, false);
		eventOverview.setEvent(event);
		List<Pass> passList = passRepository.findByEventIdAndDeleted(eventId, false);
		long totalAccepted = passList.stream().filter(pass -> pass.getPassStatus() != null
				&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_ACCEPTED.type).collect(Collectors.toList()).size();
		long totalRejected = passList.stream().filter(pass -> pass.getPassStatus() != null
				&& pass.getPassStatus().getId() == PassStatusEnum.RSVP_REGRETTED.type).collect(Collectors.toList()).size();
		long totalAllocated = passList.stream().filter(pass -> pass.getPassStatus() != null
				&& pass.getPassStatus().getId() == PassStatusEnum.ALLOCATED.type).collect(Collectors.toList()).size();
		long totalAttended = passList.stream().filter(pass -> pass.getPassStatus() != null
				&& pass.getPassStatus().getId() == PassStatusEnum.ATTENDED.type).collect(Collectors.toList()).size();
		long totalIssued = passList.size();
		totalAllocated = totalAllocated + totalAccepted + totalRejected + totalAttended;
		long totalAvailable = 0;
		if(event.getTotalInvitations() != null) {
			totalAvailable = event.getTotalInvitations() - totalIssued;
		} 
		eventOverview.setTotalAccepted(totalAccepted);
		eventOverview.setTotalAllocated(totalAllocated);
		eventOverview.setTotalIssued(totalIssued);
		eventOverview.setTotalRejected(totalRejected);
		eventOverview.setTotalAvailable(totalAvailable);
		
		return eventOverview;
	}
}
