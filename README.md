# FreshLife 智慧生鲜商城

## 项目简介

FreshLife 是一个基于 Spring Boot + MySQL + Redis 的智慧生鲜商城后端项目，面向生鲜商品浏览、用户登录认证、购物车管理和订单交易流程。

项目采用前后端分离架构，目前已完成后端核心业务闭环，后续计划接入 HarmonyOS ArkTS 前端。

---

## 技术栈

### 后端

- Java 17
- Spring Boot 3.3.5
- Spring MVC
- MyBatis-Plus 3.5.9
- MySQL 8
- Redis
- JWT
- BCrypt
- Lombok

### 工具

- IntelliJ IDEA
- Apifox
- Maven
- Git / GitHub

---

## 项目结构

```text
backend
├── src/main/java/com/freshlife
│   ├── common          # 通用返回结果
│   ├── config          # 配置类
│   ├── controller      # 控制器层
│   ├── dto             # 请求参数对象
│   ├── entity          # 数据库实体
│   ├── exception       # 全局异常处理
│   ├── mapper          # MyBatis-Plus Mapper
│   ├── service         # 业务接口
│   ├── utils           # 工具类
│   └── vo              # 响应视图对象
│
├── src/main/resources
│   └── application.yml
│
├── database
│   └── freshlife.sql
│
├── docs
│   └── API.md
│
└── README.md
```

---

## 已完成功能

### 1. 用户模块

已完成：

- 用户注册
- 用户登录
- BCrypt 密码加密
- JWT Token 生成
- JWT Token 解析
- 获取当前登录用户信息

接口：

```http
POST /api/user/register
POST /api/user/login
GET  /api/user/info
```

---

### 2. 商品模块

已完成：

- 商品列表查询
- 商品详情查询
- Redis 商品缓存
- 商品状态过滤
- 商品分页查询

接口：

```http
GET /api/product/list
GET /api/product/detail/{id}
GET /api/product/search
```

---

### 3. 购物车模块

已完成：

- 添加商品到购物车
- 查询当前用户购物车
- 修改购物车商品数量
- 删除购物车商品
- 当前用户数据隔离
- 商品小计计算

接口：

```http
POST   /api/cart/add
GET    /api/cart/list
PUT    /api/cart/update
DELETE /api/cart/remove/{cartId}
```

---

### 4. 订单模块

已完成：

- 创建订单
- 查询订单列表
- 查询订单详情
- 取消订单
- 支付订单
- 完成订单
- 创建订单明细
- 扣减商品库存
- 取消订单恢复库存
- 清空购物车
- 事务控制

接口：

```http
POST /api/order/create
GET  /api/order/list
GET  /api/order/detail/{orderId}
PUT  /api/order/cancel/{orderId}
PUT  /api/order/pay/{orderId}
PUT  /api/order/finish/{orderId}
```

---

## 业务流程

### 正常下单流程

```text
用户注册
   ↓
用户登录
   ↓
获取 Token
   ↓
浏览商品
   ↓
加入购物车
   ↓
创建订单
   ↓
支付订单
   ↓
完成订单
```

订单状态变化：

```text
WAIT_PAY → PAID → FINISHED
```

---

### 取消订单流程

```text
创建订单
   ↓
WAIT_PAY
   ↓
取消订单
   ↓
CANCELLED
```

取消订单后会自动恢复商品库存。

---

## API 测试结果

已使用 Apifox 完成接口测试。

### 用户模块

| 接口 | 状态 |
|---|---|
| POST /api/user/register | 通过 |
| POST /api/user/login | 通过 |
| GET /api/user/info | 通过 |

### 商品模块

| 接口 | 状态 |
|---|---|
| GET /api/product/list | 通过 |
| GET /api/product/detail/{id} | 通过 |

### 购物车模块

| 接口 | 状态 |
|---|---|
| POST /api/cart/add | 通过 |
| GET /api/cart/list | 通过 |
| PUT /api/cart/update | 通过 |
| DELETE /api/cart/remove/{cartId} | 通过 |

### 订单模块

| 接口 | 状态 |
|---|---|
| POST /api/order/create | 通过 |
| GET /api/order/list | 通过 |
| GET /api/order/detail/{orderId} | 通过 |
| PUT /api/order/cancel/{orderId} | 通过 |
| PUT /api/order/pay/{orderId} | 通过 |
| PUT /api/order/finish/{orderId} | 通过 |

---

## 核心测试结果

### 商品列表

```http
GET http://localhost:8080/api/product/list
```

