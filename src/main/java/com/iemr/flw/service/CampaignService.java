package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.OrsCampaignDTO;
import com.iemr.flw.dto.iemr.OrsCampaignResponseDTO;
import com.iemr.flw.dto.iemr.PolioCampaignDTO;
import com.iemr.flw.utils.exception.IEMRException;

import java.util.List;

public interface CampaignService {
    Object saveOrsCampaign(List<OrsCampaignDTO> orsCampaignDTO, String token) throws IEMRException;
    Object savePolioCampaign(List<PolioCampaignDTO> polioCampaignDTOS, String token) throws IEMRException;
    List<OrsCampaignResponseDTO> getOrsCampaign(String token) throws IEMRException;

    List<PolioCampaignDTO> getPolioCampaign(String token) throws IEMRException;
}
