package hu.bme.aut.payroll.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bme.aut.payroll.domain.JobGroup;
import hu.bme.aut.payroll.domain.JobType;
import hu.bme.aut.payroll.dto.JobTypeDto;
import hu.bme.aut.payroll.repository.JobGroupRepository;
import hu.bme.aut.payroll.repository.JobTypeRepository;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(value="TestUser",roles= {"WORKER", "BOSS", "ADMIN"})
public class JobTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobTypeRepository jobTypeRepository;

    private static JobGroup testJobGroup;

    @BeforeAll
    public static void init(
        @Autowired JobGroupRepository jobGroupRepository) {
        testJobGroup = new JobGroup();
        testJobGroup.setName("Test_Team_01");

        jobGroupRepository.save(testJobGroup);
    }

    // The service returns an empty collection if no registry is found, so status code shall be 200 in all valid cases
    @Test
    public void getAllJobGroups_Ok() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/jobtype")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.collection").exists());
    }

    @Test
    public void getJobTypeById_IdExists_Ok() throws Exception {
        // Arrange
        JobType jobType = new JobType();
        jobType.setName("Test_JobType_01");
        jobType.setJobGroup(testJobGroup);
        jobType = jobTypeRepository.save(jobType);

        // Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/jobtype/{id}", jobType.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(jobType.getId()));
    }

    @Test
    public void getJobTypeById_IdDoesNotExists_NotFound() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/jobtype/{id}", Long.MAX_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void createJobType_SuccessfullyCreated_Created() throws Exception {
        // Arrange
        JobTypeDto jobTypeDto = new JobTypeDto();
        jobTypeDto.name = "TestJobType_1";
        jobTypeDto.jobGroupId = testJobGroup.getId();

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/jobtype")
                .content(new ObjectMapper().writeValueAsString(jobTypeDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void createJobType_NonUniqueName_BadRequest() throws Exception {
        // Arrange
        JobType jobType = new JobType();
        jobType.setName("ToBeConflictedJobType");
        jobType.setJobGroup(testJobGroup);
        jobTypeRepository.save(jobType);

        JobTypeDto jobTypeDto = new JobTypeDto();
        jobTypeDto.name = "ToBeConflictedJobType";
        jobTypeDto.jobGroupId = testJobGroup.getId();

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/jobtype")
                .content(new ObjectMapper().writeValueAsString(jobTypeDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deleteJobType_SuccessfulDelete_NoContent() throws Exception {
        // Arrange
        JobType jobType = new JobType();
        jobType.setName("ToBeDeletedJobType");
        jobType.setJobGroup(testJobGroup);
        jobType = jobTypeRepository.save(jobType);

        // Act & Assert
        mockMvc.perform( MockMvcRequestBuilders.delete("/api/jobtype/{id}", jobType.getId()) )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteJobType_UnknownId_NotFound() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform( MockMvcRequestBuilders.delete("/api/jobtype/{id}", Long.MAX_VALUE) )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
