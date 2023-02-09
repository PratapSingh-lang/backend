package in.co.bel.ims.initial.service.fwk;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import in.co.bel.ims.initial.service.dto.ImsResponse;
import in.co.bel.ims.initial.service.util.ImsJpaUpdateUtil;

public class ImsServiceTemplate<T, S extends ImsJPATemplate<T>> {

	@Autowired
	private S s;

	private boolean success = false;

	@GetMapping("/getAll")
	public ImsResponse getAll() {
		ImsResponse imsResponse = new ImsResponse();

		List<T> responseList = s.findAllByDeleted(Sort.by(Order.asc("id")), false);
		success = true;
		imsResponse.setSuccess(success);
		imsResponse.setData(responseList);
		imsResponse.setMessage("Data retrieved Successfully!");
		return imsResponse;

	}

	@GetMapping("/getById/{id}")
	public ImsResponse getById(@PathVariable int id) {
		ImsResponse imsResponse = new ImsResponse();
		Optional<T> response = s.findById(id);
		success = true;
		imsResponse.setSuccess(success);
		imsResponse.setMessage("Data retrieved Successfully!");
		imsResponse.setData(response.get());
		return imsResponse;
	}

	@PostMapping("/save")
	public T create(@Valid @RequestBody  T t) {
		try {
			Method[] declaredMethodsList = t.getClass().getDeclaredMethods();
			for (Method method : declaredMethodsList) {
				if (method.getName().contains("setCreatedTimestamp")) {
					t.getClass().getMethod("setCreatedTimestamp", LocalDateTime.class).invoke(t, LocalDateTime.now());
				}
			}

		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s.saveAndFlush(t);
	}

	@PostMapping("/saveAll")
	public List<T> createAll(@Valid @RequestBody List<T> t) {

		t.forEach(entity -> {
			try {
				Method[] declaredMethodsList = t.getClass().getDeclaredMethods();
				for (Method method : declaredMethodsList) {
					if (method.getName().contains("setCreatedTimestamp")) {
						t.getClass().getMethod("setCreatedTimestamp", LocalDateTime.class).invoke(t,
								LocalDateTime.now());
					}
				}

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return s.saveAllAndFlush(t);
	}

	@DeleteMapping("/deleteById/{id}")
	public void delete(@PathVariable int id) {
		T entityToDelete = s.findById(id).get();
		try {
			Field[] entities = entityToDelete.getClass().getDeclaredFields();
			for (Field field : entities) {
				if (field.getDeclaredAnnotation(JsonIgnore.class) != null) {
					@SuppressWarnings("unchecked")
					Set<T> referenceEntities = (Set<T>) entityToDelete.getClass()
							.getMethod("get" + StringUtils.capitalize(field.getName())).invoke(entityToDelete);
					referenceEntities.forEach(refEntity -> {
						try {
							refEntity.getClass().getMethod("setDeleted", Boolean.class).invoke(refEntity, true);
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
				}
			}
			entityToDelete.getClass().getMethod("setDeleted", Boolean.class).invoke(entityToDelete, true);
			s.save(entityToDelete);

		} catch (IllegalArgumentException | SecurityException | NoSuchMethodException | IllegalAccessException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	@PutMapping("/update")
	public ImsResponse update(@Valid @RequestBody T t) {
		ImsResponse imsResponse = new ImsResponse();
		Object sourceEntity = null;
		Object entityToUpdate = null;

		try {
			sourceEntity = s.findById((int) t.getClass().getMethod("getId").invoke(t));
			entityToUpdate = sourceEntity.getClass().getMethod("get").invoke(sourceEntity);
			ImsJpaUpdateUtil.copyEntityProperties(t, entityToUpdate);
			Method[] declaredMethodsList = t.getClass().getDeclaredMethods();
			for (Method method : declaredMethodsList) {
				if (method.getName().contains("setModifiedTimestamp")) {
					entityToUpdate.getClass().getMethod("setModifiedTimestamp", LocalDateTime.class)
							.invoke(entityToUpdate, LocalDateTime.now());
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		T response = s.save((T) entityToUpdate);
		success = true;
		imsResponse.setMessage("Data updated Successfully!");
		imsResponse.setSuccess(success);
		imsResponse.setData(response);
		return imsResponse;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, Object> handleEntityValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, Object> errors = new HashMap<>();
		Map<String, String> validationErrors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			validationErrors.put(fieldName, errorMessage);
		});
		errors.put("message", validationErrors);
		errors.put("success", false);
		return errors;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	public Map<String, Object> handleCustomExceptions(Exception ex) {
		ex.printStackTrace();
		Map<String, Object> errors = new HashMap<>();
		errors.put("success", false);
		errors.put("message", "Something went wrong at the server!");
		return errors;

	}
}
