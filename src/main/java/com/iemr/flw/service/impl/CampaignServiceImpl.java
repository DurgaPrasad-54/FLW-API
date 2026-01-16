package com.iemr.flw.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.CampaignOrs;
import com.iemr.flw.domain.iemr.FilariasisCampaign;
import com.iemr.flw.domain.iemr.PulsePolioCampaign;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.repo.iemr.FilariasisCampaignRepo;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private OrsCampaignRepo orsCampaignRepo;

    @Autowired
    private PulsePolioCampaignRepo pulsePolioCampaignRepo;

    @Autowired
    private FilariasisCampaignRepo filariasisCampaignRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public List<CampaignOrs> saveOrsCampaign(List<OrsCampaignDTO> orsCampaignDTO, String token) throws IEMRException, JsonProcessingException {
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
            campaignOrsEntity.setStartDate(campaignDTO.getFields().getStartDate());
            campaignOrsEntity.setEndDate(campaignDTO.getFields().getEndDate());

            try {
                String familiesStr = campaignDTO.getFields().getNumberOfFamilies();
                if (familiesStr != null && !familiesStr.trim().isEmpty()) {
                    double familiesDouble = Double.parseDouble(familiesStr);
                    campaignOrsEntity.setNumberOfFamilies((int) familiesDouble);
                } else {
                    campaignOrsEntity.setNumberOfFamilies(0); // default 0
                }
            } catch (NumberFormatException e) {
                throw new IEMRException("Invalid number format for families: " + campaignDTO.getFields().getNumberOfFamilies());
            }


            List<String> photos = campaignDTO.getFields().getCampaignPhotos();
            String photosStr = (photos != null && !photos.isEmpty())
                    ? String.join(",", photos)
                    : null;
            campaignOrsEntity.setCampaignPhotos(photosStr);


            campaignOrsRequest.add(campaignOrsEntity);
        }

        if (!campaignOrsRequest.isEmpty()) {
            List<CampaignOrs> savedCampaigns = orsCampaignRepo.saveAll(campaignOrsRequest);
            return savedCampaigns;
        }

        return Collections.emptyList();
    }

    @Override
    public List<OrsCampaignResponseDTO> getOrsCampaign(String token) throws IEMRException {
        Integer userId = jwtUtil.extractUserId(token);
        List<OrsCampaignResponseDTO> orsCampaignDTOSResponse = new ArrayList<>();
        int page = 0;
        int pageSize = 10;
        Page<CampaignOrs> campaignOrsPage;
        do {
            Pageable pageable = PageRequest.of(page, pageSize);
            campaignOrsPage = orsCampaignRepo.findByUserId(userId, pageable);
            for (CampaignOrs campaignOrs : campaignOrsPage.getContent()) {
                OrsCampaignResponseDTO dto = convertOrsToDTO(campaignOrs);
                orsCampaignDTOSResponse.add(dto);
            }
            page++;
        } while (campaignOrsPage.hasNext());
        return orsCampaignDTOSResponse;
    }


    @Override
    @Transactional
    public List<PulsePolioCampaign> savePolioCampaign(List<PolioCampaignDTO> polioCampaignDTOs, String token)
            throws IEMRException, JsonProcessingException {

        if (polioCampaignDTOs == null || polioCampaignDTOs.isEmpty()) {
            throw new IEMRException("Campaign data is required");
        }

        List<PulsePolioCampaign> campaignPolioRequest = new ArrayList<>();
        Integer userId = jwtUtil.extractUserId(token);
        String userName = jwtUtil.extractUsername(token);

        for (PolioCampaignDTO campaignDTO : polioCampaignDTOs) {
            if (campaignDTO.getFields() == null) {
                continue;
            }

            PulsePolioCampaign campaignPolioEntity = new PulsePolioCampaign();
            campaignPolioEntity.setUserId(userId);
            campaignPolioEntity.setCreatedBy(userName);
            campaignPolioEntity.setUpdatedBy(userName);

            // Set start and end dates
            campaignPolioEntity.setStartDate(campaignDTO.getFields().getStartDate());
            campaignPolioEntity.setEndDate(campaignDTO.getFields().getEndDate());

            // Parse number of children
            try {
                String childrenStr = campaignDTO.getFields().getNumberOfChildren();
                if (childrenStr != null && !childrenStr.trim().isEmpty()) {
                    try {
                        // parse as double first, then cast to int
                        double childrenDouble = Double.parseDouble(childrenStr);
                        campaignPolioEntity.setNumberOfChildren((int) childrenDouble);
                    } catch (NumberFormatException e) {
                        campaignPolioEntity.setNumberOfChildren(0); // default 0 if invalid
                    }
                } else {
                    campaignPolioEntity.setNumberOfChildren(0);
                }
            } catch (NumberFormatException e) {
                throw new IEMRException("Invalid number format for children: " + e.getMessage());
            }

            // Convert photos to base64 JSON array

            List<String> photosList = campaignDTO.getFields().getCampaignPhotos();
            String photosStr = (photosList != null && !photosList.isEmpty())
                    ? String.join(",", photosList)
                    : null;

            campaignPolioEntity.setCampaignPhotos(photosStr);


            campaignPolioRequest.add(campaignPolioEntity);
        }

        if (!campaignPolioRequest.isEmpty()) {
            List<PulsePolioCampaign> savedCampaigns = pulsePolioCampaignRepo.saveAll(campaignPolioRequest);
            return savedCampaigns;
        }

        throw new IEMRException("No valid campaign data to save");
    }


    @Override
    @Transactional
    public List<FilariasisCampaign> saveFilariasisCampaign(List<FilariasisCampaignDTO> filariasisCampaignDTOS, String token) throws IEMRException {

        if (filariasisCampaignDTOS == null || filariasisCampaignDTOS.isEmpty()) {
            throw new IEMRException("Campaign data is required");
        }

        List<FilariasisCampaign> campaignPolioRequest = new ArrayList<>();
        Integer userId = jwtUtil.extractUserId(token);
        String userName = jwtUtil.extractUsername(token);

        for (FilariasisCampaignDTO campaignDTO : filariasisCampaignDTOS) {
            if (campaignDTO.getFields() == null) {
                continue;
            }

            FilariasisCampaign filariasisCampaign = new FilariasisCampaign();
            filariasisCampaign.setUserId(userId);
            filariasisCampaign.setCreatedBy(userName);
            filariasisCampaign.setUpdatedBy(userName);

            // Set start and end dates
            filariasisCampaign.setStartDate(campaignDTO.getFields().getStartDate());
            filariasisCampaign.setEndDate(campaignDTO.getFields().getEndDate());

            // Parse number of children
            try {
                String numberOfFamilies = campaignDTO.getFields().getNumberOfFamilies();
                String numberOfIndividuals = campaignDTO.getFields().getNumberOfIndividuals();
                if (numberOfFamilies != null && !numberOfFamilies.trim().isEmpty()) {
                    try {
                        // parse as double first, then cast to int
                        double noDouble = Double.parseDouble(numberOfFamilies);
                        filariasisCampaign.setNumberOfFamilies((int) noDouble);
                    } catch (NumberFormatException e) {
                        filariasisCampaign.setNumberOfFamilies(0); // default 0 if invalid
                    }
                } else {
                    filariasisCampaign.setNumberOfFamilies(0);
                }

                if (numberOfIndividuals != null && !numberOfIndividuals.trim().isEmpty()) {
                    try {
                        // parse as double first, then cast to int
                        double noDouble = Double.parseDouble(numberOfIndividuals);
                        filariasisCampaign.setNumberOfFamilies((int) noDouble);
                    } catch (NumberFormatException e) {
                        filariasisCampaign.setNumberOfIndividuals(0); // default 0 if invalid
                    }
                } else {
                    filariasisCampaign.setNumberOfIndividuals(0);
                }
            } catch (NumberFormatException e) {
                throw new IEMRException("Invalid number format for children: " + e.getMessage());
            }

            // Convert photos to base64 JSON array

            List<String> photosList = campaignDTO.getFields().getMdaPhotos();
            String photosStr = (photosList != null && !photosList.isEmpty())
                    ? String.join(",", photosList)
                    : null;

            filariasisCampaign.setCampaignPhotos(photosStr);


            campaignPolioRequest.add(filariasisCampaign);
        }

        if (!campaignPolioRequest.isEmpty()) {
            List<FilariasisCampaign> savedCampaigns = filariasisCampaignRepo.saveAll(campaignPolioRequest);
            return savedCampaigns;
        }

        throw new IEMRException("No valid campaign data to save");
    }


    @Override
    public List<PolioCampaignResponseDTO> getPolioCampaign(String token) throws IEMRException {
        Integer userId = jwtUtil.extractUserId(token);
        List<PolioCampaignResponseDTO> polioCampaignDTOSResponse = new ArrayList<>();
        int page = 0;
        int pageSize = 10;
        Page<PulsePolioCampaign> campaignPolioPage;
        do {
            Pageable pageable = PageRequest.of(page, pageSize);
            campaignPolioPage = pulsePolioCampaignRepo.findByUserId(userId, pageable);
            for (PulsePolioCampaign campaignOrs : campaignPolioPage.getContent()) {
                PolioCampaignResponseDTO dto = convertPolioToDTO(campaignOrs);
                polioCampaignDTOSResponse.add(dto);
            }
            page++;
        } while (campaignPolioPage.hasNext());
        return polioCampaignDTOSResponse;
    }

    @Override
    public List<FilariasisResponseDTO> getAllFilariasisCampaign(String token) throws IEMRException {
        Integer userId = jwtUtil.extractUserId(token);
        List<FilariasisResponseDTO> filariasisResponseDTOS = new ArrayList<>();
        int page = 0;
        int pageSize = 10;
        Page<FilariasisCampaign> campaignPolioPage;
        do {
            Pageable pageable = PageRequest.of(page, pageSize);
            campaignPolioPage = filariasisCampaignRepo.findByUserId(userId, pageable);
            for (FilariasisCampaign campaignOrs : campaignPolioPage.getContent()) {
                FilariasisResponseDTO dto = convertFilariasisDTO(campaignOrs);
                filariasisResponseDTOS.add(dto);
            }
            page++;
        } while (campaignPolioPage.hasNext());
        return filariasisResponseDTOS;
    }


    private OrsCampaignResponseDTO convertOrsToDTO(CampaignOrs campaign) {
        OrsCampaignResponseDTO dto = new OrsCampaignResponseDTO();
        OrsCampaignListResponseDTO orsCampaignListDTO = new OrsCampaignListResponseDTO();
        if (campaign.getCampaignPhotos() != null) {
            List<String> photosList = parseBase64Json(campaign.getCampaignPhotos());
            orsCampaignListDTO.setCampaignPhotos(photosList); // ✅ Now List<String> matches
        }        orsCampaignListDTO.setEndDate(campaign.getEndDate());

        orsCampaignListDTO.setStartDate(campaign.getStartDate());
        orsCampaignListDTO.setNumberOfFamilies(String.valueOf(campaign.getNumberOfFamilies()));
        dto.setFields(orsCampaignListDTO);
        return dto;
    }

    private PolioCampaignResponseDTO convertPolioToDTO(PulsePolioCampaign campaign) {
        PolioCampaignResponseDTO dto = new PolioCampaignResponseDTO();
        PolioCampaignListResponseDTO polioCampaignListDTO = new PolioCampaignListResponseDTO();
        if (campaign.getCampaignPhotos() != null) {
            List<String> photosList = parseBase64Json(campaign.getCampaignPhotos());
            polioCampaignListDTO.setCampaignPhotos(photosList); // ✅ Now List<String> matches
        }
        polioCampaignListDTO.setEndDate(campaign.getEndDate());
        polioCampaignListDTO.setStartDate(campaign.getStartDate());
        polioCampaignListDTO.setNumberOfChildren(String.valueOf(campaign.getNumberOfChildren()));
        dto.setFields(polioCampaignListDTO);
        return dto;
    }

    private FilariasisResponseDTO convertFilariasisDTO(FilariasisCampaign campaign) {
        FilariasisResponseDTO dto = new FilariasisResponseDTO();
        FilariasisCampaignListResponseDTO filariasisCampaignListDTO = new FilariasisCampaignListResponseDTO();
        if (campaign.getCampaignPhotos() != null) {
            List<String> photosList = parseBase64Json(campaign.getCampaignPhotos());
            filariasisCampaignListDTO.setMdaPhotos(photosList); // ✅ Now List<String> matches
        }
        filariasisCampaignListDTO.setEndDate(campaign.getEndDate());
        filariasisCampaignListDTO.setStartDate(campaign.getStartDate());
        filariasisCampaignListDTO.setNumberOfFamilies(String.valueOf(campaign.getNumberOfFamilies()));
        filariasisCampaignListDTO.setNumberOfIndividuals(String.valueOf(campaign.getNumberOfIndividuals()));
        dto.setFields(filariasisCampaignListDTO);
        return dto;
    }
    private List<String> parseBase64Json(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }


    private String convertPhotosToBase64Json(List<String> photos) throws IEMRException {
        try {
            List<String> cleanedBase64Images = new ArrayList<>();

            for (String photo : photos) {
                if (photo != null && !photo.trim().isEmpty()) {

                    // remove data:image/...;base64, if present
                    String cleanBase64 = photo.contains(",")
                            ? photo.substring(photo.indexOf(",") + 1)
                            : photo;

                    cleanedBase64Images.add(cleanBase64);
                }
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(cleanedBase64Images);

        } catch (Exception e) {
            throw new IEMRException("Error processing base64 photos: " + e.getMessage());
        }
    }

}

