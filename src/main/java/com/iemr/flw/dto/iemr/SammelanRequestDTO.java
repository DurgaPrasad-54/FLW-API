package com.iemr.flw.dto.iemr;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
@Data
public class SammelanRequestDTO {

    private Integer ashaId;                    // ASHA worker ID
    private LocalDate date;                 // Meeting date
    private String place;                   // Dropdown: HWC / Anganwadi Centre / Community Center
    private Integer participants;           // Number of participants attended

}
