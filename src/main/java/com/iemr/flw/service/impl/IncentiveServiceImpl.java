package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.identity.RMNCHBeneficiaryDetailsRmnch;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityLangMapping;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.IncentiveActivityLangMappingRepo;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.IncentiveService;
import com.iemr.flw.utils.JwtUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IncentiveServiceImpl implements IncentiveService {
    private final Logger logger = LoggerFactory.getLogger(ChildCareServiceImpl.class);

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private IncentiveActivityLangMappingRepo incentiveActivityLangMappingRepo;

    @Autowired
    IncentivesRepo incentivesRepo;

    @Autowired
    IncentiveRecordRepo recordRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceRoleRepo userRepo;
    @Override
    public String saveIncentivesMaster(List<IncentiveActivityDTO> activityDTOS) {
        try {
            activityDTOS.forEach(activityDTO -> {
                IncentiveActivity activity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup(activityDTO.getName(), activityDTO.getGroup());

                if (activity == null) {
                    activity = new IncentiveActivity();
                    modelMapper.map(activityDTO, activity);
                } else {
                    Long id = activity.getId();
                    modelMapper.map(activityDTO, activity);
                    activity.setId(id);
                }
                incentivesRepo.save(activity);
            });
            String saved = "";
            activityDTOS.forEach(dto -> saved.concat(dto.getGroup() + ": " + dto.getName()));
            return "saved master data for " + saved ;
        } catch (Exception e) {
            
        }
        return null;
    }

    @Override
    public String getIncentiveMaster(IncentiveRequestDTO incentiveRequestDTO) {

        try {


            List<IncentiveActivity> incs = incentivesRepo.findAll();

            List<IncentiveActivityDTO> dtos = incs.stream().map(inc -> {
                IncentiveActivityDTO dto = modelMapper.map(inc, IncentiveActivityDTO.class);

                // Fetch language mapping
                IncentiveActivityLangMapping mapping = incentiveActivityLangMappingRepo
                        .findByIdAndName(inc.getId(),inc.getName());



                if (mapping != null) {
                    dto.setName(mapping.getName());
                    dto.setGroupName(mapping.getGroup());
                    if(Objects.equals(incentiveRequestDTO.getLangCode(), "en")){
                        dto.setDescription(mapping.getDescription());



                    }else  if(Objects.equals(incentiveRequestDTO.getLangCode(), "as")){

                        if(mapping.getAssameActivityDescription()!=null){
                            dto.setDescription(mapping.getAssameActivityDescription());

                        }else {
                            dto.setDescription(mapping.getDescription());

                        }

                    }else  if(Objects.equals(incentiveRequestDTO.getLangCode(), "hi")){
                        if(mapping.getHindiActivityDescription()!=null){
                            dto.setDescription(mapping.getHindiActivityDescription());

                        }else {
                            dto.setDescription(mapping.getDescription());

                        }

                    }

                }else {
                    dto.setGroupName(inc.getGroup());

                }

                return dto;
            }).collect(Collectors.toList());
            checkMonthlyAshaIncentive();

            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();

            return gson.toJson(dtos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public String getAllIncentivesByUserId(GetBenRequestHandler request) {
        List<IncentiveRecordDTO> dtos = new ArrayList<>();
        List<IncentiveActivityRecord> entities = recordRepo.findRecordsByAsha(request.getAshaId(), request.getFromDate(), request.getToDate());
        entities.forEach(entry -> {
            if(entry.getName()==null){
                if(entry.getBenId()!=0){
                    Long regId = beneficiaryRepo.getBenRegIdFromBenId(entry.getBenId());
                    logger.info("rmnchBeneficiaryDetailsRmnch"+regId);
                    BigInteger benDetailId = beneficiaryRepo.findByBenRegIdFromMapping(BigInteger.valueOf(regId)).getBenDetailsId();
                    RMNCHMBeneficiarydetail rmnchBeneficiaryDetails = beneficiaryRepo.findByBeneficiaryDetailsId(benDetailId);
                    String beneName = rmnchBeneficiaryDetails.getFirstName()+" "+rmnchBeneficiaryDetails.getLastName();
                    entry.setName(beneName);

                }else{
                  entry.setName("");

                }

            }

            dtos.add(modelMapper.map(entry, IncentiveRecordDTO.class));
        });

        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
        return gson.toJson(dtos);
    }
    private void checkMonthlyAshaIncentive(){
        IncentiveActivity MOBILEBILLREIMB_ACTIVITY = incentivesRepo.findIncentiveMasterByNameAndGroup("MOBILE_BILL_REIMB", GroupName.OTHER_INCENTIVES.getDisplayName());
        IncentiveActivity ADDITIONAL_ASHA_INCENTIVE = incentivesRepo.findIncentiveMasterByNameAndGroup("ADDITIONAL_ASHA_INCENTIVE", GroupName.ADDITIONAL_INCENTIVE.getDisplayName());
        IncentiveActivity ASHA_MONTHLY_ROUTINE = incentivesRepo.findIncentiveMasterByNameAndGroup("ASHA_MONTHLY_ROUTINE", GroupName.ASHA_MONTHLY_ROUTINE.getDisplayName());
        if(MOBILEBILLREIMB_ACTIVITY!=null){
            addMonthlyAshaIncentiveRecord(MOBILEBILLREIMB_ACTIVITY);
        }
        if(ADDITIONAL_ASHA_INCENTIVE!=null){
            addMonthlyAshaIncentiveRecord(ADDITIONAL_ASHA_INCENTIVE);

        }

        if(ASHA_MONTHLY_ROUTINE!=null){
            addMonthlyAshaIncentiveRecord(ASHA_MONTHLY_ROUTINE);

        }
    }

    private void addMonthlyAshaIncentiveRecord(IncentiveActivity incentiveActivity){
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());

        Timestamp startOfMonth = Timestamp.valueOf(firstDay.atStartOfDay());
        Timestamp endOfMonth = Timestamp.valueOf(lastDay.atTime(23, 59, 59));

        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(
                        incentiveActivity.getId(),
                        startOfMonth,
                        endOfMonth,
                        0L
                );

        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(incentiveActivity.getId());
            record.setCreatedDate(timestamp);
            record.setCreatedBy(jwtUtil.getUserNameFromStorage());
            record.setStartDate(timestamp);
            record.setEndDate(timestamp);
            record.setUpdatedDate(timestamp);
            record.setUpdatedBy(jwtUtil.getUserNameFromStorage());
            record.setBenId(0L);
            record.setAshaId(userRepo.getUserIdByName(jwtUtil.getUserNameFromStorage()));
            record.setAmount(Long.valueOf(incentiveActivity.getRate()));
            recordRepo.save(record);
        }
    }


}
