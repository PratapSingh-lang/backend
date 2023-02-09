package in.co.bel.ims.initial.service.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.co.bel.ims.initial.data.repository.EnclosureGroupRepository;
import in.co.bel.ims.initial.data.repository.EventRepository;
import in.co.bel.ims.initial.data.repository.PaidPassHoldersRepository;
import in.co.bel.ims.initial.data.repository.PassDayLimitCategoryRepository;
import in.co.bel.ims.initial.data.repository.PassDayLimitRepository;
import in.co.bel.ims.initial.data.repository.PassRepository;
import in.co.bel.ims.initial.data.repository.PassSubcategoryRepository;
import in.co.bel.ims.initial.entity.EnclosureGroup;
import in.co.bel.ims.initial.entity.Event;
import in.co.bel.ims.initial.entity.Pass;
import in.co.bel.ims.initial.entity.PassDayLimit;
import in.co.bel.ims.initial.entity.PassDayLimitCategory;
import in.co.bel.ims.initial.entity.PassSubcategory;
import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.dto.PassDayLimitCategoryRequest;
import in.co.bel.ims.initial.service.dto.PassSubcategoryByDayLimitRequest;
import in.co.bel.ims.initial.service.fwk.ImsServiceTemplate;
import in.co.bel.ims.initial.service.util.ImsJpaUpdateUtil;
import in.co.bel.ims.initial.service.util.PassCategoryEnum;
import in.co.bel.ims.initial.service.util.PassStatusEnum;
import in.co.bel.ims.initial.service.util.RoleEnum;

