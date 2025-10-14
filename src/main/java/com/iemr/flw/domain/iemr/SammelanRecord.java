package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sammelan_record",
        uniqueConstraints = @UniqueConstraint(name = "uk_asha_month", columnNames = {"asha_id", "meeting_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SammelanRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "asha_id", nullable = false)
    private Integer ashaId;


    @Column(name = "meeting_date", nullable = false)
    private LocalDate meetingDate;


    @Column(nullable = false)
    private String place;


    @Column(nullable = false, name = "participants")
    private int participants;


    @Column(columnDefinition = "TEXT", name = "remarks")
    private String remarks;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;


    @Lob
    @Column(columnDefinition = "LONGTEXT",name = "attachments")
    private String attachments;
}