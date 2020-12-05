<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀详情页</title>
    <%@include file="common/head.jsp" %>
</head>
<body>
<div class="container">
    <div class="panel panel-default text-center">
        <div class="panel-heading">
            <h1>${seckill.name}</h1>
        </div>

        <div class="panel-body">
            <h2 class="text-danger">
                <!-- 显示time图标 -->
                <span class="glyphicon glyphicon-time"></span>
                <!-- 展示倒计时 -->
                <span class="glyphicon" id="seckill-box"></span>
            </h2>
        </div>
    </div>
</div>

<!--这里我们准备用前端就将用户的登陆模块做完，具体如下-->
<%-- 登录弹出层，输入电话，其中该模块是fade也就是隐藏的，只有当我们在seckill.js中发现cookie层手机号不对时
 方法会将之显示，让我们进行手机号登录--%>
<div id="killPhoneModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title text-center">
                    <span class="glyphicon glyphicon-phone"></span>秒杀电话:
                </h3>
            </div>
            <!--主干内容，弹出对话框，输入手机号-->
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <input type="text" name="killPhone" id="killPhoneKey"
                               placeholder="填手机号^o^" class="form-control"/>
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <!-- 验证信息 -->
                <!--当用户手机号如果填写失败，就将失败的信息放到此span标签中-->
                <span id="killPhoneMessage" class="glyphicon"></span>
                <button type="button" id="killPhoneBtn" class="btn btn-success">
                    <span class="glyphicon glyphicon-phone"></span>
                    Submit
                </button>
            </div>
        </div>
    </div>
</div>

</body>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>

<!-- 使用CDN 获取公共js http://www.bootcdn.cn/ -->
<!-- jQuery cookie操作插件 -->
<script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<!-- jQuery countDown倒计时插件 -->
<script src="http://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>

<%-- 开始编写交互逻辑，这个逻辑我们写在自己编写的seckill.js中，在这里（jsp）我们进行加载，然后调用里面的方法，也
 就是说，虽然可以在jsp中直接写出来js的程序、方法，但为了使得调理清晰、可读性强，我们最好将js单独提取出来然后在提
 取出来的js文件中编写我们的js代码，然后在jsp中调用（如果js的方法需要传参，我们就在jsp页面通过EL表达式传入）--%>
<!--<script src="/js/script/seckill.js" type="text/javascript"/>千万注意：这么写浏览器是不加载的-->
<script src="/js/script/seckill.js" type="text/javascript"></script>
<script type="text/javascript">
    $(function () {
        //我们调用seckill.js中的init方法，使用EL表达式给这个方法传入参数（json类型的）
        seckill.detail.init({
            seckillId : ${seckill.seckillId},
            startTime : ${seckill.startTime.time},//转为毫秒
            endTime : ${seckill.endTime.time}
        });
    });
</script>
</html>

