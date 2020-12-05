package com.seckill.exception;
//通用异常/秒杀业务相关的异常都走这个（运行期异常）（所以继承RuntimeException）
//运行期异常不需要手动try/catch，同时Spring事务只接受运行期异常的回滚策略
public class SeckillException extends RuntimeException{
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
