package com.iemr.flw.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.SammelanAttachment;
import com.iemr.flw.domain.iemr.SammelanRecord;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.repo.iemr.SammelanAttachmentRepository;
import com.iemr.flw.repo.iemr.SammelanRecordRepository;
import com.iemr.flw.service.SammelanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SammelanServiceImpl implements SammelanService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    private SammelanRecordRepository recordRepo;
    @Autowired
    private SammelanAttachmentRepository attachmentRepo;

    private SammelanRecord record;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public SammelanResponseDTO submitSammelan(SammelanRequestDTO sammelanRequestDTO, MultipartFile[] images) {
        SammelanResponseDTO response = new SammelanResponseDTO();

        try {
            validateRequest(sammelanRequestDTO);
            LocalDate localDate = sammelanRequestDTO.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            YearMonth ym = YearMonth.from(localDate);

            // Check for existing record in same month
            boolean exists = recordRepo.existsByAshaIdAndMeetingDateBetween(
                    sammelanRequestDTO.getAshaId(),
                    ym.atDay(1),
                    ym.atEndOfMonth()
            );
            if (exists) {
                throw new IllegalArgumentException("Sammelan already submitted for this month.");
            }

            // Save Sammelan record
            record = new SammelanRecord();
            record.setAshaId(sammelanRequestDTO.getAshaId());
            logger.info("Meeting Date:"+sammelanRequestDTO.getDate());
            record.setMeetingDate(sammelanRequestDTO.getDate());
            record.setPlace(sammelanRequestDTO.getPlace());
            record.setParticipants(sammelanRequestDTO.getParticipants());
            record = recordRepo.save(record);

            // Save Attachments
            if (images != null && images.length > 0) {
                List<String> base64Images = List.of(images)
                        .stream()
                        .filter(file -> !file.isEmpty())
                        .map(file -> {
                            try {
                                return Base64.getEncoder().encodeToString(file.getBytes());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());

                String imagesJson = null;
                try {
                    imagesJson = objectMapper.writeValueAsString(base64Images);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                record.setAttachments(imagesJson);
            }

            // Prepare Response DTO
            response.setId(record.getId());
            response.setAshaId(record.getAshaId());
            response.setDate(record.getMeetingDate());
            response.setPlace(record.getPlace());
            response.setParticipants(record.getParticipants());


        } catch (Exception e) {

            logger.info("Exception: "+e.getMessage());
        }
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
            try {
                if (record.getAttachments() != null) {
                    List<String> images = objectMapper.readValue(
                            record.getAttachments(),
                            new TypeReference<List<String>>() {
                            }
                    );
                    dto.setImagePaths(images);
                }
            } catch (Exception e) {
                dto.setImagePaths(List.of());
            }

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


    }
}
