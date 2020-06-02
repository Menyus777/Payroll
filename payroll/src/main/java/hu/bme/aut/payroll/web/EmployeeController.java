package hu.bme.aut.payroll.web;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import hu.bme.aut.payroll.config.EmailConfig;
import hu.bme.aut.payroll.domain.Employee;
import hu.bme.aut.payroll.domain.JobType;
import hu.bme.aut.payroll.domain.User;
import hu.bme.aut.payroll.domain.WorkStatus;
import hu.bme.aut.payroll.dto.EmployeeCollectionDto;
import hu.bme.aut.payroll.dto.EmployeeDto;
import hu.bme.aut.payroll.dto.ModifyEmployeeDto;
import hu.bme.aut.payroll.repository.EmployeeRepository;
import hu.bme.aut.payroll.repository.JobTypeRepository;
import hu.bme.aut.payroll.repository.UserRepository;
import hu.bme.aut.payroll.web.service.auth.Roles;
import hu.bme.aut.payroll.web.service.domain.PaymentCalculator;
import hu.bme.aut.payroll.web.service.email.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController extends ControllerBase{

	private final EmployeeRepository employeeRepository;
	private final JobTypeRepository jobTypeRepository;
	private final UserRepository userRepository;
	private final PaymentCalculator paymentCalculator;
	private final EmailService emailService;

	public EmployeeController(EmployeeRepository employeeRepository, JobTypeRepository jobTypeRepository,
		UserRepository userRepository, PaymentCalculator paymentCalculator, EmailService emailService) {
		this.employeeRepository = employeeRepository;
		this.jobTypeRepository = jobTypeRepository;
		this.userRepository = userRepository;
		this.paymentCalculator = paymentCalculator;
		this.emailService = emailService;
	}

	/**
	 * Gets all the employees from the database
	 * @return all the employees in the database
	 */
	@GetMapping
	@Secured(Roles.ADMIN)
	public ResponseEntity<EmployeeCollectionDto> getAll() {
		LogStepIn();

		List<Employee> employeeList = employeeRepository.findAll();
		EmployeeCollectionDto employeeCollectionDto = new EmployeeCollectionDto();

		for (Employee employee : employeeList) {
			employeeCollectionDto.collection.add(toDto(employee));
		}

		return LogStepOut(ResponseEntity.ok(employeeCollectionDto));
	}

	/**
	 * Gets the specific employee by id
	 * @param id the unique identifier of the JobType
	 * @return job type data transfer object
	 */
	@GetMapping("{id}")
	@Secured(Roles.ADMIN)
	public ResponseEntity<EmployeeDto> getById(@PathVariable long id) {
		LogStepIn();

		Employee employee = employeeRepository.findById(id);

		if (employee == null)
			return LogStepOut(ResponseEntity.notFound().build());
		else
			return LogStepOut(ResponseEntity.ok(toDto(employee)));
	}

	/**
	 * Gets all the employees that directly belongs to the current user
	 * @return employee collection data transfer object
	*/
	@GetMapping("/my-employee")
	@Secured(Roles.BOSS)
	public ResponseEntity<EmployeeCollectionDto> getAllMyEmployees() {
		LogStepIn();

		// Gets the currently logged in user's name
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Long bossId = userRepository.findByName(username).getEmployee().getId();

		// Adds the employees that directly belongs to this boss
		EmployeeCollectionDto employeeCollectionDto = new EmployeeCollectionDto();
		List<Employee> employeeList = employeeRepository.findAll();
		for(Employee employee : employeeList) {
			if(employee.getBossId().equals(bossId))
				employeeCollectionDto.collection.add(toDto(employee));
		}

		return LogStepOut(ResponseEntity.ok(employeeCollectionDto));
	}

	/**
	 * Gets the currently logged in user employee info
	 * @return the currently logged in employee info
	 */
	@GetMapping("/my-info")
	@Secured({Roles.ADMIN, Roles.BOSS, Roles.WORKER})
	public ResponseEntity<EmployeeDto> getMyInfo() {
		LogStepIn();

		// Gets the currently logged in user's name
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Employee employee = userRepository.findByName(username).getEmployee();
		return LogStepOut(ResponseEntity.ok(toDto(employee)));
	}

	/**
	 * Creates a new employee
	 * @param employeeDto the to be created employee
	 * @return the created employee
	 */
	@PostMapping
	@Secured(Roles.ADMIN)
	public ResponseEntity<?> create(@Valid @RequestBody EmployeeDto employeeDto, @RequestParam(required = false) Boolean isBoss) {
		LogStepIn();

		// Checking if the job type specified in the dto exists or not
		JobType jobType = jobTypeRepository.findById(employeeDto.jobTypeId.longValue());
		if(jobType == null)
			return LogStepOut(ResponseEntity.badRequest().body("The Job Type you are trying to bind does not exist!"));

		// Validation for name & e-mail attribute
		List<Employee> employeeList = employeeRepository.findAll();
		for (Employee employeeElement : employeeList) {
			if(employeeElement.getName().equals(employeeDto.name) || employeeElement.getEmail().equals(employeeDto.email))
				return LogStepOut(ResponseEntity.badRequest().body("An employee with this name and email combination already exists!"));
		}

		// Validation for work status
		if(WorkStatus.get(employeeDto.workStatus) == null)
			return LogStepOut(ResponseEntity.badRequest().body("The work status of the employee is invalid!"));

		// Saving the entity
		Employee employee = toEntity(employeeDto, jobType);

		// Saving the entity
		Employee createdEmployee = employeeRepository.save(employee);
		EmployeeDto createdEmployeeDto = toDto(createdEmployee);

		// Creating a user from the employee
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		User user = new User();
		user.setName(createdEmployee.getName());
		String randomPass = generateRandomPassword(10);
		user.setPassword(encoder.encode(randomPass));
		user.setRoles(Collections.singletonList((isBoss != null && isBoss) ? Roles.BOSS : Roles.WORKER));
		user.setEmployee(createdEmployee);
		User createdUser = userRepository.save(user);

		// Binding the user to the employee
		createdEmployee.setUser(createdUser);

		// Sending email about creation
		emailService.sendEmail(EmailConfig.serviceEmailAddress, createdEmployee.getEmail(), "Welcome",
				MessageFormat.format(
				"Welcome to our organization." +
						"\nYour password is: {0}" +
						"\nYou can login to your account here: {1}",
			randomPass, "http://localhost:8080/login"));

		// Getting the request URI for created status location header
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
		builder.path("/" + createdEmployeeDto.id);

		return LogStepOut(ResponseEntity.created(builder.build().toUri()).body(createdEmployeeDto));
	}

	/**
	 * Updates the currently logged in employee data
	 * @param modifyEmployeeDto the requested changes
	 * @return the modified employee dto
	 */
	@PutMapping("/my-info")
	@Secured({Roles.ADMIN, Roles.BOSS, Roles.WORKER})
	public ResponseEntity<?> updateSelf(@Valid @RequestBody ModifyEmployeeDto modifyEmployeeDto) {
		LogStepIn();

		Employee employee = employeeRepository.findById(modifyEmployeeDto.id.longValue());
		if(employee == null)
			return LogStepOut(ResponseEntity.notFound().build());

		// Get the logged in user's name
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// Check if logged in user's name match the name of the employee to be changed
		if (employee.getName().equals(username)) {
			modifyEntityFromModifyEmployeeDto(modifyEmployeeDto, employee);
			if (modifyEmployeeDto.password != null && modifyEmployeeDto.password.length() > 0) {
				PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

				// Change password if provided a new one
				User employeeUser = userRepository.findByName(username);
				employeeUser.setPassword(encoder.encode(modifyEmployeeDto.password));

				// Save changed user credentials
				userRepository.save(employeeUser);
			}

			Employee modifiedEmployee = employeeRepository.save(employee);

			return LogStepOut(ResponseEntity.ok(toDto(modifiedEmployee)));
		}

		return LogStepOut(ResponseEntity.badRequest().body("The requested id does not match the authenticated user's id."));
	}

	/**
	 * Updates an employee that directly belongs to this boss
	 * @param modifyEmployeeDto the requested changes
	 * @return the modified employee dto
	 */
	@PutMapping("/my-employee")
	@Secured(Roles.BOSS)
	public ResponseEntity<?> updateEmployee(@Valid @RequestBody ModifyEmployeeDto modifyEmployeeDto) {
		LogStepIn();

		Employee employee = employeeRepository.findById(modifyEmployeeDto.id.longValue());
		if(employee == null)
			return LogStepOut(ResponseEntity.notFound().build());

		String bossUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		Employee employeeBoss = userRepository.findByName(bossUsername).getEmployee();
		if (employee.getBossId().equals(employeeBoss.getId())) {
			if(modifyEmployeeDto.grossPayment != null)
				employee.setGrossPayment(modifyEmployeeDto.grossPayment);

			if(modifyEmployeeDto.workHours != null)
				employee.setWorkHours(modifyEmployeeDto.workHours);

			if(modifyEmployeeDto.workStatus != null)
				employee.setWorkStatus(WorkStatus.get(modifyEmployeeDto.workStatus));

			if(modifyEmployeeDto.isEntrant != null)
				employee.setEntrant(modifyEmployeeDto.isEntrant);

			Employee modifiedEmployee = employeeRepository.save(employee);

			return LogStepOut(ResponseEntity.ok(toDto(modifiedEmployee)));
		}

		return LogStepOut(ResponseEntity.badRequest().body("The requested id does not match the authenticated user's id."));
	}

	/**
	 * Deletes an Employee by id
	 * @param id the unique identifier of the Employee to be deleted
	 */
	@DeleteMapping("{id}")
	@Secured(Roles.ADMIN)
	public ResponseEntity<?> delete(@PathVariable long id) {
		LogStepIn();

		Employee employee = employeeRepository.findById(id);
		if (employee == null) {
			return LogStepOut(ResponseEntity.notFound().build());
		}
		else {
			userRepository.deleteById(employee.getUser().getId());
			employeeRepository.deleteById(id);
			return LogStepOut(ResponseEntity.noContent().build());
		}
	}

	//region Helper Methods

	/**
	 * Converts the EmployeeDto to Entity aka domain model
	 * @param employeeDto the data transfer object
	 * @param jobType the job type group the employee belongs to
	 * @return employee domain model object
	 */
	private Employee toEntity(EmployeeDto employeeDto, JobType jobType){
		Employee employee = new Employee();
		employee.setId(employeeDto.id);

		employee.setName(employeeDto.name);
		employee.setEmail(employeeDto.email);

		employee.setJobtype(jobType);

		employee.setBossId(employeeDto.bossId);

		employee.setGrossPayment(employeeDto.grossPayment);
		int netPayment = paymentCalculator.getNetPayment(employeeDto.grossPayment, employeeDto.children);
		employee.setNetPayment(netPayment);
		int employerTotalCost = paymentCalculator.getEmployerTotalCost(employeeDto.grossPayment, employeeDto.isEntrant);
		employee.setEmployerTotalCost(employerTotalCost);

		employee.setWorkHours(employeeDto.workHours);

		employee.setChildren(employeeDto.children);

		employee.setWorkStatus(WorkStatus.get(employeeDto.workStatus));

		employee.setEntrant(employeeDto.isEntrant);
		employee.setJustMarried(employeeDto.isJustMarried);
		employee.setSingleParent(employeeDto.isSingleParent);

		return employee;
	}

	/**
	 * Converts the Employee to a lightweight Data Transfer Object
	 * @param employee domain model object
	 * @return employee data transfer object
	 */
	private EmployeeDto toDto(Employee employee){
		EmployeeDto employeeDto = new EmployeeDto();
		employeeDto.id = employee.getId();

		employeeDto.name = employee.getName();
		employeeDto.email = employee.getEmail();

		employeeDto.jobTypeId = employee.getJobtype().getId();

		employeeDto.bossId = employee.getBossId();

		employeeDto.netPayment = employee.getNetPayment();
		employeeDto.grossPayment = employee.getGrossPayment();
		employeeDto.employerTotalCost = employee.getEmployerTotalCost();
		employeeDto.workHours = employee.getWorkHours();

		employeeDto.children = employee.getChildren();

		employeeDto.workStatus = employee.getWorkStatus().getStringValue();

		employeeDto.isEntrant = employee.isEntrant();
		employeeDto.isJustMarried = employee.isJustMarried();
		employeeDto.isSingleParent = employee.isSingleParent();

		return employeeDto;
	}

	/**
	 * Converts the ModifyEmployeeDto to Entity aka domain model
	 * @param modifyEmployeeDto the data transfer object
	 * @param originalEmployee the original employee
	 */
	private void modifyEntityFromModifyEmployeeDto(ModifyEmployeeDto modifyEmployeeDto, Employee originalEmployee){
		originalEmployee.setId(modifyEmployeeDto.id);

		if(modifyEmployeeDto.children != null)
			originalEmployee.setChildren(modifyEmployeeDto.children);

		if(modifyEmployeeDto.isJustMarried != null)
			originalEmployee.setJustMarried(modifyEmployeeDto.isJustMarried);

		if(modifyEmployeeDto.isSingleParent != null)
			originalEmployee.setSingleParent(modifyEmployeeDto.isSingleParent);
	}

	/**
	 * Generate random password
	 * @param len length of the generated password
	 * @return
	 */
	public static String generateRandomPassword(int len) {
		String AB = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();

		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();
	}

	//endregion
}
