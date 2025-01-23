package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.SysCoin;
import com.starchain.dao.SysCoinMapper;
import com.starchain.service.ISysCoinService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
@Service
public class SysCoinServiceImpl extends ServiceImpl<SysCoinMapper, SysCoin> implements ISysCoinService {
}
