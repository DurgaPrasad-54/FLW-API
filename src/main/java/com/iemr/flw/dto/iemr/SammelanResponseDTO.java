package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
@Data
public class SammelanResponseDTO {

    private Long id;
    private Integer ashaId;
    private Timestamp date;
    private String place;
    private Integer participants;
    private Double incentiveAmount;
    private String incentiveStatus;


}
