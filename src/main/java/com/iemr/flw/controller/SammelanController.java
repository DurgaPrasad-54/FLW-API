package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.SammelanService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import java.util.List;

@RestController
@RequestMapping(value = "/sammelans")
public class SammelanController {
    @Autowired
    private  SammelanService service;



    @RequestMapping(value = "saveAll",method = RequestMethod.POST)
    public ResponseEntity<SammelanResponseDTO> create(

            @RequestPart("date") String date,
            @RequestPart("place") String place,
            @RequestPart("participants") String participants,
            @RequestPart("ashaId") String ashaId,
            @RequestPart(value = "meetingImages", required = false) MultipartFile[] images) {
        SammelanRequestDTO sammelanRequestDTO = new SammelanRequestDTO();
        sammelanRequestDTO.setPlace(place);
        sammelanRequestDTO.setDate(Timestamp.valueOf(date));
        sammelanRequestDTO.setAshaId(Integer.valueOf(ashaId));


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

