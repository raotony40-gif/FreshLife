# FreshLife 智慧生鲜商城

## 项目简介

FreshLife 是基于 HarmonyOS + Spring Boot 构建的智慧生鲜商城系统。

项目实现了商品展示、商品详情查询、购物车管理、订单管理等核心电商功能，并通过 Redis 缓存提升系统性能。

采用前后端分离架构：

- Frontend：HarmonyOS NEXT（ArkTS）
- Backend：Spring Boot 3
- Database：MySQL 8
- Cache：Redis

---

## 系统架构

```text
HarmonyOS App
       │
       ▼
Spring Boot REST API
       │
 ┌─────┴─────┐
 ▼           ▼
MySQL      Redis
```

---

## 技术栈

### 前端

- HarmonyOS NEXT
- ArkTS
- ArkUI

### 后端

- Spring Boot 3.x
- Spring MVC
- Spring Data Redis
- MyBatis-Plus
- JWT

### 数据库

- MySQL 8.0

### 缓存

- Redis 7.x

### 开发工具

- IntelliJ IDEA
- DevEco Studio
- Apifox
- GitHub

---

## 项目结构

```text
FreshLife
│
├── backend
│   ├── controller
│   ├── service
│   ├── mapper
│   ├── entity
│   ├── config
│   ├── exception
│   └── utils
│
├── database
│   └── freshlife.sql
│
├── docs
│   ├── API.md
│   └── Design.md
│
├── frontend
│   └── HarmonyOS App
│
└── README.md
```

---

## 已实现功能

### 商品列表

接口：

```http
GET /api/product/list
```

功能：

- 商品分页查询
- Redis缓存
- MyBatis-Plus分页

测试结果：

✅ 已完成

---

### 商品详情

接口：

```http
GET /api/product/detail/{id}
```

功能：

- 商品详情查询
- Redis缓存

测试结果：

✅ 已完成

---

## 开发计划

### 用户模块

计划实现：

- 用户注册
- 用户登录
- JWT认证
- 用户信息管理

---

### 购物车模块

计划实现：

- 添加商品
- 删除商品
- 修改数量
- 查询购物车

---

### 订单模块

计划实现：

- 创建订单
- 查询订单
- 取消订单
- 状态管理

---

## 数据库设计

核心表：

```text
user
product
cart
orders
order_item
```

目前已完成：

```text
product
```

测试数据：

```text
西红柿
鸡蛋
牛奶
苹果
鸡胸肉
```

---

## 环境要求

### JDK

```text
Java 17+
```

推荐：

```text
Temurin JDK 17
```

---

### MySQL

```text
MySQL 8.0+
```

数据库名称：

```text
freshlife
```

---

### Redis

```text
Redis 7+
```

默认配置：

```text
localhost:6379
```

---

## 本地部署

### 1. 克隆项目

```bash
git clone https://github.com/yourname/freshlife.git
```

---

### 2. 创建数据库

```sql
CREATE DATABASE freshlife;
```

导入：

```text
database/freshlife.sql
```

---

### 3. 修改配置文件

application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/freshlife
    username: freshlife
    password: freshlife123456
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

启动成功后访问：

```text
http://localhost:8080/api/product/list
```

---

## API测试结果

### 商品列表

请求：

```http
GET /api/product/list
```

结果：

```json
{
  "code": 0,
  "message": "成功"
}
```

✅ 测试通过

---

### 商品详情

请求：

```http
GET /api/product/detail/1
```

结果：

```json
{
  "code": 0,
  "message": "成功"
}
```

✅ 测试通过

---

## 当前开发进度

### 后端

```text
商品模块        ██████████ 100%
MySQL集成      ██████████ 100%
Redis缓存      ██████████ 100%

用户模块        ███░░░░░░░ 30%
购物车模块      ░░░░░░░░░░ 0%
订单模块        ░░░░░░░░░░ 0%
```

### 前端

```text
HarmonyOS首页   ░░░░░░░░░░ 0%
商品列表页      ░░░░░░░░░░ 0%
商品详情页      ░░░░░░░░░░ 0%
接口联调        ░░░░░░░░░░ 0%
```

---

## 项目亮点

- HarmonyOS NEXT 原生开发
- Spring Boot + MyBatis-Plus
- Redis缓存优化
- 前后端分离架构
- RESTful API设计
- JWT认证机制
- 电商业务场景实践

---

## 作者

Tony Rao

FreshLife 智慧生鲜商城

2026 Spring Semester

Course Project / Portfolio Project
