package com.clm.modules.system.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Data
@NoArgsConstructor
public class MonitorDataDTO {

    private String time;

    private String value;


    public MonitorDataDTO(String time, String value) {
        this.time = time;
        this.value = value;
    }
}
