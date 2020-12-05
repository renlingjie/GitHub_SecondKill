<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- 引入jstl -->
<%@include file="common/tag.jsp"%>

<!DOCTYPE html>
<html>
<head>
    <title>秒杀列表页</title>
    <!--每个页面都有的东西，我们提取出来，放到common目录下，在此进行静态包含-->
    <!--注：静态包含指被包含的jsp和主jsp代码合并，一起执行；动态包含是两个jsp分别执行，结果合并-->
    <%@include file="common/head.jsp"%>
</head>
<body>
<%-- 页面显示部分 --%>
<!--下面的类选择器选择的都是bootstrap为我们封装好的css组件，我们直接用-->
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading text-center">
            <h2>秒杀列表</h2>
        </div>
        <div class="panel-body">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>名称</th>
                        <th>库存</th>
                        <th>开始时间</th>
                        <th>结束时间</th>
                        <th>创建时间</th>
                        <th>详情页</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach items="${list}" var="sk">
                    <tr>
                        <td>${sk.name}</td>
                        <td>${sk.number}</td>
                        <!--我们在common的tag中引入了一个JSTL，prefix（相当于ID）是ftm，是格式化输出我们的时间的-->
                        <td>
                            <fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                        </td>
                        <td>
                            <fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                        </td>
                        <td>
                            <fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                        </td>
                        <td><!--详情页是一个超链接，我们想将超链接变成一个按钮，也是在bootstrap中使用它封装好的一个css样式（class的那个）-->
                            <a class="btn btn-info" href="/seckill/${sk.seckillId}/detail" target="_blank">link</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
<!-- jQuery文件，是作为bootstrap的底层依赖。务必在bootstrap.min.js之前引入 -->
<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
</html>

