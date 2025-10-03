package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.SammelanRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SammelanRecordRepository extends JpaRepository<SammelanRecord, Long> {

    // Check if ASHA has already submitted record in same month
    boolean existsByAshaIdAndMeetingDateBetween(Integer ashaId, LocalDate startDate, LocalDate endDate);

    // Fetch history
    List<SammelanRecord> findByAshaIdOrderByMeetingDateDesc(Integer ashaId);
}
