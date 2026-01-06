package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.ChronicDiseaseVisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChronicDiseaseVisitRepository
        extends JpaRepository<ChronicDiseaseVisitEntity, Long> {
}
