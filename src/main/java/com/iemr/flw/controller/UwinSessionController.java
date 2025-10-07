package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.UwinSessionRequestDTO;
import com.iemr.flw.dto.iemr.UwinSessionResponseDTO;
import com.iemr.flw.service.UwinSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("uwin/session")
@RequiredArgsConstructor
public class UwinSessionController {

    private final UwinSessionService service;

    @PostMapping("/saveAll")
    public ResponseEntity<UwinSessionResponseDTO> saveSession(
            @RequestBody UwinSessionRequestDTO uwinSessionRequestDTO) throws Exception {

        UwinSessionRequestDTO dto = new UwinSessionRequestDTO();
        dto.setAshaId(uwinSessionRequestDTO.getAshaId());
        dto.setDate(uwinSessionRequestDTO.getDate());
        dto.setPlace(uwinSessionRequestDTO.getPlace());
        dto.setParticipants(uwinSessionRequestDTO.getParticipants());
        dto.setAttachments(uwinSessionRequestDTO.getAttachments());

        return ResponseEntity.ok(service.saveSession(dto));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<UwinSessionResponseDTO>> getSessions(@RequestParam Integer ashaId) throws Exception {
        return ResponseEntity.ok(service.getSessionsByAsha(ashaId));
    }
}
