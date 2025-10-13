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
    private String hbycVisitDay; // 3 Months, 6 Months, etc.

    @SerializedName("household_id")
    private Long houseHoldId;

    @SerializedName("due_date")
    private String hbycDueDate;

    @SerializedName("visit_date")
    private String hbycVisitDate;

    @SerializedName("is_baby_alive")
    private String isBabyAlive; // Yes/No

    @SerializedName("date_of_death")
    private String dateOfDeath;

    @SerializedName("reason_for_death")
    private String reasonForDeath;

    @SerializedName("place_of_death")
    private String placeOfDeath;

    @SerializedName("other_place_of_death")
    private String otherPlaceOfDeath;

    @SerializedName("baby_weight")
    private BigDecimal babyWeight;

    @SerializedName("is_child_sick")
    private String isChildSick;

    @SerializedName("exclusive_breastfeeding")
    private String isExclusiveBreastfeeding;

    @SerializedName("mother_counseled_ebf")
    private String isMotherCounseledExbf;

    @SerializedName("complementary_feeding")
    private String hasChildStartedComplementaryFeeding;

    @SerializedName("mother_counseled_cf")
    private String isMotherCounseledCf;

    @SerializedName("weight_recorded")
    private String isWeightRecordedByAww;

    @SerializedName("developmental_delay")
    private String isDevelopmentalDelay;

    @SerializedName("measles_vaccine")
    private String isMeaslesVaccineGiven;

    @SerializedName("vitamin_a")
    private String isVitaminAGiven;

    @SerializedName("ors_available")
    private String isOrsAvailable;

    @SerializedName("ifa_available")
    private String isIfaSyrupAvailable;

    @SerializedName("ors_given")
    private String isOrsGiven;

    @SerializedName("ifa_given")
    private String isIfaSyrupGiven;

    @SerializedName("handwash_counseling")
    private String isHandwashingCounselingGiven;

    @SerializedName("parenting_counseling")
    private String isParentingCounselingGiven;

    @SerializedName("family_planning_counseling")
    private String isFamilyPlanningCounselingGiven;

    @SerializedName("diarrhoea_episode")
    private String diarrhoeaEpisode;

    @SerializedName("breathing_difficulty")
    private String pneumoniaSymptoms;

    @SerializedName("temperature_check")
    private BigDecimal temperature;

    @SerializedName("mcp_card_images")
    private List<String> mcpCardImages;

    @SerializedName("created_at")
    private LocalDateTime createdAt;

    @SerializedName("updated_at")
    private LocalDateTime updatedAt;




}
