package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PolioCampaignListDTO {
    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("number_of_children")
    private String numberOfChildren;

    @JsonProperty("campaign_photos")
    private String campaignPhotos;


}
