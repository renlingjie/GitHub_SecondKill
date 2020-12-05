package com.seckill.dao;

import com.seckill.pojo.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillDao {
    //1、减库存：传入的killTime就是我们表中的createTime（秒杀商品的时间）
    //返回的int是我们减的库存数量，因为按照传进来的seckillId必然能将对应的
    //商品库存数量减1（number=number where seckill_id=seckillId）但是
    //如果返回的不是1，那么只可能是0，表示我们减库存失败
    int reduceNumber(@Param("seckillId")long seckillId, @Param("killTime")Date killTime);

    //2、根据ID查询秒杀商品的状态（主要是反馈库存）
    Seckill queryById(long seckillId);

    //3、根据键入数量查询该数量的秒杀商品列表
    List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit);

    //4、根据存储过程来秒杀商品，并生成成功记录明细
    void killByProcedure(Map<String,Object>paramMap);
}
