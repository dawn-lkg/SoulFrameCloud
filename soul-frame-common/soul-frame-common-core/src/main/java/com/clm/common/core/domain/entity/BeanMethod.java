package com.clm.common.core.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Data
public class BeanMethod {

    @Schema(description = "方法名")
    private String label;

    @Schema(description = "实际值")
    private String value;
}
