package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class EyeCheckupListDTO {
    @SerializedName("visit_date")
    private String visitDate;

    @SerializedName("symptoms_observed")
    private String symptomsObserved;

    @SerializedName("eye_affected")
    private String eyeAffected;

    @SerializedName("referred_to")
    private String referredTo;

    @SerializedName("follow_up_status")
    private String followUpStatus;

    @SerializedName("date_of_surgery")
    private String dateOfSurgery;

}
