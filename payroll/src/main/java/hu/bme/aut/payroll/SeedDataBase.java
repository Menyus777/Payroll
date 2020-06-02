package hu.bme.aut.payroll;

import hu.bme.aut.payroll.domain.*;
import hu.bme.aut.payroll.repository.*;
import hu.bme.aut.payroll.web.service.auth.Roles;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Calendar;
import java.util.Collections;

/**
 * Seeds the database with default values for showcase and for initial users
 */
@Component
public class SeedDataBase {

    private final JobGroupRepository jobGroupRepository;
    private final JobTypeRepository jobTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final HungarianTaxFeesRepository hungarianTaxFeesRepository;
    private final UserRepository userRepository;

    public SeedDataBase(JobGroupRepository jobGroupRepository, JobTypeRepository jobTypeRepository,
                        EmployeeRepository employeeRepository, HungarianTaxFeesRepository hungarianTaxFeesRepository,
                        UserRepository userRepository) {
        this.jobGroupRepository = jobGroupRepository;
        this.jobTypeRepository = jobTypeRepository;
        this.employeeRepository = employeeRepository;
        this.hungarianTaxFeesRepository = hungarianTaxFeesRepository;
        this.userRepository = userRepository;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedDatabasesAccordingToDependency();
    }

    /**
     * Seeds the database according to db dependency, this will make sure the table connections
     * will set up too.
     */
    private void seedDatabasesAccordingToDependency() {
        seedJobGroupTable();
        seedHungarianTaxFeesTable();
    }

    private void seedJobGroupTable() {
        JobGroup development = new JobGroup();
        development.setName("Development Team");
        development = jobGroupRepository.save(development);

        JobGroup managemenet = new JobGroup();
        managemenet.setName("Management Team");
        managemenet = jobGroupRepository.save(managemenet);

        JobGroup hr = new JobGroup();
        hr.setName("HR Team");
        hr = jobGroupRepository.save(hr);

        JobGroup it = new JobGroup();
        it.setName("IT");
        it = jobGroupRepository.save(it);

        seedJobTypeTable(development, managemenet, hr, it);
    }

    private void seedJobTypeTable(JobGroup development, JobGroup managemenet, JobGroup hr, JobGroup it) {
        JobType seniordev = new JobType();
        seniordev.setName("Senior Software Developer");
        seniordev.setJobGroup(development);
        seniordev = jobTypeRepository.save(seniordev);

        JobType po = new JobType();
        po.setName("Product Owner");
        po.setJobGroup(development);
        po = jobTypeRepository.save(po);

        JobType manager = new JobType();
        manager.setName("Team Manager");
        manager.setJobGroup(managemenet);
        manager = jobTypeRepository.save(manager);

        JobType communication = new JobType();
        communication.setName("Communication Manager");
        communication.setJobGroup(hr);
        communication = jobTypeRepository.save(communication);

        JobType systemAdministrator = new JobType();
        systemAdministrator.setName("System Administrator");
        systemAdministrator.setJobGroup(it);
        systemAdministrator = jobTypeRepository.save(systemAdministrator);

        seedEmployeeTable(seniordev, po, manager, communication, systemAdministrator);
    }

