package com.starchain.controller;

import com.starchain.common.entity.UserWallet;
import com.starchain.common.entity.dto.UserWalletDto;
import com.starchain.common.result.ClientResponse;
import com.starchain.common.result.ResultGenerator;
import com.starchain.service.IUserWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
@RestController
@RequestMapping("/asset")
@Slf4j
@Api("用户资产相关接口")
public class AssetController {


    @Autowired
    private IUserWalletService userWalletService;

    /**
     * 查询指定币种的钱包地址
     *
     * @param
     * @return 币种地址
     */
    @PostMapping("/address/")
    @ApiOperation(value = "查询指定币种的钱包地址", notes = "查询指定币种的钱包地址")
    public ClientResponse address(@RequestBody UserWalletDto userWalletDto) {
        log.info("查询指定网络类型的USDT 钱包地址,coinName为:{}", userWalletDto.getUsdtNetwork());
        if (userWalletDto.getUsdtNetwork() == null || userWalletDto.getUserId() == null || userWalletDto.getBusinessId() == null) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        // 获取充币地址
        UserWallet wallet = userWalletService.findWalletAddress(userWalletDto);
        return ResultGenerator.genSuccessResult(wallet);
    }




}
