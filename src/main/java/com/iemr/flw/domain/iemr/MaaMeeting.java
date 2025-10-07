package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "maa_meeting", schema = "db_iemr")
public class MaaMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "meeting_date")
    private Timestamp meetingDate;

    @Column(name = "place")
    private String place;

    @Column(name = "participants")
    private Integer participants;

    @Column(name = "quarter")
    private Integer quarter;

    @Column(name = "year")
    private Integer year;

    @Column(name = "asha_id")
    private Integer ashaId;

    // Store multiple images as JSON of base64 strings
    @Lob
    @Column(name = "meeting_images", columnDefinition = "LONGTEXT")
    private String meetingImagesJson;

    @Column(name = "created_by")
    private String createdBy;
}
