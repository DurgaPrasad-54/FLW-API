package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.SammelanAttachment;
import com.iemr.flw.domain.iemr.SammelanRecord;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.repo.iemr.SammelanAttachmentRepository;
import com.iemr.flw.repo.iemr.SammelanRecordRepository;
import com.iemr.flw.service.SammelanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SammelanServiceImpl implements SammelanService {
    @Autowired
    private  SammelanRecordRepository recordRepo;
    @Autowired
    private  SammelanAttachmentRepository attachmentRepo;

    private SammelanRecord record;



    @Override
    public SammelanResponseDTO submitSammelan(SammelanRequestDTO dto) {
        validateRequest(dto);
        LocalDate localDate = dto.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        YearMonth ym = YearMonth.from(localDate);

        // Check for existing record in same month
        boolean exists = recordRepo.existsByAshaIdAndMeetingDateBetween(
                dto.getAshaId(),
                ym.atDay(1),
                ym.atEndOfMonth()
        );
        if (exists) {
            throw new IllegalArgumentException("Sammelan already submitted for this month.");
        }

        // Save Sammelan record
        record = new SammelanRecord();
        record.setAshaId(dto.getAshaId());
        record.setMeetingDate(dto.getDate());
        record.setPlace(dto.getPlace());
        record.setParticipants(dto.getParticipants());
        record = recordRepo.save(record);

        // Save Attachments
        if (dto.getAttachments() != null) {
            List<SammelanAttachment> attachments = dto.getAttachments().stream().map(a -> {
                SammelanAttachment att = new SammelanAttachment();
                att.setSammelanRecord(record);
                att.setFileName(a.getFileName());
                att.setFileType(a.getFileType());
                att.setFileSize(a.getFileSize());
                return att;
            }).collect(Collectors.toList());
            attachmentRepo.saveAll(attachments);
            record.setAttachments(attachments);
        }

        // Save incentive audit


        // Prepare Response DTO
        SammelanResponseDTO response = new SammelanResponseDTO();
        response.setId(record.getId());
        response.setAshaId(record.getAshaId());
        response.setDate(record.getMeetingDate());
        response.setPlace(record.getPlace());
        response.setParticipants(record.getParticipants());

        return response;
    }

    @Override
    public List<SammelanResponseDTO> getSammelanHistory(Integer ashaId) {
        List<SammelanRecord> records = recordRepo.findByAshaIdOrderByMeetingDateDesc(ashaId);
        return records.stream().map(record -> {
            SammelanResponseDTO dto = new SammelanResponseDTO();
            dto.setId(record.getId());
            dto.setAshaId(record.getAshaId());
            dto.setDate(record.getMeetingDate());
            dto.setPlace(record.getPlace());
            dto.setParticipants(record.getParticipants());

            return dto;
        }).collect(Collectors.toList());
    }

    private void validateRequest(SammelanRequestDTO dto) {
        LocalDate date = dto.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (date == null) {
            throw new IllegalArgumentException("Date is mandatory.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in future.");
        }
        if (date.isBefore(LocalDate.now().minusMonths(2))) {
            throw new IllegalArgumentException("Backdate not allowed beyond 2 months.");
        }
        if (dto.getParticipants() < 0 || dto.getParticipants() > 999) {
            throw new IllegalArgumentException("Participants must be between 0–999.");
        }
        if (dto.getAttachments() == null || dto.getAttachments().size() < 2) {
            throw new IllegalArgumentException("Minimum 2 attachments required.");
        }
        if (dto.getAttachments().size() > 5) {
            throw new IllegalArgumentException("Maximum 5 attachments allowed.");
        }
    }
}
