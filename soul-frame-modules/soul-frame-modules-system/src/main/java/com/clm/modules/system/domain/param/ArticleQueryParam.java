package com.clm.modules.system.domain.param;

import com.clm.common.core.domain.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章查询参数
 *
 * @author AI Assistant
 * @date 2024-10-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文章查询参数")
public class ArticleQueryParam extends BaseParam {

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "标签ID")
    private Long tagId;

    @Schema(description = "状态(0:草稿 1:已发布 2:已下架)")
    private Integer status;

    @Schema(description = "是否置顶(0:否 1:是)")
    private Integer isTop;

    @Schema(description = "是否热门(0:否 1:是)")
    private Integer isHot;

    @Schema(description = "是否推荐(0:否 1:是)")
    private Integer isRecommend;

    @Schema(description = "开始时间")
    private String beginTime;

    @Schema(description = "结束时间")
    private String endTime;
}
