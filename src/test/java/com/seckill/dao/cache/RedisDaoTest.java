package com.seckill.dao.cache;

import com.seckill.dao.SeckillDao;
import com.seckill.pojo.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
//该注解用于让junit知道Spring的配置文件在哪
@ContextConfiguration({"classpath:SpringConfig.xml"})
public class RedisDaoTest {
    private long id = 1001;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;
    @Test
    public void getSeckill() throws Exception{
        //get and put
        //首先通过ID获取缓存中的ID对应的seckill对象
        Seckill seckill = redisDao.getSeckill(id);
        //如果缓存中seckill对象为空
        if (seckill == null){
            //我们就要执行put，从数据库中获取seckill，同时放入缓存
            seckill = seckillDao.queryById(id);
            if (seckill != null){//只要从数据库拿到的不为空，那就放入redis中
                String result = redisDao.putSeckill(seckill);
                System.out.println(result);
                redisDao.getSeckill(id);//这个时候缓存中应该有seckill对象，我们再尝试拿一下
                System.out.println(seckill);
            }
        }
    }
}