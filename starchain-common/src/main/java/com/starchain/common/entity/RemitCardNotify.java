package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("remit_card_notify")
@ApiModel(value = "RemitCardNotify", description = "汇款卡回调记录")
public class RemitCardNotify {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "通知ID", example = "NOTIFY12345")
    private String notifyId;

    @ApiModelProperty(value = "汇款类型编码", example = "UQR")
    private String remitCode;

    @ApiModelProperty(value = "业务类型", example = "RemitCard")
    private String businessType;

    @ApiModelProperty(value = "汇款卡ID", example = "TPY12345")
    private String tpyCardId;

    @ApiModelProperty(value = "汇款卡标识", example = "CARD12345")
    private String cardId;

    @ApiModelProperty(value = "状态", example = "SUCCESS")
    private String status;

    @ApiModelProperty(value = "状态描述", example = "Remit Card Success")
    private String statusDesc;

    @ApiModelProperty(value = "创建时间", example = "2025-01-01T12:00:00")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "是否成功状描述", example = "CardOpen success")
    private Integer successConfirm;

    @ApiModelProperty(value = "确认次数", example = "CardOpen success")
    private Integer confirmCount;

    @ApiModelProperty(value = "更新时间", example = "2025-01-01T12:00:00")
    private LocalDateTime updateTime;
}