@RestController
@CrossOrigin
@RequestMapping("/app/passDayLimitCategory")
public class PassDayLimitCategoryController
		extends ImsServiceTemplate<PassDayLimitCategory, PassDayLimitCategoryRepository> {

	@Autowired
	PassDayLimitRepository passDayLimitRepository;
	@Autowired
	PassDayLimitCategoryRepository passDayLimitCategoryRepository;
	@Autowired
	EventRepository eventRepository;
	@Autowired
	EnclosureGroupRepository enclosureGroupRepository;
	@Autowired
	PassSubcategoryRepository passSubcategoryRepository;
	@Autowired
	PaidPassHoldersRepository paidPassHoldersRepository;
	@Autowired
	PassRepository passRepository;
	
	
	@GetMapping("/getAllByEvent/{eventId}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse getAllByEvent(@PathVariable int eventId) {
		ImsResponse imsResponse = new ImsResponse();
		List<PassDayLimitCategory> passDayLimitCategoryList = passDayLimitCategoryRepository.findAll();
		passDayLimitCategoryList = passDayLimitCategoryList.stream().filter(item -> item.getPassDayLimit() != null
				&& item.getPassDayLimit().getEvent() != null && item.getPassDayLimit().getEvent().getId() == eventId).collect(Collectors.toList());
		imsResponse.setData(passDayLimitCategoryList);
		imsResponse.setMessage("Data retrieved successfully");
		imsResponse.setSuccess(true);
		return imsResponse;
	}
	
	
	@PostMapping("/saveWithDateRange")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse create(@Valid @RequestBody PassDayLimitCategoryRequest passDayLimitCategoryRequest) {
		ImsResponse imsResponse = new ImsResponse();
		LocalDate startDate = passDayLimitCategoryRequest.getPassDayLimit().getStartDate();
		Event event = eventRepository.findByIdAndDeleted(passDayLimitCategoryRequest.getPassDayLimit().getEvent(),
				false);
		EnclosureGroup enclosureGroup = enclosureGroupRepository
				.findById(passDayLimitCategoryRequest.getPassDayLimitCategory().getEnclosureGroup().getId()).get();
		PassSubcategory passSubcategory = passSubcategoryRepository
				.findById(passDayLimitCategoryRequest.getPassDayLimitCategory().getPassSubcategory().getId()).get();

		LocalDate endDate = passDayLimitCategoryRequest.getPassDayLimit().getEndDate();

		List<LocalDate> dateRange = getDatesBetween(startDate, endDate);

		for (LocalDate localDate : dateRange) {
			PassDayLimitCategory passDayLimitCategoryExisting = passDayLimitCategoryRepository
					.findByPassDayLimitEventIdAndPassDayLimitDateAndRoleIdAndPassSubcategoryIdAndEnclosureGroupId(
							event.getId(), localDate,
							passDayLimitCategoryRequest.getPassDayLimitCategory().getRole().getId(),
							passDayLimitCategoryRequest.getPassDayLimitCategory().getPassSubcategory().getId(),
							passDayLimitCategoryRequest.getPassDayLimitCategory().getEnclosureGroup().getId());
			if (passDayLimitCategoryExisting == null) {
				PassDayLimitCategory passDayLimitCategory = new PassDayLimitCategory();
				passDayLimitCategory.setPassLimit(passDayLimitCategoryRequest.getPassDayLimitCategory().getPassLimit());
				passDayLimitCategory.setEnclosureGroup(enclosureGroup);
				passDayLimitCategory.setPassSubcategory(passSubcategory);
				passDayLimitCategory.setRole(passDayLimitCategoryRequest.getPassDayLimitCategory().getRole());
				PassDayLimit passDayLimit = new PassDayLimit();
				passDayLimit.setDate(localDate);
				passDayLimit.setEvent(event);
				passDayLimit = passDayLimitRepository.save(passDayLimit);
				passDayLimitCategory.setPassDayLimit(passDayLimit);
				if(passDayLimitCategoryRequest.getPassDayLimitCategory().getRole().getId() == RoleEnum.ROLE_CITIZEN.role)
					passDayLimitCategory.setDeleted(true);
				super.create(passDayLimitCategory);
				imsResponse.setSuccess(true);
				imsResponse.setData("Created Successfully");
			} else {
				imsResponse.setSuccess(false);
				imsResponse.setData("Day limit data for the Date - " + localDate + ", Event - " + event.getName()
						+ ", Enclosure Group - " + enclosureGroup.getName() + ", Pass Subcategory - "
						+ passSubcategory.getName() + " already exists! ");
				return imsResponse;
			}
		}

		return imsResponse;
	}
	
	@PostMapping("/getAllPassSubcategory")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('NODALOFFICER') or hasRole('INVITATIONADMIN') or hasRole('CITIZEN') or hasRole('COUNTEREMP')")
	List<PassSubcategory> getAllPassSubcategory(@RequestBody PassSubcategoryByDayLimitRequest passSubcategoryRequest){
		List<PassDayLimitCategory> passDayLimitCategories = passDayLimitCategoryRepository.findAllByPassDayLimitEventIdAndPassDayLimitDateAndRoleIdAndDeleted(
				passSubcategoryRequest.getEventId(), passSubcategoryRequest.getForDate(),
				passSubcategoryRequest.getRoleId(), false);
		
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		final LocalDateTime fromDateTime = LocalDateTime.parse(LocalDate.now() + " 00:00:00.000", formatter);
		final LocalDateTime toDateTime = LocalDateTime.parse(LocalDate.now() + " 23:59:59.999", formatter);
		
		
		
		Map<Integer, PassSubcategory> uniquePassSubcategorys = new HashMap<>();
		for (PassDayLimitCategory passDayLimitCategory : passDayLimitCategories) {

			int exhaustedTickets = passRepository
					.findByPassCategoryIdAndPassSubcategoryIdAndEventIdAndCreatedTimestampBetweenAndEnclosureEnclosureGroupIdAndImsUserByImsUserIdRoleIdAndPassStatusIdNotAndDeleted(
							PassCategoryEnum.PAIDTICKET.type, passDayLimitCategory.getPassSubcategory().getId(),
							passSubcategoryRequest.getEventId(), fromDateTime, toDateTime,
							passDayLimitCategory.getEnclosureGroup().getId(), passSubcategoryRequest.getRoleId(),
							PassStatusEnum.CANCELLED.type, false)
					.size();
			int releasedTickets = passDayLimitCategory.getPassLimit();

			if (passDayLimitCategory.getPassSubcategory() != null && (releasedTickets - exhaustedTickets) > 0) {
				System.out.println(exhaustedTickets + " PassDayLimitCategoryController.getAllPassSubcategory() "
						+ releasedTickets);
				if (uniquePassSubcategorys.get(passDayLimitCategory.getPassSubcategory().getId()) == null)
					uniquePassSubcategorys.put(passDayLimitCategory.getPassSubcategory().getId(),
							passDayLimitCategory.getPassSubcategory());
			}
		}    
		

		
		
		List<PassSubcategory> passSubcategorysDistinct = uniquePassSubcategorys.values().stream().collect(Collectors.toList());
		
		return passSubcategorysDistinct;
	}
	
//	@GetMapping("/getTicketAvailabilityStats")
//	List<TicketStatsDto> getTicketAvailabilityStats() {
//
//		List<TicketStatsDto> ticketStatsDtos = new ArrayList<>();
//		List<Event> events = eventRepository.findAllByDeleted(false);
//
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
//		final LocalDateTime fromDateTime = LocalDateTime.parse(LocalDate.now() + " 00:00:00.000", formatter);
//		final LocalDateTime toDateTime = LocalDateTime.parse(LocalDate.now() + " 23:59:59.999", formatter);
//		
//		for (Event event : events) {
//			TicketStatsDto ticketStatsDto = new TicketStatsDto();
//			Map<String, Integer> passTypeToCount = new HashMap<>();
//			List<PassDayLimitCategory> passDayLimitCategories = passDayLimitCategoryRepository
//					.findAllByPassDayLimitEventIdAndRoleIdAndPassDayLimitDateAndDeleted(event.getId(),
//							RoleEnum.ROLE_CITIZEN.role, LocalDate.now(), false);
//			ticketStatsDto.setEventName(event.getName());
//			for (PassDayLimitCategory passDayLimitCategory : passDayLimitCategories) {
//				int count = passRepository.getCountOfCurrentDateAndEventIdAndPassSubCategoryAndPassStatusNotAndDeleted(
//						fromDateTime, toDateTime, event.getId(), passDayLimitCategory.getPassSubcategory().getId(),
//						PassStatusEnum.CANCELLED.type);
//				String passSubcategory = passDayLimitCategory.getPassSubcategory().getName();
//				if (passTypeToCount.containsKey(passSubcategory)) {
//					int countExisting = (passTypeToCount.get(passSubcategory) + passDayLimitCategory.getPassLimit())
//							- count;
//					passTypeToCount.put(passSubcategory, countExisting);
//				} else {
//					passTypeToCount.put(passSubcategory, (passDayLimitCategory.getPassLimit() - count));
//				}
//				
//			}
//			ticketStatsDto.setStatsData(passTypeToCount);
//			ticketStatsDtos.add(ticketStatsDto);
//			
//		}
//		
//
//		return ticketStatsDtos;
//	}
	
	@PostMapping("/getPaidTicketAvailability")
	@PreAuthorize("hasRole('CITIZEN') or hasRole('COUNTEREMP')")
	Map<String, Map<String, Integer>> getPaidTicketAvailability(@RequestBody PassSubcategoryByDayLimitRequest passSubcategoryRequest){
		List<PassDayLimitCategory> passDayLimitCategories = passDayLimitCategoryRepository
				.findAllByPassDayLimitEventIdAndPassDayLimitDateAndRoleIdAndDeleted(passSubcategoryRequest.getEventId(),
						passSubcategoryRequest.getForDate(), passSubcategoryRequest.getRoleId(), false);
		
		List<Pass> listOfPasses = passRepository
				.findAllByEventIdAndPassCategoryIdAndPassStatusIdNotAndImsUserByImsUserIdRoleIdAndCreatedTimestampBetween(
						passSubcategoryRequest.getEventId(), PassCategoryEnum.PAIDTICKET.type, PassStatusEnum.CANCELLED.type,
						passSubcategoryRequest.getRoleId(), passSubcategoryRequest.getForDate().atStartOfDay(),
				passSubcategoryRequest.getForDate().atTime(LocalTime.of(23, 59, 59)));
		
		Map<String, Map<String, Integer>> existingPaidPassSubcategoryWise = new HashMap<String, Map<String, Integer>>();
		for (Pass pass : listOfPasses) {
			if (pass.getPassSubcategory() != null && pass.getEnclosure() != null && pass.getEnclosure().getEnclosureGroup() != null) {
				if (existingPaidPassSubcategoryWise.get(pass.getPassSubcategory().getName()) == null) {
					Map<String, Integer> enclosureGroupWiseLimit = new HashMap<String, Integer>();
					enclosureGroupWiseLimit.put(pass.getEnclosure().getEnclosureGroup().getName(), 1);
					existingPaidPassSubcategoryWise.put(pass.getPassSubcategory().getName(),
							enclosureGroupWiseLimit);
				} else {
					Map<String, Integer> enclosureGroupWiseLimit = existingPaidPassSubcategoryWise
							.get(pass.getPassSubcategory().getName());
					if (enclosureGroupWiseLimit.get(pass.getEnclosure().getEnclosureGroup().getName()) == null)
						enclosureGroupWiseLimit.put(pass.getEnclosure().getEnclosureGroup().getName(), 1);
					else
						enclosureGroupWiseLimit.put(pass.getEnclosure().getEnclosureGroup().getName(),
								enclosureGroupWiseLimit.get(pass.getEnclosure().getEnclosureGroup().getName()) + 1);
				}
			}
		}

		Map<String, List<PassDayLimitCategory>> uniquePassSubcategorys = new HashMap<>();
		for (PassDayLimitCategory passDayLimitCategory : passDayLimitCategories) {
			if (passDayLimitCategory.getPassSubcategory() != null) {
				if (uniquePassSubcategorys.get(passDayLimitCategory.getPassSubcategory().getName()) == null) {
					List<PassDayLimitCategory> dayLimitCategoryList = new ArrayList<PassDayLimitCategory>();
					dayLimitCategoryList.add(passDayLimitCategory);
					uniquePassSubcategorys.put(passDayLimitCategory.getPassSubcategory().getName(),
							dayLimitCategoryList);
				} else {
					List<PassDayLimitCategory> dayLimitCategoryList = uniquePassSubcategorys
							.get(passDayLimitCategory.getPassSubcategory().getName());
					dayLimitCategoryList.add(passDayLimitCategory);
				}
			}
		}
		
		Map<String, Map<String, Integer>> passSubcategoryWiseLimit = new HashMap<>();

		for (String key : uniquePassSubcategorys.keySet()) {
			if (passSubcategoryWiseLimit.get(key) == null) {
				Map<String, Integer> enclosureGroupWiseLimit = new HashMap<String, Integer>();
				List<PassDayLimitCategory> dayLimitCategoryList = uniquePassSubcategorys.get(key);
				for (PassDayLimitCategory dayLimitCategory : dayLimitCategoryList) {
					if (enclosureGroupWiseLimit.get(dayLimitCategory.getEnclosureGroup().getName()) == null)
						enclosureGroupWiseLimit.put(dayLimitCategory.getEnclosureGroup().getName(),
								dayLimitCategory.getPassLimit());
					else
						enclosureGroupWiseLimit.put(dayLimitCategory.getEnclosureGroup().getName(),
								enclosureGroupWiseLimit.get(dayLimitCategory.getEnclosureGroup().getName())
										+ dayLimitCategory.getPassLimit());
				}
				Map<String, Integer> enclosureGroupWiseBookedPassCount = existingPaidPassSubcategoryWise.get(key);
				if (enclosureGroupWiseBookedPassCount != null) {
					for (String bookedEnclosureGroupKey : enclosureGroupWiseBookedPassCount.keySet()) {
						int bookedCount = enclosureGroupWiseBookedPassCount.get(bookedEnclosureGroupKey);
						if (enclosureGroupWiseLimit.get(bookedEnclosureGroupKey) != null) {
							if(bookedCount < enclosureGroupWiseLimit.get(bookedEnclosureGroupKey))
								enclosureGroupWiseLimit.put(bookedEnclosureGroupKey,
										enclosureGroupWiseLimit.get(bookedEnclosureGroupKey) - bookedCount);
							else
								enclosureGroupWiseLimit.put(bookedEnclosureGroupKey, 0);
						}
					}
				}
				passSubcategoryWiseLimit.put(key, enclosureGroupWiseLimit);
			}
		}
		
		return passSubcategoryWiseLimit;
	}

	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public List<PassDayLimitCategory> createAll(@Valid List<PassDayLimitCategory> passDayLimitCategorys) {
		for (PassDayLimitCategory passDayLimitCategory : passDayLimitCategorys) {
			PassDayLimit passDayLimit = passDayLimitRepository.save(passDayLimitCategory.getPassDayLimit());
			passDayLimitCategory.setPassDayLimit(passDayLimit);
		}
		return super.createAll(passDayLimitCategorys);
	}
	
	private static List<LocalDate> getDatesBetween(
			LocalDate startDate, LocalDate endDate) {

		long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		List<LocalDate> dateList = IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween)
				.mapToObj(i -> startDate.plusDays(i)).collect(Collectors.toList());
		dateList.add(endDate);
		return dateList;
	}
	
	@Override
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public void delete(@PathVariable int id) {
		passDayLimitCategoryRepository.deleteById(id);
	}
	
	public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Map<String, Integer>> passSubcategoryWiseLimit = new HashMap<>();
		Map<String, Integer> enclosureGroupWiseLimit = new HashMap<String, Integer>();
		enclosureGroupWiseLimit.put("North", 20);
		enclosureGroupWiseLimit.put("South", 50);
		passSubcategoryWiseLimit.put("INR20", enclosureGroupWiseLimit);
		System.out.println(objectMapper.writeValueAsString(passSubcategoryWiseLimit));;
		
	}
	
	@PutMapping("/update")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('INVITATIONADMIN')")
	public ImsResponse update(@Valid @RequestBody PassDayLimitCategory passDayLimitCategory) {
		ImsResponse imsResponse = new ImsResponse();
		PassDayLimitCategory entityToUpdate = passDayLimitCategoryRepository.findById(passDayLimitCategory.getId()).get(); 
		boolean deleted = entityToUpdate.getDeleted();	
		System.out.println("PassDayLimitCategoryController.update() "+deleted);
		ImsJpaUpdateUtil.copyEntityProperties(passDayLimitCategory, entityToUpdate);
		entityToUpdate.setDeleted(deleted);
		PassDayLimitCategory response = passDayLimitCategoryRepository.save(entityToUpdate);
		imsResponse.setMessage("Pass Day Limit Data updated Successfully!");
		imsResponse.setSuccess(true);
		imsResponse.setData(response);
		return imsResponse;
	}
}
