package com.seckill.service;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.pojo.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

//该注解可以让junit启动时加载SpringIOC容器
@RunWith(SpringJUnit4ClassRunner.class)
//该注解用于让junit知道Spring的配置文件在哪
@ContextConfiguration({"classpath:SpringConfig.xml"})
public class SeckillServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;
    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getById() {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void combineTest() {
        long id = 1002;
        //根据ID，查找到秒杀商品是否存在、当前时间是否在秒杀时间段内，如果都满足，则生成md5密钥
        //同时，将boolean类型的是否暴露秒杀借口的变量exposed改为true
        Exposer exposer = seckillService.exportSeckillUrl(id);
        //boolean类型的get方法特殊，就是一个isXXX，这里判断md5密钥是否为空
        if(exposer.isExposed()){//如果exposed为true，执行秒杀
            logger.info("exposer={}",exposer);//打印一下我们的dto，方便看一下当前时间、生成的密钥
            long phone = 12222221110L;
            //因为为true，说明已经暴露接口，同时生成密钥了我们可以直接获取，因为这是我们本机生成的，
            //所以必然和本机生成的密钥一样，所以下面的"执行秒杀"方法最开始的密钥判断一定相等，必然可以执行"执行秒杀"操作
            String md5 = exposer.getMd5();
            try{//上面都说了，传进来的md5是我们本机生成的，自然没问题一定会放行
                SeckillExecution ececution = seckillService.executeSeckill(id,phone,md5);
                logger.info("result={}",ececution);
            //下面两个异常我们都知道。为了能一目了然，只需要打印一下信息，不要再打印异常轨迹了
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }else {//秒杀未开启
            logger.warn("exposer={}",exposer);
        }
    }

    @Test
    public void executeSeckillByProcedure(){
        long seckillId = 1002L;
        long phone = 13984792911L;
        //获取md5
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution seckillExecution = seckillService.executeSeckillByProcedure(seckillId, phone, md5);
            logger.info(seckillExecution.getStateInfo());
        }else {//秒杀未开启
            logger.warn("exposer={}",exposer);
        }
    }

}