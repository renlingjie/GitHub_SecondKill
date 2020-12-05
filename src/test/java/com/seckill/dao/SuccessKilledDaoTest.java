package com.seckill.dao;

import com.seckill.pojo.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
//该注解可以让junit启动时加载SpringIOC容器
@RunWith(SpringJUnit4ClassRunner.class)
//该注解用于让junit知道Spring的配置文件在哪
@ContextConfiguration({"classpath:SpringConfig.xml"})
public class SuccessKilledDaoTest {
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Test
    public void insertSuccessKilled() {
        //第一次执行后结果为1，自然的，因为成功插入了。第二次执行由于主键重复，所以插入失败，为0
        //而且由于写了ignore，如果不写，这里就不会返回0了，而是报错"主键冲突异常"
        long id = 1001L;
        long phoneNumber = 17888888888L;
        int insertCount = successKilledDao.insertSuccessKilled(id,phoneNumber);
        System.out.println("insertCount="+insertCount);
    }

    @Test
    public void queryByIdWithSeckill() {
        long id = 1001L;
        long phoneNumber = 17888888888L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id,phoneNumber);
        System.out.println(successKilled);//state为0，表示插入语句执行，将默认值-1改为0了
        System.out.println(successKilled.getSeckill());
    }
}