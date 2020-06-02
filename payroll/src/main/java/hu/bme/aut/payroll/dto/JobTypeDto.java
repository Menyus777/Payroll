package hu.bme.aut.payroll.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Represent a type of job inside the organization
 */
public class JobTypeDto {

    /**
     * The unique identifier of the job type
     */
    public Long id;

    /**
     * The name of the Job Type
     */
    @NotBlank
    public String name;

    /**
     * The id of job group this Job Type belongs to
     */
    @NotNull
    public Long jobGroupId;
}
