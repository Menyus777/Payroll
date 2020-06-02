package hu.bme.aut.payroll.dto;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Represent a hungarian tax fees
 */
public class HungarianTaxFeesDto {

    /**
     * The unique identifier of the hungarian tax fees
     */
    public Long id;

    /**
     * The SZJA of the hungarian tax fee
     */
    @NotNull
    public Double SZJA;

    /**
     * The entrant fare according to the hungarian tax fee
     */
    @NotNull
    public Double entrantFare;

    /**
     * The work status discount according to the hungarian tax fee
     */
    @NotNull
    public Double workStatusDiscount;

    /**
     * The child fare according to the hungarian tax fee
     */
    @NotNull
    public Double childFare;

    /**
     * The contribution vocational training according to the hungarian tax fee
     */
    @NotNull
    public Double contributionVocationalTraining;

    /**
     * The labor market contribution according to the hungarian tax fee
     */
    @NotNull
    public Double laborMarketContribution;

    /**
     * The health insurance contribution according to the hungarian tax fee
     */
    @NotNull
    public Double healthInsuranceContribution;

    /**
     * The pension contribution according to the hungarian tax fee
     */
    @NotNull
    public Double pensionContribution;

    /**
     * The social contribution according to the hungarian tax fee
     */
    @NotNull
    public Double socialContribution;

    /**
     * The just married fare according to the hungarian tax fee
     */
    @NotNull
    public Double justMarriedFare;

    /**
     * The expiration date according to the hungarian tax fees
     */
    @NotNull
    public Date startDate;
}
