package hu.bme.aut.payroll.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Represent an employee inside the organization
 */
public class EmployeeDto {

    /**
     * The unique identifier of the employee
     */
    public Long id;

    /**
     * The name of the employee
     */
    @NotBlank
    public String name;

    /**
     * The e-mail address of the employee
     */
    @Email
    public String email;

    /**
     * The id of the jobType this employee is employed
     */
    @NotNull
    public Long jobTypeId;

    /**
     * The id of the employee who is the boss of this employee
     */
    @NotNull
    public Long bossId;

    /**
     * The Net Payment/month of this employee
     */
    public Integer netPayment;
    /**
     * The Gross Payment/month of this employee
     */
    @NotNull
    public Integer grossPayment;
    /**
     * The total cost of this employee for a month for the organization
     */
    public Integer employerTotalCost;
    /**
     * The number of hours this employee works per week
     */
    @NotNull
    public Integer workHours;

    /**
     * The number of child this employee has
     */
    @NotNull
    public Integer children;

    /**
     * The work status of the employee eg.: Retired, Student..
     */
    @NotBlank
    public String workStatus;

    /**
     * Tells whether the employee is an entrant or not
     */
    @NotNull
    public Boolean isEntrant;
    /**
     * Tells whether the employee is in a fresh first marriage or not
     */
    @NotNull
    public Boolean isJustMarried;
    /**
     * Tells whether the employee is a single parent or not
     */
    @NotNull
    public Boolean isSingleParent;
}
