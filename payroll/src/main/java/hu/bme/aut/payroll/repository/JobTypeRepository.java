package hu.bme.aut.payroll.repository;

import hu.bme.aut.payroll.domain.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTypeRepository extends JpaRepository<JobType, Long> {

    @Query("SELECT jt FROM JobType jt WHERE jt.id = :id")
    JobType findById(@Param("id") long id);

    @Modifying
    @Query("DELETE FROM JobType jt WHERE jt.id = :id")
    void deleteById(@Param("id") long id);
}
