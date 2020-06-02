package hu.bme.aut.payroll.dto;

import javax.validation.constraints.NotBlank;

/**
 * Represent a type of job group inside the organization
 */
public class JobGroupDto {

    /**
     * The unique identifier of the job group
     */
    public Long id;

    /**
     * The name of the job group
     */
    @NotBlank
    public String name;
}
