package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.MaaMeeting;
import com.iemr.flw.dto.iemr.MaaMeetingListResponseDTO;
import com.iemr.flw.dto.iemr.MaaMeetingRequestDTO;
import com.iemr.flw.dto.iemr.MaaMeetingResponseDTO;
import com.iemr.flw.service.MaaMeetingService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("maa-meetings")
public class MaaMeetingController {


    private final MaaMeetingService service;

    public MaaMeetingController(MaaMeetingService service) {
        this.service = service;
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveMeeting(@ModelAttribute List<MaaMeetingRequestDTO> dto) {
        try {
            MaaMeeting saved = service.saveMeeting(dto);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getAll")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched meetings",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MaaMeetingListResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getMeetings() {
        MaaMeetingListResponseDTO response = new MaaMeetingListResponseDTO();

        try {
            response.setData(service.getAllMeetings());
            response.setStatusCode(200);
            response.setStatus("Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setStatus("Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
