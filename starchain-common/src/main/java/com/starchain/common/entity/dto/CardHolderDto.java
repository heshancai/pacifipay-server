package com.starchain.common.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.starchain.common.validatedInterface.AddCardHolder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
@Data
@Schema(description = "持卡人信息传输对象")
public class CardHolderDto extends PageDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID", example = "1")
    private Long id; // 主键ID

    @Schema(description = "卡类型编码", example = "TPY_MDN6", hidden = true)
    private String cardCode;


    @Schema(description = "用户ID", example = "123456", required = true)
    //  groups = {CardHolderDto.class} 可以自定义 只有在Controller 设置了该groups 才会校验
    @NotNull(message = "userId 不能为空", groups = {AddCardHolder.class})
    private Long userId;

    @Schema(description = "商家Id", example = "100001", required = true)
    @NotNull(message = "商家Id 不能为空", groups = {AddCardHolder.class})
    private Long businessId;

    /**
     * 仅隐藏 Swagger 文档 @Schema(hidden = true)
     * 完全不让前端看到字段（JSON 不返回） @JsonIgnore
     * 允许前端传，但不能返回 @JsonProperty(access = Access.WRITE_ONLY)
     */
    @Schema(description = "商户持卡人ID", example = "商户端生成", hidden = true)
    private String merchantCardHolderId;

    @Schema(description = "持卡人名字", example = "张", required = true)
    @NotNull(message = "firstName 不能为空", groups = {AddCardHolder.class})
    private String firstName;

    @Schema(description = "持卡人姓氏", example = "三", required = true)
    @NotNull(message = "lastName 不能为空", groups = {AddCardHolder.class})
    private String lastName;

    @Schema(description = "手机号国家代码", example = "+86", required = true)
    @NotNull(message = "phoneCountry 不能为空", groups = {AddCardHolder.class})
    private String phoneCountry;

    @Schema(description = "手机号", example = "13800138000", required = true)
    @NotNull(message = "phoneNumber 不能为空")
    private String phoneNumber;

    @Schema(description = "证件号码", example = "110101199003072316", required = true)
    @NotNull(message = "证件号码不能为空", groups = {AddCardHolder.class})
    private String idAccount;

    @Schema(description = "邮箱地址", example = "zhangsan@example.com", required = true)
    @NotNull(message = "email 不能为空", groups = {AddCardHolder.class})
    @Email(message = "邮箱格式不正确") // 使用 @Email 注解进行邮箱格式校验
    private String email;

    @Schema(description = "地址", example = "中国北京市朝阳区XX街道", required = true)
    @NotNull(message = "address 不能为空", groups = {AddCardHolder.class})
    private String address;

    @Schema(description = "性别", example = "男/女", required = true)
    @NotNull(message = "gender 不能为空", groups = {AddCardHolder.class})
    private String gender;


    @Schema(description = "出生日期", example = "1990-01-01", required = true)
    @NotNull(message = "birthday 不能为空", groups = {AddCardHolder.class})
    private String birthday;

    @Schema(description = "太平洋的持卡人唯一值-银行卡端返回", example = "TPY123456", hidden = true)
    private String tpyshCardHolderId;

    @Schema(description = "创建状态 1 创建成功 0创建中 2 创建失败", hidden = true)
    private Integer status;

    @Schema(description = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; // 卡创建时间

    @Schema(description = "更新时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
