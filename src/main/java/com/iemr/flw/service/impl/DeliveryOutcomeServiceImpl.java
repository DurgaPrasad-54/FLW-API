package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iemr.flw.domain.identity.RMNCHBeneficiaryDetailsRmnch;
import com.iemr.flw.domain.iemr.DeliveryOutcome;
import com.iemr.flw.domain.iemr.HbncVisit;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.DeliveryOutcomeDTO;
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
            logger.info("delOutList"+gson.toJson(deliveryOutcome));

            // Determine delivery number
            int deliveryNumber = deliveryOutcome.getDeliveryOutcome(); // 1,2,3,4
            boolean isJsyBeneficiary = deliveryOutcome.getIsJSYBenificiary();
             if(deliveryOutcome.getPlaceOfDelivery()!=null && !deliveryOutcome.getPlaceOfDelivery().isEmpty()){
                 institutionalDelivery = true;

             }else {
                 institutionalDelivery = false;
             }
            Optional<RMNCHBeneficiaryDetailsRmnch> rmnchBeneficiaryDetailsRmnch = beneficiaryRepo.findById(deliveryOutcome.getBenId());
             if(rmnchBeneficiaryDetailsRmnch.isPresent()){
                 logger.info("rmnchBeneficiaryDetailsRmnch"+rmnchBeneficiaryDetailsRmnch.get());
             }
            String location = houseHoldRepo.getByHouseHoldID(rmnchBeneficiaryDetailsRmnch.get().getHouseoldId()).getResidentialArea(); // "Rural" or "Urban"

            // JSY incentive name construction

            List<String> activityNames = new ArrayList<>();
            if(location.equalsIgnoreCase("Rural")) {
                switch(deliveryNumber) {
                    case 1:
                        if(isJsyBeneficiary) activityNames.add("1st_RURAL_DELIVERY");
                        if(institutionalDelivery) activityNames.add("1st_RURAL_INSTITUTIONAL_DELIVERY");
                        break;
                    case 2:
                        if(isJsyBeneficiary) activityNames.add("2nd_RURAL_DELIVERY");
                        if(institutionalDelivery) activityNames.add("2nd_RURAL_INSTITUTIONAL_DELIVERY");
                        break;
                    case 3:
                        if(isJsyBeneficiary) activityNames.add("3RD_RURAL_DELIVERY_ANC");
                        if(institutionalDelivery) activityNames.add("3rd_RURAL_INSTITUTIONAL_DELIVERY");
                        break;
                    case 4:
                        if(isJsyBeneficiary) activityNames.add("4th_RURAL_DELIVERY");
                        if(institutionalDelivery) activityNames.add("4th_RURAL_INSTITUTIONAL_DELIVERY");
                        break;
                }
            } else if(location.equalsIgnoreCase("Urban")) {
                if(isJsyBeneficiary) activityNames.add("URBAN_DELIVERY");
                if(institutionalDelivery) activityNames.add("URBAN_INSTITUTIONAL_DELIVERY");
            }

            // For each activity, create record
            for(String activityName : activityNames){
                IncentiveActivity incentiveActivity = incentivesRepo.findIncentiveMasterByNameAndGroup(activityName,"JSY");
                if(incentiveActivity != null){
                    createIncentiveRecordforJsy(deliveryOutcome, deliveryOutcome.getBenId(), incentiveActivity);
                }
            }
        });

    }
    private void createIncentiveRecordforJsy(DeliveryOutcome delOutList, Long benId, IncentiveActivity immunizationActivity) {
        logger.info("benId"+benId);

        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(immunizationActivity.getId(), delOutList.getCreatedDate(), benId);
        if (record == null) {
            logger.info("setStartDate"+delOutList.getDateOfDelivery());
            logger.info("setCreatedDate"+delOutList.getCreatedDate());
            record = new IncentiveActivityRecord();
            record.setActivityId(immunizationActivity.getId());
            record.setCreatedDate(delOutList.getDateOfDelivery());
            record.setCreatedBy(delOutList.getCreatedBy());
            record.setName(immunizationActivity.getName());
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
    }
}
