package hu.bme.aut.payroll.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bme.aut.payroll.domain.*;
import hu.bme.aut.payroll.dto.EmployeeDto;
import hu.bme.aut.payroll.dto.ModifyEmployeeDto;
import hu.bme.aut.payroll.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Calendar;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(value="TestUser",roles= {"WORKER", "BOSS", "ADMIN"})
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository repository;
    @Autowired
    private UserRepository userRepository;


    private static JobGroup testJobGroup;
    private static JobType testJobType;
    private static HungarianTaxFees taxFees;
    private static Employee testEmployee;
    private static User testUser;

    @BeforeAll
    public static void init(
            @Autowired JobGroupRepository jobGroupRepository,
            @Autowired JobTypeRepository jobTypeRepository,
            @Autowired HungarianTaxFeesRepository hungarianTaxFeesRepository,
            @Autowired UserRepository userRepository,
            @Autowired EmployeeRepository repository) {
        testJobGroup = new JobGroup();
        testJobGroup.setName("TestGroup");
        testJobGroup = jobGroupRepository.save(testJobGroup);

        testJobType = new JobType();
        testJobType.setName("TestType");
        testJobType.setJobGroup(testJobGroup);
        testJobType = jobTypeRepository.save(testJobType);

        taxFees = new HungarianTaxFees();
        taxFees.setSZJA(1.0);
        taxFees.setEntrantFare(1.0);
        taxFees.setWorkStatusDiscount(1.0);
        taxFees.setChildFare(1.0);
        taxFees.setContributionVocationalTraining(1.0);
        taxFees.setLaborMarketContribution(1.0);
        taxFees.setHealthInsuranceContribution(1.0);
        taxFees.setPensionContribution(1.0);
        taxFees.setSocialContribution(1.0);
        taxFees.setJustMarriedFare(1.0);
        taxFees.setStartDate(Calendar.getInstance().getTime());
        taxFees = hungarianTaxFeesRepository.save(taxFees);

        testUser = new User();
        testUser.setName("TestUser");
        testUser.setPassword("12345");

        testEmployee = new Employee();
        testEmployee.setName("TestUser");
        testEmployee.setEmail("testuser@test.com");
        testEmployee.setBossId(0L);
        testEmployee.setGrossPayment(500_000);
        testEmployee.setJobtype(testJobType);
        testEmployee.setWorkStatus(WorkStatus.ADULT);

        testEmployee = repository.save(testEmployee);
        testUser.setEmployee(testEmployee);
        testUser = userRepository.save(testUser);
    }

    // The service returns an empty collection if no registry is found, so status code shall be 200 in all valid cases
    @Test
    public void getAllEmployees_Ok() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/employee")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.collection").exists());
    }

    @Test
    public void getEmployeeById_IdExists_Ok() throws Exception {
        // Arrange
        Employee employee = new Employee();
        employee.setName("TestWorker1");
        employee.setEmail("testworker1@organization.com");
        employee.setBossId(0L);
        employee.setGrossPayment(500_000);
        employee.setJobtype(testJobType);
        employee.setWorkStatus(WorkStatus.ADULT);
        employee = repository.save(employee);

        // Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/employee/{id}", employee.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(employee.getId()));
    }

    @Test
    public void getEmployeeById_IdDoesNotExists_NotFound() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/employee/{id}", Long.MAX_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getMyEmployees_Ok() throws Exception {
        // Arrange
        Employee employee = new Employee();
        employee.setName("TestWorker2");
        employee.setEmail("testworker@organization.com");
        employee.setBossId(testEmployee.getId());
        employee.setGrossPayment(500_000);
        employee.setJobtype(testJobType);
        employee.setWorkStatus(WorkStatus.ADULT);
        employee = repository.save(employee);

        // Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/employee/my-employee")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.collection").exists());
    }

    @Test
    public void getMyInfo_Ok() throws Exception {
        // Arrange Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/employee/my-info", testEmployee.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testEmployee.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testEmployee.getName()));
    }

    @Test
    public void createEmployee_SuccessfullyCreated_Created() throws Exception {
        // Arrange
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.name = "TestWorker3";
        employeeDto.email = "testworker3@organization.com";
        employeeDto.bossId = testEmployee.getId();
        employeeDto.grossPayment = 500_000;
        employeeDto.workHours = 40;
        employeeDto.children = 0;
        employeeDto.isEntrant = false;
        employeeDto.isJustMarried = false;
        employeeDto.isSingleParent = false;
        employeeDto.jobTypeId = testJobType.getId();
        employeeDto.workStatus = WorkStatus.ADULT.getStringValue();

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employee")
                .content(new ObjectMapper().writeValueAsString(employeeDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect((MockMvcResultMatchers.jsonPath("$.name").value(employeeDto.name)));
    }

    @Test
    public void createEmployee_NameConflict_BadRequest() throws Exception {
        // Arrange
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.name = testEmployee.getName();
        employeeDto.email = testEmployee.getName() + "@organization.com";
        employeeDto.bossId = 0L;
        employeeDto.grossPayment = 500_000;
        employeeDto.workHours = 40;
        employeeDto.children = 0;
        employeeDto.isEntrant = false;
        employeeDto.isJustMarried = false;
        employeeDto.isSingleParent = false;
        employeeDto.jobTypeId = testJobType.getId();
        employeeDto.workStatus = WorkStatus.ADULT.getStringValue();

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employee")
                .content(new ObjectMapper().writeValueAsString(employeeDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createEmployee_NonExistentJobType_BadRequest() throws Exception {
        // Arrange
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.name = testEmployee.getName();
        employeeDto.email = testEmployee.getName() + "@organization.com";
        employeeDto.bossId = 0L;
        employeeDto.grossPayment = 500_000;
        employeeDto.workHours = 40;
        employeeDto.children = 0;
        employeeDto.isEntrant = false;
        employeeDto.isJustMarried = false;
        employeeDto.isSingleParent = false;
        employeeDto.jobTypeId = Long.MAX_VALUE;
        employeeDto.workStatus = WorkStatus.ADULT.getStringValue();

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employee")
                .content(new ObjectMapper().writeValueAsString(employeeDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(value="TestUser1",roles= {"WORKER", "BOSS", "ADMIN"})
    public void updateSelf_SuccessfullyUpdates_Ok() throws Exception {
        // Arrange
        User testUser1 = new User();
        testUser1.setName("TestUser1");
        testUser1.setPassword("12345");

        Employee testEmployee1 = new Employee();
        testEmployee1.setName("TestUser1");
        testEmployee1.setEmail("testuser1@test.com");
        testEmployee1.setBossId(0L);
        testEmployee1.setGrossPayment(500_000);
        testEmployee1.setJobtype(testJobType);
        testEmployee1.setWorkStatus(WorkStatus.ADULT);

        testEmployee1 = repository.save(testEmployee1);
        testUser1.setEmployee(testEmployee1);
        testUser1 = userRepository.save(testUser1);

        ModifyEmployeeDto modifyEmployeeDto = new ModifyEmployeeDto();
        modifyEmployeeDto.id = testEmployee1.getId();
        modifyEmployeeDto.password = "newPassword";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/employee/my-info")
                .content(new ObjectMapper().writeValueAsString(modifyEmployeeDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testEmployee1.getId()));
    }

    @Test
    @WithMockUser(value="TestUser2",roles= {"WORKER", "BOSS", "ADMIN"})
    public void updateSelf_NoMatchingUser_NotFound() throws Exception {
        // Arrange
        ModifyEmployeeDto modifyEmployeeDto = new ModifyEmployeeDto();
        modifyEmployeeDto.id = Long.MAX_VALUE;
        modifyEmployeeDto.password = "newPassword";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/employee/my-info")
                .content(new ObjectMapper().writeValueAsString(modifyEmployeeDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(value="TestUser2",roles= {"WORKER", "BOSS", "ADMIN"})
    public void updateSelf_TryingToModifyAnotherUser_BadRequest() throws Exception {
        // Arrange
        User testUser2 = new User();
        testUser2.setName("TestUser2");
        testUser2.setPassword("12345");

        Employee testEmployee2 = new Employee();
        testEmployee2.setName("TestUser2");
        testEmployee2.setEmail("testuser2@test.com");
        testEmployee2.setBossId(0L);
        testEmployee2.setGrossPayment(500_000);
        testEmployee2.setJobtype(testJobType);
        testEmployee2.setWorkStatus(WorkStatus.ADULT);

        testEmployee2 = repository.save(testEmployee2);
        testUser2.setEmployee(testEmployee2);
        testUser2 = userRepository.save(testUser2);

        ModifyEmployeeDto modifyEmployeeDto = new ModifyEmployeeDto();
        modifyEmployeeDto.id = testEmployee.getId();
        modifyEmployeeDto.password = "newPassword";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/employee/my-info")
                .content(new ObjectMapper().writeValueAsString(modifyEmployeeDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
