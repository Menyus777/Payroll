package hu.bme.aut.payroll.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
public class JobType {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jobgroup_id")
    private JobGroup jobgroup;

    @OneToMany(mappedBy = "jobtype", fetch = FetchType.EAGER)
    private List<Employee> employees;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobGroup getJobGroup() {
        return jobgroup;
    }

    public void setJobGroup(JobGroup jobgroup) {
        this.jobgroup = jobgroup;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
