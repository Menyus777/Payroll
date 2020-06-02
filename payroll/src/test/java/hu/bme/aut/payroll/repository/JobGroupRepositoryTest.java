package hu.bme.aut.payroll.repository;

import hu.bme.aut.payroll.domain.JobGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JobGroupRepositoryTest {

    @Autowired
    private JobGroupRepository repository;

    @Test
    public void findById_ReturnsRightJobGroup() {
        // Arrange
        JobGroup jobGroup = new JobGroup();
        jobGroup.setName("Test Team_1");
        JobGroup savedJobGroup = repository.save(jobGroup);

        // Act
        JobGroup foundJobGroup = repository.findById(savedJobGroup.getId().longValue());

        // Assert
        assertEquals(savedJobGroup.getId().longValue(), foundJobGroup.getId().longValue());
    }

    @Test
    public void findById_ReturnsNullOnInvalidId() {
        // Act
        JobGroup foundJobGroup = repository.findById(Long.MAX_VALUE);

        // Assert
        assertNull(foundJobGroup, "On invalid id it should return null");
    }

    @Test
    public void deleteById_Deleted() {
        // Arrange
        JobGroup jobGroup1 = new JobGroup();
        jobGroup1.setName("Test Team_2");

        JobGroup jobGroup2 = new JobGroup();
        jobGroup2.setName("Test Team_3");

        repository.save(jobGroup1);
        JobGroup savedJobGroup2 = repository.save(jobGroup2);

        // Act
        long sizeBefore = repository.count();
        repository.deleteById(savedJobGroup2.getId().longValue());
        long sizeAfter = repository.count();

        // Assert
        assertNotEquals(sizeBefore, sizeAfter, "The number of job groups must have been decremented");
    }
}
