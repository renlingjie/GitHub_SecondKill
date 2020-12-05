-- 秒杀执行存储过程程序
-- 1、更改分隔符为$$
DELIMITER $$
-- 2、创建存储过程
CREATE PROCEDURE `seckill`.`execute_seckill`
  (in v_seckill_id bigint,in v_phone bigint,--指定传入参数、输出参数
    in v_kill_time timestamp,out r_result int)
  --3、存储过程主体
  BEGIN
  --3.1、定义计数变量，默认值为0，其值用于判定Sql语句的增、删、改是否成功执行
    DECLARE insert_count int DEFAULT 0;
    --3.2、开启事务
    START TRANSACTION;
    --3.2.1、向表success_killed中的seckill_id,user_phone,create_time字段插入我们的各传入参数
    insert ignore into success_killed
      (seckill_id,user_phone,create_time)
      values (v_seckill_id,v_phone,v_kill_time);
      --3.2.2、调用row_count()函数返回上面影响行数，结果存储到我们上面DECLARE声明的变量insert_count中
    select row_count() into insert_count;
    --3.2.3、根据insert_count中判定Sql执行结果。>0，则表示修改的行数；=0，未修改；<0，Sql错误
    IF (insert_count = 0) THEN--未修改
      ROLLBACK;
      set r_result = -1;--结果标识-1：重复秒杀
    ELSEIF(insert_count < 0) THEN--Sql错误
      ROLLBACK;
      SET R_RESULT = -2;--结果标识-2：系统异常
    ELSE--insert成功插入，可继续执行update（reduce）
      update seckill
      set number = number-1
      where seckill_id = v_seckill_id
        and end_time > v_kill_time
        and start_time < v_kill_time
        and number > 0;
      select row_count() into insert_count;--同样通过该函数将该update语句执行结果存到变量中
      IF (insert_count = 0) THEN--减库存失败
        ROLLBACK;
        set r_result = 0;--结果标识0：秒杀结束
      ELSEIF (insert_count < 0) THEN
        ROLLBACK;
        set r_result = -2;--结果标识-1：重复秒杀
      ELSE
        COMMIT;
        set r_result = 1;--结果标识1：秒杀成功
      END IF;--IF/END IF
    END IF;--IF/END IF
  END;
$$
-- 4、存储过程定义结束，更回";"分隔符
DELIMITER ;
-- 5、定义输出参数名称为@r_result，其默认值为-3。非成功/异常的结果标识-3：数据篡改
set @r_result=-3;
-- 6、通过CALL调用存储过程execute_seckill
call execute_seckill(1003,13502178891,now(),@r_result);
-- 7、获取结果
select @r_result;

-- 存储过程
-- 1:存储过程优化：事务行级锁持有的时间
-- 2:不要过度依赖存储过程
-- 3:简单的逻辑可以应用存储过程
-- 4:QPS:一个秒杀单6000/qps