    private void seedEmployeeTable(JobType seniordev, JobType po, JobType manager, JobType communication,
        JobType systemAdministrator) {
        Employee levente = new Employee();
        levente.setName("levente");
        levente.setEmail("kisslevente@organization.com");
        levente.setJobtype(manager);
        levente.setBossId(0L);
        levente.setNetPayment(831_250);
        levente.setGrossPayment(1_250_000);
        levente.setEmployerTotalCost(1_487_500);
        levente.setWorkHours(40);
        levente.setChildren(0);
        levente.setWorkStatus(WorkStatus.ADULT);
        levente.setEntrant(false);
        levente.setJustMarried(false);
        levente.setSingleParent(false);
        levente = employeeRepository.save(levente);

        Employee zoltan = new Employee();
        zoltan.setName("zoltan");
        zoltan.setEmail("kisszoltan@organization.com");
        zoltan.setJobtype(po);
        zoltan.setBossId(levente.getId());
        zoltan.setNetPayment(665_000);
        zoltan.setGrossPayment(1_000_000);
        zoltan.setEmployerTotalCost(1_190_000);
        zoltan.setWorkHours(40);
        zoltan.setChildren(0);
        zoltan.setWorkStatus(WorkStatus.ADULT);
        zoltan.setEntrant(false);
        zoltan.setJustMarried(false);
        zoltan.setSingleParent(false);
        zoltan = employeeRepository.save(zoltan);

        Employee dani = new Employee();
        dani.setName("dani");
        dani.setEmail("kisdani@organization.com");
        dani.setJobtype(seniordev);
        dani.setBossId(levente.getId());
        dani.setNetPayment(565_250);
        dani.setGrossPayment(850_000);
        dani.setEmployerTotalCost(1_011_500);
        dani.setWorkHours(40);
        dani.setChildren(0);
        dani.setWorkStatus(WorkStatus.ADULT);
        dani.setEntrant(false);
        dani.setJustMarried(false);
        dani.setSingleParent(false);
        dani = employeeRepository.save(dani);

        Employee szilvi = new Employee();
        szilvi.setName("szilvia");
        szilvi.setEmail("kisszilvia@organization.com");
        szilvi.setJobtype(communication);
        szilvi.setBossId(levente.getId());
        szilvi.setNetPayment(332_500);
        szilvi.setGrossPayment(500_000);
        szilvi.setEmployerTotalCost(595_000);
        szilvi.setWorkHours(40);
        szilvi.setChildren(0);
        szilvi.setWorkStatus(WorkStatus.ADULT);
        szilvi.setEntrant(false);
        szilvi.setJustMarried(false);
        szilvi.setSingleParent(false);
        szilvi = employeeRepository.save(szilvi);

        Employee bela = new Employee();
        bela.setName("bela");
        bela.setEmail("bela@organization.com");
        bela.setJobtype(systemAdministrator);
        bela.setBossId(0L);
        bela.setNetPayment(831_250);
        bela.setGrossPayment(1_250_000);
        bela.setEmployerTotalCost(1_487_500);
        bela.setWorkHours(40);
        bela.setChildren(0);
        bela.setWorkStatus(WorkStatus.ADULT);
        bela.setEntrant(false);
        bela.setJustMarried(false);
        bela.setSingleParent(false);
        bela = employeeRepository.save(bela);

        seedUserTable(levente, zoltan, dani, szilvi, bela);
    }

    private void seedUserTable(Employee levente, Employee zoltan, Employee dani, Employee szilvi, Employee bela) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        User userLevente = new User();
        userLevente.setEmployee(levente);
        userLevente.setName(levente.getName());
        userLevente.setPassword(encoder.encode("levente"));
        userLevente.setRoles(Collections.singletonList(Roles.BOSS));
        userRepository.save(userLevente);

        User userZoltan = new User();
        userZoltan.setEmployee(zoltan);
        userZoltan.setName(zoltan.getName());
        userZoltan.setPassword(encoder.encode("zoltan"));
        userZoltan.setRoles(Collections.singletonList(Roles.WORKER));
        userRepository.save(userZoltan);

        User userDani = new User();
        userDani.setEmployee(dani);
        userDani.setName(dani.getName());
        userDani.setPassword(encoder.encode("dani"));
        userDani.setRoles(Collections.singletonList(Roles.WORKER));
        userRepository.save(userDani);

        User userSzilvia = new User();
        userSzilvia.setEmployee(szilvi);
        userSzilvia.setName(szilvi.getName());
        userSzilvia.setPassword(encoder.encode("szilvia"));
        userSzilvia.setRoles(Collections.singletonList(Roles.WORKER));
        userRepository.save(userSzilvia);

        User userBela = new User();
        userBela.setEmployee(bela);
        userBela.setName(bela.getName());
        userBela.setPassword(encoder.encode("bela"));
        userBela.setRoles(Collections.singletonList(Roles.ADMIN));
        userRepository.save(userBela);
    }

    private void seedHungarianTaxFeesTable() {
        HungarianTaxFees taxfee = new HungarianTaxFees();
        taxfee.setSZJA(0.15);
        taxfee.setEntrantFare(0.05);
        taxfee.setWorkStatusDiscount(0.12);
        taxfee.setChildFare(5000.0);
        taxfee.setContributionVocationalTraining(0.015);
        taxfee.setLaborMarketContribution(0.015);
        taxfee.setHealthInsuranceContribution(0.07);
        taxfee.setPensionContribution(0.1);
        taxfee.setSocialContribution(0.175);
        taxfee.setJustMarriedFare(5000.0);
        taxfee.setStartDate(Calendar.getInstance().getTime());
        hungarianTaxFeesRepository.save(taxfee);
    }
}
