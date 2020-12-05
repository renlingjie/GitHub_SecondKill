package com.seckill.exception;
//重复秒杀异常（运行期异常），如果重复提交请求，则抛此异常
//继承通用异常SeckillException
public class RepeatKillException extends RuntimeException{
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
