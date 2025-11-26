package com.iemr.flw.service.impl;

import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.time.LocalDate;  
import java.time.format.DateTimeFormatter;  
  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
import org.springframework.beans.factory.annotation.Value;  
import org.springframework.stereotype.Service;  
import org.springframework.web.multipart.MultipartFile;

import com.iemr.flw.dto.crashlogs.CrashLogRequest;
import com.iemr.flw.service.CrashLogService;
import com.iemr.flw.utils.exception.IEMRException;  

@Service
public class CrashLogServiceImpl implements CrashLogService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Value("${crash.logs.base.path}")
    private String crashLogsBasePath;

    @Override
    public String saveCrashLog(CrashLogRequest request, Integer userId, MultipartFile file) throws IEMRException {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new IEMRException("Uploaded file is empty");
            }

            // Extract date for folder organization
            LocalDate date = LocalDate.now();
            String dateFolder = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

            // Create directory structure: crashlogs/2025-11-25/
            File dateDir = new File(crashLogsBasePath, dateFolder);
            if (!dateDir.exists()) {
                boolean created = dateDir.mkdirs();
                if (!created) {
                    throw new IEMRException("Failed to create directory: " + dateDir.getAbsolutePath());
                }
            }

            // Generate filename: userId_appVersion_deviceId_timestamp.txt
            String filename = String.format("%d_%s_%s_%s.txt",
                    userId,
                    sanitizeFilename(request.getAppVersion()),
                    sanitizeFilename(request.getDeviceId()),
                    request.getTimestamp());

            File crashLogFile = new File(dateDir, filename);

            // Write file content
            try (FileOutputStream fos = new FileOutputStream(crashLogFile)) {
                fos.write(file.getBytes());
            }

            String relativePath = dateFolder + "/" + filename;
            logger.info("Crash log saved successfully: " + relativePath);

            return relativePath;

        } catch (IOException e) {
            logger.error("Error saving crash log: " + e.getMessage(), e);
            throw new IEMRException("Error saving crash log: " + e.getMessage(), e);
        }
    }

    private String sanitizeFilename(String input) {
        if (input == null) {
            return "unknown";
        }
        return input.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}
