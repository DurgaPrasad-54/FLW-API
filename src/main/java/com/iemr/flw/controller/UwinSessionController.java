package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.UwinSessionRequestDTO;
import com.iemr.flw.dto.iemr.UwinSessionResponseDTO;
import com.iemr.flw.service.UwinSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/uwin/session", headers = "Authorization")
@RequiredArgsConstructor
public class UwinSessionController {

    private final UwinSessionService service;

    @RequestMapping(value = "saveAll",method = RequestMethod.POST,headers = "Authorization",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UwinSessionResponseDTO> saveSession(
            @RequestPart("meetingDate") String meetingDate,
            @RequestPart("place") String place,
            @RequestPart("participants") String participants,
            @RequestPart("ashaId") String ashaId,
            @RequestPart("createdBy") String createdBy,
            @RequestPart(value = "meetingImages", required = false) List<MultipartFile> images) throws Exception {

        UwinSessionRequestDTO dto = new UwinSessionRequestDTO();
        dto.setAshaId(Integer.valueOf(ashaId));
        dto.setDate(Timestamp.valueOf(meetingDate));
        dto.setPlace(place);
        dto.setParticipants(Integer.valueOf(participants));
        dto.setCreatedBy(createdBy);
        dto.setAttachments(images != null ? images.toArray(new MultipartFile[0]) : null);

        return ResponseEntity.ok(service.saveSession(dto));
    }

    @RequestMapping(value = "getAll",method = RequestMethod.POST,headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getSessions(@RequestBody GetBenRequestHandler getBenRequestHandler) throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", getBenRequestHandler.getUserId());
            data.put("entries", service.getSessionsByAsha(getBenRequestHandler.getAshaId()));
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("message", "Success");
        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("errorMessage", e.getMessage());

        }

        return ResponseEntity.ok(response);
    }
}
