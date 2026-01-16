package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TBScreeningDTO {

    private Long id;

    private Long benId;

    private Timestamp visitDate;
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
    private Boolean isConfirmed = false;
    private Boolean referred;
    private String followUps;
}
