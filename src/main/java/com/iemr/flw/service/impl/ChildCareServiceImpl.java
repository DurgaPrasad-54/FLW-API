package com.iemr.flw.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.ChildCareService;
import com.iemr.flw.utils.JwtUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChildCareServiceImpl implements ChildCareService {

    private final Logger logger = LoggerFactory.getLogger(ChildCareServiceImpl.class);

    @Autowired
    private HbycRepo hbycRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private HbncVisitRepo hbncVisitRepo;


    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;

    @Autowired
    private ChildVaccinationRepo childVaccinationRepo;

    @Autowired
    private SamVisitRepository samVisitRepository;

    @Autowired
    private VaccineRepo vaccineRepo;

    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private JwtUtil jwtUtil;



    @Override
    public String registerHBYC(List<HbycRequestDTO> hbycDTOs) {
        try {

            List<HbycChildVisit> hbycList = new ArrayList<>();
            hbycDTOs.forEach(it -> {
                HbycDTO hbycDTO = it.getFields();
                hbycDTO.setVisit_date(it.getVisitDate());
                HbycChildVisit hbyc =
                        hbycRepo.findByBeneficiaryIdAndVisit_day(it.getBeneficiaryId(), hbycDTO.getVisit_day());

                if (hbyc != null) {
                    Long id = hbyc.getId();
                    modelMapper.map(it, hbycDTO);
                    hbyc.setId(id);
                    hbyc.setUserId(userRepo.getUserIdByName(it.getUserName()));
                    hbyc.setCreated_by(it.getUserName());
                } else {
                    hbyc = new HbycChildVisit();
                    modelMapper.map(it, hbycDTO);
                    hbyc.setId(null);
                    hbyc.setUserId(userRepo.getUserIdByName(it.getUserName()));
                    hbyc.setCreated_by(it.getUserName());
                    hbyc.setBeneficiaryId(it.getBeneficiaryId());
                    hbyc.setVisit_date(it.getVisitDate());
                    hbyc.setHousehold_id(it.getHouseHoldId());
                    hbyc.setVisit_day(hbycDTO.getVisit_day());
                    hbyc.setHbyc_due_date(hbycDTO.getDue_date());
                    hbyc.setBaby_weight(hbycDTO.getBaby_weight());
                    hbyc.setPlace_of_death(hbycDTO.getPlace_of_death());
                    hbyc.setReason_for_death(hbycDTO.getReason_for_death());
                    hbyc.setOther_place_of_death(hbycDTO.getOther_place_of_death());
                    hbyc.setIs_child_sick(convertBollen(hbycDTO.getIs_child_sick()));

                    hbyc.setIs_baby_alive(convertBollen(hbycDTO.getIs_baby_alive()));
                    hbyc.setDate_of_death(hbycDTO.getDate_of_death());
                    hbyc.setExclusive_breastfeeding(convertBollen(hbycDTO.getExclusive_breastfeeding()));
                    hbyc.setMother_counseled_ebf(convertBollen(hbycDTO.getMother_counseled_ebf()));
                    hbyc.setComplementary_feeding(convertBollen(hbycDTO.getComplementary_feeding()));
                    hbyc.setMother_counseled_cf(convertBollen((hbycDTO.getMother_counseled_cf())));
                    hbyc.setWeight_recorded(convertBollen(hbycDTO.getWeight_recorded()));
                    hbyc.setDevelopmental_delay(convertBollen(hbycDTO.getDevelopmental_delay()));
                    hbyc.setMeasles_vaccine(convertBollen(hbycDTO.getMeasles_vaccine()));
                    hbyc.setVitamin_a(convertBollen(hbycDTO.getVitamin_a()));
                    hbyc.setOrs_available(convertBollen(hbycDTO.getOrs_available()));
                    hbyc.setIfa_available(convertBollen(hbycDTO.getIfa_available()));
                    hbyc.setOrs_given(convertBollen(hbycDTO.getOrs_given()));
                    hbyc.setIfa_given(convertBollen(hbycDTO.getIfa_given()));
                    hbyc.setHandwash_counseling(convertBollen(hbycDTO.getHandwash_counseling()));
                    hbyc.setParenting_counseling(convertBollen(hbycDTO.getParenting_counseling()));
                    hbyc.setFamily_planning_counseling(convertBollen(hbycDTO.getFamily_planning_counseling()));
                    hbyc.setDiarrhoea_episode(convertBollen(hbycDTO.getDiarrhoea_episode()));
                    hbyc.setBreathing_difficulty(convertBollen(hbycDTO.getBreathing_difficulty()));
                    hbyc.setTemperature(hbycDTO.getTemperature_check());
                    hbyc.setMcp_card_images(hbycDTO.getMcp_card_images());

                }
                hbycList.add(hbyc);
            });
            hbycRepo.saveAll(hbycList);
            checkAndAddHbyncIncentives(hbycList);

            return "no of hbyc details saved: " + hbycDTOs.size();
        } catch (Exception e) {
            logger.info("error while saving hbyc details: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<HbycVisitResponseDTO> getHbycRecords(GetBenRequestHandler dto) {
        List<HbycVisitResponseDTO> result = new ArrayList<>();

        try {
            List<HbycChildVisit> hbycChildVisits = hbycRepo.findByUserId(dto.getAshaId());

            for (HbycChildVisit hbycChildVisit : hbycChildVisits) {
                HbycVisitResponseDTO hbycRequestDTO = new HbycVisitResponseDTO();
                hbycRequestDTO.setId(hbycChildVisit.getId());
                hbycRequestDTO.setBeneficiaryId(hbycChildVisit.getBeneficiaryId());
                hbycRequestDTO.setHouseHoldId(hbycChildVisit.getHousehold_id());
                hbycRequestDTO.setVisitDate(hbycChildVisit.getVisit_date());

                // Prepare dynamic fields map
                Map<String, Object> fields = new HashMap<>();

                addIfValid(fields, "visit_day", hbycChildVisit.getVisit_day());
                addIfValid(fields, "due_date", hbycChildVisit.getHbyc_due_date());
                addIfValid(fields, "visit_date", hbycChildVisit.getVisit_date());
                addIfValid(fields, "is_baby_alive", convert(hbycChildVisit.getIs_baby_alive()));
                addIfValid(fields, "date_of_death", hbycChildVisit.getDate_of_death());
                addIfValid(fields, "reason_for_death", hbycChildVisit.getReason_for_death());
                addIfValid(fields, "place_of_death", hbycChildVisit.getPlace_of_death());
                addIfValid(fields, "other_place_of_death", hbycChildVisit.getOther_place_of_death());
                addIfValid(fields, "baby_weight", hbycChildVisit.getBaby_weight());
                addIfValid(fields, "is_child_sick", convert(hbycChildVisit.getIs_child_sick()));
                addIfValid(fields, "exclusive_breastfeeding", convert(hbycChildVisit.getExclusive_breastfeeding()));
                addIfValid(fields, "mother_counseled_ebf", convert(hbycChildVisit.getMother_counseled_ebf()));
//                addIfValid(fields, "complementary_feeding", convert(hbycChildVisit.getComplementary_feeding()));
                addIfValid(fields, "mother_counseled_cf", convert(hbycChildVisit.getMother_counseled_cf()));
                addIfValid(fields, "weight_recorded", convert(hbycChildVisit.getWeight_recorded()));
                addIfValid(fields, "developmental_delay", convert(hbycChildVisit.getDevelopmental_delay()));
                addIfValid(fields, "measles_vaccine", convert(hbycChildVisit.getMeasles_vaccine()));
                addIfValid(fields, "vitamin_a", convert(hbycChildVisit.getVitamin_a()));
                addIfValid(fields, "ors_available", convert(hbycChildVisit.getOrs_available()));
                addIfValid(fields, "ifa_available", convert(hbycChildVisit.getIfa_available()));
                addIfValid(fields, "ors_given", convert(hbycChildVisit.getOrs_given()));
                addIfValid(fields, "ifa_given", convert(hbycChildVisit.getIfa_given()));
                addIfValid(fields, "handwash_counseling", convert(hbycChildVisit.getHandwash_counseling()));
                addIfValid(fields, "parenting_counseling", convert(hbycChildVisit.getParenting_counseling()));
                addIfValid(fields, "family_planning_counseling", convert(hbycChildVisit.getFamily_planning_counseling()));
                addIfValid(fields, "diarrhoea_episode", convert(hbycChildVisit.getDiarrhoea_episode()));
//                addIfValid(fields, "breathing_difficulty", convert(hbycChildVisit.getBreathing_difficulty()));
                addIfValid(fields, "temperature_check", hbycChildVisit.getTemperature());
                addIfValid(fields, "mcp_card_images", hbycChildVisit.getMcp_card_images());


                // Set fields map in DTO
                hbycRequestDTO.setFields(fields);
                result.add(hbycRequestDTO);
            }

        } catch (Exception e) {
            logger.error("Error while fetching HBYC details: ", e);
        }

        return result;

    }

    @Override
    public List<HbncVisitResponseDTO> getHBNCDetails(GetBenRequestHandler dto) {
        List<HbncVisitResponseDTO> result = new ArrayList<>();
        try {
            List<HbncVisit> hbncVisits = hbncVisitRepo.findByAshaId(dto.getAshaId());

            for (HbncVisit visit : hbncVisits) {
                HbncVisitResponseDTO responseDTO = new HbncVisitResponseDTO();
                responseDTO.setId(visit.getId());
                responseDTO.setBeneficiaryId(visit.getBeneficiaryId()); // Update with actual value
                responseDTO.setHouseHoldId(visit.getHouseHoldId());   // Update with actual value
                responseDTO.setVisitDate(visit.getVisit_date().split(" ")[0]); // Format visit.getVisitDate()

                // Convert all fields to Map
                Map<String, Object> fields = new HashMap<>();
                addIfValid(fields, "visit_day", visit.getVisit_day());
                addIfValid(fields, "due_date", visit.getDue_date());
                addIfValid(fields, "is_baby_alive", convert(visit.getIs_baby_alive()));
                addIfValid(fields, "date_of_death", visit.getDate_of_death());
                addIfValid(fields, "reason_for_death", visit.getReasonForDeath());
                addIfValid(fields, "place_of_death", visit.getPlace_of_death());
                addIfValid(fields, "other_place_of_death", visit.getOther_place_of_death());
                addIfValid(fields, "baby_weight", visit.getBaby_weight());
                addIfValid(fields, "urine_passed", convert(visit.getUrine_passed()));
                addIfValid(fields, "stool_passed", convert(visit.getStool_passed()));
                addIfValid(fields, "diarrhoea", convert(visit.getDiarrhoea()));
                addIfValid(fields, "vomiting", convert(visit.getVomiting()));
                addIfValid(fields, "convulsions", convert(visit.getConvulsions()));
                addIfValid(fields, "activity", visit.getActivity());
                addIfValid(fields, "sucking", visit.getSucking());
                addIfValid(fields, "breathing", visit.getBreathing());
                addIfValid(fields, "chest_indrawing", visit.getChest_indrawing());
                addIfValid(fields, "temperature", visit.getTemperature());
                addIfValid(fields, "jaundice", convert(visit.getJaundice()));
                addIfValid(fields, "umbilical_stump", visit.getUmbilical_stump());
                addIfValid(fields, "discharged_from_sncu", convert(visit.getDischarged_from_sncu()));
                addIfValid(fields, "discharge_summary_upload", visit.getDischarge_summary_upload());

                // Add more fields as required

                responseDTO.setFields(fields);
                result.add(responseDTO);
            }

        } catch (Exception e) {
            logger.error("Error in getHBNCDetails: ", e);
        }
        return result;
    }

    private void addIfValid(Map<String, Object> map, String key, Object value) {
        if (value == null) return;

        if (value instanceof String && ((String) value).trim().isEmpty()) return;

        map.put(key, value);
    }

    private String convert(Boolean value) {
        if (value == null) return null;
        return value ? "Yes" : "No";
    }

    private Boolean convertBollen(String value) {
        if (value.equals("Yes")) {
            return true;
        } else {
            return false;
        }
    }

    private String convert(Object value) {
        if (value == null) return null;
        if (value instanceof Boolean) return (Boolean) value ? "Yes" : "No";
        return value.toString();
    }


    private String convert(String value) {
        return "true".equalsIgnoreCase(value) ? "Yes" : "No";
    }


    @Override
    public String saveHBNCDetails(List<HbncRequestDTO> hbncRequestDTOs) {
        try {
            List<HbncVisit> hbncList = new ArrayList<>();

            hbncRequestDTOs.forEach(it -> {
                if (it.getVisitDate() != null) {

                    HbncVisitDTO hbncVisitDTO = it.getFields();
                    hbncVisitDTO.setVisit_date(it.getVisitDate());
                    HbncVisit hbncVisit = hbncVisitRepo.findByBeneficiaryIdAndVisit_day(it.getBeneficiaryId(), hbncVisitDTO.getVisit_day());

                    if (hbncVisit != null) {
                        Long id = hbncVisit.getId();
                        modelMapper.map(hbncVisitDTO, hbncVisit);
                        hbncVisit.setId(id);
                    } else {
                        hbncVisit = new HbncVisit();
                        modelMapper.map(hbncVisitDTO, hbncVisit);
                        hbncVisit.setBeneficiaryId(it.getBeneficiaryId());
                        hbncVisit.setAshaId(userRepo.getUserIdByName(it.getUserName()));
                        hbncVisit.setCreatedBy(it.getUserName());
                        hbncVisit.setHouseHoldId(it.getHouseHoldId());
                        hbncVisit.setId(null);
                    }
                    hbncList.add(hbncVisit);
                }
            });


            hbncVisitRepo.saveAll(hbncList);
            checkAndAddHbncIncentives(hbncList);


            logger.info("HBNC details saved");
            return "no of hbnc details saved: " + (hbncList.size());
        } catch (Exception e) {
            logger.info("Saving HBNC details failed with error : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ChildVaccinationDTO> getChildVaccinationDetails(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<ChildVaccination> vaccinationDetails = childVaccinationRepo.getChildVaccinationDetails(user, dto.getFromDate(), dto.getToDate());

            List<ChildVaccinationDTO> result = new ArrayList<>();
            vaccinationDetails.forEach(childVaccination -> {
                ChildVaccinationDTO vaccinationDTO = mapper.convertValue(childVaccination, ChildVaccinationDTO.class);
                BigInteger benId = beneficiaryRepo.getBenIdFromRegID(childVaccination.getBeneficiaryRegId());
                vaccinationDTO.setBeneficiaryId(benId.longValue());

                result.add(vaccinationDTO);
            });
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String saveChildVaccinationDetails(List<ChildVaccinationDTO> childVaccinationDTOs) {
        try {
            List<ChildVaccination> vaccinationList = new ArrayList<>();
            childVaccinationDTOs.forEach(it -> {
                Long benRegId = beneficiaryRepo.getRegIDFromBenId(it.getBeneficiaryId());

                ChildVaccination vaccination =
                        childVaccinationRepo.findChildVaccinationByBeneficiaryRegIdAndCreatedDateAndVaccineName(benRegId, it.getCreatedDate(), it.getVaccineName());

                if (vaccination != null) {
                    long id = vaccination.getId();
                    modelMapper.map(it, vaccination);
                    vaccination.setId(id);
                } else {
                    vaccination = new ChildVaccination();
                    modelMapper.map(it, vaccination);
                    vaccination.setBeneficiaryRegId(benRegId);
                    vaccination.setProcessed("N");
                }
                vaccinationList.add(vaccination);
            });
            childVaccinationRepo.saveAll(vaccinationList);
            checkAndAddIncentives(vaccinationList);
            logger.info("Child Vaccination details saved");
            return "No of child vaccination details saved: " + vaccinationList.size();
        } catch (Exception e) {
            logger.info("Saving Child Vaccination details failed with error : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<VaccineDTO> getAllChildVaccines(String category) {
        try {
            List<Vaccine> vaccines = vaccineRepo.getAllByCategory(category);

            List<VaccineDTO> result = new ArrayList<>();
            vaccines.forEach(vaccine -> {
                VaccineDTO vaccineDTO = mapper.convertValue(vaccine, VaccineDTO.class);
                switch (vaccineDTO.getImmunizationService()) {
                    case "Birth Dose Vaccines":
                        vaccineDTO.setImmunizationService("BIRTH");
                        break;
                    case "6 Weeks Vaccines":
                        vaccineDTO.setImmunizationService("WEEK_6");
                        break;
                    case "10 Weeks Vaccines":
                        vaccineDTO.setImmunizationService("WEEK_10");
                        break;
                    case "14 Weeks Vaccines":
                        vaccineDTO.setImmunizationService("WEEK_14");
                        break;
                    case "9-12 Months":
                        vaccineDTO.setImmunizationService("MONTH_9_12");
                        break;
                    case "16-24 Months Vaccines":
                        vaccineDTO.setImmunizationService("MONTH_16_24");
                        break;
                    case "5-6 Years Vaccine":
                        vaccineDTO.setImmunizationService("YEAR_5_6");
                        break;
                    case "10 Years Vaccine":
                        vaccineDTO.setImmunizationService("YEAR_10");
                        break;
                    case "16 Years Vaccine":
                        vaccineDTO.setImmunizationService("YEAR_16");
                        break;
                    default:
                        vaccineDTO.setImmunizationService("CATCH_UP");
                        break;
                }
                result.add(vaccineDTO);
            });
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String saveSamDetails(List<SamDTO> samRequest) {
        try {
            List<SamVisit> vaccinationList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            samRequest.forEach(samDTO -> {
                SamVisit samVisits = new  SamVisit();
                modelMapper.map(samVisits,samDTO.getFields());
                samVisits.setBeneficiaryId(samDTO.getBeneficiaryId());
                samVisits.setHouseholdId(samDTO.getHouseHoldId());
                samVisits.setVisitDate(LocalDate.parse(samDTO.getVisitDate(),formatter));
                samVisits.setUserId(userRepo.getUserIdByName(jwtUtil.getUserNameFromStorage()));
                samVisits.setCreatedBy(jwtUtil.getUserNameFromStorage());
                vaccinationList.add(samVisits);
            });

            samVisitRepository.saveAll(vaccinationList);


            return "Saved " + samRequest.size() + " SAM visit records successfully";
        }catch (Exception e){
            logger.error(e.getMessage());
        }

        return "Fail";

        // Handle MultipartFile → Base64 JSON
//            MultipartFile file = dto.getViewDischargeDocs();
//            if (file != null && !file.isEmpty()) {
//                try {
//                    List<String> base64List = Arrays.asList(file).stream()
//                            .filter(f -> !f.isEmpty())
//                            .map(f -> {
//                                try {
//                                    return Base64.getEncoder().encodeToString(f.getBytes());
//                                } catch (IOException e) {
//                                    throw new RuntimeException("Error converting file to Base64", e);
//                                }
//                            })
//                            .collect(Collectors.toList());
//
//                    String jsonBase64 = mapper.writeValueAsString(base64List);
//                    entity.setViewDischargeDocs(jsonBase64);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return "Failed to process file: " + file.getOriginalFilename();
//                }
//            }



    }

    @Override
    public List<SamVisitResponseDTO> getSamVisitsByBeneficiary(GetBenRequestHandler request) {

        List<SamVisit> entities = samVisitRepository.findByUserId(request.getAshaId());

        return entities.stream().map(entity -> {
            SamVisitResponseDTO dto = new SamVisitResponseDTO();
            dto.setBeneficiaryId(entity.getBeneficiaryId());
            dto.setVisitDate(entity.getVisitDate());
            dto.setVisitLabel(entity.getVisitLabel());
            dto.setMuac(entity.getMuac());
            dto.setWeightForHeightStatus(entity.getWeightForHeightStatus());
            dto.setIsChildReferredNrc(entity.getIsChildReferredNrc());
            dto.setIsChildAdmittedNrc(entity.getIsChildAdmittedNrc());
            dto.setNrcAdmissionDate(entity.getNrcAdmissionDate());
            dto.setIsChildDischargedNrc(entity.getIsChildDischargedNrc());
            dto.setNrcDischargeDate(entity.getNrcDischargeDate());
            dto.setFollowUpVisitDate(entity.getFollowUpVisitDate());
            dto.setSamStatus(entity.getSamStatus());
            dto.setDischargeSummary(entity.getDischargeSummary());

            // Convert Base64 JSON string to List<String>
            try {
                if (entity.getViewDischargeDocs() != null) {
                    List<String> base64List = mapper.readValue(
                            entity.getViewDischargeDocs(), new TypeReference<List<String>>() {});
                    dto.setViewDischargeDocs(base64List);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return dto;
        }).collect(Collectors.toList());
    }


    private void checkAndAddHbyncIncentives(List<HbycChildVisit> hbycList) {
        hbycList.forEach(hbyc -> {
            IncentiveActivity hbyncVisitActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("HBYC_QUARTERLY_VISITS", GroupName.CHILD_HEALTH.getDisplayName());

            IncentiveActivity hbyncOrsPacketActivityAM =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("ORS_DISTRIBUTION", GroupName.CHILD_HEALTH.getDisplayName());

            IncentiveActivity hbyncOrsPacketActivityCH =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("ORS_DISTRIBUTION", GroupName.ACTIVITY.getDisplayName());
            if (hbyncVisitActivity != null) {
                if (hbyc.getVisit_date() != null) {
                    createIncentiveRecordforHbyncVisit(hbyc, hbyc.getBeneficiaryId(), hbyncVisitActivity, hbyc.getCreated_by());

                }

            }
            if (hbyncOrsPacketActivityAM != null) {
                if (hbyc.getOrs_given()) {
                    createIncentiveRecordforHbyncOrsDistribution(hbyc, hbyc.getBeneficiaryId(), hbyncOrsPacketActivityAM, hbyc.getCreated_by());

                }

            }

            if (hbyncOrsPacketActivityCH != null) {
                if (hbyc.getOrs_given()) {
                    createIncentiveRecordforHbyncOrsDistribution(hbyc, hbyc.getBeneficiaryId(), hbyncOrsPacketActivityCH, hbyc.getCreated_by());

                }

            }

        });


    }

    private void checkAndAddHbncIncentives(List<HbncVisit> hbncVisits) {
        hbncVisits.forEach(hbncVisit -> {
            boolean isVisitDone = List.of("1st Day", "3rd Day", "7th Day", "42nd Day")
                    .stream()
                    .allMatch(hbncVisits::contains);

            GroupName.setIsCh(false);
            Long benId = hbncVisit.getBeneficiaryId();
            if (hbncVisit.getVisit_day().equals("42nd Day")) {
                IncentiveActivity visitActivityAM = incentivesRepo.findIncentiveMasterByNameAndGroup("HBNC_0_42_DAYS", GroupName.CHILD_HEALTH.getDisplayName());
                IncentiveActivity visitActivityCH = incentivesRepo.findIncentiveMasterByNameAndGroup("HBNC_0_42_DAYS", GroupName.ACTIVITY.getDisplayName());

                createIncentiveRecordforHbncVisit(hbncVisit, benId, visitActivityAM, "HBNC_0_42_DAYS");
                createIncentiveRecordforHbncVisit(hbncVisit, benId, visitActivityCH, "HBNC_0_42_DAYS_CH");

            }
            logger.info("getDischarged_from_sncu" + hbncVisit.getDischarged_from_sncu());

            if (hbncVisit.getVisit_day().equals("7th Day") && hbncVisit.getDischarged_from_sncu() && hbncVisit.getBaby_weight() < 2.5) {
                IncentiveActivity babyDisChargeSNCUAActivity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("SNCU_LBW_FOLLOWUP", GroupName.CHILD_HEALTH.getDisplayName());

                createIncentiveRecordforHbncVisit(hbncVisit, benId, babyDisChargeSNCUAActivity, "SNCU_LBW_FOLLOWUP");

            }
            logger.info("getIs_baby_alive" + hbncVisit.getIs_baby_alive());
            if (!hbncVisit.getIs_baby_alive()) {
                IncentiveActivity isChildDeathActivity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("CHILD_DEATH_REPORTING", GroupName.CHILD_HEALTH.getDisplayName());

                createIncentiveRecordforHbncVisit(hbncVisit, benId, isChildDeathActivity, "CHILD_DEATH_REPORTING");
            }


        });


    }

    private void checkAndAddIncentives(List<ChildVaccination> vaccinationList) {


        vaccinationList.forEach(vaccination -> {
            Long benId = beneficiaryRepo.getBenIdFromRegID(vaccination.getBeneficiaryRegId()).longValue();
            Integer userId = userRepo.getUserIdByName(vaccination.getCreatedBy());
            Integer immunizationServiceId = getImmunizationServiceIdForVaccine(vaccination.getVaccineId().shortValue());
            if (immunizationServiceId < 6) {
                IncentiveActivity immunizationActivityAM =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FULL_IMMUNIZATION_0_1", GroupName.IMMUNIZATION.getDisplayName());
                IncentiveActivity immunizationActivityCH =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FULL_IMMUNIZATION_0_1", GroupName.ACTIVITY.getDisplayName());
                if (immunizationActivityAM != null && childVaccinationRepo.getFirstYearVaccineCountForBenId(vaccination.getBeneficiaryRegId())
                        .equals(childVaccinationRepo.getFirstYearVaccineCount())) {
                    createIncentiveRecord(vaccination, benId, userId, immunizationActivityAM);
                }

                if (immunizationActivityCH != null && childVaccinationRepo.getFirstYearVaccineCountForBenId(vaccination.getBeneficiaryRegId())
                        .equals(childVaccinationRepo.getFirstYearVaccineCount())) {
                    createIncentiveRecord(vaccination, benId, userId, immunizationActivityCH);
                }
            } else if (immunizationServiceId == 7) {
                IncentiveActivity immunizationActivity2AM =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("COMPLETE_IMMUNIZATION_1_2", GroupName.IMMUNIZATION.getDisplayName());
                IncentiveActivity immunizationActivity2CH =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("COMPLETE_IMMUNIZATION_1_2", GroupName.ACTIVITY.getDisplayName());
                if (immunizationActivity2AM != null && childVaccinationRepo.getSecondYearVaccineCountForBenId(vaccination.getBeneficiaryRegId())
                        .equals(childVaccinationRepo.getSecondYearVaccineCount())) {
                    createIncentiveRecord(vaccination, benId, userId, immunizationActivity2AM);
                }
                if (immunizationActivity2CH != null && childVaccinationRepo.getSecondYearVaccineCountForBenId(vaccination.getBeneficiaryRegId())
                        .equals(childVaccinationRepo.getSecondYearVaccineCount())) {
                    createIncentiveRecord(vaccination, benId, userId, immunizationActivity2CH);
                }
            }
            IncentiveActivity immunizationActivity5AM =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("DPT_IMMUNIZATION_5_YEARS", GroupName.IMMUNIZATION.getDisplayName());
            if (immunizationActivity5AM != null && childVaccinationRepo.checkDptVaccinatedUser(vaccination.getBeneficiaryRegId()) == 1) {
                createIncentiveRecord(vaccination, benId, userId, immunizationActivity5AM);
            }

            IncentiveActivity immunizationActivity5CH =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("DPT_IMMUNIZATION_5_YEARS", GroupName.ACTIVITY.getDisplayName());
            if (immunizationActivity5CH != null && childVaccinationRepo.checkDptVaccinatedUser(vaccination.getBeneficiaryRegId()) == 1) {
                createIncentiveRecord(vaccination, benId, userId, immunizationActivity5CH);
            }
        });
    }

    private void createIncentiveRecord(ChildVaccination vaccination, Long benId, Integer userId, IncentiveActivity immunizationActivity) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(immunizationActivity.getId(), vaccination.getCreatedDate(), benId);

        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(immunizationActivity.getId());
            record.setCreatedDate(vaccination.getCreatedDate());
            record.setCreatedBy(vaccination.getCreatedBy());
            record.setStartDate(vaccination.getCreatedDate());
            record.setEndDate(vaccination.getCreatedDate());
            record.setUpdatedDate(vaccination.getCreatedDate());
            record.setUpdatedBy(vaccination.getCreatedBy());
            record.setBenId(benId);
            record.setAshaId(userId);
            record.setAmount(Long.valueOf(immunizationActivity.getRate()));
            recordRepo.save(record);
        }
    }

    private void createIncentiveRecordforHbncVisit(HbncVisit hbncVisit, Long benId, IncentiveActivity immunizationActivity, String activityName) {
        logger.info("RecordIncentive" + activityName);

        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(immunizationActivity.getId(), hbncVisit.getCreatedDate(), benId);

        if (record == null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            // Convert to LocalDate
            LocalDate localDate = LocalDate.parse(hbncVisit.getVisit_date(), formatter);

            // Convert LocalDate to Timestamp (00:00:00 by default)
            Timestamp visitDate = Timestamp.valueOf(localDate.atStartOfDay());
            record = new IncentiveActivityRecord();
            record.setActivityId(immunizationActivity.getId());
            record.setCreatedDate(visitDate);
            record.setCreatedBy(hbncVisit.getCreatedBy());
            record.setStartDate(visitDate);
            record.setEndDate(visitDate);
            record.setUpdatedDate(visitDate);
            record.setUpdatedBy(hbncVisit.getCreatedBy());
            record.setBenId(benId);
            record.setAshaId(hbncVisit.getAshaId());
            record.setAmount(Long.valueOf(immunizationActivity.getRate()));
            recordRepo.save(record);
        }
    }

    private void createIncentiveRecordforHbyncVisit(HbycChildVisit data, Long benId, IncentiveActivity immunizationActivity, String createdBy) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Convert to LocalDate
        LocalDate localDate = LocalDate.parse(data.getVisit_date(), formatter);

        // Convert LocalDate to Timestamp (00:00:00 by default)
        Timestamp visitDate = Timestamp.valueOf(localDate.atStartOfDay());
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(immunizationActivity.getId(), visitDate, benId);


        if (record == null) {

            record = new IncentiveActivityRecord();
            record.setActivityId(immunizationActivity.getId());
            record.setCreatedDate(visitDate);
            record.setCreatedBy(createdBy);
            record.setStartDate(visitDate);
            record.setEndDate(visitDate);
            record.setUpdatedDate(visitDate);
            record.setUpdatedBy(createdBy);
            record.setBenId(benId);
            record.setAshaId(beneficiaryRepo.getUserIDByUserName(createdBy));
            record.setAmount(Long.valueOf(immunizationActivity.getRate()));
            recordRepo.save(record);
        }
    }


    private void createIncentiveRecordforHbyncOrsDistribution(HbycChildVisit data, Long benId, IncentiveActivity immunizationActivity, String createdBy) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Convert to LocalDate
        LocalDate localDate = LocalDate.parse(data.getVisit_date(), formatter);

        // Convert LocalDate to Timestamp (00:00:00 by default)
        Timestamp visitDate = Timestamp.valueOf(localDate.atStartOfDay());
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(immunizationActivity.getId(), visitDate, benId);


        if (record == null) {

            record = new IncentiveActivityRecord();
            record.setActivityId(immunizationActivity.getId());
            record.setCreatedDate(visitDate);
            record.setCreatedBy(createdBy);
            record.setStartDate(visitDate);
            record.setEndDate(visitDate);
            record.setUpdatedDate(visitDate);
            record.setUpdatedBy(createdBy);
            record.setBenId(benId);
            record.setAshaId(beneficiaryRepo.getUserIDByUserName(createdBy));
            record.setAmount(Long.valueOf(immunizationActivity.getRate()));
            recordRepo.save(record);
        }
    }

    private Integer getImmunizationServiceIdForVaccine(Short vaccineId) {
        return vaccineRepo.getImmunizationServiceIdByVaccineId(vaccineId);
    }

    public void getTomorrowImmunizationReminders(Integer userID) {

    }

}
