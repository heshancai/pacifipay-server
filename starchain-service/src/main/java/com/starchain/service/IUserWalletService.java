package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.UserWallet;
import com.starchain.common.entity.dto.UserWalletDto;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
public interface IUserWalletService extends IService<UserWallet> {

    /**
     * 获取钱包地址
     * @param userWalletDto
     * @return
     */
    UserWallet findWalletAddress(UserWalletDto userWalletDto);
}
