package hu.bme.aut.payroll.repository;

import hu.bme.aut.payroll.domain.JobGroup;
import hu.bme.aut.payroll.domain.JobType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JobTypeRepositoryTest {

    @Autowired
    private JobTypeRepository repository;

    private static JobGroup testJobGroup;

    @BeforeAll
    public static void beforeAll(@Autowired JobGroupRepository jobGroupRepository) {
        testJobGroup = new JobGroup();
        testJobGroup.setName("Test Team_4");

        jobGroupRepository.save(testJobGroup);
    }

    @Test
    public void findById_ReturnsRightJobGroup() {
        // Arrange
        JobType jobType = new JobType();
        jobType.setName("Test Job_1");
        jobType.setJobGroup(testJobGroup);

        JobType savedJobType = repository.save(jobType);

        // Act
        JobType foundJobType = repository.findById(savedJobType.getId().longValue());

        // Assert
        assertEquals(foundJobType.getId().longValue(), savedJobType.getId().longValue());
    }

    @Test
    public void findById_ReturnsNullOnInvalidId() {
        // Act
        JobType foundJobType = repository.findById(Long.MAX_VALUE);

        // Assert
        assertNull(foundJobType, "On invalid id it should return null");
    }

    @Test
    public void deleteById_Deleted() {
        // Arrange
        JobType jobType1 = new JobType();
        jobType1.setName("Test Job_2");
        jobType1.setJobGroup(testJobGroup);

        JobType jobType2 = new JobType();
        jobType2.setName("Test Job_3");
        jobType2.setJobGroup(testJobGroup);

        repository.save(jobType1);
        JobType savedJobType = repository.save(jobType2);

        // Act
        long sizeBefore = repository.count();
        repository.deleteById(savedJobType.getId().longValue());
        long sizeAfter = repository.count();

        // Assert
        assertNotEquals(sizeBefore, sizeAfter, "The number of job types must have been decremented");
    }
}
