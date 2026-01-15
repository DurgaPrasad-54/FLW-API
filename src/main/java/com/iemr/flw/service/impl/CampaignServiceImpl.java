package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.CampaignOrs;
import com.iemr.flw.domain.iemr.PulsePolioCampaign;
import com.iemr.flw.dto.iemr.OrsCampaignDTO;
import com.iemr.flw.dto.iemr.OrsCampaignListDTO;
import com.iemr.flw.dto.iemr.PolioCampaignDTO;
import com.iemr.flw.dto.iemr.PolioCampaignListDTO;
import com.iemr.flw.repo.iemr.OrsCampaignRepo;
import com.iemr.flw.repo.iemr.PulsePolioCampaignRepo;
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
import java.util.Collections;
import java.util.List;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private OrsCampaignRepo orsCampaignRepo;

    @Autowired
    private PulsePolioCampaignRepo pulsePolioCampaignRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public List<CampaignOrs> saveOrsCampaign(List<OrsCampaignDTO> orsCampaignDTO, String token) throws IEMRException {
        if (orsCampaignDTO == null || orsCampaignDTO.isEmpty()) {
            return Collections.emptyList(); //
        }

        List<CampaignOrs> campaignOrsRequest = new ArrayList<>();
        Integer userId = jwtUtil.extractUserId(token);
        String userName = jwtUtil.extractUsername(token);

        for (OrsCampaignDTO campaignDTO : orsCampaignDTO) {
            if (campaignDTO.getFields() == null) {
                continue;
            }

            CampaignOrs campaignOrsEntity = new CampaignOrs();
            campaignOrsEntity.setUserId(userId);
            campaignOrsEntity.setCreatedBy(userName);
            campaignOrsEntity.setUpdatedBy(userName);

            try {
                campaignOrsEntity.setNumberOfFamilies(
                        Integer.parseInt(campaignDTO.getFields().getNumberOfFamilies())
                );
            } catch (NumberFormatException e) {
                throw new IEMRException("Invalid number format for families");
            }

            campaignOrsEntity.setCampaignPhotos(campaignDTO.getFields().getCampaignPhotos());
            campaignOrsRequest.add(campaignOrsEntity);
        }

        if (!campaignOrsRequest.isEmpty()) {
            List<CampaignOrs> savedCampaigns = orsCampaignRepo.saveAll(campaignOrsRequest);
            return savedCampaigns;
        }

        return Collections.emptyList();
    }

    @Override
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
                OrsCampaignDTO dto = convertOrsToDTO(campaignOrs);
                orsCampaignDTOSResponse.add(dto);
            }
            page++;
        }while (campaignOrsPage.hasNext());
        return orsCampaignDTOSResponse;
    }


    @Override
    @Transactional
    public List<PulsePolioCampaign> savePolioCampaign(List<PolioCampaignDTO> orsCampaignDTO, String token) throws IEMRException {
        if (orsCampaignDTO == null || orsCampaignDTO.isEmpty()) {
            return Collections.emptyList(); //
        }

        List<PulsePolioCampaign> campaignPolioRequest = new ArrayList<>();
        Integer userId = jwtUtil.extractUserId(token);
        String userName = jwtUtil.extractUsername(token);

        for (PolioCampaignDTO campaignDTO : orsCampaignDTO) {
            if (campaignDTO.getFields() == null) {
                continue;
            }

            PulsePolioCampaign campaignPolioEntity = new PulsePolioCampaign();
            campaignPolioEntity.setUserId(userId);
            campaignPolioEntity.setCreatedBy(userName);
            campaignPolioEntity.setUpdatedBy(userName);

            try {
                campaignPolioEntity.setNumberOfChildren(
                        Integer.parseInt(campaignDTO.getFields().getNumberOfChildren())
                );
            } catch (NumberFormatException e) {
                throw new IEMRException("Invalid number format for families");
            }

            campaignPolioEntity.setCampaignPhotos(campaignDTO.getFields().getCampaignPhotos());
            campaignPolioRequest.add(campaignPolioEntity);
        }

        if (!campaignPolioRequest.isEmpty()) {
            List<PulsePolioCampaign> savedCampaigns = pulsePolioCampaignRepo.saveAll(campaignPolioRequest);
            return savedCampaigns;
        }

        return Collections.emptyList();
    }

    @Override
    public List<PolioCampaignDTO> getPolioCampaign(String token) throws IEMRException {
        Integer userId = jwtUtil.extractUserId(token);
        List<PolioCampaignDTO> polioCampaignDTOSResponse = new ArrayList<>();
        int page=0;
        int pageSize =10;
        Page<PulsePolioCampaign> campaignPolioPage;
        do{
            Pageable pageable  = PageRequest.of(page,pageSize);
            campaignPolioPage = pulsePolioCampaignRepo.findByUserId(userId,pageable);
            for(PulsePolioCampaign campaignOrs:campaignPolioPage.getContent()){
                PolioCampaignDTO dto = convertPolioToDTO(campaignOrs);
                polioCampaignDTOSResponse.add(dto);
            }
            page++;
        }while (campaignPolioPage.hasNext());
        return polioCampaignDTOSResponse;
    }


    private OrsCampaignDTO convertOrsToDTO(CampaignOrs campaign) {
        OrsCampaignDTO dto = new OrsCampaignDTO();
        OrsCampaignListDTO orsCampaignListDTO = new OrsCampaignListDTO();
        orsCampaignListDTO.setCampaignPhotos(campaign.getCampaignPhotos());
        orsCampaignListDTO.setEndDate(campaign.getEndDate());
        orsCampaignListDTO.setStartDate(campaign.getStartDate());
        orsCampaignListDTO.setNumberOfFamilies(String.valueOf(campaign.getNumberOfFamilies()));
        dto.setFields(orsCampaignListDTO);
        return dto;
    }

    private PolioCampaignDTO convertPolioToDTO(PulsePolioCampaign campaign) {
        PolioCampaignDTO dto = new PolioCampaignDTO();
        PolioCampaignListDTO polioCampaignListDTO = new PolioCampaignListDTO();
        polioCampaignListDTO.setCampaignPhotos(campaign.getCampaignPhotos());
        polioCampaignListDTO.setEndDate(campaign.getEndDate());
        polioCampaignListDTO.setStartDate(campaign.getStartDate());
        polioCampaignListDTO.setNumberOfChildren(String.valueOf(campaign.getNumberOfChildren()));
        dto.setFields(polioCampaignListDTO);
        return dto;
    }





}
