package hu.bme.aut.payroll.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
public class HungarianTaxFees {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private Double SZJA;
    @Column(nullable = false)
    private Double entrantFare;
    @Column(nullable = false)
    private Double workStatusDiscount;
    @Column(nullable = false)
    private Double childFare;
    @Column(nullable = false)
    private Double contributionVocationalTraining;
    @Column(nullable = false)
    private Double laborMarketContribution;
    @Column(nullable = false)
    private Double healthInsuranceContribution;
    @Column(nullable = false)
    private Double pensionContribution;
    @Column(nullable = false)
    private Double socialContribution;
    @Column(nullable = false)
    private Double justMarriedFare;
    @Column(nullable = false)
    private Date startDate;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Double getSZJA() {  return SZJA;  }
    public void setSZJA(Double SZJA) {
        this.SZJA = SZJA;
    }

    public Double getEntrantFare() {  return entrantFare;  }
    public void setEntrantFare(Double entrantFare) {
        this.entrantFare = entrantFare;
    }

    public Double getWorkStatusDiscount() {  return workStatusDiscount;  }
    public void setWorkStatusDiscount(Double workStatus) {
        this.workStatusDiscount = workStatus;
    }

    public Double getChildFare() {  return childFare;  }
    public void setChildFare(Double childFare) {
        this.childFare = childFare;
    }

    public Double getContributionVocationalTraining() {  return contributionVocationalTraining;  }
    public void setContributionVocationalTraining(Double contributionVocationalTraining) {
        this.contributionVocationalTraining = contributionVocationalTraining;
    }

    public Double getLaborMarketContribution() {  return laborMarketContribution;  }
    public void setLaborMarketContribution(Double laborMarketContribution) {
        this.laborMarketContribution = laborMarketContribution;
    }

    public Double getHealthInsuranceContribution() {  return healthInsuranceContribution;  }
    public void setHealthInsuranceContribution(Double healthInsuranceContribution) {
        this.healthInsuranceContribution = healthInsuranceContribution;
    }

    public Double getPensionContribution() {  return pensionContribution;  }
    public void setPensionContribution(Double pensionContribution) {
        this.pensionContribution = pensionContribution;
    }

    public Double getSocialContribution() {  return socialContribution;  }
    public void setSocialContribution(Double socialContribution) {
        this.socialContribution = socialContribution;
    }

    public Double getJustMarriedFare() {  return justMarriedFare;  }
    public void setJustMarriedFare(Double justMarriedFare) {
        this.justMarriedFare = justMarriedFare;
    }

    public Date getStartDate() {  return startDate;  }
    public void setStartDate(Date expirationDate) {
        this.startDate = expirationDate;
    }
}
