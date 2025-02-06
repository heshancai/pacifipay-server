package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-02-06
 * @Description
 */
@ApiModel(value = "CardCancelRecord", description = "销卡申请记录表")
@Data
@TableName("card_cancel_record")  // 表名修改为 card_cancel_record
public class CardCancelRecord {

    @ApiModelProperty(value = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "卡ID，收款人证件号码")
    private String cardId;

    @ApiModelProperty(value = "卡类型编码")
    private String cardCode;

    @ApiModelProperty(value = "用户Id，第三方传递过来")
    private Long userId;

    @ApiModelProperty(value = "商家Id")
    private Long businessId;

    @ApiModelProperty(value = "记录创建状态 0 创建中 1 创建成功 2 创建失败")
    private Integer createStatus;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
