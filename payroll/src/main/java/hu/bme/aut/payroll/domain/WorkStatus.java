package hu.bme.aut.payroll.domain;

import java.util.HashMap;
import java.util.Map;

public enum WorkStatus
{
    ADULT("Adult"),
    STUDENT("Student"),
    RETIRED("Retired");

    private String workStatus;

    //Lookup table
    private static final Map<String, WorkStatus> lookupTable = new HashMap<>();

    //Populate the lookup table on loading time
    static {
        for(WorkStatus workStatus : WorkStatus.values()) {
            lookupTable.put(workStatus.getStringValue(), workStatus);
        }
    }

    WorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    /**
     * Gets the work status string value
     * @return the string value of the enum
     */
    public String getStringValue() {
        return workStatus;
    }

    /**
     * Gets the enum appropriate enum value from a string
     * @param workStatus the string value of the enum field
     * @return the binded enum
     */
    public static WorkStatus get(String workStatus)
    {
        return lookupTable.get(workStatus);
    }

}
