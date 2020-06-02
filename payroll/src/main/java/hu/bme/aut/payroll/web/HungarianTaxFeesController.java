package hu.bme.aut.payroll.web;

import hu.bme.aut.payroll.domain.Employee;
import hu.bme.aut.payroll.domain.HungarianTaxFees;
import hu.bme.aut.payroll.dto.HungarianTaxFeesDto;
import hu.bme.aut.payroll.repository.EmployeeRepository;
import hu.bme.aut.payroll.repository.HungarianTaxFeesRepository;
import hu.bme.aut.payroll.web.service.auth.Roles;
import hu.bme.aut.payroll.web.service.domain.PaymentCalculator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/hungariantaxfees")
public class HungarianTaxFeesController extends ControllerBase {

    private final HungarianTaxFeesRepository hungarianTaxFeesRepository;
    private final EmployeeRepository employeeRepository;
    private final PaymentCalculator paymentCalculator;

    public HungarianTaxFeesController(HungarianTaxFeesRepository hungarianTaxFeesRepository,
                                      EmployeeRepository employeeRepository, PaymentCalculator paymentCalculator) {
        this.hungarianTaxFeesRepository = hungarianTaxFeesRepository;
        this.employeeRepository = employeeRepository;
        this.paymentCalculator = paymentCalculator;
    }

    /**
     * Gets the specific hungarian tax fee by id
     * @param id the unique identifier of the HungaryTaxFees
     * @return hungary tax fees data transfer object
     */
    @GetMapping("{id}")
    @Secured(Roles.ADMIN)
    public ResponseEntity<HungarianTaxFeesDto> getById(@PathVariable long id) {
        LogStepIn();

        HungarianTaxFees hungarianTaxFees = hungarianTaxFeesRepository.findById(id);

        if (hungarianTaxFees == null)
            return LogStepOut(ResponseEntity.notFound().build());
        else
            return LogStepOut(ResponseEntity.ok(toDto(hungarianTaxFees)));
    }

    /**
     * Gets the specific hungarian tax fee by id
     * @return latest hungary tax fee data transfer object
     */
    @GetMapping("/latest")
    @Secured({Roles.ADMIN, Roles.BOSS, Roles.WORKER})
    public ResponseEntity<HungarianTaxFeesDto> getLatest() {
        LogStepIn();

        HungarianTaxFees hungarianTaxFees = hungarianTaxFeesRepository.findFirstByOrderByStartDateDesc();

        if (hungarianTaxFees == null)
            return LogStepOut(ResponseEntity.notFound().build());
        else
            return LogStepOut(ResponseEntity.ok(toDto(hungarianTaxFees)));
    }

    /**
     * Creates a new HungaryTaxFees
     * @param hungarianTaxFeesDto job group data transfer object
     */
    @PostMapping
    @Secured(Roles.ADMIN)
    public ResponseEntity<?> create(@Valid @RequestBody HungarianTaxFeesDto hungarianTaxFeesDto) {
        LogStepIn();

        HungarianTaxFees hungarianTaxFees = toEntity(hungarianTaxFeesDto);

        // Saving the entity
        HungarianTaxFeesDto createdHungarianTaxFeesDto = toDto(hungarianTaxFeesRepository.save(hungarianTaxFees));

        // Updating employee's payment information
        List<Employee> employeeList = employeeRepository.findAll();
        for (Employee employee : employeeList) {
            int netPayment = paymentCalculator.getNetPayment(employee.getGrossPayment(), employee.getChildren());
            employee.setNetPayment(netPayment);

            int employerTotalCost = paymentCalculator.getEmployerTotalCost(employee.getGrossPayment(), employee.isEntrant());
            employee.setEmployerTotalCost(employerTotalCost);

            employeeRepository.save(employee);
        }
        employeeRepository.flush();

        // Getting the request URI for created status location header
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        builder.path("/" + createdHungarianTaxFeesDto.id);

        return LogStepOut(ResponseEntity.created(builder.build().toUri()).body(createdHungarianTaxFeesDto));
    }

    //region Helper Methods

    /**
     * Converts the HungarianTaxFees to Entity aka domain model
     * @param hungarianTaxFeesDto the data transfer object
     * @return hungarian tax fees domain model object
     */
    private HungarianTaxFees toEntity(HungarianTaxFeesDto hungarianTaxFeesDto) {
        HungarianTaxFees hungarianTaxFees = new HungarianTaxFees();
        hungarianTaxFees.setSZJA(hungarianTaxFeesDto.SZJA);
        hungarianTaxFees.setEntrantFare(hungarianTaxFeesDto.entrantFare);
        hungarianTaxFees.setWorkStatusDiscount(hungarianTaxFeesDto.workStatusDiscount);
        hungarianTaxFees.setChildFare(hungarianTaxFeesDto.childFare);
        hungarianTaxFees.setContributionVocationalTraining(hungarianTaxFeesDto.contributionVocationalTraining);
        hungarianTaxFees.setLaborMarketContribution(hungarianTaxFeesDto.laborMarketContribution);
        hungarianTaxFees.setHealthInsuranceContribution(hungarianTaxFeesDto.healthInsuranceContribution);
        hungarianTaxFees.setPensionContribution(hungarianTaxFeesDto.pensionContribution);
        hungarianTaxFees.setSocialContribution(hungarianTaxFeesDto.socialContribution);
        hungarianTaxFees.setJustMarriedFare(hungarianTaxFeesDto.justMarriedFare);
        hungarianTaxFees.setStartDate(hungarianTaxFeesDto.startDate);

        return hungarianTaxFees;
    }

    /**
     * Converts the HungarianTaxFees to a lightweight Data Transfer Object
     * @param hungarianTaxFees domain model object
     * @return hungarian tax fees data transfer object
     */
    private HungarianTaxFeesDto toDto(HungarianTaxFees hungarianTaxFees) {
        HungarianTaxFeesDto hungarianTaxFeesDto = new HungarianTaxFeesDto();
        hungarianTaxFeesDto.id = hungarianTaxFees.getId();
        hungarianTaxFeesDto.SZJA = hungarianTaxFees.getSZJA();
        hungarianTaxFeesDto.entrantFare = hungarianTaxFees.getEntrantFare();
        hungarianTaxFeesDto.workStatusDiscount = hungarianTaxFees.getWorkStatusDiscount();
        hungarianTaxFeesDto.childFare = hungarianTaxFees.getChildFare();
        hungarianTaxFeesDto.contributionVocationalTraining = hungarianTaxFees.getContributionVocationalTraining();
        hungarianTaxFeesDto.laborMarketContribution = hungarianTaxFees.getLaborMarketContribution();
        hungarianTaxFeesDto.healthInsuranceContribution = hungarianTaxFees.getHealthInsuranceContribution();
        hungarianTaxFeesDto.pensionContribution = hungarianTaxFees.getPensionContribution();
        hungarianTaxFeesDto.socialContribution = hungarianTaxFees.getSocialContribution();
        hungarianTaxFeesDto.justMarriedFare = hungarianTaxFees.getJustMarriedFare();
        hungarianTaxFeesDto.startDate = hungarianTaxFees.getStartDate();

        return hungarianTaxFeesDto;
    }

    //endregion
}

