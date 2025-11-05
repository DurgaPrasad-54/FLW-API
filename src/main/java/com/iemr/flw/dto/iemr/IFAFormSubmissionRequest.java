package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.Map;

@Data
public class IFAFormSubmissionRequest {
    private Long beneficiaryId;
    private Long houseHoldId;
    private String userName;
    private String visitDate;
    private IFAFormFieldsDTO fields;
}

