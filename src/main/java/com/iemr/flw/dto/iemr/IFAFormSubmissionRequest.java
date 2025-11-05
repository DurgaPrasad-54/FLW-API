package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.Map;

@Data
public class IFAFormSubmissionRequest {
    private Long beneficiaryId;
    private String formId;
    private Long houseHoldId;
    private String userName;
    private String visitDate;
    private Map<String, Object> fields;
}
