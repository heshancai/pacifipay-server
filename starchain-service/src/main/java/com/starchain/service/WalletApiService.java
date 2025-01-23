package com.starchain.service;

/**
 * @author
 * @date 2025-01-12
 * @Description
 */
public interface WalletApiService {


    /**
     *  获取充币地址
     * @param
     * @param count
     * @param batchNo
     * @return
     */
    String createNewAddress(String coinName, int count, String batchNo);
}
