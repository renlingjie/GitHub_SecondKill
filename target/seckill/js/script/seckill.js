//存档主要交互逻辑js代码
//javascript很容易写乱，所以我们要模块化。就如我们的java.com.seckill就一个模块seckill（秒杀模块）
//我们javascript想要模块化，没办法像Java那样可以打包，但是他可以通过如下格式进行模块划分：
//var 模块名={子模块名1:{},子模块名2:{},...}，所以最终每个子模块路径就是:模块名.子模块XX.子模块XX下的方法
var seckill = {//秒杀功能模块
    //子模块1：封装秒相关的ajax的URL
    URL:{
        now:function () {
            return '/seckill/time/now';
        },
        exposer : function (seckillId) {
            return '/seckill/'+seckillId+'/exposer';
        },
        execution : function (seckillId, md5) {
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },


    //子模块2：验证手机号
    validatePhone:function(phone){
        //NAN表示不是数字，如果是数字则false，取反就是true，下面表示如果不为空、11位字符且是数字则返回true
        if(phone && phone.length == 11 && !isNaN(phone)){
            return true;
        }else {
            return false;
        }
    },


    //子模块3、暴露"秒杀"接口/按钮（用于倒计时计时结束秒杀开始，且库存不为0）
    //传入两个参数，一个是秒杀商品ID（用于查询对应商品的开始时间和结束时间，与当前时间进行对比，判定是否暴露秒杀借口），一个是节点变量（
    //将我们的一个标签传入，然后对这个标签进行操作，例如下面的，我们通过选择器，将获取到的div标签存储到一个变量中，这个变量就是一个节
    //点变量，我们对它操作实际上就是对div标签进行操作）
    handleSeckillkill:function(seckillId,node){
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>')
        //通过这个URL（是controller层的exposer方法，返回值是SeckillResult，里面有两个参数，对应两种情况：
        //（1）success属性是false+错误信息；（2）success属性是true+秒杀接口）我们获取到秒杀接口
        //存储到result中，如果result存在且属性success的值为true，表示成功获取。
        $.post(seckill.URL.exposer(seckillId), {}, function (result){//POST请求
            //如果结果存在，同时结果的success属性为true，那么就暴露秒杀接口
            if (result && result['success']){
                var exposer = result['data'];
                if (exposer['exposed']){
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId,md5);
                    //绑定"一次"点击事件（如果直接用click，是一直绑定，防止用户多次点击，传递相同URL给服务器带来巨大压力）
                    $('#killBtn').one('click',function () {
                        //执行秒杀请求（点击按钮后执行下面操作）
                        //1.先禁用按钮
                        $(this).addClass('disabled');
                        //2.发送秒杀请求执行秒杀
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //3.显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>');
                            }

                        });
                    });
                    //执行完这些操作后，再将节点变量代表的div标签显示出来（这个时候div就会显示我们上面的执行结果）
                    node.show();
                } else {
                    //未开启秒杀（每个人运行相同的计时时间，时间上实际上是有偏差的，所以针对那些计时器快于我们系统时间的，他
                    //以这们会发现明明已经计时结束，应该秒杀却没有，这是因为我们服务器没到他们的却到了，应该以我们的为主，
                    //但是第一个时候我们再把我我们服务器的时间给他作为标准，即使他计时器比我们快，可能还会早一点计时结束，所
                    //次两者的差距可能就只是几十毫秒，第二次在几十毫秒中他们计时器领先我们的可能就是零点零几毫秒，就几乎同步了）
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新计算计时逻辑
                    seckill.countdown(seckillId,now,start,end);
                }
            } else {//如果上面的情况都不是，那它失败的日志信息我们打印在前端浏览器的控制台上，方便我们分析
                console.log('result:'+result);
            }
        });
    },


    //子模块4、由秒杀时间执行相关秒杀业务
    //根据Ajax得到的当前时间以及"jsp从域中得到的开始结束时间（在params中）"来判断是倒计时、秒
    //杀执行、秒杀结束这三种情况的哪一种，从而有不用的前端效果
    countdown: function (seckillId, nowTime, startTime, endTime) {
        //detail中专门有一个ID是seckill-box的div标签，用来展示我们的计时区域/结果。我们还是通过ID选
        //择器拿到他，然后根据不同的情况在这个div中输入不同的内容
        var seckillBox = $('#seckill-box');
        //时间判断
        if (nowTime > endTime) {
            //秒杀结束
            seckillBox.html('秒杀结束！');
        } else if (nowTime < startTime) {
            //秒杀未开始，计时事件绑定
            var killTime = new Date(startTime+1000);
            //调用jQuery中的countdown事件，传入秒杀时间和一个回调函数，该函数会在我们时间变化的时候做时间的输出
            seckillBox.countdown(killTime, function (event) {
                //定义时间格式
                var format = event.strftime('秒杀倒计时： %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                //时间完成后回调事件：也就是说，countdown会为我们倒计时，但是倒计时结束后，并不会刷新成为"秒杀执行"，我们
                //执行jQuery的finish.countdown，表示倒计时结束，然后执行回调函数handleSeckillkill，它用来显示秒杀按钮
            }).on('finish.countdown', function () {
                seckill.handleSeckillkill(seckillId,seckillBox);
            });
        } else {
            //秒杀开始，同样也是调用handleSeckillkill，显示秒杀按钮
            seckill.handleSeckillkill(seckillId,seckillBox);
        }
    },


    //子模块5：详情页秒杀逻辑
    detail: {
        //init方法：详情页初始化
        init: function (params) {//params就是我们在jsp中通过EL表达式传入的参数
            //功能1：用户手机验证和登录，计时交互
            //首先我们需要拿到用户登陆的手机号，因为我们没有后端，所以我们从cookie中获取（查找手机号）
            //注意：这里cookie中的killPhone属性是我们下面定义的，第一次获取这个属性cookie是没有的，
            //所以直接会执行if (!seckill.validatePhone(killPhone))，然后只要用户输入的是手机号
            //才会将这个手机号写入到cookie中，同时这个手机号对应的属性起名为killPhone
            var killPhone = $.cookie('killPhone');
            //调用子模块2
            if (!seckill.validatePhone(killPhone)) {
                //如果不是手机号，我们要绑定手机号（注意取反了，所以这里是不是手机号的情况）
                //这个时候我们通过ID选择器获取到detail.jsp中的登陆弹出层模块
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//将隐藏（fade）的弹出层模块显示
                    //弹出层模块显示出来，说明用户需要手机号登陆，没登陆之前该弹出层不会关闭，所以需要设置
                    backdrop:'static',//禁止位置关闭（也就是移动弹出页面不能关闭）
                    keyboard:false,//关闭键盘事件（也就是不能通过esc之类的快捷键关闭该弹出层）
                });
                //同样我们通过ID选择器获取到detail.jsp中的submit按钮，给该按钮绑定单击事件
                $('#killPhoneBtn').click(function(){
                    //当用户点击了submit，我们默认用户填写了弹出层的表单，我们就通过ID选择器获取到detail.jsp中
                    //的input标签中的表单内容（通过jQuery的val()即可获取到值）
                    var inputPhone = $('#killPhoneKey').val();
                    //同时我们要再次验证该表单中的内容是否是手机号
                    if(seckill.validatePhone(inputPhone)){
                        //如果是手机号，先将电话写入cookie的killPhone，同时控制cookie的有效期是7天，该cookie
                        //只在路径是'/seckill'下有效（防止该cookie经其他路径传入后端，增加服务器负担）
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});
                        //然后刷新页面，让它从头开始跑，再次获取cookie中的手机号（肯定是手机号），则就不执
                        //行if(!seckill.validatePhone(killPhone))中的代码，直接else（是手机号）中的代码
                        window.location.reload();
                    }else {
                        //如果还是失败的，我们通过ID选择器获取到detail.jsp中的killPhoneMessage对应的span标签，用来显示
                        //错误信息，先隐藏（因为运行时会有过程代码，用户看到体验不好），300ms后再显示错误信息，效果会好一些，
                        //html中传入的label标签就是span标签中要显示的，class中引用了bootstrap的css样式
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(300);
                    }
                });
            }
            //走到这里说明手机号已经登录了
            //这里就是执行计时交互，我们根据init方法传进来的params获取到jsp通过EL表达式传给我们的参数：
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(),{},function (result) {//GET请求
                //通过这个URL（是controller层的time方法，返回值是SeckillResult，里面有两个参数，对应两种情况：
                //（1）success属性是false+错误信息；（2）success属性是true+系统当前时间）我们获取到系统当前时
                //间，存储到result中，如果result存在且属性success的值为true，表示成功获取。
                if (result && result['success']){
                    var nowTime = result['data']//定义一个变量存储result中的数据
                    //调用子模块3
                    seckill.countdown(seckillId,nowTime,startTime,endTime);
                }else {
                    //如果result结果不对就在客户端控制台打印我们的result，查看它到底是什么。必须加"//TODO"
                    console.log('result:'+result);//TODO
                }
            })
        }
    }
}