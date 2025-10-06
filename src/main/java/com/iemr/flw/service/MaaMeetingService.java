package com.iemr.flw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.MaaMeeting;
import com.iemr.flw.dto.iemr.MaaMeetingRequestDTO;
import com.iemr.flw.dto.iemr.MaaMeetingResponseDTO;
import com.iemr.flw.repo.iemr.MaaMeetingRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaaMeetingService {

    private final MaaMeetingRepository repository;
    private final ObjectMapper objectMapper;

    public MaaMeetingService(MaaMeetingRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public MaaMeeting saveMeeting(List<MaaMeetingRequestDTO> dto) throws Exception {
        MaaMeeting meeting = new MaaMeeting();
        dto.forEach(maaMeetingRequestDTO -> {
                    meeting.setMeetingDate(maaMeetingRequestDTO.getMeetingDate());
                    meeting.setPlace(maaMeetingRequestDTO.getPlace());
                    meeting.setParticipants(maaMeetingRequestDTO.getParticipants());
                    meeting.setAshaId(maaMeetingRequestDTO.getAshaId());

                    // Convert images to Base64
                    if (maaMeetingRequestDTO.getMeetingImages() != null && maaMeetingRequestDTO.getMeetingImages().length > 0) {
                        List<String> base64Images = List.of(maaMeetingRequestDTO.getMeetingImages())
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
                        meeting.setMeetingImagesJson(imagesJson);
                    }
                }
        );


        return repository.save(meeting);
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
