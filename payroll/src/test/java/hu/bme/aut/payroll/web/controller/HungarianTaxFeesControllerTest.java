package hu.bme.aut.payroll.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bme.aut.payroll.domain.Employee;
import hu.bme.aut.payroll.domain.HungarianTaxFees;
import hu.bme.aut.payroll.dto.HungarianTaxFeesDto;
import hu.bme.aut.payroll.repository.EmployeeRepository;
import hu.bme.aut.payroll.repository.HungarianTaxFeesRepository;
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
public class HungarianTaxFeesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HungarianTaxFeesRepository repository;
    @Autowired
    private EmployeeRepository employeeRepository;

    private static HungarianTaxFees defaultTaxFee;
    private static HungarianTaxFeesDto defaultTaxFeeDto;

    @BeforeAll
    public static void init() {
        defaultTaxFee = new HungarianTaxFees();
        defaultTaxFee.setSZJA(1.0);
        defaultTaxFee.setEntrantFare(1.0);
        defaultTaxFee.setWorkStatusDiscount(1.0);
        defaultTaxFee.setChildFare(1.0);
        defaultTaxFee.setContributionVocationalTraining(1.0);
        defaultTaxFee.setLaborMarketContribution(1.0);
        defaultTaxFee.setHealthInsuranceContribution(1.0);
        defaultTaxFee.setPensionContribution(1.0);
        defaultTaxFee.setSocialContribution(1.0);
        defaultTaxFee.setJustMarriedFare(1.0);
        defaultTaxFee.setStartDate(Calendar.getInstance().getTime());

        defaultTaxFeeDto = new HungarianTaxFeesDto();
        defaultTaxFeeDto.SZJA = 1.0;
        defaultTaxFeeDto.entrantFare = 1.0;
        defaultTaxFeeDto.workStatusDiscount = 1.0;
        defaultTaxFeeDto.childFare = 1.0;
        defaultTaxFeeDto.contributionVocationalTraining = 1.0;
        defaultTaxFeeDto.laborMarketContribution = 1.0;
        defaultTaxFeeDto.healthInsuranceContribution = 1.0;
        defaultTaxFeeDto.pensionContribution = 1.0;
        defaultTaxFeeDto.socialContribution = 1.0;
        defaultTaxFeeDto.justMarriedFare = 1.0;
        defaultTaxFeeDto.startDate = Calendar.getInstance().getTime();
    }

    @Test
    public void getHungarianTaxFeesById_IdExists_Ok() throws Exception {
        // Arrange
        HungarianTaxFees hungarianTaxFees = repository.save(defaultTaxFee);

        // Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/hungariantaxfees/{id}", hungarianTaxFees.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(hungarianTaxFees.getId()));

        // Cleanup - Don't put in @AfterEach because not every test shall call this
        repository.deleteAll();
    }

    @Test
    public void getHungarianTaxFeesById_IdDoesNotExists_NotFound() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/hungariantaxfees/{id}", Long.MAX_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getLatestHungarianTaxFees_Ok() throws Exception {
        // Arrange
        HungarianTaxFees hungarianTaxFeesOld = repository.save(defaultTaxFee);

        HungarianTaxFees hungarianTaxFeesLatest = defaultTaxFee;
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 9000);
        hungarianTaxFeesLatest.setStartDate(calendar2.getTime());
        hungarianTaxFeesLatest = repository.save(hungarianTaxFeesLatest);

        // Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/hungariantaxfees/latest")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(hungarianTaxFeesLatest.getId()));

        // Cleanup - Don't put in @AfterEach because not every test shall call this
        repository.deleteAll();
    }

    @Test
    public void create_SuccessfullyCreated_Created() throws Exception {
        // Arrange
        HungarianTaxFeesDto hungarianTaxFeesDto = defaultTaxFeeDto;
        // eliminating null values from other tests
        for (Employee e: employeeRepository.findAll()) {
            if(e.getGrossPayment() == null || e.isEntrant() == null|| e.getChildren() == null){
                e.setGrossPayment(500_000);
                e.setEntrant(true);
                e.setChildren(0);
                employeeRepository.save(e);
            }
        }
        employeeRepository.flush();

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/hungariantaxfees")
                .content(new ObjectMapper().writeValueAsString(hungarianTaxFeesDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

        // Cleanup - Don't put in @AfterEach because not every test shall call this
        repository.deleteAll();
    }

    @Test
    public void create_InvalidOrMissingValuesInDto_BadRequest() throws Exception {
        // Arrange
        HungarianTaxFeesDto hungarianTaxFeesDto = defaultTaxFeeDto;
        hungarianTaxFeesDto.childFare = null;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/hungariantaxfees")
                .content(new ObjectMapper().writeValueAsString(hungarianTaxFeesDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Cleanup
        hungarianTaxFeesDto.childFare = 1.0;
    }
}
