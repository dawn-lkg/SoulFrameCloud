package com.clm.api.notice;

import com.clm.api.notice.domain.MessageDTO;
import com.clm.api.notice.domain.MessageVO;
import com.clm.common.core.constants.ServiceNameConstants;
import com.clm.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@FeignClient(contextId = "remoteMessageService", value = ServiceNameConstants.NOTICE_SERVICE)
public interface RemoteMessageService {


    /**
     * 发送消息
     */
    @PostMapping("/notice/message/send")
    Result<MessageVO> sendMessage(@RequestBody MessageDTO messageDTO);

}
