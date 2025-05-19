package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-02-06
 * @Description
 */
@Data
@TableName("card_cancel_record")
@Schema(title = "CardCancelRecord", description = "销卡申请记录表")
public class CardCancelRecord {

    @Schema(description = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "卡ID，收款人证件号码", example = "CARD123456")
    private String cardId;

    @Schema(description = "卡类型编码", example = "TpyMDN6")
    private String cardCode;

    @Schema(description = "用户Id，第三方传递过来", example = "123456")
    private Long userId;

    @Schema(description = "商家Id", example = "987654")
    private Long businessId;

    @Schema(description = "记录创建状态 0 创建中 1 创建成功 2 创建失败", example = "1")
    private Integer createStatus;

    @Schema(description = "创建时间", example = "2025-02-06 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-02-06 10:30:00")
    private LocalDateTime updateTime;

    @Schema(description = "完成时间", example = "2023-10-15 12:30:45")
    private LocalDateTime finishTime;
}