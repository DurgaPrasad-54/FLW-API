package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class PolioCampaignResponseDTO {
    private Long id;
    private String visitDate;
    private PolioCampaignListResponseDTO fields;
}
