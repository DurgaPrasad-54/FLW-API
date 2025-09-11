package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;

@Entity
@Table(name = "m_incentive_activity_lang_mapping",schema = "db_iemr")
public class IncentiveActivityLangMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    @Column(name = "language_id", nullable = false)
    private Long languageId;

    @Column(name = "activity_name")
    private String activityName;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "activity_description", length = 500)
    private String activityDescription;

    // 🔹 Constructors
    public IncentiveActivityLangMapping() {
    }

    public IncentiveActivityLangMapping(Long activityId, Long languageId, String activityName, String groupName, String activityDescription) {
        this.activityId = activityId;
        this.languageId = languageId;
        this.activityName = activityName;
        this.groupName = groupName;
        this.activityDescription = activityDescription;
    }

    // 🔹 Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }
}
