package hu.bme.aut.payroll.repository;

import hu.bme.aut.payroll.domain.Employee;
import hu.bme.aut.payroll.domain.HungarianTaxFees;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Calendar;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class HungarianTaxFeesRepositoryTest {

    @Autowired
    private HungarianTaxFeesRepository repository;

    private static HungarianTaxFees defaultTaxFee;

    @BeforeAll
    public static void init() {
        defaultTaxFee = new HungarianTaxFees();
        defaultTaxFee.setSZJA(1.0);
        defaultTaxFee.setEntrantFare(1.0);
        defaultTaxFee.setWorkStatusDiscount(1.0);
        defaultTaxFee.setChildFare(1.0);
        defaultTaxFee.setContributionVocationalTraining(1.0);
        defaultTaxFee.setLaborMarketContribution(1.0);
        defaultTaxFee.setHealthInsuranceContribution(1.0);
        defaultTaxFee.setPensionContribution(1.0);
        defaultTaxFee.setSocialContribution(1.0);
        defaultTaxFee.setJustMarriedFare(1.0);
        defaultTaxFee.setStartDate(new Date());
    }

    @BeforeEach
    public void before() {
        this.repository.deleteAll();
    }

    @Test
    public void findById_ReturnsRightHungarianTaxFees() {
        // Arrange
        HungarianTaxFees savedTaxFee = repository.save(defaultTaxFee);

        // Act
        HungarianTaxFees foundTaxFee = repository.findById(savedTaxFee.getId().longValue());

        // Assert
        assertEquals(foundTaxFee.getId().longValue(), savedTaxFee.getId().longValue());
    }

    @Test
    public void findById_ReturnsNullOnInvalidId() {
        // Act
        HungarianTaxFees foundTaxFee = repository.findById(Long.MAX_VALUE);

        // Assert
        assertNull(foundTaxFee, "On invalid id it should return null");
    }

    @Test
    public void findFirstByOrderByStartDateDesc_GetsTheLatestOne() {
        // Arrange
        HungarianTaxFees hungarianTaxFeef1 = defaultTaxFee;
        hungarianTaxFeef1.setStartDate(Calendar.getInstance().getTime());
        HungarianTaxFees savedTaxFee1 = repository.save(hungarianTaxFeef1);

        HungarianTaxFees hungarianTaxFeef2 = defaultTaxFee;
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 5000);
        hungarianTaxFeef2.setStartDate(calendar2.getTime());
        HungarianTaxFees savedTaxFee2 = repository.save(hungarianTaxFeef2);

        HungarianTaxFees hungarianTaxFeef3 = defaultTaxFee;
        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(Calendar.YEAR, 9900);
        hungarianTaxFeef3.setStartDate(calendar3.getTime());
        HungarianTaxFees latestTaxFee = repository.save(hungarianTaxFeef3);

        // Act
        HungarianTaxFees result = repository.findFirstByOrderByStartDateDesc();

        // Assert
        assertEquals(latestTaxFee.getId().longValue(), result.getId().longValue(),
            "findFirstByOrderByStartDateDesc() should always return the latest Tax fee entry from the DB");
    }

    @Test
    public void findFirstByOrderByStartDateDesc_ReturnsNullOnEmptyTable() {
        // Act
        HungarianTaxFees result = repository.findFirstByOrderByStartDateDesc();

        // Assert
        assertNull(result, "If the table is not yet seeded it should return null");
    }
}
