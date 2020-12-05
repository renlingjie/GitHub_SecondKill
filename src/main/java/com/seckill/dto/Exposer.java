package com.seckill.dto;
//暴露秒杀地址DTO
public class Exposer {
    //告诉接口使用方接口是否开启（暴露）
    private boolean exposed;
    //一种机密措施（加密为md5）
    private String md5;
    private long seckillId;
    //系统当前时间（主要用于如果用户调这个接口的时候，秒杀还没有开始，我们就不能告诉他秒杀
    //地址，但是我们可以返回一个系统时间，然后通过秒杀时间减去系统时间告诉用户还要等多久）
    private long now;
    private long start;//秒杀开启时间
    private long end;//秒杀结束时间
    //为方便初始化，创建多个构造方法，里面传入的参数不尽相同
    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }
    public Exposer(boolean exposed,long seckillId,long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }
    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Exposer{" +
                "exposed=" + exposed +
                ", md5='" + md5 + '\'' +
                ", seckillId=" + seckillId +
                ", now=" + now +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
