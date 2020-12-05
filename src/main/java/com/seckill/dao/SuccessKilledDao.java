package com.seckill.dao;

import com.seckill.pojo.SuccessKilled;
import org.apache.ibatis.annotations.Param;

public interface SuccessKilledDao {
    //1、插入购买成功记录。由于我们使用了联合唯一主键，所以一个人无论如何只能买
    //某一类商品中的一个，如果多买无法插入，视为购买失败。而失效的体现其实和SeckillDao
    //中减库存类似，int返回的就是插入成功的条数，正常应该返回1，若失败返回的肯定是0
    int insertSuccessKilled(@Param("seckillId")long seckillId, @Param("userPhone") long userPhone);

    //2、根据秒杀商品ID查询该商品秒杀成功的明细记录，同时该记录要携带秒杀商品的属性因为我们给1000个
    //秒杀成功的每一个对象都加入了秒杀商品这一复合属性，所以每一条秒杀明细都应该有记录它秒杀的商品属性
    //同时一个秒杀商品的ID必然对应多条秒杀记录，为了精确到指定的某一条，所以我们还需要用户的手机号
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
}
