-- 数据库初始化脚本
-- 创建数据库
create database seckill;
-- 使用数据库
use seckill;
-- 创建秒杀商品的库存
create table seckill(
seckill_id bigint not null auto_increment comment '商品库存ID',
name varchar (120) not null comment '商品名称',
number int not null comment '库存数量',
start_time timestamp not null comment '秒杀开启时间',--添加当前时间的时间戳
end_time timestamp not null comment '秒杀结束时间',
create_time timestamp not null default current_timestamp comment '创建时间',
primary key (seckill_id),--指定主键
key idx_start_time(start_time),--给出索引，提高查询效率
key idx_end_time(end_time),
key idx_create_time(create_time)
)engine=innodb auto_increment=1000 default charset=utf8 comment='秒杀库存表';
--因为只有innodb存储引擎支持事务，所以我们告诉数据库我们采用innodb引擎，同时自增起点为1000，
--该表字符编码集为utf-8，注释为秒杀库存表

--初始化数据
insert into
  seckill(name,number,start_time,end_time)
values
  ('1000元秒杀mate40',100,'2020-11-11 00:00:00','2020-11-11 01:00:00'),
  ('500元秒杀iPhone12',200,'2020-11-11 00:00:00','2020-11-11 01:00:00'),
  ('8000元秒杀Mac',300,'2020-11-11 00:00:00','2020-11-11 01:00:00'),
  ('100元秒杀小米',400,'2020-11-11 00:00:00','2020-11-11 01:00:00');

--秒杀成功明细表
--用户登陆认证相关的信息
create table success_killed(
seckill_id bigint not null comment '秒杀商品ID',
user_phone bigint not null comment '用户手机号',
state tinyint not null default -1 comment '状态标识：-1无效，0成功，1已付款，2已发货',
create_time timestamp not null comment '创建时间',
primary key (seckill_id,user_phone),--联合主键，商品的库存ID与用户手机号就组成了唯一的主键.
--因为商品有多件，故商品ID不能唯一标识某用户秒杀的某商品，某用户又可以同时秒杀多件商品，所以也不能唯一标识，但组合起来就可以
key idx_create_time(create_time)
)engine=innodb default charset=utf8 comment='秒杀成功明细表';--这里不需要起点1000