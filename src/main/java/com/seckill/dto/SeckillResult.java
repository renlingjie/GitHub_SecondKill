package com.seckill.dto;
//获取Ajax请求，同时将结果json封装为该对象
public class SeckillResult<T> {
    //判断请求是否成功的标识
    private boolean success;
    //泛型类型的数据。因为暴露接口、执行秒杀都会用到Ajax，所以这里的T可能是
    //Exposer或SeckillExecution
    private T data;
    //错误信息
    private String error;
    //请求成功，则携带的是数据信息，所以构造函数的参数就是success/data
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }
    //请求失败，则携带的是错误信息，所以构造函数的参数就是success/error
    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "SeckillResult{" +
                "success=" + success +
                ", data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}
