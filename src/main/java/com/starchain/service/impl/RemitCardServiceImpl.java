package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.RemitCardMapper;
import com.starchain.entity.RemitCard;
import com.starchain.service.IRemitCardService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-02
 * @Description
 */
@Service
public class RemitCardServiceImpl extends ServiceImpl<RemitCardMapper, RemitCard> implements IRemitCardService {
    @Override
    public Boolean addRemitCard(RemitCard remitCard) {
        return null;
    }
}
