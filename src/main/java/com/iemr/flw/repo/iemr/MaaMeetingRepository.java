package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.MaaMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaaMeetingRepository extends JpaRepository<MaaMeeting, Long> {
}
