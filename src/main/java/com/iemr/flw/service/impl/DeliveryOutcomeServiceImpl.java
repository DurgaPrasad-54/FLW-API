package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iemr.flw.domain.identity.RMNCHBeneficiaryDetailsRmnch;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.iemr.DeliveryOutcome;
import com.iemr.flw.domain.iemr.HbncVisit;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.DeliveryOutcomeDTO;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.identity.HouseHoldRepo;
import com.iemr.flw.repo.iemr.DeliveryOutcomeRepo;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.DeliveryOutcomeService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.Validate;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class DeliveryOutcomeServiceImpl implements DeliveryOutcomeService {

    @Autowired
    private DeliveryOutcomeRepo deliveryOutcomeRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

//    @Autowired
//    private EligibleCoupleRegisterRepo ecrRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;

    @Autowired
    private HouseHoldRepo houseHoldRepo;

   private boolean isJsyBeneficiary;


    private Gson gson = new Gson();

    private final Logger logger = LoggerFactory.getLogger(DeliveryOutcomeServiceImpl.class);

    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();

    boolean institutionalDelivery = false;

    @Override
    public String registerDeliveryOutcome(List<DeliveryOutcomeDTO> deliveryOutcomeDTOS) {

        try {
            List<DeliveryOutcome> delOutList = new ArrayList<>();
//            List<EligibleCoupleRegister> ecrList = new ArrayList<>();
            deliveryOutcomeDTOS.forEach(it -> {
                DeliveryOutcome deliveryoutcome = deliveryOutcomeRepo.findDeliveryOutcomeByBenIdAndIsActive(it.getBenId(), true);

//                EligibleCoupleRegister ecr = ecrRepo.findEligibleCoupleRegisterByBenId(it.getBenId());
//                ecr.setNumLiveChildren(it.getLiveBirth() + ecr.getNumLiveChildren());
//                ecr.setNumChildren(it.getDeliveryOutcome() + ecr.getNumChildren());
//                ecrList.add(ecr);

                if (deliveryoutcome != null) {
                    Long id = deliveryoutcome.getId();
                    modelMapper.map(it, deliveryoutcome);
                    deliveryoutcome.setId(id);
                } else {
                    deliveryoutcome = new DeliveryOutcome();
                    modelMapper.map(it, deliveryoutcome);
                    deliveryoutcome.setId(null);
                }
                delOutList.add(deliveryoutcome);
            });
            deliveryOutcomeRepo.saveAll(delOutList);

            checkAndAddJsyIncentive(delOutList);
//            ecrRepo.save(ecrList);
            return "no of delivery outcome details saved: " + delOutList.size();
        } catch (Exception e) {
            return "error while saving delivery outcome details: " + e.getMessage();
        }
    }
    @Override
    public List<DeliveryOutcomeDTO> getDeliveryOutcome(GetBenRequestHandler dto) {
        try{
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<DeliveryOutcome> deliveryOutcomeList = deliveryOutcomeRepo.getDeliveryOutcomeByAshaId(user, dto.getFromDate(), dto.getToDate());
            return deliveryOutcomeList.stream()
                    .map(deliveryOutcome -> mapper.convertValue(deliveryOutcome, DeliveryOutcomeDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    public void  checkAndAddJsyIncentive(List<DeliveryOutcome> delOutList){

        delOutList.forEach(deliveryOutcome -> {
            IncentiveActivity incentiveActivityInstJSY1= incentivesRepo.findIncentiveMasterByNameAndGroup("JSY_1ST_DEL_INST_RURAL", GroupName.JSY.getDisplayName());
            IncentiveActivity incentiveActivityInstJSY2= incentivesRepo.findIncentiveMasterByNameAndGroup("JSY_2ND_DEL_INST_RURAL", GroupName.JSY.getDisplayName());
            IncentiveActivity incentiveActivityInstJSY3= incentivesRepo.findIncentiveMasterByNameAndGroup("JSY_3RD_DEL_INST_RURAL", GroupName.JSY.getDisplayName());
            IncentiveActivity incentiveActivityInstJSY4= incentivesRepo.findIncentiveMasterByNameAndGroup("JSY_4TH_DEL_INST_RURAL", GroupName.JSY.getDisplayName());


            logger.info("delOutList"+gson.toJson(deliveryOutcome));
            IncentiveActivity incentiveActivityJSY1 = incentivesRepo.findIncentiveMasterByNameAndGroup("JSY_1ST_DEL_ANC_RURAL", GroupName.JSY.getDisplayName());
            if (incentiveActivityJSY1 != null) {
                if(deliveryOutcome.getDeliveryOutcome()==1){
                    createIncentiveRecordforJsy(deliveryOutcome,deliveryOutcome.getBenId(),incentiveActivityJSY1);
                    if(deliveryOutcome.getPlaceOfDelivery()!=null) {
                        createIncentiveRecordforJsy(deliveryOutcome, deliveryOutcome.getBenId(), incentiveActivityInstJSY1);
                    }
                }
            }


            IncentiveActivity incentiveActivityJSY2 = incentivesRepo.findIncentiveMasterByNameAndGroup("JSY_2ND_DEL_ANC_RURAL", GroupName.JSY.getDisplayName());
            if (incentiveActivityJSY2 != null) {
                if(deliveryOutcome.getDeliveryOutcome()==2){
                    createIncentiveRecordforJsy(deliveryOutcome,deliveryOutcome.getBenId(),incentiveActivityJSY2);
                    if(deliveryOutcome.getPlaceOfDelivery()!=null) {

                        createIncentiveRecordforJsy(deliveryOutcome, deliveryOutcome.getBenId(), incentiveActivityInstJSY2);
                    }
                }
            }

            IncentiveActivity incentiveActivityJSY3= incentivesRepo.findIncentiveMasterByNameAndGroup("JSY_3RD_DEL_ANC_RURAL", GroupName.JSY.getDisplayName());
            if (incentiveActivityJSY3 != null) {
                if(deliveryOutcome.getDeliveryOutcome()==3){
                    createIncentiveRecordforJsy(deliveryOutcome,deliveryOutcome.getBenId(),incentiveActivityJSY3);
                    if(deliveryOutcome.getPlaceOfDelivery()!=null) {
                        createIncentiveRecordforJsy(deliveryOutcome, deliveryOutcome.getBenId(), incentiveActivityInstJSY3);
                    }
                }
            }

            IncentiveActivity incentiveActivityJSY4= incentivesRepo.findIncentiveMasterByNameAndGroup("JSY_4TH_DEL_ANC_RURAL", GroupName.JSY.getDisplayName());
            if (incentiveActivityJSY4 != null) {
                if(deliveryOutcome.getDeliveryOutcome()==4){
                    createIncentiveRecordforJsy(deliveryOutcome,deliveryOutcome.getBenId(),incentiveActivityJSY4);
                    if(deliveryOutcome.getPlaceOfDelivery()!=null){
                        createIncentiveRecordforJsy(deliveryOutcome,deliveryOutcome.getBenId(),incentiveActivityInstJSY4);

                    }
                }
            }




            // Determine delivery number
            int deliveryNumber = deliveryOutcome.getDeliveryOutcome(); // 1,2,3,4
            if(deliveryOutcome.getIsJSYBenificiary()!=null){

                isJsyBeneficiary = deliveryOutcome.getIsJSYBenificiary();

            }
             if(deliveryOutcome.getPlaceOfDelivery()!=null && !deliveryOutcome.getPlaceOfDelivery().isEmpty()){
                 institutionalDelivery = true;

             }else {
                 institutionalDelivery = false;
             }
            Optional<RMNCHBeneficiaryDetailsRmnch> rmnchBeneficiaryDetailsRmnch = beneficiaryRepo.findById(deliveryOutcome.getBenId());
             if(rmnchBeneficiaryDetailsRmnch.isPresent()){
                 logger.info("rmnchBeneficiaryDetailsRmnch"+rmnchBeneficiaryDetailsRmnch.get());
             }

            String location = "Rural";

            // JSY incentive name construction
            List<String> activityNames = new ArrayList<>();


//            if(location.equalsIgnoreCase("Rural")) {
//                switch(deliveryNumber) {
//                    case 1:
//                        if(isJsyBeneficiary){
//                            if(deliveryOutcome.getDeliveryOutcome()==0){
//                                activityNames.add("JSY_1ST_DEL_ANC_RURAL");
//                            }
//                        }
//                        if(institutionalDelivery) {
//                            if(deliveryOutcome.getLiveBirth()==0)
//                                activityNames.add("JSY_1ST_DEL_INST_RURAL");
//                        }
//                        break;
//
//                    case 2:
//                        if(isJsyBeneficiary){
//                            if(deliveryOutcome.getLiveBirth()==1){
//                                activityNames.add("JSY_2ND_DEL_ANC_RURAL");
//                            }
//                        }
//                        if(institutionalDelivery) {
//                            if(deliveryOutcome.getLiveBirth()==1)
//                                activityNames.add("JSY_2ND_DEL_INST_RURAL");
//                        }
//                        break;
//
//                    case 3:
//                        if(isJsyBeneficiary){
//                            if(deliveryOutcome.getLiveBirth()==2){
//                                activityNames.add("JSY_3RD_DEL_ANC_RURAL");
//                            }
//                        }
//                        if(institutionalDelivery) {
//                            if(deliveryOutcome.getLiveBirth()==2)
//                                activityNames.add("JSY_3RD_DEL_INST_RURAL");
//                        }
//                        break;
//
//                    case 4:
//                        if(isJsyBeneficiary){
//                            if(deliveryOutcome.getLiveBirth()==3){
//                                activityNames.add("JSY_4TH_DEL_ANC_RURAL");
//                            }
//                        }
//                        if(institutionalDelivery) {
//                            if(deliveryOutcome.getLiveBirth()==3)
//                                activityNames.add("JSY_4TH_DEL_INST_RURAL");
//                        }
//                        break;
//                }
//            } else if(location.equalsIgnoreCase("Urban")) {
//                if(isJsyBeneficiary){
//                    if(deliveryOutcome.getLiveBirth()==0){
//                        activityNames.add("JSY_ANC_URBAN");
//                    }
//                }
//                if(institutionalDelivery){
//                    if(deliveryOutcome.getLiveBirth()==0)
//                        activityNames.add("JSY_INST_URBAN");
//                }
//            }


            // For each activity, create record
//            for(String activityName : activityNames){
//                IncentiveActivity incentiveActivity = incentivesRepo.findIncentiveMasterByNameAndGroup(activityName, GroupName.JSY.getDisplayName());
//                if(incentiveActivity != null){
//                    createIncentiveRecordforJsy(deliveryOutcome, deliveryOutcome.getBenId(), incentiveActivity);
//                }
//            }
            IncentiveActivity institutionalDeliveryActivityAM = incentivesRepo.findIncentiveMasterByNameAndGroup("MH_MOTIVATE_INST_DEL", GroupName.MATERNAL_HEALTH.getDisplayName());
            IncentiveActivity institutionalDeliveryActivityCH = incentivesRepo.findIncentiveMasterByNameAndGroup("INST_DELIVERY_ESCORT", GroupName.ACTIVITY.getDisplayName());
            String placeOfDelivery = deliveryOutcome.getPlaceOfDelivery();

            if (placeOfDelivery != null &&
                    (!placeOfDelivery.equalsIgnoreCase("home") ||
                    !placeOfDelivery.equalsIgnoreCase("in transit") ||
                    !placeOfDelivery.equalsIgnoreCase("other private hospital"))) {

                // Institutional delivery (eligible case)
                if (institutionalDeliveryActivityAM != null) {
                    createIncentiveRecordforJsy(deliveryOutcome, deliveryOutcome.getBenId(), institutionalDeliveryActivityAM);
                }

                if (institutionalDeliveryActivityCH != null) {
                    createIncentiveRecordforJsy(deliveryOutcome, deliveryOutcome.getBenId(), institutionalDeliveryActivityCH);
                }
            }
        });



    }
    private void createIncentiveRecordforJsy(DeliveryOutcome delOutList, Long benId, IncentiveActivity immunizationActivity) {
        logger.info("benId"+benId);

        try {
            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(immunizationActivity.getId(), delOutList.getCreatedDate(), benId);


            if (record == null) {
                logger.info("setStartDate"+delOutList.getDateOfDelivery());
                logger.info("setCreatedDate"+delOutList.getCreatedDate());
                record = new IncentiveActivityRecord();
                record.setActivityId(immunizationActivity.getId());
                record.setCreatedDate(delOutList.getDateOfDelivery());
                record.setCreatedBy(delOutList.getCreatedBy());
                record.setStartDate(delOutList.getDateOfDelivery());
                record.setEndDate(delOutList.getDateOfDelivery());
                record.setUpdatedDate(delOutList.getCreatedDate());
                record.setUpdatedBy(delOutList.getCreatedBy());
                record.setBenId(benId);
                record.setAshaId(userRepo.getUserIdByName(delOutList.getUpdatedBy()));
                record.setAmount(Long.valueOf(immunizationActivity.getRate()));
                recordRepo.save(record);
            }else {
                logger.info("benId:"+record.getId());

            }
        }catch (Exception e){
         logger.error("JSY Incentive:",e);
        }


    }
}
