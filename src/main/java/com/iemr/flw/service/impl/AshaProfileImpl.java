package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.AshaWorker;
import com.iemr.flw.domain.iemr.M_User;
import com.iemr.flw.repo.iemr.AshaProfileRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.AshaProfileService;
import com.iemr.flw.service.EmployeeMasterInter;

import com.iemr.flw.repo.iemr.EmployeeMasterRepo;
import com.iemr.flw.service.AshaProfileService;
import com.iemr.flw.service.EmployeeMasterInter;
import com.iemr.flw.utils.JwtAuthenticationUtil;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.exception.IEMRException;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class AshaProfileImpl implements AshaProfileService {
    @Autowired
    AshaProfileRepo ashaProfileRepo;
    @Autowired
    EmployeeMasterInter employeeMasterInter;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private EmployeeMasterRepo userLoginRepo;
    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtAuthenticationUtil jwtAuthenticationUtil ;
    @Autowired
    private UserServiceRoleRepo userServiceRoleRepo;
    private final Logger logger = LoggerFactory.getLogger(AshaProfileImpl.class);

    @Transactional
    @Override
    public AshaWorker saveEditData(AshaWorker ashaWorkerRequest) {
        try {
            Objects.requireNonNull(ashaWorkerRequest, "ashaWorker must not be null");
            AshaWorker savedWorker = ashaWorkerRequest.getId() != null
                    ? ashaProfileRepo.saveAndFlush(updateProfile(ashaWorkerRequest))
                    : ashaProfileRepo.saveAndFlush(ashaWorkerRequest);
            logger.info("ASHA worker profile saved successfully: {}", savedWorker);
            return savedWorker;
        } catch (Exception e) {
            logger.error("Error saving ASHA worker profile: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save ASHA worker profile", e);
        }

    }



    @Override
    public AshaWorker getProfileData(Integer userId) {

        try {

            Objects.requireNonNull(userId, "employeeId must not be null");
            return ashaProfileRepo.findByEmployeeId(userId)
                    .orElseGet(() -> {
                        return getDetails(userId);
                    });
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve ASHA worker profile", e);
        }
    }

    private AshaWorker getDetails(Integer userID) {

        try {
            M_User m_user = Objects.requireNonNull(
                    employeeMasterInter.getUserDetails(userID),
                    "User details not found for ID: " + userID
            );

            AshaWorker ashaWorker = new AshaWorker();
            ashaWorker.setEmployeeId(m_user.getUserID());
            ashaWorker.setDob(m_user.getDOB());
            ashaWorker.setDateOfJoining(m_user.getDOJ());
            ashaWorker.setName(String.format("%s %s",
                    Objects.toString(m_user.getFirstName(), ""),
                    Objects.toString(m_user.getLastName(), "")).trim());
            ashaWorker.setMobileNumber(m_user.getContactNo());
            ashaWorker.setAlternateMobileNumber(m_user.getEmergencyContactNo());
            ashaWorker.setProviderServiceMapID(m_user.getServiceProviderID());
            return ashaWorker;
        } catch (Exception e) {
            logger.error("Error creating ASHA worker profile from user details for ID {}: {}", userID, e.getMessage(), e);
            throw new RuntimeException("Failed to create ASHA worker profile from user details", e);
        }
    }


    public AshaWorker updateProfile(AshaWorker request) {
        AshaWorker existing = ashaProfileRepo.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("ASHA worker not found"));

        if (request.getAbhaNumber() != null) existing.setAbhaNumber(request.getAbhaNumber());
        if (request.getEmployeeId() != null) existing.setEmployeeId(request.getEmployeeId());
        if (request.getDob() != null) existing.setDob(request.getDob());
        if (request.getAlternateMobileNumber() != null) existing.setAlternateMobileNumber(request.getAlternateMobileNumber());
        if (request.getAnm1Mobile() != null) existing.setAnm1Mobile(request.getAnm1Mobile());
        if (request.getAnm2Name() != null) existing.setAnm2Name(request.getAnm2Name());
        if (request.getIfsc() != null) existing.setIfsc(request.getIfsc());
        if (request.getAwwName() != null) existing.setAwwName(request.getAwwName());
        if (request.getName() != null) existing.setName(request.getName());
        if (request.getVillage() != null) existing.setVillage(request.getVillage());
        if (request.getBankAccount() != null) existing.setBankAccount(request.getBankAccount());
        if (request.getChoName() != null) existing.setChoName(request.getChoName());
        if (request.getChoMobile() != null) existing.setChoMobile(request.getChoMobile());
        if (request.getAshaFamilyMember() != null) existing.setAshaFamilyMember(request.getAshaFamilyMember());
        if (request.getDateOfJoining() != null) existing.setDateOfJoining(request.getDateOfJoining());
        if (request.getMobileNumber() != null) existing.setMobileNumber(request.getMobileNumber());
        if (request.getAshaHouseholdRegistration() != null) existing.setAshaHouseholdRegistration(request.getAshaHouseholdRegistration());
        if (request.getFatherOrSpouseName() != null) existing.setFatherOrSpouseName(request.getFatherOrSpouseName());
        if (request.getPopulationCovered() != null) existing.setPopulationCovered(request.getPopulationCovered());
        if (request.getAnm1Name() != null) existing.setAnm1Name(request.getAnm1Name());
        if (request.getAnm2Mobile() != null) existing.setAnm2Mobile(request.getAnm2Mobile());
        if (request.getAwwMobile() != null) existing.setAwwMobile(request.getAwwMobile());
        if (request.getProviderServiceMapID() != null) existing.setProviderServiceMapID(request.getProviderServiceMapID());
        if (request.getProfileImage() != null) existing.setProfileImage(request.getProfileImage());
        if (request.getIsFatherOrSpouse() != null) existing.setIsFatherOrSpouse(request.getIsFatherOrSpouse());
        if (request.getSupervisorName() != null) existing.setSupervisorName(request.getSupervisorName());
        if (request.getSupervisorMobile() != null) existing.setSupervisorMobile(request.getSupervisorMobile());

        return existing;
    }


}
