package com.seckill.exception;
//秒杀关闭异常。比如时间到了、库存光了。就不能执行秒杀了，同时抛此异常
//继承通用异常SeckillException
public class SeckillCloseException extends RuntimeException{
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
