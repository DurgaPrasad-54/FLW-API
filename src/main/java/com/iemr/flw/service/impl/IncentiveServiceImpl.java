package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityLangMapping;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.IncentiveActivityLangMappingRepo;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.service.IncentiveService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class IncentiveServiceImpl implements IncentiveService {

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private IncentiveActivityLangMappingRepo incentiveActivityLangMappingRepo;

    @Autowired
    IncentivesRepo incentivesRepo;

    @Autowired
    IncentiveRecordRepo recordRepo;


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

                    }

                }else {
                    dto.setGroupName(inc.getGroup());

                }

                return dto;
            }).collect(Collectors.toList());

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
                BigInteger benDetailId = beneficiaryRepo.findByBenRegIdFromMapping((beneficiaryRepo.getRegIDFromBenId(entry.getBenId()))).getBenDetailsId();
                RMNCHMBeneficiarydetail rmnchBeneficiaryDetailsRmnch = beneficiaryRepo.findByBeneficiaryDetailsId(benDetailId);
                String beneName = rmnchBeneficiaryDetailsRmnch.getFirstName()+" "+rmnchBeneficiaryDetailsRmnch.getLastName();
                entry.setName(beneName);
            }

            dtos.add(modelMapper.map(entry, IncentiveRecordDTO.class));
        });

        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
        return gson.toJson(dtos);
    }
}
