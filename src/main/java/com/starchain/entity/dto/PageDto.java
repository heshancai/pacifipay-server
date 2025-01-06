package com.starchain.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author
 * @date 2025-01-04
 * @Description
 */
@Data
@ApiModel(value = "分页对象-PageDto", description = "分页对象")
public class PageDto implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(name = "current", value = "当前页", example = "1", required = false)
    private Integer pageNum = 1;

    @ApiModelProperty(name = "size", value = "每页数量", example = "1", required = false)
    private Integer pageSize = 10;

    @ApiModelProperty(name = "beginTime", value = "开始时间", example = "yyyy-MM-dd HH:mm:ss", required = false)
    private String beginTime;

    @ApiModelProperty(name = "endTime", value = "结束时间", example = "yyyy-MM-dd HH:mm:ss", required = false)
    private String endTime;

}
