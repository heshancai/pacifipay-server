package com.starchain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardRemittanceUrlConstants;
import com.starchain.dao.RemitCardMapper;
import com.starchain.entity.RemitCard;
import com.starchain.entity.dto.RemitCardDto;
import com.starchain.entity.dto.RemitRateDto;
import com.starchain.entity.response.RemitCardResponse;
import com.starchain.exception.StarChainException;
import com.starchain.service.IRemitCardService;
import com.starchain.service.IdWorker;
import com.starchain.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author
 * @date 2025-01-02
 * @Description
 */
@Slf4j
@Service
public class RemitCardServiceImpl extends ServiceImpl<RemitCardMapper, RemitCard> implements IRemitCardService {

    @Autowired
    private PacificPayConfig pacificPayConfig;

    @Autowired
    private IdWorker idWorker;

    @Override
    public Boolean addRemitCard(RemitCardDto remitCardDto) {
        try {
            // 参数检查
            validateRemitCardDto(remitCardDto);

            // 设置 remitCode 和 cardId
            String cardId = String.valueOf(idWorker.nextId());
            remitCardDto.setCardId(cardId);
            log.info("汇款卡唯一标识生成,{}", cardId);

            log.info("开始添加汇款卡，请求参数：{}", remitCardDto);

            // 获取 Token
            String token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            log.info("成功获取 Token：{}", token);

            // 发送请求并获取响应
            String requestUrl = pacificPayConfig.getBaseUrl() + CardRemittanceUrlConstants.ADD_REMIT_CARD;
            String requestBody = JSONObject.toJSONString(remitCardDto);
            log.info("发送请求，URL：{}，请求体：{}", requestUrl, requestBody);

            String responseStr = HttpUtils.doPostMiPay(requestUrl, token, requestBody, pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("收到响应：{}", responseStr);

            // 解析响应
            RemitCardResponse remitCardResponse = JSONObject.parseObject(responseStr, RemitCardResponse.class);
            Assert.notNull(remitCardResponse.getTpyCardId(), "汇款卡ID-银行卡端响应不能为空");
            RemitCard remitCard = new RemitCard();
            BeanUtils.copyProperties(remitCardResponse, remitCard);
            remitCard.setUserId(remitCardDto.getUserId());
            remitCard.setChannelId(remitCardDto.getChannelId());
            remitCard.setCreateTime(LocalDateTime.now());
            remitCard.setUpdateTime(LocalDateTime.now());
            remitCard.setCreateStatus(0);
            remitCard.setCanCelStatus(0);
            // 如果 extraParams 不为空，设置相关字段到 remitCard 对象中
            JSONObject extraParam = remitCardResponse.getExtraParams();
            if (!ObjectUtils.isEmpty(extraParam)) {
                log.info("extraParams 不为空，开始设置额外参数：{}", extraParam);
                // 遍历 extraParams 列表
                remitCard.setSwiftCode(extraParam.getString("swiftCode"));
                remitCard.setRemitName(extraParam.getString("remitName"));
                remitCard.setBankCode(extraParam.getString("bankCode"));
                remitCard.setRemitBank(extraParam.getString("remitBank"));
                remitCard.setRemitBankBranchCode(extraParam.getString("remitBankBranchCode"));
                remitCard.setBsbCode(extraParam.getString("bsbCode"));
                remitCard.setSortCode(extraParam.getString("sortCode"));
                remitCard.setAchNumber(extraParam.getString("achNumber"));
                remitCard.setIdNumber(extraParam.getString("idNumber"));
                remitCard.setRemitBankAddress(extraParam.getString("remitBankAddress"));
                remitCard.setToMoneyKind(extraParam.getString("toMoneyKind"));
                remitCard.setToMoneyCountry2(extraParam.getString("toMoneyCountry2"));

                log.info("额外参数设置完成：{}", remitCard);
            }

            log.info("发起申请汇款卡，最终 remitCard 对象：{}", remitCard);
            return this.save(remitCard);
        } catch (IllegalArgumentException e) {
            log.error("参数检查失败：{}", e.getMessage());
            throw new StarChainException(e.getMessage());
        } catch (Exception e) {
            log.error("添加汇款卡时发生异常", e);
            throw new StarChainException("添加汇款卡失败");
        }
    }

    /**
     * 检查 RemitCardDto 参数是否合法
     *
     * @param remitCardDto 汇款卡信息
     * @throws IllegalArgumentException 如果参数不合法，抛出异常
     */
    private void validateRemitCardDto(RemitCardDto remitCardDto) {
        if (remitCardDto == null) {
            throw new IllegalArgumentException("dto不能为空");
        }

        // 检查通用必填字段
        if (ObjectUtils.isEmpty(remitCardDto.getUserId())) {
            throw new IllegalArgumentException("userId不能为空");
        }
        if (ObjectUtils.isEmpty(remitCardDto.getChannelId())) {
            throw new IllegalArgumentException("channelId不能为空");
        }
        if (!StringUtils.hasText(remitCardDto.getRemitFirstName())) {
            throw new IllegalArgumentException("remitFirstName不能为空");
        }
        if (!StringUtils.hasText(remitCardDto.getRemitLastName())) {
            throw new IllegalArgumentException("remitLastName不能为空");
        }
        if (!StringUtils.hasText(remitCardDto.getRemitBankNo())) {
            throw new IllegalArgumentException("remitBankNo不能为空");
        }

        // 检查 UQR 系列必填字段
        String remitCode = remitCardDto.getRemitCode();
        if (remitCode != null && remitCode.startsWith("UQR")) {
            Map<String, Object> extraParams = remitCardDto.getExtraParams();

            // 通用 UQR 系列必填字段
            if (!StringUtils.hasText((String) extraParams.get("swiftCode"))) {
                throw new IllegalArgumentException("Swift码不能为空");
            }
            if (!StringUtils.hasText((String) extraParams.get("remitName"))) {
                throw new IllegalArgumentException("姓名不能为空");
            }
            if (!StringUtils.hasText((String) extraParams.get("remitBank"))) {
                throw new IllegalArgumentException("remitBank不能为空");
            }
            if (!StringUtils.hasText((String) extraParams.get("toMoneyKind"))) {
                throw new IllegalArgumentException("toMoneyKind不能为空");
            }
            if (!StringUtils.hasText((String) extraParams.get("toMoneyCountry2"))) {
                throw new IllegalArgumentException("toMoneyCountry2不能为空");
            }
            if (!StringUtils.hasText((String) extraParams.get("remitBankAddress"))) {
                throw new IllegalArgumentException("remitBankAddress不能为空");
            }

            // 特定 UQR 类型必填字段
            switch (remitCode) {
                case "UQR_HKD":
                    if (!StringUtils.hasText((String) extraParams.get("bankCode"))) {
                        throw new IllegalArgumentException("bankCode不能为空");
                    }
                case "UQR_CAD":
                    if (!StringUtils.hasText((String) extraParams.get("bankCode"))) {
                        throw new IllegalArgumentException("bankCode不能为空");
                    }
                    if (!StringUtils.hasText((String) extraParams.get("remitBankBranchCode"))) {
                        throw new IllegalArgumentException("remitBankBranchCode不能为空");
                    }
                    break;
                case "UQR_AUD":
                    if (!StringUtils.hasText((String) extraParams.get("bsbCode"))) {
                        throw new IllegalArgumentException("bsbCode不能为空");
                    }
                    break;
                case "UQR_GBP":
                    if (!StringUtils.hasText((String) extraParams.get("sortCode"))) {
                        throw new IllegalArgumentException("sortCode不能为空");
                    }
                    break;
                case "UQR_USD":
                    if (!StringUtils.hasText((String) extraParams.get("achNumber"))) {
                        throw new IllegalArgumentException("achNumber不能为空");
                    }
                    break;
                case "UQR_CNH":
                    if (!StringUtils.hasText((String) extraParams.get("idNumber"))) {
                        throw new IllegalArgumentException("idNumber不能为空");
                    }
                    if (!StringUtils.hasText((String) extraParams.get("remitBankBranchCode"))) {
                        throw new IllegalArgumentException("remitBankBranchCode不能为空");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获取汇款汇率
     *
     * @param remitRateDto
     * @return
     */
    @Override
    public RemitRateDto getRemitRate(String token, RemitRateDto remitRateDto) {

        Assert.notNull(remitRateDto.getRemitCode(), "汇款类型编码不能为空");
        Assert.notNull(remitRateDto.getToMoneyKind(), "汇款目标币种编码不能为空");
        try {
            String requestUrl = pacificPayConfig.getBaseUrl() + CardRemittanceUrlConstants.GET_REMIT_RATE;
            String requestBody = JSONObject.toJSONString(remitRateDto);
            log.info("发送请求，URL：{}，请求体：{}", requestUrl, requestBody);
            String responseStr = HttpUtils.doPostMiPay(requestUrl, token, requestBody, pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("收到响应：{}", responseStr);
            return JSONObject.parseObject(responseStr, RemitRateDto.class);
        } catch (Exception e) {
            log.error("添加汇款卡时发生异常", e);
            throw new StarChainException("添加汇款卡失败");
        }
    }

    @Override
    public Boolean updateRemitCard(RemitCardDto remitCardDto) {
        LambdaQueryWrapper<RemitCard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RemitCard::getTpyCardId, remitCardDto.getCardId());
        RemitCard remitCard = this.getOne(queryWrapper);
        if (remitCard == null) {
            throw new StarChainException("汇款卡不存在");
        }
        validateRemitCardDto(remitCardDto);
        // 获取 Token
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            log.info("成功获取 Token：{}", token);

            String requestUrl = pacificPayConfig.getBaseUrl() + CardRemittanceUrlConstants.UPDATE_REMIT_CARD;
            String requestBody = JSONObject.toJSONString(remitCardDto);
            log.info("发送请求，URL：{}，请求体：{}", requestUrl, requestBody);

            String responseStr = HttpUtils.doPostMiPay(requestUrl, token, requestBody, pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("收到响应：{}", responseStr);
            // 解析响应
            RemitCardResponse remitCardResponse = JSONObject.parseObject(responseStr, RemitCardResponse.class);

            // 如果 extraParams 不为空，设置相关字段到 remitCard 对象中
            LambdaUpdateWrapper<RemitCard> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(RemitCard::getTpyCardId, remitCardResponse.getCardId());
            updateWrapper.set(RemitCard::getUpdateTime, LocalDateTime.now());
            updateWrapper.set(RemitCard::getRemitBankNo, remitCardResponse.getRemitBankNo());
            updateWrapper.set(RemitCard::getRemitFirstName, remitCardResponse.getRemitFirstName());
            updateWrapper.set(RemitCard::getRemitLastName, remitCardResponse.getRemitLastName());

            // 根据响应体设置保存数据库
            if (remitCardResponse.getExtraParams() != null) {
                // 设置额外参数
                updateWrapper.set(RemitCard::getEmail, remitCardResponse.getExtraParams().getString("email"));
                updateWrapper.set(RemitCard::getSwiftCode, remitCardResponse.getExtraParams().getString("swiftCode"));
                updateWrapper.set(RemitCard::getBankCode, remitCardResponse.getExtraParams().getString("bankCode"));
                updateWrapper.set(RemitCard::getRemitBank, remitCardResponse.getExtraParams().getString("remitBank"));
                updateWrapper.set(RemitCard::getRemitBankBranchCode, remitCardResponse.getExtraParams().getString("remitBankBranchCode"));
                updateWrapper.set(RemitCard::getRemitBankAddress, remitCardResponse.getExtraParams().getString("remitBankAddress"));
                updateWrapper.set(RemitCard::getToMoneyKind, remitCardResponse.getExtraParams().getString("toMoneyKind"));
                updateWrapper.set(RemitCard::getToMoneyCountry2, remitCardResponse.getExtraParams().getString("toMoneyCountry2"));
            }
            return update(updateWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public RemitCard getRemitCard(RemitCardDto remitCardDto) {
        // 查询汇款卡是否已经删除

        try {
            // 获取 Token
            String token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            log.info("成功获取 Token：{}", token);

            // 发送请求并获取响应
            String requestUrl = pacificPayConfig.getBaseUrl() + CardRemittanceUrlConstants.GET_REMIT_CARD;
            String requestBody = JSONObject.toJSONString(remitCardDto);
            log.info("发送请求，URL：{}，请求体：{}", requestUrl, requestBody);

            String responseStr = HttpUtils.doPostMiPay(requestUrl, token, requestBody, pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("收到响应：{}", responseStr);

            // 解析响应
            RemitCardResponse remitCardResponse = JSONObject.parseObject(responseStr, RemitCardResponse.class);
            Assert.notNull(remitCardResponse.getTpyCardId(), "汇款卡ID-银行卡端响应不能为空");

            // 更新数据库
            LambdaUpdateWrapper<RemitCard> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(RemitCard::getTpyCardId, remitCardResponse.getTpyCardId());
            updateWrapper.set(RemitCard::getUpdateTime, LocalDateTime.now());
            updateWrapper.set(RemitCard::getRemitBankNo, remitCardResponse.getRemitBankNo());
            updateWrapper.set(RemitCard::getRemitFirstName, remitCardResponse.getRemitFirstName());
            updateWrapper.set(RemitCard::getRemitLastName, remitCardResponse.getRemitLastName());

            if (remitCardResponse.getExtraParams() != null) {
                updateWrapper.set(RemitCard::getEmail, remitCardResponse.getExtraParams().getString("email"));
                updateWrapper.set(RemitCard::getSwiftCode, remitCardResponse.getExtraParams().getString("swiftCode"));
                updateWrapper.set(RemitCard::getBankCode, remitCardResponse.getExtraParams().getString("bankCode"));
                updateWrapper.set(RemitCard::getRemitBank, remitCardResponse.getExtraParams().getString("remitBank"));
                updateWrapper.set(RemitCard::getRemitBankBranchCode, remitCardResponse.getExtraParams().getString("remitBankBranchCode"));
                updateWrapper.set(RemitCard::getRemitBankAddress, remitCardResponse.getExtraParams().getString("remitBankAddress"));
                updateWrapper.set(RemitCard::getToMoneyKind, remitCardResponse.getExtraParams().getString("toMoneyKind"));
                updateWrapper.set(RemitCard::getToMoneyCountry2, remitCardResponse.getExtraParams().getString("toMoneyCountry2"));
            }

            boolean updateResult = update(updateWrapper);
            if (!updateResult) {
                throw new StarChainException("更新汇款卡信息失败");
            }

            // 返回数据库完整对象
            LambdaQueryWrapper<RemitCard> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RemitCard::getTpyCardId, remitCardResponse.getTpyCardId());
            return this.getOne(queryWrapper);
        } catch (Exception e) {
            log.error("查询获取收款卡信息时发生异常", e);
            throw new StarChainException("查询获取收款卡信息失败");
        }
    }

    @Override
    public Boolean delRemitCard(RemitCardDto remitCardDto) {
        try {
            // 获取 Token
            String token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            log.info("成功获取 Token：{}", token);

            // 发送请求并获取响应
            String requestUrl = pacificPayConfig.getBaseUrl() + CardRemittanceUrlConstants.DEL_REMIT_CARD;
            String requestBody = JSONObject.toJSONString(remitCardDto);
            log.info("发送请求，URL：{}，请求体：{}", requestUrl, requestBody);

            String responseStr = HttpUtils.doPostMiPay(requestUrl, token, requestBody, pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("收到响应：{}", responseStr);

            // 解析响应
            RemitCardResponse remitCardResponse = JSONObject.parseObject(responseStr, RemitCardResponse.class);
            Assert.notNull(remitCardResponse, "删除收款卡失败");

            // 更新数据库
            LambdaUpdateWrapper<RemitCard> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(RemitCard::getTpyCardId, remitCardDto.getTpyCardId());
            updateWrapper.set(RemitCard::getCancelStatus, 1); // 设置为已删除状态
            updateWrapper.set(RemitCard::getUpdateTime, LocalDateTime.now());

            return update(updateWrapper);
        } catch (Exception e) {
            log.error("删除收款卡时发生异常", e);
            throw new StarChainException("删除收款卡失败");
        }
    }

    @Override
    public JSONObject remitDetail(RemitCardDto remitCardDto) {
        try {
            // 获取 Token
            String token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            log.info("成功获取 Token：{}", token);

            // 发送请求并获取响应
            String requestUrl = pacificPayConfig.getBaseUrl() + CardRemittanceUrlConstants.REMIT_DETAIL;
            String requestBody = JSONObject.toJSONString(remitCardDto);
            log.info("发送请求，URL：{}，请求体：{}", requestUrl, requestBody);

            String responseStr = HttpUtils.doPostMiPay(requestUrl, token, requestBody, pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("收到响应：{}", responseStr);

            // 解析响应
            JSONObject jsonObject = JSONObject.parseObject(responseStr);

            // 更新数据库
            return jsonObject;
        } catch (Exception e) {
            log.error("删除收款卡时发生异常", e);
            throw new StarChainException("删除收款卡失败");
        }
    }
}
