package com.starchain.controller;

import com.starchain.common.entity.UserWallet;
import com.starchain.common.entity.dto.UserWalletDto;
import com.starchain.common.result.ClientResponse;
import com.starchain.common.result.ResultGenerator;
import com.starchain.service.IUserWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "用户资产相关接口")
public class AssetController {

    @Autowired
    private IUserWalletService userWalletService;

    /**
     * 查询指定币种的钱包地址
     *
     * @param userWalletDto 请求参数，包含用户ID、业务ID和USDT网络类型
     * @return 返回查询到的钱包地址信息
     */
    @PostMapping("/address/")
    @Operation(summary = "查询指定币种的钱包地址", description = "根据传入的用户信息查询指定币种的钱包地址")
    public ClientResponse address(@RequestBody @Parameter(description = "钱包请求参数") UserWalletDto userWalletDto) {
        log.info("查询指定网络类型的USDT 钱包地址,coinName为:{}", userWalletDto.getUsdtNetwork());
        if (userWalletDto.getUsdtNetwork() == null || userWalletDto.getUserId() == null || userWalletDto.getBusinessId() == null) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        // 获取充币地址
        UserWallet wallet = userWalletService.findWalletAddress(userWalletDto);
        return ResultGenerator.genSuccessResult(wallet);
    }
}
