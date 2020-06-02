package hu.bme.aut.payroll.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bme.aut.payroll.domain.JobGroup;
import hu.bme.aut.payroll.dto.JobGroupDto;
import hu.bme.aut.payroll.repository.JobGroupRepository;
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
public class JobGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobGroupRepository jobGroupRepository;

    // The service returns an empty collection if no registry is found, so status code shall be 200 in all valid cases
    @Test
    public void getAllJobGroups_Ok() throws Exception {
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/jobgroup")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.collection").exists());
    }

    @Test
    public void getJobGroupById_IdExists_Ok() throws Exception {
        // Arrange
        JobGroup jobGroup = new JobGroup();
        jobGroup.setName("MyJobGroup");
        jobGroup = jobGroupRepository.save(jobGroup);

        // Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/jobgroup/{id}", jobGroup.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(jobGroup.getId()));
    }

    @Test
    public void getJobGroupById_IdDoesNotExists_NotFound() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/jobgroup/{id}", Long.MAX_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void createJobGroup_SuccessfullyCreated_Created() throws Exception {
        // Arrange
        JobGroupDto jobGroupDto = new JobGroupDto();
        jobGroupDto.name = "TestJobGroup_1";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/jobgroup")
                .content(new ObjectMapper().writeValueAsString(jobGroupDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void createJobGroup_NonUniqueName_BadRequest() throws Exception {
        // Arrange
        JobGroup jobGroup = new JobGroup();
        jobGroup.setName("ToBeConflictedJobGroup");
        jobGroupRepository.save(jobGroup);

        JobGroupDto jobGroupDto = new JobGroupDto();
        jobGroupDto.name = "ToBeConflictedJobGroup";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/jobgroup")
                .content(new ObjectMapper().writeValueAsString(jobGroupDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deleteJobGroup_SuccessfulDelete_NoContent() throws Exception {
        // Arrange
        JobGroup jobGroup = new JobGroup();
        jobGroup.setName("ToBeDeletedJobGroup");
        jobGroup = jobGroupRepository.save(jobGroup);

        // Act & Assert
        mockMvc.perform( MockMvcRequestBuilders.delete("/api/jobgroup/{id}", jobGroup.getId()) )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteJobGroup_UnknownId_NotFound() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform( MockMvcRequestBuilders.delete("/api/jobgroup/{id}", Long.MAX_VALUE) )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
