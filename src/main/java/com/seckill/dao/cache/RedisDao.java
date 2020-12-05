package com.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.seckill.pojo.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class RedisDao{
    //1、已经通过pom引入了Java访问redis的客户端，我们先将这个客户端创建出来
    //JedisPool有点类似于我们数据库连接池的connectionPool
    private final JedisPool jedisPool;
    //2、生成我们的缓存日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //3、由protostuff的RuntimeSchema方法创建出某对象字节码文件对应的对象的序列化二进制数组
    /*
    缘由：Java中一个对象的存储结构实际上是不连续的，我们要通过序列化将之提炼为一个连续的数据串，因为redis
    的value是一个byte array，所以我们要将键对应的值序列化为byte array(二进制数组)然后存到redis中我
    们知道Java自带的序列化工具是Serializable，我们在类上加这个implements Serializable 即可，但是这个
    性能不是最好的，所以我们要引入别的序列化接口（在pom.xml中有引入依赖protostuff，性能优异的序列化接口）
    */
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
    //4、创建一个该Dao的构造方法，传入参数ip、port，创建出我们的jedisPool
    public RedisDao(String ip,int port){
        jedisPool = new JedisPool(ip,port);
    }
    //5、redis缓存中存在seckill，我们就获取这个seckill直接用
    //从redis中取对象：序列化的bytes---->反序列化得到对象---->将对象存储到新容器中，并作为返回值返回）
    public Seckill getSeckill(long seckillId){
        //通过seckillId，不再走数据库，而是直接从redis中拿到我们的Seckill对象
        try {
            //也是类似于数据库连接池，我们这里通过缓存池拿到我们一个redis客户端jedis
            Jedis jedis = jedisPool.getResource();
            try {
                //反序列化1：构建一个键值对的键名，对应的值就是存储我们的Seckill对象
                String key = "seckill:"+seckillId;
                //反序列化2：将我们的对象转换为字节数组，为接下来的反序列化作准备
                byte[] bytes = jedis.get(key.getBytes());
                //反序列化3：只要字节数组不为空，我们就可以使用schema，将我们redis序列化的字节数组通过schema传入到一个空对象容器中
                if (bytes != null){
                    Seckill seckill = schema.newMessage();//通过schema来new一个序列化容器对象的空对象容器
                    //通过这个方法就可以将装载序列化的bytes，经过schema反序列化得到seckill，存储到我们我们上面的seckill空容器中
                    ProtobufIOUtil.mergeFrom(bytes,seckill,schema);
                    return seckill;
                }
            }finally {
                //最终也和数据库连接池一样，我们要关闭jedis
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
    //6、缓存中没有seckill，我们就创建这个seckill到缓存中
    //将我们的对象序列化到redis中：数据库得到对象---->对象序列化变成redis的value格式byte array---->作为value存入redis
    public String putSeckill(Seckill seckill){
        try {
            //也是类似于数据库连接池，我们这里通过缓存池拿到我们一个redis客户端jedis
            Jedis jedis = jedisPool.getResource();
            try {
                //序列化1：构建一个键值对的键名，对应的值就是存储我们的Seckill对象
                String key = "seckill:"+seckill.getSeckillId();
                //序列化2：将传入的对象同样通过schema序列化为redis中对应的value，存储在byte array格式的数组中
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill,schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //序列化3：指定超时缓存，即超过时间重新缓存
                int timeout = 60*60;//一小时
                String result = jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            }finally {
                //最终也和数据库连接池一样，我们要关闭jedis
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
