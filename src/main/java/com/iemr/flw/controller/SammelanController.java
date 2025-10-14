package com.iemr.flw.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.SammelanService;
import com.iemr.flw.utils.DateUtil;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/sammelans")
public class SammelanController {
    private final Logger logger = LoggerFactory.getLogger(DeathReportsController.class);

    @Autowired
    private  SammelanService service;



    @RequestMapping(value = "saveAll",method = RequestMethod.POST)
    public ResponseEntity<SammelanResponseDTO> create(

            @RequestPart("date") String date,
            @RequestPart("place") String place,
            @RequestPart("participants") String participants,
            @RequestPart("ashaId") String ashaId,
            @RequestPart(value = "SammelanImages", required = false) MultipartFile[] images) throws JsonProcessingException {
        SammelanRequestDTO sammelanRequestDTO = new SammelanRequestDTO();
        sammelanRequestDTO.setPlace(place);
        sammelanRequestDTO.setParticipants(Integer.valueOf(participants));
        sammelanRequestDTO.setDate(Timestamp.valueOf(date));
        sammelanRequestDTO.setAshaId(Integer.valueOf(ashaId));
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(sammelanRequestDTO);
        logger.info("📥 Incoming HBYC Request: \n" + json+"date"+date);
        SammelanResponseDTO resp = service.submitSammelan(sammelanRequestDTO,images);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }


    @GetMapping("/getAll")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched meetings",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SammelanListResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getMeetings(@RequestParam Integer ashaId) {
        SammelanListResponseDTO response = new SammelanListResponseDTO();

        try {
            response.setData(service.getSammelanHistory(ashaId));
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

