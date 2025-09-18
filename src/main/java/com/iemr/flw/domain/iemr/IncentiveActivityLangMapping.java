package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "m_incentive_activity_lang_mapping",schema = "db_iemr")
public class IncentiveActivityLangMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_name")
    private String name;

    @Column(name = "activity_description")
    private String description;

    @Column(name = "payment_parameter")
    private String paymentParam;

    @Column(name = "rate_per_activity")
    private Integer rate;

    @Column(name = "state_code")
    private Integer state;

    @Column(name = "district_code")
    private Integer district;

    @Column(name = "group_name")
    private String group;


    @Column(name = "fmr_code")
    private String fmrCode;

    @Column(name = "fmr_code_old")
    private String fmrCodeOld;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "assame_activity_description")
    private String assameActivityDescription;


}
