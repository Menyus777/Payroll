package hu.bme.aut.payroll.repository;

import hu.bme.aut.payroll.domain.JobGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobGroupRepository extends JpaRepository<JobGroup, Long> {

    @Query("SELECT jg FROM JobGroup jg WHERE jg.id = :id")
    JobGroup findById(@Param("id") long id);

    @Modifying
    @Query("DELETE FROM JobGroup jg WHERE jg.id = :id")
    void deleteById(@Param("id") long id);
}
