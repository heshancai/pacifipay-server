package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.config.PacificPayConfig;
import com.starchain.dao.UserWalletMapper;
import com.starchain.entity.UserWallet;
import com.starchain.entity.dto.UserWalletDto;
import com.starchain.service.IUserWalletService;
import com.starchain.service.IdWorker;
import com.starchain.service.WalletApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
@Service
@Slf4j
public class UserWalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements IUserWalletService {

    @Value("${wallet.base-url}")
    private String walletBaseUrl;

    @Value("${wallet.base-url}")
    private String walletServerPublicKey;

    @Autowired
    private PacificPayConfig pacificPayConfig;

    @Autowired
    private WalletApiService walletApiService;
    @Autowired
    protected IdWorker idWorker;

    @Override
    public UserWallet findWalletAddress(UserWalletDto userWalletDto) {
        // 钱包地址判断
        LambdaQueryWrapper<UserWallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWallet::getUserId, userWalletDto.getUserId());
        queryWrapper.eq(UserWallet::getChannelId, userWalletDto.getChannelId());
        queryWrapper.eq(UserWallet::getUsdtNetwork, userWalletDto.getUsdtNetwork());
        queryWrapper.eq(UserWallet::getLockStatus, 1);
        UserWallet userWallet = this.getOne(queryWrapper);
        if (userWallet == null) {
            //获取批次号 唯一性
            String bathNo = String.valueOf(idWorker.nextId());
            // 调用钱包服务生成钱包地址
            String newAddress = walletApiService.createNewAddress(userWalletDto.getCoinId(), 1, bathNo);
            userWallet = UserWallet.builder()
                    .userId(userWalletDto.getUserId())
                    .coinId(userWalletDto.getCoinId())
                    .channelId(userWalletDto.getChannelId())
                    .usdtNetwork(userWalletDto.getUsdtNetwork())
                    .address(newAddress)
                    .usdtNetwork(userWalletDto.getUsdtNetwork())
                    .lockStatus(1)
                    .build();
            this.save(userWallet);
            return userWallet;
        }
        return userWallet;
    }


}
