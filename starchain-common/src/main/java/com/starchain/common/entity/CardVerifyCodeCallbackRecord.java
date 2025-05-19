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
@TableName("card_verify_code_callback_record")
@Schema(description = "卡验证码通知记录实体")
public class CardVerifyCodeCallbackRecord {

    @Schema(description = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "通知ID", example = "NOTIFY123456789")
    private String notifyId;

    @Schema(description = "卡类型", example = "CARD_TYPE_001")
    private String cardCode;

    @Schema(description = "业务类型", example = "VERIFY_CODE")
    private String businessType;

    @Schema(description = "卡ID", example = "1234567890")
    private String cardId;

    @Schema(description = "验证码", example = "123456")
    private String verifyCode;

    @Schema(description = "创建时间", example = "2023-10-15 12:30:45")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2023-10-15 12:30:45")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}