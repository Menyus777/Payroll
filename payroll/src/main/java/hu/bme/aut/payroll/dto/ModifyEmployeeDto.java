package hu.bme.aut.payroll.dto;

import javax.validation.constraints.NotNull;

public class ModifyEmployeeDto {

    /**
     * The unique identifier of the employee
     */
    @NotNull
    public Long id;

    /**
     * The password of the current employee
     */
    public String password;

    /**
     * The Gross Payment/month of this employee
     */
    public Integer grossPayment;

    /**
     * The number of hours this employee works per week
     */
    public Integer workHours;

    /**
     * The number of child this employee has
     */
    public Integer children;

    /**
     * The work status of the employee eg.: Retired, Student..
     */
    public String workStatus;

    /**
     * Tells whether the employee is an entrant or not
     */
    public Boolean isEntrant;
    /**
     * Tells whether the employee is in a fresh first marriage or not
     */
    public Boolean isJustMarried;
    /**
     * Tells whether the employee is a single parent or not
     */
    public Boolean isSingleParent;
}
