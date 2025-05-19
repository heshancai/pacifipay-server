package com.starchain.common.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author
 * @date 2025-01-04
 * @Description
 */
@Data
@Schema(title = "分页对象-PageDto", description = "分页对象")
public class PageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "开始时间", example = "yyyy-MM-dd HH:mm:ss")
    private String beginTime;

    @Schema(description = "结束时间", example = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

}
