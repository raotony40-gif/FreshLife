FreshLife
项目简介
FreshLife 是一个社区生鲜购物系统。
技术栈：
HarmonyOS ArkTS
Spring Boot
MySQL
JWT
Maven
当前项目包含 HarmonyOS 前端原型、Spring Boot 后端服务、MySQL 数据库脚本和项目文档。前端登录页已接入真实后端登录接口，商品、购物车、订单页面已完成页面骨架和跳转；后端已实现用户、商品、购物车、订单核心接口。
项目结构
FRESH-LIFE/
├── frontend/ HarmonyOS前端
├── backend/ SpringBoot后端
├── database/ 数据库脚本
├── docs/ 项目文档
└── README.md
已实现功能
用户模块
用户登录
JWT认证
Token保存
登录状态管理
说明：HarmonyOS 登录页已调用真实后端登录接口，并将登录成功返回的 token 保存到本地状态中。
商品模块
商品列表
商品详情
商品分类
库存显示
说明：后端已提供商品列表、商品详情和商品搜索接口；前端商品列表页和商品详情页当前使用模拟数据展示页面效果。
购物车模块
添加购物车
数量统计
金额计算
说明：后端已提供购物车添加、查询、修改数量、删除接口；前端购物车页当前使用模拟数据展示数量和金额。
订单模块
创建订单
订单列表
订单状态展示
说明：后端已提供创建订单、订单列表、订单详情、取消订单、支付订单、完成订单接口；前端订单页当前使用模拟数据展示订单状态。
HarmonyOS前端
登录页
商品列表页
商品详情页
购物车页
订单页
页面跳转
环境要求
后端
JDK 17+
Maven 3.9+
MySQL 8
前端
DevEco Studio 6.1.1
HarmonyOS API 24
项目启动
启动数据库
数据库脚本位置：
database/freshlife.sql
执行脚本：
mysql -ufreshlife -p freshlife < database/freshlife.sql
如果本地数据库账号不同，请同步修改：
backend/src/main/resources/application.yml
启动后端
cd backend
mvn spring-boot:run
后端默认地址：
http://127.0.0.1:8080/api
局域网或模拟器访问时，请使用当前电脑的局域网 IP，例如：
http://10.47.0.106:8080/api
启动前端
使用 DevEco Studio 打开：
frontend/
运行方式：
DevEco Studio -> Run
前端登录接口配置位置：
frontend/entry/src/main/ets/service/HttpService.ets
主要接口
用户接口
POST /api/user/login
GET  /api/user/info
商品接口
GET /api/product/list
GET /api/product/detail/{id}
GET /api/product/search
购物车接口
POST   /api/cart/add
GET    /api/cart/list
PUT    /api/cart/update
DELETE /api/cart/remove/{cartId}
订单接口
POST /api/order/create
GET  /api/order/list
GET  /api/order/detail/{orderId}
PUT  /api/order/cancel/{orderId}
PUT  /api/order/pay/{orderId}
PUT  /api/order/finish/{orderId}
完整接口文档：
docs/API.md
当前状态
后端核心接口已完成
HarmonyOS 前端项目可构建运行
登录接口已完成前后端联调
商品、购物车、订单页面已完成页面骨架
商品、购物车、订单前端接口联调待继续完善
后续计划
商品列表接入真实后端接口
商品详情接入真实后端接口
购物车页面接入真实后端接口
订单页面接入真实后端接口
增加用户中心
增加收藏功能
完善支付流程
