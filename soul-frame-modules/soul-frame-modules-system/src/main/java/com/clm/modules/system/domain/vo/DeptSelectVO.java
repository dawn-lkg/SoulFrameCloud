package com.clm.modules.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Data
@Schema(description = "部门下拉列表")
public class DeptSelectVO {

    @Schema(description = "部门id")
    private Long id;

    @Schema(description = "部门名称")
    private String title;

    @Schema(description = "父部门id")
    private Long parentId;

    @Schema(description = "子列表")
    private List<DeptSelectVO> children;

    @Schema(description = "人员数量")
    private Integer userCount;


}
