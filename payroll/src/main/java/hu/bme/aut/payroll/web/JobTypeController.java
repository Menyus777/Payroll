package hu.bme.aut.payroll.web;

import hu.bme.aut.payroll.domain.JobGroup;
import hu.bme.aut.payroll.domain.JobType;
import hu.bme.aut.payroll.dto.JobTypeCollectionDto;
import hu.bme.aut.payroll.dto.JobTypeDto;
import hu.bme.aut.payroll.repository.JobGroupRepository;
import hu.bme.aut.payroll.repository.JobTypeRepository;
import hu.bme.aut.payroll.web.service.auth.Roles;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/jobtype")
public class JobTypeController extends ControllerBase {

    private final JobTypeRepository jobTypeRepository;
    private final JobGroupRepository jobGroupRepository;

    public JobTypeController(JobTypeRepository jobTypeRepository, JobGroupRepository jobGroupRepository) {
        this.jobTypeRepository = jobTypeRepository;
        this.jobGroupRepository = jobGroupRepository;
    }

    /**
     * Gets all the job types from the database
     * @return all the job types in the database
     */
    @GetMapping
    @Secured({Roles.ADMIN, Roles.BOSS, Roles.WORKER})
    public ResponseEntity<JobTypeCollectionDto> getAll() {
        LogStepIn();

        List<JobType> jobTypeList = jobTypeRepository.findAll();
        JobTypeCollectionDto jobTypeCollectionDto = new JobTypeCollectionDto();

        for (JobType jobType : jobTypeList) {
            jobTypeCollectionDto.collection.add(toDto(jobType));
        }

        return LogStepOut(ResponseEntity.ok(jobTypeCollectionDto));
    }

    /**
     * Gets the specific job type by id
     * @param id the unique identifier of the JobType
     * @return job type data transfer object
     */
    @GetMapping("{id}")
    @Secured({Roles.ADMIN, Roles.BOSS, Roles.WORKER})
    public ResponseEntity<JobTypeDto> getById(@PathVariable long id) {
        LogStepIn();

        JobType jobtype = jobTypeRepository.findById(id);

        if (jobtype == null)
            return LogStepOut(ResponseEntity.notFound().build());
        else
            return LogStepOut(ResponseEntity.ok(toDto(jobtype)));
    }

    /**
     * Creates a new JobType
     * @param jobTypeDto the to be created job type
     * @return the created job type
     */
    @PostMapping
    @Secured(Roles.ADMIN)
    public ResponseEntity<?> create(@Valid @RequestBody JobTypeDto jobTypeDto) {
        LogStepIn();

        // Checking if the job group specified in the dto exists or not
        JobGroup jobTypeGroup = jobGroupRepository.findById(jobTypeDto.jobGroupId.longValue());
        if(jobTypeGroup == null)
            return LogStepOut(ResponseEntity.badRequest().body("The Job Group you are trying to bind does not exist!"));

        // Validation for name attribute
        List<JobType> jobTypeList = jobTypeRepository.findAll();
        for (JobType jobTypeElement : jobTypeList) {
            if(jobTypeElement.getName().equals(jobTypeDto.name))
                return LogStepOut(ResponseEntity.badRequest().body("A Job Type with this name already exist!"));
        }

        JobType jobType = toEntity(jobTypeDto, jobTypeGroup);

        // Saving the entity
        JobTypeDto createdJobTypeDto = toDto(jobTypeRepository.save(jobType));

        // Getting the request URI for created status location header
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        builder.path("/" + createdJobTypeDto.id);

        return LogStepOut(ResponseEntity.created(builder.build().toUri()).body(createdJobTypeDto));
    }

    /**
     * Deletes a JobType by id
     * @param id the unique identifier of the JobType to be deleted
     */
    @DeleteMapping("{id}")
    @Secured(Roles.ADMIN)
    public ResponseEntity<?> delete(@PathVariable long id) {
        LogStepIn();

        JobType jobType = jobTypeRepository.findById(id);
        if (jobType == null) {
            return LogStepOut(ResponseEntity.notFound().build());
        }
        else {
            jobTypeRepository.deleteById(id);
            return LogStepOut(ResponseEntity.noContent().build());
        }
    }

    //region Helper Methods

    /**
     * Converts the JobTypeDto to Entity aka domain model
     * @param jobTypeDto the data transfer object
     * @param jobTypeGroup the group the job type belongs to
     * @return job type domain model object
     */
    private JobType toEntity(JobTypeDto jobTypeDto, JobGroup jobTypeGroup){
        JobType jobType = new JobType();
        jobType.setName(jobTypeDto.name);
        jobType.setJobGroup(jobTypeGroup);

        return jobType;
    }

    /**
     * Converts the JobType to a lightweight Data Transfer Object
     * @param jobType domain model object
     * @return job type data transfer object
     */
    private JobTypeDto toDto(JobType jobType){
        JobTypeDto jobTypeDto = new JobTypeDto();
        jobTypeDto.id = jobType.getId();
        jobTypeDto.name = jobType.getName();
        jobTypeDto.jobGroupId = jobType.getJobGroup().getId();

        return jobTypeDto;
    }

    //endregion
}
