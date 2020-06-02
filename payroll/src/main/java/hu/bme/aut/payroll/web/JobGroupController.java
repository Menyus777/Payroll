package hu.bme.aut.payroll.web;

import hu.bme.aut.payroll.domain.JobGroup;
import hu.bme.aut.payroll.dto.JobGroupCollectionDto;
import hu.bme.aut.payroll.dto.JobGroupDto;
import hu.bme.aut.payroll.repository.JobGroupRepository;
import hu.bme.aut.payroll.web.service.auth.Roles;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/jobgroup")
public class JobGroupController extends ControllerBase {

    private final JobGroupRepository jobGroupRepository;

    public JobGroupController(JobGroupRepository jobGroupRepository) {
        this.jobGroupRepository = jobGroupRepository;
    }

    /**
     * Gets all the job groups from the database
     * @return all the job groups in the database
     */
    @GetMapping
    @Secured({Roles.ADMIN, Roles.BOSS, Roles.WORKER})
    public ResponseEntity<JobGroupCollectionDto> getAll() {
        LogStepIn();

        List<JobGroup> jobGroupList = jobGroupRepository.findAll();
        JobGroupCollectionDto jobGroupCollectionDto = new JobGroupCollectionDto();

        for (JobGroup jobGroup : jobGroupList) {
            jobGroupCollectionDto.collection.add(toDto(jobGroup));
        }

        return LogStepOut(ResponseEntity.ok(jobGroupCollectionDto));
    }

    /**
     * Gets the specific job group by id
     * @param id the unique identifier of the JobGroup
     * @return job group data transfer object
     */
    @GetMapping("{id}")
    @Secured({Roles.ADMIN, Roles.BOSS, Roles.WORKER})
    public ResponseEntity<JobGroupDto> getById(@PathVariable long id) {
        LogStepIn();

        JobGroup jobGroup = jobGroupRepository.findById(id);

        if (jobGroup == null)
            return LogStepOut(ResponseEntity.notFound().build());
        else
            return LogStepOut(ResponseEntity.ok(toDto(jobGroup)));
    }

    /**
     * Creates a new JobGroup
     * @param jobGroupDto job group data transfer object
     */
    @PostMapping
    @Secured(Roles.ADMIN)
    public ResponseEntity<?> create(@Valid @RequestBody JobGroupDto jobGroupDto) {
        LogStepIn();

        // Validation for name attribute
        List<JobGroup> jobGroupList = jobGroupRepository.findAll();
        for (JobGroup jobGroupElement : jobGroupList) {
            if(jobGroupElement.getName().equals(jobGroupDto.name))
                return LogStepOut(ResponseEntity.badRequest().body("A Job Group with this name already exists!"));
        }

        JobGroup jobGroup = toEntity(jobGroupDto);

        // Saving the entity
        JobGroupDto createdJobGroupDto = toDto(jobGroupRepository.save(jobGroup));

        // Getting the request URI for created status location header
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        builder.path("/" + createdJobGroupDto.id);

        return LogStepOut(ResponseEntity.created(builder.build().toUri()).body(createdJobGroupDto));
    }

    /**
     * Deletes a JobGroup by id
     * @param id the unique identifier of the JobGroup to be deleted
     */
    @DeleteMapping("{id}")
    @Secured(Roles.ADMIN)
    public ResponseEntity<?> delete(@PathVariable long id) {
        LogStepIn();

        JobGroup jobGroup = jobGroupRepository.findById(id);

        if (jobGroup == null) {
            return LogStepOut(ResponseEntity.notFound().build());
        }
        else {
            jobGroupRepository.deleteById(id);
            return LogStepOut(ResponseEntity.noContent().build());
        }
    }

    //region Helper Methods

    /**
     * Converts the JobGroup to Entity aka domain model
     * @param jobGroupDto the data transfer object
     * @return job group domain model object
     */
    private JobGroup toEntity(JobGroupDto jobGroupDto) {
        JobGroup jobGroup = new JobGroup();
        jobGroup.setName(jobGroupDto.name);

        return jobGroup;
    }

    /**
     * Converts the JobGroup to a lightweight Data Transfer Object
     * @param jobGroup domain model object
     * @return job group data transfer object
     */
    private JobGroupDto toDto(JobGroup jobGroup) {
        JobGroupDto jobGroupDto = new JobGroupDto();
        jobGroupDto.id = jobGroup.getId();
        jobGroupDto.name = jobGroup.getName();

        return jobGroupDto;
    }

    //endregion
}
