package com.seckill.web;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.dto.SeckillResult;
import com.seckill.enums.SeckillStateEnums;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.pojo.Seckill;
import com.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SeckillController {
    //统一的日志记录格式：sil4j包中的LoggerFactory工厂
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;
    //1、秒杀列表
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){//四种返回值类型，这里老师用String
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list";
    }
    //2、详情页
    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    //获取URL中的占位符。我们知道Rest风格的请求有一个占位符，那么我们就可以通过这个注解获取到这个占位符，
    //注解中的属性的值与@RequestMapping中的占位符处的命名相同
    public String detail(@PathVariable("seckillId")Long seckillId,Model model){
        if (seckillId == null){
            //如果所请求的某ID的详情页中的ID为空，我们要重定向回list（秒杀列表）
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null){
            //如果ID不为空，但是根据ID查询出来的商品为空，我们请求转发回list（秒杀列表）
            //一个重定向回去、一个转发回去，单纯的只是想展现这两种方式
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }
    //注1：下面三个方法，都涉及Ajax，所以【"成功返回POJO/失败返回Service上抛的异常信息"+"成功/失败状态标识"】
    //组成最终的返回结果。返回结果根据不同的情况new不同的构造方法，同时采用泛型，规范三个不同返回的POJO类型
    //注2：produces = {"application/json;charset=UTF-8"}：为了证实声称默认的JSON编码是UTF-8
    //3、系统当前时间
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        //因为new一个对象这个操作一般情况下不可能报错，所以是true
        return new SeckillResult(true,now.getTime());
    }
    //4、暴露秒杀接口
    @RequestMapping(value = "/{seckillId}/exposer",method = RequestMethod.POST,
    produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId")Long seckillId){
        SeckillResult<Exposer> result;
        try{
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            logger.error(e.getMessage(),e.getCause());
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }
    //5、执行秒杀
    @RequestMapping(value = "/{seckillId}/{md5}/execution",method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    //传进来的参数中没有phone中，而是在cookie中，所以我们通过@CookieValue获取phone，同时cookie如果没有
    //phone会报错，我们不让它报错，而是在我们程序中进行判断的时候作为一种情况，所以require不是必须的为false
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId")Long seckillId,
     @PathVariable("md5")String md5, @CookieValue(value = "killPhone",required = false)Long phone){
        if (phone == null){
            return new SeckillResult<SeckillExecution>(false,"未注册");
        }
        SeckillResult<SeckillExecution> result;
        try{
            //通过存储过程去获取执行结果
            SeckillExecution execution = seckillService.executeSeckillByProcedure(seckillId,phone,md5);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (RepeatKillException e){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnums.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);//为true才会现实异常信息
        }catch (SeckillCloseException e){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnums.END);
            return new SeckillResult<SeckillExecution>(true,execution);//为true才会现实异常信息
        }catch (Exception e){
            logger.error(e.getMessage(),e.getCause());
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnums.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,execution);//为true才会现实异常信息
        }
    }
}
