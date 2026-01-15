package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.CampaignOrs;
import com.iemr.flw.dto.iemr.OrsCampaignDTO;
import com.iemr.flw.dto.iemr.OrsCampaignListDTO;
import com.iemr.flw.repo.iemr.OrsCampaignRepo;
import com.iemr.flw.service.CampaignService;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.exception.IEMRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private OrsCampaignRepo orsCampaignRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public Object saveOrsCampaign(List<OrsCampaignDTO> orsCampaignDTO, String token) throws IEMRException {
        if (orsCampaignDTO == null || orsCampaignDTO.isEmpty()) {
            return null;
        }

        List<CampaignOrs> campaignOrsRequest = new ArrayList<>();
        Integer userId = jwtUtil.extractUserId(token);
        String userName = jwtUtil.extractUsername(token);

        for (OrsCampaignDTO campaignDTO : orsCampaignDTO) {
            if (campaignDTO.getFields() == null) {
                continue; // or throw exception based on your requirements
            }

            CampaignOrs campaignOrsEntity = new CampaignOrs(); // Create new entity for each DTO
            campaignOrsEntity.setUserId(userId);
            campaignOrsEntity.setCreatedBy(userName);
            campaignOrsEntity.setUpdatedBy(userName);

            try {
                campaignOrsEntity.setNumberOfFamilies(
                        Integer.parseInt(campaignDTO.getFields().getNumberOfFamilies())
                );
            } catch (NumberFormatException e) {
                // Handle invalid number format - log or throw exception
                throw new IEMRException("Invalid number format for families");
            }

            campaignOrsEntity.setCampaignPhotos(campaignDTO.getFields().getCampaignPhotos());
            campaignOrsRequest.add(campaignOrsEntity);
        }

        if (!campaignOrsRequest.isEmpty()) {
            orsCampaignRepo.saveAll(campaignOrsRequest);
            return campaignOrsRequest;
        }

        return null;
    }

    @Override
    @Transactional
    public List<OrsCampaignDTO> getOrsCampaign(String token) throws IEMRException {
        Integer userId = jwtUtil.extractUserId(token);
        List<OrsCampaignDTO> orsCampaignDTOSResponse = new ArrayList<>();
        int page=0;
        int pageSize =10;
        Page<CampaignOrs> campaignOrsPage;
        do{
            Pageable pageable  = PageRequest.of(page,pageSize);
            campaignOrsPage = orsCampaignRepo.findByUserId(userId,pageable);
            for(CampaignOrs campaignOrs:campaignOrsPage.getContent()){
                OrsCampaignDTO dto = convertToDTO(campaignOrs);
                orsCampaignDTOSResponse.add(dto);
            }
            page++;
        }while (campaignOrsPage.hasNext());
        return orsCampaignDTOSResponse;
    }
    private OrsCampaignDTO convertToDTO(CampaignOrs campaign) {
        OrsCampaignDTO dto = new OrsCampaignDTO();
        OrsCampaignListDTO orsCampaignListDTO = new OrsCampaignListDTO();
        orsCampaignListDTO.setCampaignPhotos(campaign.getCampaignPhotos());
        orsCampaignListDTO.setEndDate(campaign.getEndDate());
        orsCampaignListDTO.setStartDate(campaign.getStartDate());
        orsCampaignListDTO.setNumberOfFamilies(String.valueOf(campaign.getNumberOfFamilies()));
        dto.setFields(orsCampaignListDTO);
        return dto;
    }
}
