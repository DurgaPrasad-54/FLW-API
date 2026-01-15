package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.OrsCampaignDTO;
import com.iemr.flw.utils.exception.IEMRException;

import java.util.List;

public interface CampaignService {
    Object saveOrsCampaign(List<OrsCampaignDTO> orsCampaignDTO, String token) throws IEMRException;
    List<OrsCampaignDTO> getOrsCampaign(String token) throws IEMRException;
}
