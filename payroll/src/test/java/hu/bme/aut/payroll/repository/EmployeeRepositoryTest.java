package hu.bme.aut.payroll.repository;

import hu.bme.aut.payroll.domain.Employee;
import hu.bme.aut.payroll.domain.JobGroup;
import hu.bme.aut.payroll.domain.JobType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;

    private static JobGroup testJobGroup;
    private static JobType testJobType;

    @BeforeAll
    public static void init(
        @Autowired JobGroupRepository jobGroupRepository,
        @Autowired JobTypeRepository jobTypeRepository,
        @Autowired EmployeeRepository repository) {
        testJobGroup = new JobGroup();
        testJobGroup.setName("Test Team_5");

        jobGroupRepository.save(testJobGroup);

        testJobType = new JobType();
        testJobType.setName("Test Job_4");
        testJobType.setJobGroup(testJobGroup);

        jobTypeRepository.save(testJobType);
    }

    @Test
    public void findById_ReturnsRightEmployee() {
        // Arrange
        Employee employee = new Employee();
        employee.setName("Test_Employee_1");
        employee.setEmail("testemplyoee1@organization.com");
        employee.setBossId(0L);
        employee.setGrossPayment(500_000);
        employee.setJobtype(testJobType);

        Employee savedEmployee = repository.save(employee);

        // Act
        Employee foundEmployee = repository.findById(employee.getId().longValue());

        // Assert
        assertEquals(foundEmployee.getId().longValue(), savedEmployee.getId().longValue());
    }

    @Test
    public void findById_ReturnsNullOnInvalidId() {
        // Act
        Employee foundEmployee = repository.findById(Long.MAX_VALUE);

        // Assert
        assertNull(foundEmployee, "On invalid id it should return null");
    }

    @Test
    public void deleteById_Deleted() {
        // Arrange
        Employee employee1 = new Employee();
        employee1.setName("Test_Employee_2");
        employee1.setEmail("testemplyoee2@organization.com");
        employee1.setBossId(0L);
        employee1.setGrossPayment(500_000);
        employee1.setJobtype(testJobType);

        Employee employee2 = new Employee();
        employee2.setName("Test_Employee_3");
        employee2.setEmail("testemplyoee3@organization.com");
        employee2.setBossId(0L);
        employee2.setGrossPayment(500_000);
        employee2.setJobtype(testJobType);

        List<Employee> js = repository.findAll();
        repository.save(employee1);
        Employee savedEmployee2 = repository.save(employee2);

        // Act
        long sizeBefore = repository.count();
        repository.deleteById(savedEmployee2.getId().longValue());
        long sizeAfter = repository.count();

        // Assert
        assertNotEquals(sizeBefore, sizeAfter, "The number of job groups must have been decremented");
    }
}
