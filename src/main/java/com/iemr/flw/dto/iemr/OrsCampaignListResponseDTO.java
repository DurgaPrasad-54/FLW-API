package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrsCampaignListResponseDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String numberOfFamilies;
    private List<String> campaignPhotos; // Base64 or URLs
}