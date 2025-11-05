package com.iemr.flw.dto.iemr;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IFAFormSubmissionResponse {
    private Integer formId;
    private Long beneficiaryId;
    private String visitDate;
    private String createdBy;
    private String createdAt;
    private IFAFormFieldsDTO fields;
}
