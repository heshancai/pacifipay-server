package com.starchain.callBack;

import com.alibaba.fastjson2.JSONObject;
import com.starchain.entity.WalletCallbackRecord;
import com.starchain.entity.response.WalletRechargeCallbackResponse;
import com.starchain.enums.WalletSideEnum;
import com.starchain.result.ClientResponse;
import com.starchain.result.ResultGenerator;
import com.starchain.service.IWalletCallbackRecordService;
import com.starchain.service.IWalletCallbackService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-04
 * @Description
 */
@Slf4j
@RequestMapping("/callback")
@RestController
public class WalletCallbackController {


    @Autowired
    private IWalletCallbackRecordService walletCallbackRecordService;


    @Autowired
    private IWalletCallbackService walletCallbackService;


    /**
     * 钱包充值回调
     */
    @ApiOperation(value = "钱包充值回调")
    @PostMapping("/recharge")
    public ClientResponse rechargeCallback(HttpServletRequest request) {
        try {
            // 验签 解密数据
            String realJsonStr = handleMsg(request);
            WalletRechargeCallbackResponse walletRechargeCallbackResponse = JSONObject.parseObject(realJsonStr, WalletRechargeCallbackResponse.class);
            // 数据非空校验
            checkRecharge(walletRechargeCallbackResponse);
            // 生成充币记录
            WalletCallbackRecord walletCallbackRecord = walletCallbackRecordService.checkDepositRecordIsExist(walletRechargeCallbackResponse, WalletSideEnum.DEPOSIT.getKey());
            // 处理充值 并且计算手续费
            walletCallbackService.dealDeposit(walletRechargeCallbackResponse, new BigDecimal(walletRechargeCallbackResponse.getDepositAmount()), walletCallbackRecord);
            return ResultGenerator.genSuccessResult();
        } catch (Exception e) {
            log.warn("wallet rechargeCallBack 异常：", e);
            return ResultGenerator.genFailResult("rechargeCallback 异常");
        }
    }

    /**
     * 参数非空校验
     *
     * @param walletRechargeCallbackResponse
     */
    private void checkRecharge(WalletRechargeCallbackResponse walletRechargeCallbackResponse) {
        Assert.notNull(walletRechargeCallbackResponse, "回调信息为空");
        Assert.hasText(walletRechargeCallbackResponse.getDepositAddress(), "充值地址不能为空");
        Assert.hasText(walletRechargeCallbackResponse.getCurrencySymbol(), "币种标识不能为空");
        Assert.hasText(walletRechargeCallbackResponse.getTxId(), "交易ID不可以为空");
        Assert.hasText(walletRechargeCallbackResponse.getDepositAmount(), "充值金额不能为空");
        Assert.notNull(walletRechargeCallbackResponse.getConfirmTimes(), "确认次数不能为空");
        Assert.hasText(walletRechargeCallbackResponse.getDepositId(), "充值ID不能为空");
        Assert.hasText(walletRechargeCallbackResponse.getNotifyId(), "通知ID不能为空");
    }

    private String handleMsg(HttpServletRequest request) {
        return null;
    }
}
