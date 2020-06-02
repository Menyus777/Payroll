package hu.bme.aut.payroll.repository;

import hu.bme.aut.payroll.domain.HungarianTaxFees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HungarianTaxFeesRepository extends JpaRepository<HungarianTaxFees, Long> {

    @Query("SELECT hg FROM HungarianTaxFees hg WHERE hg.id = :id")
    HungarianTaxFees findById(@Param("id") long id);

    HungarianTaxFees findFirstByOrderByStartDateDesc();
}
