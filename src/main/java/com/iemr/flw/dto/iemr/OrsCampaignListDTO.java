package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
public class OrsCampaignListDTO {
    private LocalDate start_date;
    private LocalDate end_date;
    private String number_of_families;
    private String campaign_photos;


}
