package com.iemr.flw.dto.iemr;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.List;

@Data
public class HbycDTO {


    @SerializedName("beneficiary_id")
    private Long beneficiaryId;

    @SerializedName("visit_day")
    private String hbycVisit; // 3 Months, 6 Months, etc.

    @SerializedName("due_date")
    private String hbycDueDate;

    @SerializedName("hbyc_visit_date")
    private String hbycVisitDate;

    @SerializedName("is_baby_alive")
    private Boolean isBabyAlive;

    @SerializedName("date_of_death")
    private String dateOfDeath;

    @SerializedName("reason_for_death")
    private String reasonForDeath;

    @SerializedName("place_of_death")
    private String placeOfDeath;

    @SerializedName("other_place_of_death")
    private String otherPlaceOfDeath;

    @SerializedName("baby_weight")
    private BigDecimal babyWeight; // 0.5 - 7.0

    @SerializedName("is_child_sick")
    private Boolean isChildSick;

    @SerializedName("is_exclusive_breastfeeding")
    private Boolean isExclusiveBreastfeeding;

    @SerializedName("is_mother_counseled_exbf")
    private Boolean isMotherCounseledExbf;

    @SerializedName("has_child_started_complementary_feeding")
    private Boolean hasChildStartedComplementaryFeeding;

    @SerializedName("is_mother_counseled_cf")
    private Boolean isMotherCounseledCf;

    @SerializedName("is_weight_recorded_by_aww")
    private Boolean isWeightRecordedByAww;

    @SerializedName("is_developmental_delay")
    private Boolean isDevelopmentalDelay;

    @SerializedName("is_measles_vaccine_given")
    private Boolean isMeaslesVaccineGiven;

    @SerializedName("is_vitamin_a_given")
    private Boolean isVitaminAGiven;

    @SerializedName("is_ors_available")
    private Boolean isOrsAvailable;

    @SerializedName("is_ifa_syrup_available")
    private Boolean isIfaSyrupAvailable;

    @SerializedName("is_ors_given")
    private Boolean isOrsGiven;

    @SerializedName("is_ifa_syrup_given")
    private Boolean isIfaSyrupGiven;

    @SerializedName("is_handwashing_counseling_given")
    private Boolean isHandwashingCounselingGiven;

    @SerializedName("is_parenting_counseling_given")
    private Boolean isParentingCounselingGiven;

    @SerializedName("is_family_planning_counseling_given")
    private Boolean isFamilyPlanningCounselingGiven;

    @SerializedName("diarrhoea_episode")
    private Boolean diarrhoeaEpisode;

    @SerializedName("pneumonia_symptoms")
    private Boolean pneumoniaSymptoms;

    @SerializedName("temperature")
    private BigDecimal temperature;

    @SerializedName("mcp_card_images")
    private List<String> mcpCardImages;

    @SerializedName("created_at")
    private LocalDateTime createdAt;

    @SerializedName("updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters


}
