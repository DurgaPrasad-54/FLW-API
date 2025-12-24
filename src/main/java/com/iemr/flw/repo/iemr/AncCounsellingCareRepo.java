package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.AncCare;
import com.iemr.flw.domain.iemr.AncCounsellingCare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AncCounsellingCareRepo extends JpaRepository<AncCounsellingCare, Long> {
}
