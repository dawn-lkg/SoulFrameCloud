package com.clm.api.notice;

import com.clm.api.notice.domain.NotificationDTO;
import com.clm.api.notice.domain.NotificationVO;
import com.clm.common.core.constants.ServiceNameConstants;
import com.clm.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteNotificationService", value = ServiceNameConstants.NOTICE_SERVICE)
public interface RemoteNotificationService {

    @PostMapping("/notice/notification/send")
    Result<NotificationVO> sendNotification(@RequestBody NotificationDTO notificationDTO);
    
}
