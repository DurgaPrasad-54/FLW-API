package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
public class OrsCampaignListDTO {
    @JsonProperty("start_date")
    private LocalDate StartDate;

    @JsonProperty("end_date")
    private LocalDate EndDate;

    @JsonProperty("number_of_families")
    private String NumberOfFamilies;

    @JsonProperty("campaign_photos")
    private MultipartFile[] CampaignPhotos;


}
