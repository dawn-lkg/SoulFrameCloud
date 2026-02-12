package com.clm.common.log.service;

import com.clm.api.system.RemoteLogService;
import com.clm.api.system.domain.OperLogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Service
public class AsyncLogService {

    @Autowired
    private RemoteLogService remoteLogService;

    @Async
    public void insertOperLogAsync(OperLogDTO operLog) {
        remoteLogService.insertOperLog(operLog);
    }
}
