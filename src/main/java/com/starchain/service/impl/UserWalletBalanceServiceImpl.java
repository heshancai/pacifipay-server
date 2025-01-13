package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.UserWalletBalanceMapper;
import com.starchain.entity.UserWalletBalance;
import com.starchain.service.IUserWalletBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-13
 * @Description
 */
@Service
@Slf4j
public class UserWalletBalanceServiceImpl extends ServiceImpl<UserWalletBalanceMapper, UserWalletBalance> implements IUserWalletBalanceService {
}
