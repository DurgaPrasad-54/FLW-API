package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SamListDTO {


    @SerializedName("visit_label")
    private String visitLabel;

    @SerializedName("muac")
    private Double muac;

    @SerializedName("weight_for_height_status")
    private String weightForHeightStatus;

    @SerializedName("is_child_referred_nrc")
    private String isChildReferredNrc;

    @SerializedName("is_child_admitted_nrc")
    private String isChildAdmittedNrc;

    @SerializedName("nrc_admission_date")
    private LocalDate nrcAdmissionDate;

    @SerializedName("is_child_discharged_nrc")
    private String isChildDischargedNrc;

    @SerializedName("nrc_discharge_date")
    private LocalDate nrcDischargeDate;

    @SerializedName("follow_up_visit_date")
    private LocalDate followUpVisitDate;

    @SerializedName("sam_status")
    private String samStatus;

    @SerializedName("discharge_summary")
    private String dischargeSummary;

    @SerializedName("view_discharge_docs")
    private String viewDischargeDocs;
}
