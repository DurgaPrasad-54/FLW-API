package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.TBConfirmedCaseDTO;
import com.iemr.flw.dto.iemr.TBConfirmedCase;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TBConfirmedCaseService {

    String save(TBConfirmedCaseDTO tbConfirmedCaseDTO, String token) throws Exception;

    String getByBenId(Long benId, String authorisation) throws Exception;

    String getByUserId(String authorisation) throws Exception;
}
