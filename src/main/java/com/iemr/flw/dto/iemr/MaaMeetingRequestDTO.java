package com.iemr.flw.dto.iemr;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
public class MaaMeetingRequestDTO {
    private LocalDate meetingDate;
    private String place;
    private Integer participants;
    private MultipartFile[] meetingImages; // up to 5 images
    private Integer ashaId;
    private String createdBY;
}
