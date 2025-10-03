package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.SammelanService;
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

import java.util.List;

@RestController
@RequestMapping(value = "/sammelans")
public class SammelanController {
    @Autowired
    private  SammelanService service;



    @RequestMapping(value = "saveAll",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,method = RequestMethod.POST)
    public ResponseEntity<SammelanResponseDTO> create(
            @RequestPart("payload") @Valid SammelanRequestDTO payload,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {


        SammelanResponseDTO resp = service.submitSammelan(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }


    @RequestMapping(value = "getAll",method = RequestMethod.GET)
    public List<SammelanResponseDTO> sammelanlist(@RequestParam Integer ashaId) {
        return service.getSammelanHistory(ashaId);
    }
}

