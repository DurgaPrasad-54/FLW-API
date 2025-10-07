package com.iemr.flw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.MaaMeeting;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.MaaMeetingRequestDTO;
import com.iemr.flw.dto.iemr.MaaMeetingResponseDTO;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.MaaMeetingRepository;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public MaaMeeting saveMeeting(MaaMeetingRequestDTO req) throws Exception {
        MaaMeeting meeting = new MaaMeeting();
        meeting.setMeetingDate(req.getMeetingDate());
        meeting.setPlace(req.getPlace());
        meeting.setParticipants(req.getParticipants());
        meeting.setAshaId(req.getAshaId());

        // Convert meeting images to Base64 JSON
        if (req.getMeetingImages() != null && req.getMeetingImages().length > 0) {
            List<String> base64Images = Arrays.stream(req.getMeetingImages())
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        try {
                            return Base64.getEncoder().encodeToString(file.getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException("Error converting image to Base64", e);
                        }
                    })
                    .collect(Collectors.toList());

            String imagesJson = objectMapper.writeValueAsString(base64Images);
            meeting.setMeetingImagesJson(imagesJson);
        }

        return repository.save(meeting);
    }



    public List<MaaMeetingResponseDTO> getAllMeetings(GetBenRequestHandler getBenRequestHandler) throws Exception {
        List<MaaMeeting> meetings = repository.findByAshaId(getBenRequestHandler.getAshaId());

        return meetings.stream().map(meeting -> {
            MaaMeetingResponseDTO dto = new MaaMeetingResponseDTO();
            dto.setId(meeting.getId());
            dto.setMeetingDate(meeting.getMeetingDate());
            dto.setPlace(meeting.getPlace());
            dto.setParticipants(meeting.getParticipants());
            dto.setAshaId(meeting.getAshaId());

            try {
                if (meeting.getMeetingImagesJson() != null) {
                    List<String> base64Images = objectMapper.readValue(
                            meeting.getMeetingImagesJson(),
                            new TypeReference<List<String>>() {}
                    );

                    dto.setMeetingImages(base64Images);
                } else {
                    dto.setMeetingImages(List.of());
                }
            } catch (Exception e) {
                dto.setMeetingImages(List.of());
            }

            return dto;
        }).collect(Collectors.toList());
    }

}
