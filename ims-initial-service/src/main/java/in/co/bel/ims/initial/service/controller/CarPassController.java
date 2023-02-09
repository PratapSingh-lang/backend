package in.co.bel.ims.initial.service.controller;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.CarPassRepository;
import in.co.bel.ims.initial.data.repository.CplRepository;
import in.co.bel.ims.initial.data.repository.EnclosureCplMappingRepository;
import in.co.bel.ims.initial.data.repository.EnclosurePassTypeMappingRepository;
import in.co.bel.ims.initial.data.repository.EventRepository;
import in.co.bel.ims.initial.data.repository.PassCategoryRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.data.repository.PassStatusRepository;
import in.co.bel.ims.initial.entity.CarPass;
import in.co.bel.ims.initial.entity.Cpl;
import in.co.bel.ims.initial.entity.EnclosureCplMapping;
import in.co.bel.ims.initial.entity.EnclosurePassTypeMapping;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.service.dto.CarPassEligibilityRequest;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.ImsCipherUtil;
import in.co.bel.ims.initial.service.util.LogLevelEnum;
import in.co.bel.ims.initial.service.util.LogUtil;
import in.co.bel.ims.initial.service.util.PassCategoryEnum;
import in.co.bel.ims.initial.service.util.PassStatusEnum;
import in.co.bel.ims.initial.service.util.PassSubcategoryEnum;

@RestController
@CrossOrigin
@RequestMapping("/app/carPass")
public class CarPassController extends ImsServiceTemplate<CarPass, CarPassRepository> {

	@Autowired
	CarPassRepository carPassRepository;
	@Autowired
	CplRepository cplRepository;
	@Autowired
	PassStatusRepository passStatusRepository;
	@Autowired
	EventRepository eventRepository;
	@Autowired
	PassCategoryRepository passCategoryRepository;
	@Autowired
	PassRepository passRepository;
	@Autowired
	LogUtil log;
	@Autowired
	EnclosureCplMappingRepository enclosureCplMappingRepository;
	@Autowired
	EnclosurePassTypeMappingRepository enclosurePassTypeMappingRepository;

	@PostMapping("/assignCPLPass")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse assignCPLPass(@RequestBody CarPass carPass) {
		ImsResponse imsResponse = new ImsResponse();
		String carSalt = ImsCipherUtil.generateSalt();
		carPass.setControlSalt(carSalt);
		carPass.setPassStatus(passStatusRepository.findById(PassStatusEnum.INITIATED.type).get());
		carPass.setCpl(cplRepository.findById(carPass.getCpl().getId()).get());
		carPass.setPassCategory(passCategoryRepository.findById(PassCategoryEnum.CPL.type).get());
		carPass.setEvent(eventRepository.findById(carPass.getEvent().getId()).get());
		String carControlNo = generateCPLControlNo(carPass);
		carPass.setControlNo(carControlNo);
		carPass.setControlHash(ImsCipherUtil.generateHash(carControlNo, carSalt));
		imsResponse.setData(carPassRepository.save(carPass));
		log.saveLog(null, "CarPass with the control no " + carPass.getControlNo() + " has been assigned", "ASSIGN_PASS",
				LogLevelEnum.INFO);
		imsResponse.setMessage("Assigned CarPass!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	@GetMapping("/getCarPassByEvent/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN')")
	public ImsResponse getCarPassByEvent(@PathVariable int eventId) {
		ImsResponse imsResponse = new ImsResponse();
		List<CarPass> carPasses = carPassRepository.findByEventIdAndDeleted(eventId, false);
		imsResponse.setData(carPasses);
		imsResponse.setMessage("Retrieved CarPasses!");
		imsResponse.setSuccess(true);
		return imsResponse;
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
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
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public CarPass create(@Valid @RequestBody CarPass t) {
		// TODO Auto-generated method stub
		return super.create(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public List<CarPass> createAll(@Valid @RequestBody List<CarPass> t) {
		// TODO Auto-generated method stub
		return super.createAll(t);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public void delete(@PathVariable int id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN') or hasRole('INVITEE') or hasRole('NODALOFFICER') or hasRole('CITIZEN') or hasRole('COUNTEREMP')")
	public ImsResponse update(@Valid @RequestBody CarPass t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}

	private String generateCPLControlNo(CarPass pass) {
		Event event = eventRepository.findById(pass.getEvent().getId()).get();
		Cpl cpl = cplRepository.findById(pass.getCpl().getId()).get();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(event.getDate());
		int year = calendar.get(Calendar.YEAR);

//		Pattern: <YEAR>-<EVENT Code>-<Enclosure code>-<Sequence No> Sample: 2022-IDC-Enc1-0000001

		String controlNo = year + "-" + event.getEventCode() + "-" + cpl.getName() + "-"
				+ String.format("%04d", carPassRepository.getLastIdOfPass() + 1);
		return controlNo;
	}

	@PostMapping("/isEligibleForCarPass")
//	@PreAuthorize("hasRole('COUNTEREMP') or hasRole('CITIZEN')")
	public ImsResponse isEligibleForCarPass(@Valid @RequestBody CarPassEligibilityRequest carPassEligibilityRequest) {
		ImsResponse imsResponse = new ImsResponse();
		List<Pass> passes = passRepository.findAllByImsUserByImsUserIdIdAndEventIdAndPassStatusIdAndDeleted(
				carPassEligibilityRequest.getImsUserId(), carPassEligibilityRequest.getEventId(),  PassStatusEnum.ALLOCATED.type, false);
		List<EnclosurePassTypeMapping> enclosurePassTypeMappings = enclosurePassTypeMappingRepository
				.findByEventIdAndPassSubcategoryIdAndEnclosureEnclosureGroupIdAndDeleted(
						carPassEligibilityRequest.getEventId(), PassSubcategoryEnum.INR500.type,
						carPassEligibilityRequest.getEnclosureGroupId(), false);
		List<EnclosureCplMapping> cplMappings = null;
		if (enclosurePassTypeMappings != null && !enclosurePassTypeMappings.isEmpty()) {
			cplMappings = enclosureCplMappingRepository.findByEnclosureIdInAndDeleted(enclosurePassTypeMappings.stream()
					.map(enclosurePassTypeMapping -> enclosurePassTypeMapping.getEnclosure().getId())
					.collect(Collectors.toList()), false);
		}

		if (passes != null && passes.size() <= 0 && cplMappings != null && cplMappings.size() > 0) {
			imsResponse.setSuccess(true);
		}
		for (Pass pass : passes) {
			CarPass carPass = pass.getCarPass();
			if (carPass != null || (cplMappings != null && cplMappings.size() <= 0)) {
				imsResponse.setSuccess(false);
				return imsResponse;
			} else {
				imsResponse.setSuccess(true);
			}
		}
		return imsResponse;
	}

}
