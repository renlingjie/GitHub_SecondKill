package com.seckill.dao;

import com.seckill.pojo.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
//该注解可以让junit启动时加载SpringIOC容器
@RunWith(SpringJUnit4ClassRunner.class)
//该注解用于让junit知道Spring的配置文件在哪
@ContextConfiguration({"classpath:SpringConfig.xml"})
public class SeckillDaoTest {
    //注入Dao实现类依赖
    @Autowired
    private SeckillDao seckillDao;
    @Test
    public void reduceNumber() {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L,killTime);
        //这个时候输出自然为0，因为我们上面new的killTime为我们当前时间，不在我们数据库设定的
        //秒杀开始和结束时间之内，更新的记录自然是0，返回0；如果在这个时间内更新1条，自然显示1
        System.out.println("updateCount="+updateCount);
    }
    @Test
    public void queryById() {
        long id = 1000;//我们四件秒杀商品是从1000开始的（1000～1003）
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }
    @Test
    public void queryAll() {
        //其实不用这么大的范围，我们就4件商品，所以开始offset为0，范围为向后limit4即可
        List<Seckill> seckills = seckillDao.queryAll(0,100);
        for (Seckill seckill : seckills){
            System.out.println(seckill);
        }
    }
}