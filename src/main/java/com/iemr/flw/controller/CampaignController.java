package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.OrsCampaignDTO;
import com.iemr.flw.service.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/Campaign")
public class CampaignController {
    private final Logger logger = LoggerFactory.getLogger(CampaignController.class);

    @Autowired
    private CampaignService campaignService;

    @RequestMapping(value = "ors/distribution/saveAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> saveOrsDistribution(@RequestBody List<OrsCampaignDTO> orsCampaignDTO, @RequestHeader(value = "jwtToken") String token) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
          Object result  = campaignService.saveOrsCampaign(orsCampaignDTO,token);


            if (result != null) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("data", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.NOT_FOUND.value());
                response.put("message", "No records found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error save ors distribution :", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = "ors/distribution/getAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getAllOrsDistribution(@RequestHeader(value = "jwtToken") String token) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            List<OrsCampaignDTO> result  = campaignService.getOrsCampaign(token);


            if (result != null) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("data", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.NOT_FOUND.value());
                response.put("message", "No records found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error save ors distribution :", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
