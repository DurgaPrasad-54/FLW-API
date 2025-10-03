package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
@Data
public class SammelanRequestDTO {

    private Integer ashaId;                    // ASHA worker ID
    private Timestamp date;                 // Meeting date
    private String place;                   // Dropdown: HWC / Anganwadi Centre / Community Center
    private Integer participants;           // Number of participants attended
    private List<AttachmentDTO> attachments; // Min 2, Max 5 files

}
