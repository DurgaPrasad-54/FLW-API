package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TBSuspectedDTO {

    private Long id;

    private Long benId;
    private Long visitCode;
    private String visitLabel;
    private String typeOfTBCase;
    private String reasonForSuspicion;
    private Boolean hasSymptoms;
    private Boolean isSputumCollected;
    private String sputumSubmittedAt;
    private String nikshayId;
    private String sputumTestResult;
    private Boolean isChestXRayDone;
    private String chestXRayResult;
    private String referralFacility;
    private Boolean isTBConfirmed;
    private Boolean isDRTBConfirmed;
    private Boolean isConfirmed;
    private Boolean referred;
    private String followUps;
}
