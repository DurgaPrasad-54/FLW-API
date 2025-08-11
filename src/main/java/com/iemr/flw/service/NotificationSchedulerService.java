package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.M_User;
import com.iemr.flw.service.impl.ChildCareServiceImpl;
import com.iemr.flw.service.impl.MaternalHealthServiceImpl;
import com.iemr.flw.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class NotificationSchedulerService {
    @Autowired
    private MaternalHealthServiceImpl maternalHealthService;

    @Autowired
    EmployeeMasterInter employeeMasterInter;

    @Autowired
   private ChildCareServiceImpl childCareService;
    @Autowired
    private CookieUtil cookieUtil;
    @Scheduled(cron = "0 0 9 * * *") // every day at 9 AM
    public void triggerAncRemindersForAllAsha() {
        HttpServletRequest requestHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String jwtTokenFromCookie = cookieUtil.getJwtTokenFromCookie(requestHeader);
        for(M_User m_user: employeeMasterInter.getAllUsers()){
            maternalHealthService.sendAncDueTomorrowNotifications(String.valueOf(m_user.getUserID()));

        }
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void trigerTomorrowImmunizationReminders() {
        for(M_User m_user: employeeMasterInter.getAllUsers()){
            childCareService.getTomorrowImmunizationReminders(m_user.getUserID());

        }
    }

}
