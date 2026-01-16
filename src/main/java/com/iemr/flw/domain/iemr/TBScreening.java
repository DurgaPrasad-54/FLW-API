package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "screening_tb", schema = "db_iemr")
@Data
public class TBScreening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "visit_code", nullable = false)
    private Long visitCode;

    // Visit Information
    @Column(name = "visit_label", length = 100, nullable = false)
    private String visitLabel;

    @Column(name = "type_of_tb_case", length = 50, nullable = false)
    private String typeOfTBCase;

    @Column(name = "reason_for_suspicion", length = 500)
    private String reasonForSuspicion;

    // Symptoms
    @Column(name = "has_symptoms", nullable = false)
    private Boolean hasSymptoms = false;

    // Sputum Test
    @Column(name = "is_sputum_collected")
    private Boolean isSputumCollected;

    @Column(name = "sputum_submitted_at", length = 200)
    private String sputumSubmittedAt;

    @Column(name = "nikshay_id", length = 50)
    private String nikshayId;

    @Column(name = "sputum_test_result", length = 50)
    private String sputumTestResult;

    // Chest X-Ray
    @Column(name = "is_chest_xray_done")
    private Boolean isChestXRayDone;

    @Column(name = "chest_xray_result", length = 100)
    private String chestXRayResult;

    // Referral & Confirmation
    @Column(name = "referral_facility", length = 200)
    private String referralFacility;

    @Column(name = "is_tb_confirmed")
    private Boolean isTBConfirmed;

    @Column(name = "is_drtb_confirmed")
    private Boolean isDRTBConfirmed;

    @Column(name = "is_confirmed", nullable = false)
    private Boolean isConfirmed = false;

    @Column(name = "referred")
    private Boolean referred;

    @Column(name = "follow_ups", columnDefinition = "TEXT")
    private String followUps;


    @Column(name = "provider_service_map_id")
    private Integer providerServiceMapId;

    @Column(name = "created_by", length = 200)
    private String createdBy;

    @Column(name = "updated_by", length = 200)
    private String updatedBy;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "processed", length = 4)
    private String processed = "N";

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
        if (this.hasSymptoms == null) {
            this.hasSymptoms = false;
        }
        if (this.isConfirmed == null) {
            this.isConfirmed = false;
        }

        if (this.deleted == null) {
            this.deleted = false;
        }
        if (this.processed == null) {
            this.processed = "N";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
