package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "t_sam_visit", schema = "db_iemr")
public class SamVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beneficiary_id")
    private Long beneficiaryId;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "visit_label")
    private String visitLabel;

    @Column(name = "muac")
    private Double muac;

    @Column(name = "weight_for_height_status")
    private String weightForHeightStatus;

    @Column(name = "is_child_referred_nrc")
    private String isChildReferredNrc;

    @Column(name = "is_child_admitted_nrc")
    private String isChildAdmittedNrc;

    @Column(name = "nrc_admission_date")
    private LocalDate nrcAdmissionDate;

    @Column(name = "is_child_discharged_nrc")
    private String isChildDischargedNrc;

    @Column(name = "nrc_discharge_date")
    private LocalDate nrcDischargeDate;

    @Column(name = "follow_up_visit_date")
    private LocalDate followUpVisitDate;

    @Column(name = "sam_status")
    private String samStatus;

    @Column(name = "discharge_summary")
    private String dischargeSummary;

    @Column(name = "view_discharge_docs")
    private String viewDischargeDocs;
}
