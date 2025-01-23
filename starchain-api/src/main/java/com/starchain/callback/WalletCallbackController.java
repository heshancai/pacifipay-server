package com.starchain.callback;

import com.alibaba.fastjson2.JSONObject;
import com.starchain.entity.WalletCallbackRecord;
import com.starchain.entity.response.WalletRechargeCallbackResponse;
import com.starchain.enums.WalletSideEnum;
import com.starchain.result.ClientResponse;
import com.starchain.result.ResultGenerator;
import com.starchain.service.IWalletCallbackRecordService;
import com.starchain.service.IWalletCallbackService;
import com.starchain.service.IWalletCoinConfigService;
import com.starchain.util.AesUtils;
import com.starchain.util.SignUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author
 * @date 2025-01-04
 * @Description
 */
@Slf4j
@RequestMapping("/callback")
@RestController
public class WalletCallbackController {
    @Value("${wallet.contentSecret}")
    private String contentSecret;

    @Value("${wallet.signSecret}")
    private String signSecret;

    @Autowired
    private IWalletCallbackRecordService walletCallbackRecordService;


    @Autowired
    private IWalletCallbackService walletCallbackService;

    @Autowired
    private IWalletCoinConfigService walletCoinConfigService;


    /**
     * 钱包充值回调
     */
    @ApiOperation(value = "钱包充值回调")
    @PostMapping("/recharge")
    public ClientResponse rechargeCallback(HttpServletRequest request) {
        try {
            log.info("开始处理钱包回调信息");
            // 验签 解密数据
            String realJsonStr = handleMsg(request);
            WalletRechargeCallbackResponse walletRechargeCallbackResponse = JSONObject.parseObject(realJsonStr, WalletRechargeCallbackResponse.class);
            // 数据非空校验
            checkRecharge(walletRechargeCallbackResponse);
            // 将接口的币种符号转换为数据库的币种符号
            walletRechargeCallbackResponse.setCurrencySymbol(walletCoinConfigService.toCurrencySymbol(walletRechargeCallbackResponse.getCurrencySymbol()));
            // 生成充币记录
            WalletCallbackRecord walletCallbackRecord = walletCallbackRecordService.checkDepositRecordIsExist(walletRechargeCallbackResponse, WalletSideEnum.DEPOSIT.getKey());
            // 处理充值 并且计算手续费 记录交易记录 修改钱包余额
            walletCallbackService.dealDeposit(walletRechargeCallbackResponse, new BigDecimal(walletRechargeCallbackResponse.getDepositAmount()), walletCallbackRecord);
            return ResultGenerator.genSuccessResult();
        } catch (Exception e) {
            log.error("wallet rechargeCallBack 异常：", e);
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
        String msg = getMsg(request);
        String sign = request.getHeader("Sign");
        log.info("proxywallet callback sign：[{}], msg：[{}]", sign, msg);
        Assert.isTrue(SignUtils.validSign(msg, sign, signSecret), "验签失败");
        String realJsonStr = AesUtils.decrypt(contentSecret, msg);
        log.info("proxywallet 解密后的数据：[{}]", realJsonStr);
        return realJsonStr;
    }

    /**
     * 获取请求参数msg
     *
     * @param request request
     * @return
     */
    private String getMsg(HttpServletRequest request) {
        String requestContent = getRequestFirstName(request);
        Assert.notNull(requestContent, "充值提币请求内容不存在");
        JSONObject jsonObject = JSONObject.parseObject(requestContent);
        return jsonObject.getString("msg");
    }


    /**
     * 获取第一个请求字段的名称
     *
     * @param request request
     * @return
     */
    private String getRequestFirstName(HttpServletRequest request) {
        Map<String, String[]> m = request.getParameterMap();
        for (String key : m.keySet()) {
            return key;
        }
        return null;
    }
}
