package com.iemr.flw.domain.iemr;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import org.hibernate.annotations.Type;

@Entity
@Data
@Table(name = "t_hbyc_child_visits",schema = "db_iemr")
public class HbycChildVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "beneficiary_id")
    private Long beneficiaryId;

    @Column(name = "household_id")
    private Long houseHoldId;

    @Column(name = "visit_day")
    private String hbycVisitDay; // 3 Months, 6 Months, etc.

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "hbyc_due_date")
    private String hbycDueDate;

    @Column(name = "hbyc_visit_date")
    private String hbycVisitDate;

    @Column(name = "is_baby_alive")
    private Boolean isBabyAlive = true;

    @Column(name = "date_of_death")
    private String dateOfDeath;

    @Column(name = "reason_for_death")
    private String reasonForDeath;

    @Column(name = "place_of_death")
    private String placeOfDeath;

    @Column(name = "other_place_of_death")
    private String otherPlaceOfDeath;

    @Column(name = "baby_weight")
    private BigDecimal babyWeight; // 0.5 - 7.0

    @Column(name = "is_child_sick")
    private Boolean isChildSick;

    @Column(name = "is_exclusive_breastfeeding")
    private Boolean isExclusiveBreastfeeding;

    @Column(name = "is_mother_counseled_exbf")
    private Boolean isMotherCounseledExbf;

    @Column(name = "has_child_started_complementary_feeding")
    private Boolean hasChildStartedComplementaryFeeding;

    @Column(name = "is_mother_counseled_cf")
    private Boolean isMotherCounseledCf;

    @Column(name = "is_weight_recorded_by_aww")
    private Boolean isWeightRecordedByAww;

    @Column(name = "is_developmental_delay")
    private Boolean isDevelopmentalDelay;

    @Column(name = "is_measles_vaccine_given")
    private Boolean isMeaslesVaccineGiven;

    @Column(name = "is_vitamin_a_given")
    private Boolean isVitaminAGiven;

    @Column(name = "is_ors_available")
    private Boolean isOrsAvailable;

    @Column(name = "is_ifa_syrup_available")
    private Boolean isIfaSyrupAvailable;

    @Column(name = "is_ors_given")
    private Boolean isOrsGiven;

    @Column(name = "is_ifa_syrup_given")
    private Boolean isIfaSyrupGiven;

    @Column(name = "is_handwashing_counseling_given")
    private Boolean isHandwashingCounselingGiven;

    @Column(name = "is_parenting_counseling_given")
    private Boolean isParentingCounselingGiven;

    @Column(name = "is_family_planning_counseling_given")
    private Boolean isFamilyPlanningCounselingGiven;

    @Column(name = "diarrhoea_episode")
    private Boolean diarrhoeaEpisode;

    @Column(name = "pneumonia_symptoms")
    private Boolean pneumoniaSymptoms;

    @Column(name = "temperature")
    private BigDecimal temperature;

    @Column(name = "mcp_card_images", columnDefinition = "json")
    private List<String> mcpCardImages;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    // Getters and Setters
}
