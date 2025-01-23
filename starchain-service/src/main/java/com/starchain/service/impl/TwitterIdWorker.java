package com.starchain.service.impl;

import com.starchain.service.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-12
 * @Description 用于生成唯一的 ID。它实现了 IdWorker 接口，并使用了最初由 Twitter 开发的 Snowflake 算法。
 */
@Slf4j
@Service
public class TwitterIdWorker implements IdWorker {

    private final static long TWEPOCH = 1288711299999L ;
    private final static int WORKER_ID_BITS = 7;
    private final static int MAX_WORKER_ID = -1 ^ -1 << WORKER_ID_BITS;
    private final static int SEQUENCE_BITS = 15;
    private final static int WORKER_ID_SHIFT = SEQUENCE_BITS;
    private final static int TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private final static int SEQUENCE_MASK = -1 ^ -1 << SEQUENCE_BITS;

    private volatile int workerId;

    private volatile long sequence = 0L;
    private volatile long lastTimestamp = -1L;
    public TwitterIdWorker() {

    }
    public TwitterIdWorker(int workerId) {
        super();
        this.setWorkerId(workerId);
    }

    public void setWorkerId(int workerId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format( "worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        this.workerId = workerId;
    }

    public int getWorkerId() {
        return workerId;
    }

    @Override
    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1) & SEQUENCE_MASK;
            if (this.sequence == 0) {
                if(log.isDebugEnabled()) {
                    log.debug("########### sequenceMask={}",SEQUENCE_MASK);
                }
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0;
        }
        if (timestamp < this.lastTimestamp) {
            throw new IllegalArgumentException( String.format( "Clock moved backwards.  Refusing to generate id for %d milliseconds", this.lastTimestamp - timestamp));
        }

        this.lastTimestamp = timestamp;
        long nextId =  ((timestamp - TWEPOCH  ) << TIMESTAMP_LEFT_SHIFT)|(this.workerId << WORKER_ID_SHIFT) | (this.sequence);
        if(log.isTraceEnabled()) {
            log.trace("timestamp:{},timestampLeftShift:{},nextId:{},workerId:{},sequence:{}",timestamp,TIMESTAMP_LEFT_SHIFT,nextId,workerId,sequence);
        }
        return nextId;
    }

    /**
     * 方法在当前时间戳小于或等于上一个时间戳时等待到下一个毫秒
     * @param lastTimestamp
     * @return
     */
    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * 方法返回当前系统时间（以毫秒为单位）。
     * @return
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}
