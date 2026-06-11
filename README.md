# FreshLife 社区生鲜购物平台

FreshLife 是一个社区生鲜购物平台，面向用户日常买菜、购买水果、蛋奶、肉禽等高频场景。

当前项目已经完成 HarmonyOS NEXT 前端原型与 Spring Boot 后端核心接口联调，形成了从登录、商品浏览、购物车到订单流转的完整业务闭环。

## 项目简介

FreshLife 当前阶段目标是快速验证社区生鲜电商 MVP：

```text
登录 -> 商品浏览 -> 商品详情 -> 加入购物车 -> 创建订单 -> 订单查询
```

已完成：

- HarmonyOS NEXT 前端可运行原型
- Spring Boot 后端核心业务接口
- MySQL 核心业务表
- Redis 商品缓存
- JWT 登录认证
- Apifox 接口测试通过
- 前后端联调成功
- HarmonyOS 模拟器运行成功

## 项目演示效果

截图预留：

```text
docs/images/demo-login.png
docs/images/demo-product-list.png
docs/images/demo-product-detail.png
docs/images/demo-cart.png
docs/images/demo-order.png
```

## 技术架构

```text
HarmonyOS NEXT
      ↓
   REST API
      ↓
 Spring Boot
      ↓
 MyBatis Plus
      ↓
    MySQL
```

Redis 用于商品缓存，JWT 用于接口认证。

## 技术栈

### 前端

- HarmonyOS NEXT
- ArkTS
- ArkUI
- Stage Model
- Navigation
- Axios

### 后端

- Spring Boot 3.3.5
- MyBatis Plus
- Redis
- JWT
- Lombok

### 数据库

- MySQL 8

## 项目结构

```text
FRESH-LIFE/
├── backend/                 # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/freshlife/
│       │   ├── controller/
│       │   ├── service/
│       │   ├── mapper/
│       │   ├── entity/
│       │   ├── dto/
│       │   ├── vo/
│       │   ├── config/
│       │   ├── common/
│       │   ├── exception/
│       │   └── utils/
│       └── resources/
│           └── application.yml
├── frontend/                # HarmonyOS NEXT 前端
│   ├── oh-package.json5
│   ├── build-profile.json5
│   └── entry/src/main/ets/
│       ├── pages/
│       ├── model/
│       ├── service/
│       └── utils/
├── database/
│   └── freshlife.sql
└── docs/
    ├── API.md
    ├── TEST-RESULT.md
    └── PROJECT-STATUS.md
```

## 已实现功能

### 用户模块

- 用户登录
- JWT Token 返回
- 前端保存 Token
- 后续请求统一携带 `Authorization: Bearer {token}`

### 商品模块

- 商品列表
- 商品详情
- 商品缓存
- 商品浏览页面
- 商品详情页面

### 购物车模块

- 购物车页面
- 加入购物车接口
- 查询购物车接口
- 修改购物车数量接口
- 删除购物车商品接口

### 订单模块

- 创建订单
- 查询订单
- 查询订单详情
- 支付订单
- 完成订单
- 订单页面

## 系统流程

```text
登录
  ↓
商品浏览
  ↓
商品详情
  ↓
加入购物车
  ↓
创建订单
  ↓
订单查询
```

## API 接口概览

接口统一前缀：

```text
http://127.0.0.1:8080/api
```

### 用户接口

```http
POST /api/user/login
GET  /api/user/info
```

### 商品接口

```http
GET /api/product/list
GET /api/product/detail/{id}
GET /api/product/search
```

### 购物车接口

```http
POST   /api/cart/add
GET    /api/cart/list
PUT    /api/cart/update
DELETE /api/cart/remove/{cartId}
```

### 订单接口

```http
POST /api/order/create
GET  /api/order/list
GET  /api/order/detail/{orderId}
PUT  /api/order/cancel/{orderId}
PUT  /api/order/pay/{orderId}
PUT  /api/order/finish/{orderId}
```

详细接口说明见：

```text
docs/API.md
```

## 项目运行

### 后端启动

环境要求：

- JDK 17
- Maven 3.9+
- MySQL 8
- Redis 7

初始化数据库：

```bash
mysql -ufreshlife -p freshlife < database/freshlife.sql
```

启动 Redis：

```bash
redis-server
```

启动 Spring Boot：

```bash
cd backend
mvn spring-boot:run
```

默认后端地址：

```text
http://127.0.0.1:8080/api
```

当前后端数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/freshlife
    username: freshlife
    password: freshlife123456
```

### 前端启动

环境要求：

- DevEco Studio
- HarmonyOS / OpenHarmony SDK
- HarmonyOS 模拟器

启动方式：

```text
DevEco Studio -> Open frontend -> Run
```

前端当前登录接口：

```text
POST http://127.0.0.1:8080/api/user/login
```

当前联调用测试账号：

```text
username: tony
password: 123456
```

## 项目成果

- 前后端联调成功
- HarmonyOS 模拟器运行成功
- REST API 联调成功
- JWT 认证成功
- Apifox 测试通过
- 商品缓存接入 Redis
- 用户端核心购物流程已跑通

## 项目亮点

- 使用 HarmonyOS NEXT + ArkTS 构建移动端原型，贴近真实端侧运行环境。
- 后端采用 Spring Boot 单体架构，开发成本低，适合 MVP 快速上线。
- 通过 JWT 实现登录认证，前端统一携带 Token 访问受保护接口。
- 商品详情和商品列表接入 Redis 缓存，降低数据库压力。
- 订单创建包含库存校验、扣减库存、清空购物车等核心交易逻辑。
- 当前已形成登录、浏览、购物车、下单、支付、完成订单的业务闭环。

## 后续规划

- 收藏功能
- 商品搜索页面完善
- 支付模块接入真实支付渠道
- 用户中心
- 订单售后能力
- 管理后台商品和订单管理
