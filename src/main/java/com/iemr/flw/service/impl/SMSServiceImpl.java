package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class SMSServiceImpl {
    final Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    @Value("${send-message-url}")
    private String SMS_GATEWAY_URL;

    @Value("${sms-username}")
    private String smsUserName;

    // @Value("${sms-password}")
    private String smsPassword= "]Kt9GAp8}$S*@";

    @Value("${source-address}")
    private String smsSourceAddress;


    public String sendReminderSMS(String mobileNo, String serviceName, LocalDate date) {
        final RestTemplate restTemplate = new RestTemplate();

        String  dltTemplateId = "1007702336787892386";
        String  entityId = "1201161708885589464";

        try {

            String message = "Dear Beneficiary, this is a reminder regarding your upcoming service for "
                    + serviceName
                    + ". Please visit the nearest health facility between "
                    + date
                    + ". Regards PSMRI";
            Map<String, Object> payload = new HashMap<>();
            payload.put("customerId",smsUserName);
            payload.put("destinationAddress", mobileNo);
            payload.put("message", message);
            payload.put("sourceAddress", smsSourceAddress);
            payload.put("messageType", "SERVICE_IMPLICIT");
            payload.put("dltTemplateId", dltTemplateId);
            payload.put("entityId",entityId );
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            logger.info("userName:"+smsUserName+":"+smsPassword);

            String auth = smsUserName + ":" + smsPassword;
            headers.add("Authorization",
                    "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));

            headers.setContentType(MediaType.APPLICATION_JSON);
            logger.info("payload: "+payload);
            logger.info("header: "+headers);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            // Call API
            ResponseEntity<String> response = restTemplate.postForEntity(SMS_GATEWAY_URL, request, String.class);
            logger.info("sms-response:"+response.getBody());
            logger.info("sms-response status:"+response.getStatusCode().value());
            if(response.getStatusCode().value()==200){
                return  serviceName+" Reminder sent successfully on register mobile number";
            }else {
                return "Fail";

            }

        } catch (Exception e) {
            return "Error sending SMS: " + e.getMessage();
        }
    }

}
