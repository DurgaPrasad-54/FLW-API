package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChronicDiseaseVisitDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("benId")
    private Long benId;

    @JsonProperty("hhId")
    private Long hhId;

    @JsonProperty("formId")
    private String formId;

    @JsonProperty("version")
    private Integer version;

    @JsonProperty("visitNo")
    private Integer visitNo;

    @JsonProperty("followUpNo")
    private Integer followUpNo;

    @JsonProperty("diagnosisCodes")
    private String diagnosisCodes;

    @JsonProperty("treatmentStartDate")
    private String treatmentStartDate; // yyyy-MM-dd

    @JsonProperty("formDataJson")
    private String formDataJson;
}
