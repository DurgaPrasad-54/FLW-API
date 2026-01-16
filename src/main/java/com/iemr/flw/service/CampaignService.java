package com.iemr.flw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iemr.flw.domain.iemr.CampaignOrs;
import com.iemr.flw.domain.iemr.PulsePolioCampaign;
import com.iemr.flw.dto.iemr.OrsCampaignDTO;
import com.iemr.flw.dto.iemr.OrsCampaignResponseDTO;
import com.iemr.flw.dto.iemr.PolioCampaignDTO;
import com.iemr.flw.dto.iemr.PolioCampaignResponseDTO;
import com.iemr.flw.utils.exception.IEMRException;

import java.util.List;

public interface CampaignService {
    List<CampaignOrs> saveOrsCampaign(List<OrsCampaignDTO> orsCampaignDTO, String token) throws IEMRException, JsonProcessingException;
    List<PulsePolioCampaign> savePolioCampaign(List<PolioCampaignDTO> polioCampaignDTOS, String token) throws IEMRException, JsonProcessingException;
    List<OrsCampaignResponseDTO> getOrsCampaign(String token) throws IEMRException;

    List<PolioCampaignResponseDTO> getPolioCampaign(String token) throws IEMRException;
}
