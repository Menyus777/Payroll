package hu.bme.aut.payroll.web.service.domain;

import hu.bme.aut.payroll.domain.HungarianTaxFees;
import hu.bme.aut.payroll.repository.HungarianTaxFeesRepository;
import org.springframework.stereotype.Component;

/**
 * Helps to calculate employee salaries
 */
@Component
public class PaymentCalculator {

    private final HungarianTaxFeesRepository hungarianTaxFeesRepository;

    public PaymentCalculator(HungarianTaxFeesRepository hungarianTaxFeesRepository) {
        this.hungarianTaxFeesRepository = hungarianTaxFeesRepository;
    }

    /**
     * Calculate employee's net payment
     * @param grossPayment employee's gross payment
     * @param numberOfChildren employee's children number
     * @return calculated net payment
     */
    public int getNetPayment(double grossPayment, int numberOfChildren) {
        HungarianTaxFees taxFees = hungarianTaxFeesRepository.findFirstByOrderByStartDateDesc();

        double multiplier = 1.0;
        multiplier -= taxFees.getSZJA();
        multiplier -= taxFees.getPensionContribution();
        multiplier -= taxFees.getHealthInsuranceContribution();
        multiplier -= taxFees.getLaborMarketContribution();

        return (int)((grossPayment * multiplier)
                + taxFees.getJustMarriedFare() + taxFees.getChildFare() * numberOfChildren);
    }

    /**
     * Calculate employee's total cost
     * @param grossPayment employee's gross payment
     * @param isEntrant employee is an entrant or not
     * @return calculated total cost
     */
    public int getEmployerTotalCost(double grossPayment, boolean isEntrant) {
        HungarianTaxFees taxFees = hungarianTaxFeesRepository.findFirstByOrderByStartDateDesc();

        double multiplier = 1.0;
        multiplier += taxFees.getSocialContribution();
        multiplier += taxFees.getContributionVocationalTraining();
        if(isEntrant)
            multiplier -= taxFees.getEntrantFare();

        return (int)(grossPayment * multiplier);
    }
}
