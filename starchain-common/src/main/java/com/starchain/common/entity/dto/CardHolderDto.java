package com.starchain.common.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
@Data
@Schema(title = "持卡Dto", description = "持卡Dto")
public class CardHolderDto extends PageDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID", example = "1")
    private Long id; // 主键ID

    @Schema(description = "卡类型编码,银行卡端提供", example = "TpyMDN6")
    private String cardCode; // 卡类型编码

    @Schema(description = "用户Id 第三方传递过来")
    private Long userId;

    @Schema(description = "商家Id", example = "100001")
    private Long businessId;

    @Schema(description = "商户持卡人ID", example = "商户端生成")
    private String merchantCardHolderId;

    @Schema(description = "名", example = "John")
    private String firstName;

    @Schema(description = "姓", example = "Doe")
    private String lastName;

    @Schema(description = "手机号国家", example = "例：+86；+65")
    private String phoneCountry;

    @Schema(description = "手机号", example = "13800138000")
    private String phoneNumber;

    @Schema(description = "证件号", example = "110101199003071234")
    private String idAccount;

    @Schema(description = "邮箱", example = "john.doe@example.com")
    private String email;

    @Schema(description = "地址", example = "123 Main St, City")
    private String address;

    @Schema(description = "性别", example = "固定值，0：男；1：女；")
    private String gender;

    @Schema(description = "生日", example = "1990-03-07")
    private String birthday; // 生日

    @Schema(description = "太平洋的持卡人唯一值-银行卡端返回", example = "TPY123456")
    private String tpyshCardHolderId;

    @Schema(description = "创建状态 1 创建成功 0创建中 2 创建失败")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; // 卡创建时间

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
