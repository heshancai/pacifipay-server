package com.starchain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.config.PacificPayConfig;
import com.starchain.dao.UserWalletMapper;
import com.starchain.entity.UserWallet;
import com.starchain.entity.dto.UserWalletDto;
import com.starchain.service.IUserWalletService;
import com.starchain.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

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

    @Override
    public UserWallet findWalletAddress(UserWalletDto userWalletDto) {
        // 钱包地址判断
        LambdaQueryWrapper<UserWallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWallet::getUserId, userWalletDto.getUserId());
        queryWrapper.eq(UserWallet::getChannelId, userWalletDto.getChannelId());
        queryWrapper.eq(UserWallet::getUsdtNetwork, userWalletDto.getUsdtNetwork());
        UserWallet userWallet = this.getOne(queryWrapper);
        if (userWallet == null) {
            // 调用钱包服务生成钱包地址
            userWallet = createAddress(userWalletDto.getUserId(), userWalletDto.getChannelId(), userWalletDto.getUsdtNetwork());
        }
        return userWallet;
    }

    /**
     * 创建地址
     *
     * @param memberId
     * @param
     * @param
     */
    private UserWallet createAddress(Long memberId, Long channelId, Integer usdtNetwork) {

        JSONObject param = new JSONObject();
        param.put("memberId", memberId);
        param.put("channelId", channelId);
        param.put("usdtNetwork", usdtNetwork);

        try {
            // 获取钱包地址
            String depositAddress = HttpUtils.doPostWalletAddress(walletBaseUrl, JSON.toJSONString(param), walletServerPublicKey, pacificPayConfig.getPrivateKey());
//            log.info("depositAddress:{}", depositAddress);
//            if (StringUtils.isEmpty(depositAddress)) {
//                log.info("用户[{}]获取到币种[{}]的地址为null", memberId, coin.getCoinName());
//                memberWallet.setAddress("");
//            } else {
//                log.info("用户[{}]获取到币种[{}]的地址为[{}],保存信息到钱包表", memberId, coin.getCoinName(), depositAddress);
//                memberWallet.setAddress(depositAddress);
//            }
//            memberWallet.setUpdateTime(new Date());
//            memberWalletService.updateById(memberWallet);
//            MemberWallet memberWallet1 = memberWalletService.selectById(memberWallet.getId());
//            log.info("更新后的memberWallet1:{}", memberWallet1);
        } catch (Exception e) {
            log.error("创建地址失败   ", e);
        }
        return null;
    }

}
