package com.iemr.flw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.MaaMeeting;
import com.iemr.flw.dto.iemr.MaaMeetingRequestDTO;
import com.iemr.flw.dto.iemr.MaaMeetingResponseDTO;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.MaaMeetingRepository;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaaMeetingService {
    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;
    private final MaaMeetingRepository repository;
    private final ObjectMapper objectMapper;

    public MaaMeetingService(MaaMeetingRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public List<MaaMeeting> saveMeeting(List<MaaMeetingRequestDTO> dtoList, List<MultipartFile> files) throws Exception {
        List<MaaMeeting> meetings = new ArrayList<>();

        for (MaaMeetingRequestDTO req : dtoList) {
            MaaMeeting meeting = new MaaMeeting();
            meeting.setMeetingDate(req.getMeetingDate());
            meeting.setPlace(req.getPlace());
            meeting.setParticipants(req.getParticipants());
            meeting.setAshaId(req.getAshaId());

            List<String> base64Images = new ArrayList<>();

            // 🟢 1️⃣ Convert any images inside DTO itself
            if (req.getMeetingImages() != null && req.getMeetingImages().length > 0) {
                for (MultipartFile file : req.getMeetingImages()) {
                    if (file != null && !file.isEmpty()) {
                        base64Images.add(Base64.getEncoder().encodeToString(file.getBytes()));
                    }
                }
            }

            // 🟢 2️⃣ Convert any separate uploaded files (from @RequestPart("files"))
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (file != null && !file.isEmpty()) {
                        base64Images.add(Base64.getEncoder().encodeToString(file.getBytes()));
                    }
                }
            }

            // 🟢 3️⃣ Convert all collected base64 images to JSON array string
            if (!base64Images.isEmpty()) {
                try {
                    String imagesJson = objectMapper.writeValueAsString(base64Images);
                    meeting.setMeetingImagesJson(imagesJson);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error converting image list to JSON", e);
                }
            }

            meetings.add(meeting);
        }

        // 🟢 4️⃣ Save all records together
        return repository.saveAll(meetings);
    }


    public List<MaaMeetingResponseDTO> getAllMeetings() throws Exception {
        List<MaaMeeting> meetings = repository.findAll();
        return meetings.stream().map(meeting -> {
            MaaMeetingResponseDTO dto = new MaaMeetingResponseDTO();
            dto.setId(meeting.getId());
            dto.setMeetingDate(meeting.getMeetingDate());
            dto.setPlace(meeting.getPlace());
            dto.setParticipants(meeting.getParticipants());
            dto.setAshaId(meeting.getAshaId());

            try {
                if (meeting.getMeetingImagesJson() != null) {
                    List<String> images = objectMapper.readValue(
                            meeting.getMeetingImagesJson(),
                            new TypeReference<List<String>>() {
                            }
                    );
                    dto.setMeetingImages(images);
                }
            } catch (Exception e) {
                dto.setMeetingImages(List.of());
            }

            return dto;
        }).collect(Collectors.toList());
    }
}
