package hu.bme.aut.payroll.domain;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Employee {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @Email
    @Column(unique = true)
    private String email;

    @NotNull
    private Long bossId;

    private Integer netPayment;
    private Integer grossPayment;
    private Integer employerTotalCost;
    private Integer workHours;

    private Integer children;
    @Enumerated(EnumType.STRING)
    private WorkStatus workStatus;

    private Boolean isEntrant;
    private Boolean isJustMarried;
    private Boolean isSingleParent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jobtype_id")
    private JobType jobtype;

    @OneToOne(mappedBy = "employee")
    private User user;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getNetPayment() {
        return netPayment;
    }

    public void setNetPayment(Integer netPayment) {
        this.netPayment = netPayment;
    }

    public Integer getGrossPayment() {
        return grossPayment;
    }

    public void setGrossPayment(Integer grossPayment) {
        this.grossPayment = grossPayment;
    }

    public Integer getEmployerTotalCost() {
        return employerTotalCost;
    }

    public void setEmployerTotalCost(Integer employerTotalCost) {
        this.employerTotalCost = employerTotalCost;
    }

    public Integer getWorkHours() {
        return workHours;
    }

    public void setWorkHours(Integer workHours) {
        this.workHours = workHours;
    }

    public Boolean isEntrant() {
        return isEntrant;
    }

    public void setEntrant(Boolean entrant) {
        isEntrant = entrant;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public WorkStatus getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(WorkStatus workStatus) {
        this.workStatus = workStatus;
    }

    public Boolean isJustMarried() {
        return isJustMarried;
    }

    public void setJustMarried(Boolean justMarried) {
        isJustMarried = justMarried;
    }

    public Boolean isSingleParent() {
        return isSingleParent;
    }

    public void setSingleParent(Boolean singleParent) {
        isSingleParent = singleParent;
    }

    public Long getBossId() {
        return bossId;
    }

    public void setBossId(Long bossId) {
        this.bossId = bossId;
    }

    public JobType getJobtype() {
        return jobtype;
    }

    public void setJobtype(JobType jobtype) {
        this.jobtype = jobtype;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
