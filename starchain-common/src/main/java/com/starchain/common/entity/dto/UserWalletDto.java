package com.starchain.common.entity.dto;
import com.starchain.common.entity.UserWallet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserWalletDto extends UserWallet {

}
