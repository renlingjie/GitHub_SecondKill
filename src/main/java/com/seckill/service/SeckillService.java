package com.seckill.service;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.pojo.Seckill;

import java.util.List;

public interface SeckillService {
    //1、查询所有秒杀记录
    List<Seckill> getSeckillList();

    //2、查询单个秒杀记录
    Seckill getById(long seckillId);

    //3、秒杀开启时，输出秒杀接口的地址，否则输出系统时间和秒杀时间
    //输出的东西用一个Exposer类封装
    Exposer exportSeckillUrl(long seckillId);

    //4、执行秒杀操作（里面传进来md5，是用户的md5。如果和我们Exposer中的md5不一样，说明用户擅自更
    //改了URL，那么就不能让他执行秒杀操作）这个操作可能抛的异常我们封装好，同时列明以告诉该接口的使用方。
    //执行秒杀可能成功、失败，有成功、失败的说明，所有输出的东西用一个SeckillEcecution类封装
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
    throws SeckillException, RepeatKillException, SeckillCloseException;

    //5、执行秒杀操作（By 存储过程）
    SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5);
}
