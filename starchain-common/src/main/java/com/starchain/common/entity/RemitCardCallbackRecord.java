package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-06
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("remit_card_callback_record")
@Schema(description = "申请汇款卡审核通知记录实体")
public class RemitCardCallbackRecord {

    @Schema(description = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "通知ID", example = "NOTIFY123456789")
    private String notifyId;

    @Schema(description = "汇款类型编码", example = "ELR")
    private String remitCode;

    @Schema(description = "业务类型", example = "REMIT_CARD")
    private String businessType;

    @Schema(description = "汇款卡ID", example = "TPY123456789")
    private String tpyCardId;

    @Schema(description = "汇款卡标识", example = "CARD123456789")
    private String cardId;

    @Schema(description = "状态", example = "SUCCESS")
    private String status;

    @Schema(description = "状态描述", example = "审核成功")
    private String statusDesc;

    @Schema(description = "创建时间", example = "2023-10-15 12:30:45")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2023-10-15 12:30:45")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @Schema(description = "完成时间", example = "2023-10-15 12:30:45")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;
}