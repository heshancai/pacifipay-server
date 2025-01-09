package com.starchain.entity.response;

import com.starchain.entity.CardChangeRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author
 * @date 2025-01-09
 * @Description 申请换卡 响应数据
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CardChangeRecordResponse extends CardChangeRecord {
}
