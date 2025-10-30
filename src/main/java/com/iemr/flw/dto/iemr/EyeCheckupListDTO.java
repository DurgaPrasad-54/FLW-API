package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class EyeCheckupListDTO {
    @SerializedName("visit_date")
    private String visit_date;

    @SerializedName("symptoms_observed")
    private String symptoms_observed;

    @SerializedName("eye_affected")
    private String eye_affected;

    @SerializedName("referred_to")
    private String referred_to;

    @SerializedName("follow_up_status")
    private String follow_up_status;

    @SerializedName("date_of_surgery")
    private String date_of_surgery;

}
