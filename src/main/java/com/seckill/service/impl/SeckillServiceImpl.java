package com.seckill.service.impl;

import com.seckill.dao.SeckillDao;
import com.seckill.dao.SuccessKilledDao;
import com.seckill.dao.cache.RedisDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.enums.SeckillStateEnums;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.pojo.Seckill;
import com.seckill.pojo.SuccessKilled;
import com.seckill.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

@Service
public class SeckillServiceImpl implements SeckillService {
    //统一的日志记录格式：sil4j包中的LoggerFactory工厂
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Autowired
    private RedisDao redisDao;
    //md5盐值字符串，用于混淆md5（随意写，越复杂越好）
    private final String salt = "SGdgddfdSDGDgE%&56gh";
    //将ID、盐值字符串加密的方法，传入ID，自动获取加密后的结果"md5"
    private String getMd5(long seckillId){
        //这个是我们的拼接方式，将ID（可通过它查到地址信息）与盐值字符串按照下面这种方式拼接
        String base = seckillId + "/" + salt;
        //将此拼接好的字符串由Spring的一个方法加密，结果作为最终的md5返回。此方法是将字符串的字节码加密，故传入参数为字符串的字节码
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //缓存优化
        //1、访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null){
            //2、访问数据库
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null){
                //如果不存在，返回构造方法重载之一
                return new Exposer(false,seckillId);
            }else {
                //3、访问数据库得到的seckill存在，放入缓存中
                redisDao.putSeckill(seckill);
            }
        }
        //如果存在，跳出if，同时获取当前时间再做判断
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        //2、根据判断结果，如果不再开始时间和结束时间之间，那么不暴露秒杀接口，返回构造方法重载之一
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }
        //3、走到这里说明商品存在，且当前时间在该商品的秒杀时间的区间之中，所以我们要new一个用于加密的字符串md5，返回构造方法重载之一
        String md5 = getMd5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
    throws SeckillException, RepeatKillException, SeckillCloseException {
        //如果从客户端拿到的用户传进来的md5为null，或者和我们通过getMd5方法得到的md5不一致，就抛异常
        if(md5 == null || !md5.equals(getMd5(seckillId))){
            throw new SeckillException("秒杀接口地址被重写");
        }
        //如果一致，就执行具体的秒杀逻辑：减库存、生成秒杀成功明细
        Date nowTime = new Date();
        try{//操作过程中的其他异常，比如数据库断了、停电了之类的
            int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
            if (insertCount <= 0){//如果底层Dao减库存操作返回值不是1（<=0），说明秒杀结束，减库存失败
                throw new RepeatKillException("重复秒杀，秒杀失败");
            }else {//如果减库存成功，接下来就是生成秒杀成功明细
                int updateCount = seckillDao.reduceNumber(seckillId,nowTime);
                if (updateCount <= 0){//如果底层Dao插入操作返回值不是1（<=0），说明插入明细的主键重复，导致失败
                    throw new SeckillCloseException("秒杀时间到或商品已抢光，秒杀失败");
                }else {//这个时候秒杀成功，我们就显示插入的秒杀成功的明细
                    System.out.println("updateCount为"+updateCount);
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnums.SUCCESS,successKilled);
                }
            }
        } catch(SeckillCloseException e1){
            throw e1;
        } catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            //将所有编译期异常转换为运行期异常（因为我们的SeckillException继承自运行期异常）
            //只有运行期异常才会被事务回滚（我们减库存和生成秒杀成功明细必然是要组成一个食物的）
            throw new SeckillException("秒杀内部错误："+e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) {
        //首先也是md5的验证，不过不抛异常了，而是直接由枚举来说明状态（因为我们无需通过异常来判断执行状态了）
        if(md5 == null || !md5.equals(getMd5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStateEnums.DATA_REWRITE);
        }
        Date killTime = new Date();
        //创建一个Map容器，存储要传给底层Dao方法killByProcedure所需要的参数
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        //通过SeckillDao调用存储过程方法
        try {
            seckillDao.killByProcedure(map);
            //执行完成之后，从map中获取我们最终的result，如果没有，返回-2表示出错
            int result = MapUtils.getInteger(map,"result",-2);
            //然后根据result的值判断执行结果
            if (result == 1) {
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStateEnums.SUCCESS,sk);
            }else {
                //如果不成功，我们根据存储过程得到的result的值找到枚举类对应的stateInfo，传入的需要字符串类型，
                //所以我们通过stateOf将int类型的result转换为字符串
                return new SeckillExecution(seckillId,SeckillStateEnums.stateOf(result));
            }
        }catch (Exception e){//如果在上述过程中出现异常，那么返回一个内部异常
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStateEnums.INNER_ERROR);
        }
    }
}
