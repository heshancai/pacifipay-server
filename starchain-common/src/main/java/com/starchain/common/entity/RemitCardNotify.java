package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("remit_card_notify")
@Schema(description = "汇款卡回调记录")
public class RemitCardNotify {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "通知ID", example = "NOTIFY12345")
    private String notifyId;

    @Schema(description = "汇款类型编码", example = "UQR")
    private String remitCode;

    @Schema(description = "业务类型", example = "RemitCard")
    private String businessType;

    @Schema(description = "汇款卡ID", example = "TPY12345")
    private String tpyCardId;

    @Schema(description = "汇款卡标识", example = "CARD12345")
    private String cardId;

    @Schema(description = "状态", example = "SUCCESS")
    private String status;

    @Schema(description = "状态描述", example = "Remit Card Success")
    private String statusDesc;

    @Schema(description = "创建时间", example = "2025-01-01T12:00:00")
    private LocalDateTime createTime;

    @Schema(description = "是否成功状态", example = "CardOpen success")
    private Integer successConfirm;

    @Schema(description = "确认次数", example = "CardOpen success")
    private Integer confirmCount;

    @Schema(description = "更新时间", example = "2025-01-01T12:00:00")
    private LocalDateTime updateTime;
}