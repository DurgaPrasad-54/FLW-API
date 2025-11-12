package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.MdaDistributionData;
import com.iemr.flw.dto.iemr.MdaFormFieldsDTO;
import com.iemr.flw.dto.iemr.MdaFormSubmissionRequest;
import com.iemr.flw.dto.iemr.MdaFormSubmissionResponse;
import com.iemr.flw.repo.iemr.MdaFormSubmissionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MdaFormSubmissionServiceImpl implements MdaFormSubmissionService {

    @Autowired
    private final MdaFormSubmissionRepository repository;

    @Override
    public String saveFormData(List<MdaFormSubmissionRequest> requests) {
        try {
            List<MdaDistributionData> entities = new ArrayList<>();
            for (MdaFormSubmissionRequest req : requests) {
                MdaDistributionData data = MdaDistributionData.builder()
                        .beneficiaryId(req.getBeneficiaryId())
                        .formId(req.getFormId())
                        .houseHoldId(req.getHouseHoldId())
                        .userName(req.getUserName())
                        .visitDate(req.getVisitDate())
                        .mdaDistributionDate(req.getFields().getMda_distribution_date())
                        .isMedicineDistributed(req.getFields().getIs_medicine_distributed())
                        .createdBy(req.getUserName())
                        .modifiedBy(req.getUserName())
                        .build();
                entities.add(data);
            }
            repository.saveAll(entities);
            return "MDA form data saved successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while saving form data: " + e.getMessage();
        }
    }

    @Override
    public List<MdaFormSubmissionResponse> getFormDataByUserName(String userName) {
        List<MdaDistributionData> records = repository.findByUserName(userName);

        List<MdaFormSubmissionResponse> responses = new ArrayList<>();

        for (MdaDistributionData entity : records) {
            try {
                MdaFormFieldsDTO fieldsDTO = MdaFormFieldsDTO.builder()
                        .mda_distribution_date(entity.getMdaDistributionDate())
                        .is_medicine_distributed(entity.getIsMedicineDistributed())
                        .build();

                responses.add(MdaFormSubmissionResponse.builder()
                        .formId(entity.getFormId())
                        .houseHoldId(entity.getHouseHoldId())
                        .beneficiaryId(entity.getBeneficiaryId())
                        .visitDate(entity.getVisitDate())
                        .createdBy(entity.getCreatedBy())
                        .createdAt(entity.getCreatedDate())
                        .fields(fieldsDTO)
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return responses;
    }
}
