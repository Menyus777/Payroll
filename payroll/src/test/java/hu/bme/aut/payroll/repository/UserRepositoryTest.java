package hu.bme.aut.payroll.repository;

import hu.bme.aut.payroll.domain.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private static Employee testEmployee;

    @BeforeAll
    public static void init(
            @Autowired JobGroupRepository jobGroupRepository,
            @Autowired JobTypeRepository jobTypeRepository,
            @Autowired EmployeeRepository employeeRepository) {
        JobGroup jobGroup = new JobGroup();
        jobGroup.setName("Test");
        jobGroup = jobGroupRepository.save(jobGroup);

        JobType jobType = new JobType();
        jobType.setName("Test");
        jobType.setJobGroup(jobGroup);
        jobType = jobTypeRepository.save(jobType);

        testEmployee = new Employee();
        testEmployee.setName("Test_Employee_User");
        testEmployee.setEmail("testemployeeuser@organization.com");
        testEmployee.setBossId(0L);
        testEmployee.setGrossPayment(500_000);
        testEmployee.setJobtype(jobType);
        testEmployee = employeeRepository.save(testEmployee);
    }

    @Test
    public void deleteById_Deleted() {
        // Arrange
        User user1 = new User();
        user1.setName("TestUserName_1");
        user1.setEmployee(testEmployee);
        user1.setPassword("SomeVerySecretHashPassword");

        user1 = repository.save(user1);

        // Act
        long sizeBefore = repository.count();
        repository.deleteById(user1.getId());
        long sizeAfter = repository.count();

        // Assert
        assertNotEquals(sizeBefore, sizeAfter, "The number of users must have been decremented");
    }
}
