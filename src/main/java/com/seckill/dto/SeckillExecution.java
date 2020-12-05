package com.seckill.dto;

import com.seckill.enums.SeckillStateEnums;
import com.seckill.pojo.SuccessKilled;

//封装秒杀执行后的结果
public class SeckillExecution {
    private long seckillId;
    //秒杀执行结果的状态
    private int state;
    //状态的说明
    private String stateInfo;
    //如果秒杀成功，需要将秒杀的对象封装返回回去
    private SuccessKilled successKilled;
    //根据秒杀成功还是失败，封装两个构造方法，失败的有ID、状态、说明
    //成功的有ID、状态、说明、秒杀到的对象
    public SeckillExecution(long seckillId, SeckillStateEnums stateEnums) {
        this.seckillId = seckillId;
        this.state = stateEnums.getState();
        this.stateInfo = stateEnums.getStateInfo();
    }

    public SeckillExecution(long seckillId, SeckillStateEnums stateEnums, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = stateEnums.getState();
        this.stateInfo = stateEnums.getStateInfo();
        this.successKilled = successKilled;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SeckillEcecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
