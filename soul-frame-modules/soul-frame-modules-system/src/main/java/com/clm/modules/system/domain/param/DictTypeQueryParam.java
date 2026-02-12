package com.clm.modules.system.domain.param;

import com.clm.common.core.domain.BasePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypeQueryParam extends BasePageParam {
    /**
     * 字典主键
     */
    @Schema(description = "字典主键")
    private Long dictId;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称")
    private String dictName;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型")
    private String dictType;

    /**
     * 状态（0正常 1停用）
     */
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
