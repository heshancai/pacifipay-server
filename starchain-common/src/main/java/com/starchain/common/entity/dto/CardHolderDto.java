package com.starchain.common.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
@Data
@ApiModel(value = "持卡Dto", description = "持卡Dto")
public class CardHolderDto extends PageDto implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id; // 主键ID

    @ApiModelProperty(value = "卡类型编码,银行卡端提供", example = "TpyMDN6")
    private String cardCode; // 卡类型编码

    @ApiModelProperty(value = "用户Id 第三方传递过来")
    private Long userId;

    @ApiModelProperty(value = "商家Id", example = "100001")
    private Long businessId;

    @ApiModelProperty(value = "商户持卡人ID", example = "商户端生成")
    private String merchantCardHolderId;

    @ApiModelProperty(value = "名", example = "John")
    private String firstName;

    @ApiModelProperty(value = "姓", example = "Doe")
    private String lastName;

    @ApiModelProperty(value = "手机号国家", example = "例：+86；+65")
    private String phoneCountry;

    @ApiModelProperty(value = "手机号", example = "13800138000")
    private String phoneNumber;

    @ApiModelProperty(value = "证件号", example = "110101199003071234")
    private String idAccount;

    @ApiModelProperty(value = "邮箱", example = "john.doe@example.com")
    private String email;

    @ApiModelProperty(value = "地址", example = "123 Main St, City")
    private String address;

    @ApiModelProperty(value = "性别", example = "固定值，0：男；1：女；")
    private String gender;

    @ApiModelProperty(value = "生日", example = "1990-03-07")
    private String birthday; // 生日

    @ApiModelProperty(value = "太平洋的持卡人唯一值-银行卡端返回", example = "TPY123456")
    private String tpyshCardHolderId;

    @ApiModelProperty(value = "创建状态 1 创建成功 0创建中 2 创建失败")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; // 卡创建时间

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
