package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class OrsCampaignListDTO {
    private String visit_date;
    private Timestamp end_date;
    private String number_of_families;
    private String campaign_photos;


}
