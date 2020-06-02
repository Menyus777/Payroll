package hu.bme.aut.payroll.repository;

import hu.bme.aut.payroll.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE e.id = :id")
    Employee findById(@Param("id") long id);

    @Modifying
    @Query("DELETE FROM Employee e WHERE e.id = :id")
    void deleteById(@Param("id") long id);
}
