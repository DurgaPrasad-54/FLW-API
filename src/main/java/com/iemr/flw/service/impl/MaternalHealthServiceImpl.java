package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.identity.RMNCHBeneficiaryDetailsRmnch;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.MaternalHealthService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaternalHealthServiceImpl implements MaternalHealthService {

    private final Logger logger = LoggerFactory.getLogger(MaternalHealthServiceImpl.class);

    @Autowired
    PregnantWomanRegisterRepo pregnantWomanRegisterRepo;

    @Autowired
    private ANCVisitRepo ancVisitRepo;

    @Autowired
    private AncCareRepo ancCareRepo;

    @Autowired
    private PNCVisitRepo pncVisitRepo;

    @Autowired
    private PNCCareRepo pncCareRepo;

    @Autowired
    private BenVisitDetailsRepo benVisitDetailsRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private PmsmaRepo pmsmaRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;


    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();


    @Autowired
    private SMSServiceImpl smsServiceImpl;


    public static final List<String> PNC_PERIODS =
            Arrays.asList("1st Day", "3rd Day", "7th Day", "14th Day", "21st Day", "28th Day", "42nd Day");

    @Override
    public String registerPregnantWoman(List<PregnantWomanDTO> pregnantWomanDTOs) {

        try {
            List<PregnantWomanRegister> pwrList = new ArrayList<>();
            pregnantWomanDTOs.forEach(it -> {
                PregnantWomanRegister pwr =
                        pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(it.getBenId(), true);

                if (pwr != null) {
                    Long id = pwr.getId();
                    modelMapper.map(it, pwr);
                    pwr.setId(id);
                } else {
                    pwr = new PregnantWomanRegister();
                    modelMapper.map(it, pwr);
                    pwr.setId(null);
                }
                pwrList.add(pwr);
            });
            pregnantWomanRegisterRepo.saveAll(pwrList);

            logger.info(pwrList.size() + " Pregnant Woman details saved");
            return "no of pwr details saved: " + pwrList.size();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<PregnantWomanDTO> getPregnantWoman(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<PregnantWomanRegister> pregnantWomanRegisterList =
                    pregnantWomanRegisterRepo.getPWRWithBen(user, dto.getFromDate(), dto.getToDate());

            return pregnantWomanRegisterList.stream()
                    .map(pregnantWomanRegister -> mapper.convertValue(pregnantWomanRegister, PregnantWomanDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<ANCVisitDTO> getANCVisits(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<ANCVisit> ancVisits = ancVisitRepo.getANCForPW(user, dto.getFromDate(), dto.getToDate());
            return ancVisits.stream()
                    .map(anc -> mapper.convertValue(anc, ANCVisitDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String saveANCVisit(List<ANCVisitDTO> ancVisitDTOs) {
        try {
            List<ANCVisit> ancList = new ArrayList<>();
            List<AncCare> ancCareList = new ArrayList<>();
            ancVisitDTOs.forEach(it -> {
                ANCVisit ancVisit =
                        ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(it.getBenId(), it.getAncVisit(), true);

                if (ancVisit != null) {
                    Long id = ancVisit.getId();
                    modelMapper.map(it, ancVisit);
                    ancVisit.setId(id);
                } else {
                    ancVisit = new ANCVisit();
                    modelMapper.map(it, ancVisit);
                    ancVisit.setId(null);

                    Long benRegId = beneficiaryRepo.getRegIDFromBenId(it.getBenId());

                    // Saving data in BenVisitDetails table
                    PregnantWomanRegister pwr = pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(it.getBenId(), true);
                    BenVisitDetail benVisitDetail = new BenVisitDetail();
                    modelMapper.map(it, benVisitDetail);
                    benVisitDetail.setBeneficiaryRegId(benRegId);
                    benVisitDetail.setVisitCategory("ANC");
                    benVisitDetail.setVisitReason("Follow Up");
                    benVisitDetail.setPregnancyStatus("Yes");
                    benVisitDetail.setProcessed("N");
                    benVisitDetail.setModifiedBy(it.getUpdatedBy());
                    benVisitDetail.setLastModDate(it.getUpdatedDate());
                    benVisitDetail.setProviderServiceMapID(it.getProviderServiceMapID());
                    benVisitDetail = benVisitDetailsRepo.save(benVisitDetail);

                    // Saving Data in AncCare table
                    AncCare ancCare = new AncCare();
                    modelMapper.map(it, ancCare);
                    ancCare.setBenVisitId(benVisitDetail.getBenVisitId());
                    ancCare.setBeneficiaryRegId(benRegId);
                    if (pwr != null) {
                        ancCare.setLastMenstrualPeriodLmp(pwr.getLmpDate());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(pwr.getLmpDate());
                        cal.add(Calendar.DAY_OF_WEEK, 280);
                        ancCare.setExpectedDateofDelivery(new Timestamp(cal.getTime().getTime()));
                    }
                    ancCare.setTrimesterNumber(it.getAncVisit().shortValue());
                    ancCare.setModifiedBy(it.getUpdatedBy());
                    ancCare.setLastModDate(it.getUpdatedDate());
                    ancCare.setProcessed("N");
                    ancCareList.add(ancCare);
                }
                ancList.add(ancVisit);
            });

            ancVisitRepo.saveAll(ancList);
            ancCareRepo.saveAll(ancCareList);
            checkAndAddIncentives(ancList);
            logger.info("ANC visit details saved");
            return "no of anc details saved: " + ancList.size();
        } catch (Exception e) {
            logger.info("Saving ANC visit details failed with error : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<PmsmaDTO> getPmsmaRecords(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<PMSMA> pmsmaList = pmsmaRepo.getAllPmsmaByAshaId(user, dto.getFromDate(), dto.getToDate());
            return pmsmaList.stream()
                    .map(pmsma -> mapper.convertValue(pmsma, PmsmaDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String savePmsmaRecords(List<PmsmaDTO> pmsmaDTOs) {
        try {
            List<PMSMA> pmsmaList = new ArrayList<>();
            pmsmaDTOs.forEach(it -> {
                PMSMA pmsma =
                        pmsmaRepo.findPMSMAByBenIdAndIsActive(it.getBenId(), true);
                if (pmsma != null) {
                    Long id = pmsma.getId();
                    modelMapper.map(it, pmsma);
                    pmsma.setId(id);
                } else {
                    pmsma = new PMSMA();
                    modelMapper.map(it, pmsma);
                    pmsma.setId(null);
                }
                pmsmaList.add(pmsma);
            });
            pmsmaRepo.saveAll(pmsmaList);
            logger.info("PMSMA details saved");
            return "No. of PMSMA records saved: " + pmsmaList.size();
        } catch (Exception e) {
            logger.info("Saving PMSMA details failed with error : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<PNCVisitDTO> getPNCVisits(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<PNCVisit> pncVisits = pncVisitRepo.getPNCForPW(user, dto.getFromDate(), dto.getToDate());
            return pncVisits.stream()
                    .map(pnc -> mapper.convertValue(pnc, PNCVisitDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String savePNCVisit(List<PNCVisitDTO> pncVisitDTOs) {
        try {
            List<PNCVisit> pncList = new ArrayList<>();
            List<PNCCare> pncCareList = new ArrayList<>();
            pncVisitDTOs.forEach(it -> {
                PNCVisit pncVisit =
                        pncVisitRepo.findPNCVisitByBenIdAndPncPeriodAndIsActive(it.getBenId(), it.getPncPeriod(), true);

                if (pncVisit != null) {
                    Long id = pncVisit.getId();
                    modelMapper.map(it, pncVisit);
                    pncVisit.setId(id);
                } else {
                    pncVisit = new PNCVisit();
                    modelMapper.map(it, pncVisit);
                    pncVisit.setId(null);

                    Long benRegId = beneficiaryRepo.getRegIDFromBenId(it.getBenId());

                    // Saving data in BenVisitDetails table
                    PregnantWomanRegister pwr = pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(it.getBenId(), true);
                    BenVisitDetail benVisitDetail = new BenVisitDetail();
                    modelMapper.map(it, benVisitDetail);
                    benVisitDetail.setBeneficiaryRegId(benRegId);
                    benVisitDetail.setVisitCategory("PNC");
                    benVisitDetail.setVisitReason("Follow Up");
                    benVisitDetail.setPregnancyStatus("No");
                    benVisitDetail.setProcessed("N");
                    benVisitDetail.setModifiedBy(it.getUpdatedBy());
                    benVisitDetail.setLastModDate(it.getUpdatedDate());
                    benVisitDetail = benVisitDetailsRepo.save(benVisitDetail);

                    // Saving Data in AncCare table
                    PNCCare pncCare = new PNCCare();
                    modelMapper.map(it, pncCare);
                    pncCare.setBenVisitId(benVisitDetail.getBenVisitId());
                    pncCare.setBeneficiaryRegId(benRegId);
//                    pncCare.setLastMenstrualPeriodLmp(pwr.getLmpDate());
//                    Calendar cal = Calendar.getInstance();
//                    cal.setTime(pwr.getLmpDate());
//                    cal.add(Calendar.DAY_OF_WEEK, 280);
//                    pncCare.setExpectedDateofDelivery(new Timestamp(cal.getTime().getTime()));
                    pncCare.setVisitNo((short) PNC_PERIODS.indexOf(it.getPncPeriod()));
                    pncCare.setModifiedBy(it.getUpdatedBy());
                    pncCare.setLastModDate(it.getUpdatedDate());
                    pncCare.setProcessed("N");
                    pncCareList.add(pncCare);
                    checkAndAddAntaraIncentive(pncList, pncVisit);
                }
                pncList.add(pncVisit);
            });
            pncVisitRepo.saveAll(pncList);
            pncCareRepo.saveAll(pncCareList);
            logger.info("PNC visit details saved");
            return "no of pnc details saved: " + pncList.size();
        } catch (Exception e) {
            logger.info("Saving PNC visit details failed with error : " + e.getMessage());
        }
        return null;
    }

    private void checkAndAddAntaraIncentive(List<PNCVisit> recordList, PNCVisit ect) {
        Integer userId = userRepo.getUserIdByName(ect.getCreatedBy());
        logger.info("ContraceptionMethod:" + ect.getContraceptionMethod());
        if (ect.getContraceptionMethod() != null && ect.getContraceptionMethod().equals("MALE STERILIZATION")) {

            IncentiveActivity maleSterilizationActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_MALE_STER", GroupName.FAMILY_PLANNING.getDisplayName());
            if (maleSterilizationActivity != null) {
                addIncenticeRecord(recordList, ect, userId, maleSterilizationActivity);
            }
        } else if (ect.getContraceptionMethod() != null && ect.getContraceptionMethod().equals("FEMALE STERILIZATION")) {

            IncentiveActivity femaleSterilizationActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_FEMALE_STER", GroupName.FAMILY_PLANNING.getDisplayName());
            if (femaleSterilizationActivity != null) {
                addIncenticeRecord(recordList, ect, userId, femaleSterilizationActivity);
            }
        } else if (ect.getContraceptionMethod() != null && ect.getContraceptionMethod().equals("MiniLap")) {

            IncentiveActivity miniLapActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_MINILAP", GroupName.FAMILY_PLANNING.getDisplayName());
            if (miniLapActivity != null) {
                addIncenticeRecord(recordList, ect, userId, miniLapActivity);
            }
        } else if (ect.getContraceptionMethod() != null && ect.getContraceptionMethod().equals("Condom")) {

            IncentiveActivity comdomActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_CONDOM", GroupName.FAMILY_PLANNING.getDisplayName());
            if (comdomActivity != null) {
                addIncenticeRecord(recordList, ect, userId, comdomActivity);
            }
        } else if (ect.getContraceptionMethod() != null && ect.getContraceptionMethod().equals("Copper T (IUCD)")) {

            IncentiveActivity copperTActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_CONDOM", GroupName.FAMILY_PLANNING.getDisplayName());
            if (copperTActivity != null) {
                addIncenticeRecord(recordList, ect, userId, copperTActivity);
            }
        }
    }

    private void addIncenticeRecord(List<PNCVisit> recordList, PNCVisit ect, Integer userId, IncentiveActivity antaraActivity) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(antaraActivity.getId(), ect.getCreatedDate(), ect.getBenId());
        // get bene details

        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(antaraActivity.getId());
            record.setCreatedDate(ect.getPncDate());
            record.setCreatedBy(ect.getCreatedBy());
            record.setStartDate(ect.getPncDate());
            record.setEndDate(ect.getPncDate());
            record.setUpdatedDate(ect.getPncDate());
            record.setUpdatedBy(ect.getCreatedBy());
            record.setBenId(ect.getBenId());
            record.setAshaId(userId);
            record.setAmount(Long.valueOf(antaraActivity.getRate()));
            recordRepo.save(record);
        }
    }


    private void checkAndAddIncentives(List<ANCVisit> ancList) {

        IncentiveActivity anc1Activity =
                incentivesRepo.findIncentiveMasterByNameAndGroup("ANC_REGISTRATION_1ST_TRIM", GroupName.MATERNAL_HEALTH.getDisplayName());

        IncentiveActivity ancFullActivityAM =
                incentivesRepo.findIncentiveMasterByNameAndGroup("FULL_ANC", GroupName.MATERNAL_HEALTH.getDisplayName());

        IncentiveActivity ancFullActivityCH =
                incentivesRepo.findIncentiveMasterByNameAndGroup("FULL_ANC", GroupName.MATERNAL_HEALTH.getDisplayName());

        IncentiveActivity comprehensiveAbortionActivity = incentivesRepo.findIncentiveMasterByNameAndGroup("COMPREHENSIVE_ABORTION_CARE", GroupName.MATERNAL_HEALTH.getDisplayName());

        IncentiveActivity identifiedHrpActivityAM = incentivesRepo.findIncentiveMasterByNameAndGroup("EPMSMA_HRP_IDENTIFIED", GroupName.MATERNAL_HEALTH.getDisplayName());
        IncentiveActivity identifiedHrpActivityCH = incentivesRepo.findIncentiveMasterByNameAndGroup("EPMSMA_HRP_IDENTIFIED", GroupName.MATERNAL_HEALTH.getDisplayName());




        if (anc1Activity != null) {
            ancList.forEach(ancVisit -> {

                Integer userId = userRepo.getUserIdByName(ancVisit.getCreatedBy());
                if (ancVisit.getAncVisit() == 1) {
                    IncentiveActivityRecord record = recordRepo
                            .findRecordByActivityIdCreatedDateBenId(anc1Activity.getId(), ancVisit.getCreatedDate(), ancVisit.getBenId());

                    if (record == null) {
                        record = new IncentiveActivityRecord();
                        record.setActivityId(anc1Activity.getId());
                        record.setCreatedDate(ancVisit.getAncDate());
                        record.setCreatedBy(ancVisit.getCreatedBy());
                        record.setStartDate(ancVisit.getAncDate());
                        record.setEndDate(ancVisit.getAncDate());
                        record.setUpdatedDate(ancVisit.getAncDate());
                        record.setUpdatedBy(ancVisit.getCreatedBy());
                        record.setBenId(ancVisit.getBenId());
                        record.setAshaId(userId);
                        record.setAmount(Long.valueOf(anc1Activity.getRate()));
                        recordRepo.save(record);
                    }
                }

                if (ancVisit.getAncVisit() == 4) {

                    IncentiveActivityRecord recordAM = recordRepo
                            .findRecordByActivityIdCreatedDateBenId(ancFullActivityAM.getId(), ancVisit.getCreatedDate(), ancVisit.getBenId());

                    IncentiveActivityRecord recordCH = recordRepo
                            .findRecordByActivityIdCreatedDateBenId(ancFullActivityAM.getId(), ancVisit.getCreatedDate(), ancVisit.getBenId());

                    ANCVisit visit1 = ancVisitRepo
                            .findANCVisitByBenIdAndAncVisitAndIsActive(ancVisit.getBenId(), 1, true);
                    ANCVisit visit2 = ancVisitRepo
                            .findANCVisitByBenIdAndAncVisitAndIsActive(ancVisit.getBenId(), 2, true);
                    ANCVisit visit3 = ancVisitRepo
                            .findANCVisitByBenIdAndAncVisitAndIsActive(ancVisit.getBenId(), 3, true);
                    ANCVisit visit4 = ancVisitRepo
                            .findANCVisitByBenIdAndAncVisitAndIsActive(ancVisit.getBenId(), 4, true);

                    if (recordAM == null && (visit1 != null) && (visit2 != null) && (visit3 != null) && (visit4 != null)) {

                        recordAM = new IncentiveActivityRecord();
                        recordAM.setActivityId(ancFullActivityAM.getId());
                        recordAM.setCreatedDate(ancVisit.getAncDate());
                        recordAM.setCreatedBy(ancVisit.getCreatedBy());
                        recordAM.setUpdatedDate(ancVisit.getAncDate());
                        recordAM.setUpdatedBy(ancVisit.getCreatedBy());
                        recordAM.setStartDate(visit1.getAncDate());
                        recordAM.setEndDate(visit4.getAncDate());
                        recordAM.setBenId(ancVisit.getBenId());
                        recordAM.setAshaId(userId);
                        recordAM.setAmount(Long.valueOf(ancFullActivityAM.getRate()));
                        recordRepo.save(recordAM);
                    }

                    if (recordCH == null && (visit1 != null) && (visit2 != null) && (visit3 != null) && (visit4 != null)) {

                        recordCH = new IncentiveActivityRecord();
                        recordCH.setActivityId(ancFullActivityCH.getId());
                        recordCH.setCreatedDate(ancVisit.getAncDate());
                        recordCH.setCreatedBy(ancVisit.getCreatedBy());
                        recordCH.setUpdatedDate(ancVisit.getAncDate());
                        recordCH.setUpdatedBy(ancVisit.getCreatedBy());
                        recordCH.setStartDate(visit1.getAncDate());
                        recordCH.setEndDate(visit4.getAncDate());
                        recordCH.setBenId(ancVisit.getBenId());
                        recordCH.setAshaId(userId);
                        recordCH.setAmount(Long.valueOf(ancFullActivityCH.getRate()));
                        recordRepo.save(recordCH);
                    }
                }

            });
        }
        if (comprehensiveAbortionActivity != null) {

            ancList.forEach((ancVisit -> {
                IncentiveActivityRecord record = recordRepo.findRecordByActivityIdCreatedDateBenId(comprehensiveAbortionActivity.getId(), ancVisit.getCreatedDate(), ancVisit.getBenId());
                Integer userId = userRepo.getUserIdByName(ancVisit.getCreatedBy());

                if (Objects.equals(ancVisit.getIsAborted(), "true")) {

                    if (record == null) {
                        logger.info("record:"+record.getName());
                        logger.info("condition:"+ancVisit.getIsAborted());
                        record = new IncentiveActivityRecord();
                        record.setActivityId(comprehensiveAbortionActivity.getId());
                        record.setCreatedDate(ancVisit.getCreatedDate());
                        record.setCreatedBy(ancVisit.getCreatedBy());
                        record.setUpdatedDate(ancVisit.getCreatedDate());
                        record.setUpdatedBy(ancVisit.getCreatedBy());
                        record.setStartDate(ancVisit.getCreatedDate());
                        record.setEndDate(ancVisit.getCreatedDate());
                        record.setBenId(ancVisit.getBenId());
                        record.setAshaId(userId);
                        record.setAmount(Long.valueOf(comprehensiveAbortionActivity.getRate()));
                        recordRepo.save(record);
                    }

                }
            }));
        }

        if (identifiedHrpActivityAM != null) {
            ancList.forEach((ancVisit -> {
                IncentiveActivityRecord record = recordRepo.findRecordByActivityIdCreatedDateBenId(identifiedHrpActivityAM.getId(), ancVisit.getCreatedDate(), ancVisit.getBenId());
                Integer userId = userRepo.getUserIdByName(ancVisit.getCreatedBy());
                RMNCHBeneficiaryDetailsRmnch rmnchBeneficiaryDetailsRmnch = beneficiaryRepo.getDetailsByRegID(beneficiaryRepo.getRegIDFromBenId(ancVisit.getBenId()));
                String beneName = rmnchBeneficiaryDetailsRmnch.getFirstName() + " " + rmnchBeneficiaryDetailsRmnch.getLastName();
                if (ancVisit.getIsHrpConfirmed() != null) {
                    if (ancVisit.getIsHrpConfirmed()) {
                        if (record == null) {
                            record = new IncentiveActivityRecord();
                            record.setActivityId(identifiedHrpActivityAM.getId());
                            record.setCreatedDate(ancVisit.getCreatedDate());
                            record.setCreatedBy(ancVisit.getCreatedBy());
                            record.setUpdatedDate(ancVisit.getCreatedDate());
                            record.setName(beneName);
                            record.setUpdatedBy(ancVisit.getCreatedBy());
                            record.setStartDate(ancVisit.getCreatedDate());
                            record.setEndDate(ancVisit.getCreatedDate());
                            record.setBenId(ancVisit.getBenId());
                            record.setAshaId(userId);
                            record.setAmount(Long.valueOf(identifiedHrpActivityAM.getRate()));
                            recordRepo.save(record);
                        }
                    }


                }
            }));
        }


        if (identifiedHrpActivityCH != null) {
            ancList.forEach((ancVisit -> {
                IncentiveActivityRecord record = recordRepo.findRecordByActivityIdCreatedDateBenId(identifiedHrpActivityCH.getId(), ancVisit.getCreatedDate(), ancVisit.getBenId());
                Integer userId = userRepo.getUserIdByName(ancVisit.getCreatedBy());
                RMNCHBeneficiaryDetailsRmnch rmnchBeneficiaryDetailsRmnch = beneficiaryRepo.getDetailsByRegID(beneficiaryRepo.getRegIDFromBenId(ancVisit.getBenId()));
                String beneName = rmnchBeneficiaryDetailsRmnch.getFirstName() + " " + rmnchBeneficiaryDetailsRmnch.getLastName();
                if (ancVisit.getIsHrpConfirmed() != null) {
                    if (ancVisit.getIsHrpConfirmed()) {
                        if (record == null) {
                            record = new IncentiveActivityRecord();
                            record.setActivityId(identifiedHrpActivityCH.getId());
                            record.setCreatedDate(ancVisit.getCreatedDate());
                            record.setCreatedBy(ancVisit.getCreatedBy());
                            record.setUpdatedDate(ancVisit.getCreatedDate());
                            record.setName(beneName);
                            record.setUpdatedBy(ancVisit.getCreatedBy());
                            record.setStartDate(ancVisit.getCreatedDate());
                            record.setEndDate(ancVisit.getCreatedDate());
                            record.setBenId(ancVisit.getBenId());
                            record.setAshaId(userId);
                            record.setAmount(Long.valueOf(identifiedHrpActivityCH.getRate()));
                            recordRepo.save(record);
                        }
                    }


                }
            }));
        }

    }


    public void sendAncDueTomorrowNotifications(String ashaId) {
        try {
            GetBenRequestHandler request = new GetBenRequestHandler();
            request.setAshaId(Integer.valueOf(ashaId));
            request.setFromDate(Timestamp.valueOf(LocalDate.now().plusDays(1).atStartOfDay())); // Tomorrow at 00:00:00
            request.setToDate(Timestamp.valueOf(LocalDate.now().plusDays(1).atTime(LocalTime.MAX))); // Tomorrow at 23:59:59.999999999


            List<ANCVisitDTO> ancList = getANCVisits(request);

            if (ancList != null) {
                for (ANCVisitDTO anc : ancList) {
                    if (anc.getAncDate() != null && anc.getAncDate().toLocalDateTime().toLocalDate().isEqual(LocalDate.now().plusDays(1))) {
                        String ancType = anc.getAbortionType(); // ANC1, ANC2, etc.
                        String body = "Reminder: Scheduled ANC check-up (" + ancType + ") is due tomorrow.";
                        String redirectPath = "/work-plan/anc/" + ancType.toLowerCase();
                        String appType = "FLW_APP"; // or "ASHAA_APP", based on user type
                        String topic = "All"; // or some user/topic identifier
                        String title = "ANC Reminder";


                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error in sending ANC reminder notifications: {}", e.getMessage());
        }
    }


}