返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": []
  }
}
```

---

### 用户登录

```http
POST http://localhost:8080/api/user/login
```

请求：

```json
{
  "username": "tony",
  "password": "123456"
}
```

返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "jwt-token",
    "userInfo": {}
  }
}
```

---

### 创建订单

```http
POST http://localhost:8080/api/order/create
```

返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "orderId": 2,
    "orderNo": "FL20260608151948726359395",
    "totalAmount": 9.80,
    "payAmount": 9.80,
    "status": "WAIT_PAY"
  }
}
```

---

### 支付订单

```http
PUT http://localhost:8080/api/order/pay/2
```

返回：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

订单状态：

```text
WAIT_PAY → PAID
```

---

### 完成订单

```http
PUT http://localhost:8080/api/order/finish/2
```

返回：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

订单状态：

```text
PAID → FINISHED
```

---

## 数据库表

当前核心表：

```text
user
product
cart
orders
order_item
```

### user

用于保存用户信息。

主要字段：

```text
id
username
password
phone
nickname
avatar_url
status
create_time
update_time
deleted
```

### product

用于保存商品信息。

主要字段：

```text
id
name
category
price
stock
image_url
description
status
create_time
update_time
deleted
```

### cart

用于保存购物车信息。

主要字段：

```text
id
user_id
product_id
quantity
create_time
update_time
deleted
```

### orders

用于保存订单主表信息。

主要字段：

```text
id
order_no
user_id
total_amount
delivery_fee
pay_amount
status
pay_time
finish_time
cancel_time
create_time
update_time
deleted
```

### order_item

用于保存订单商品明细。

主要字段：

```text
id
order_id
product_id
product_name
product_image_url
product_price
quantity
subtotal
create_time
update_time
deleted
```

---

## 本地运行

### 1. 环境要求

```text
JDK 17+
MySQL 8+
Redis
Maven
```

---

### 2. 创建数据库

```sql
CREATE DATABASE freshlife DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

导入数据库文件：

```text
database/freshlife.sql
```

---

### 3. 修改数据库配置

文件位置：

```text
src/main/resources/application.yml
```

示例：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/freshlife?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: freshlife
    password: freshlife123456

  data:
    redis:
      host: localhost
      port: 6379
```

---

### 4. 启动 Redis

```bash
redis-server
```

---

### 5. 启动后端

运行：

```text
FreshLifeApplication
```

启动成功后控制台应出现：

```text
Tomcat started on port 8080 with context path '/api'
Started FreshLifeApplication
```

---

### 6. 测试接口

浏览器访问：

```text
http://localhost:8080/api/product/list
```

Apifox 测试登录：

```http
POST http://localhost:8080/api/user/login
```

---

## 权限说明

以下接口无需登录：

```http
POST /api/user/register
POST /api/user/login
GET  /api/product/list
GET  /api/product/detail/{id}
GET  /api/product/search
```

以下接口需要携带 JWT Token：

```http
GET    /api/user/info

POST   /api/cart/add
GET    /api/cart/list
PUT    /api/cart/update
DELETE /api/cart/remove/{cartId}

POST /api/order/create
GET  /api/order/list
GET  /api/order/detail/{orderId}
PUT  /api/order/cancel/{orderId}
PUT  /api/order/pay/{orderId}
PUT  /api/order/finish/{orderId}
```

请求头格式：

```http
Authorization: Bearer your-jwt-token
```

---

## 当前完成度

```text
后端基础架构        100%
用户认证模块        100%
商品模块            100%
购物车模块          100%
订单模块            100%
Redis缓存           80%
接口测试            100%
前端开发            0%
管理后台            0%
部署上线            0%
```

---

## 当前项目状态

FreshLife 后端目前已经完成核心电商业务闭环：

```text
注册 → 登录 → 商品浏览 → 加入购物车 → 创建订单 → 支付订单 → 完成订单
```

同时支持：

```text
取消订单 → 恢复库存
```

后端接口已通过 Apifox 测试，数据库读写、JWT 鉴权、事务控制、库存扣减和状态流转均已验证成功。

---

## 后续计划

### 前端开发

计划使用 HarmonyOS ArkTS 开发移动端前端页面：

```text
登录页
注册页
商品首页
商品详情页
购物车页
订单列表页
订单详情页
我的页面
```

---

### 后端增强

后续可继续完善：

```text
管理员商品管理
管理员订单管理
订单分页
商品分类筛选
商品搜索优化
Redis缓存优化
接口文档完善
项目部署上线
```

---

## 作者

Tony Rao

FreshLife 智慧生鲜商城后端项目

2026
