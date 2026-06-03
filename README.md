# 乐鲜生活 FreshLife

乐鲜生活是一个社区生鲜电商项目，目标是实现用户从商品浏览、加入购物车、创建订单到后续履约的基础交易链路。

当前项目已完成 SpringBoot 后端 MVP 基础能力：

- 用户注册、登录、用户信息查询
- 商品列表、商品详情、商品搜索
- 商品详情 Redis 缓存
- 商品列表 Redis 缓存
- 购物车新增、查询、修改、删除
- 订单创建、订单列表、订单详情、订单取消

HarmonyOS 用户端待开发。

## 技术栈

- SpringBoot 3
- MyBatis Plus
- MySQL 8
- Redis 7
- JWT
- Lombok
- HarmonyOS ArkTS（待开发）

## 环境要求

- JDK 17
- Maven 3.9+
- MySQL 8
- Redis 7

## 数据库初始化步骤

数据库脚本位置：

```text
database/freshlife.sql
```

执行方式：

```bash
mysql -uroot -p < database/freshlife.sql
```

默认数据库名：

```text
freshlife
```

当前脚本会创建以下核心表：

- `user`
- `product`
- `cart`
- `orders`
- `order_item`

并插入测试商品：

- 西红柿
- 鸡蛋
- 牛奶
- 苹果
- 鸡胸肉

后端默认数据库配置位于：

```text
backend/src/main/resources/application.yml
```

默认配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/freshlife
    username: root
    password: root
```

如果本地 MySQL 账号密码不同，请修改 `application.yml`。

## Redis 启动步骤

本地 Redis 默认配置：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

启动 Redis：

```bash
redis-server
```

检查 Redis 是否可用：

```bash
redis-cli ping
```

如果返回：

```text
PONG
```

说明 Redis 已启动。

当前 Redis 用途：

- 商品详情缓存：`product:{id}`，TTL 30 分钟
- 商品不存在空值缓存：`product:{id}`，TTL 5 分钟
- 商品列表缓存：`product:list:{page}:{size}:{priceSort}`，TTL 10 分钟

## SpringBoot 启动步骤

进入后端目录：

```bash
cd backend
```

编译：

```bash
mvn clean package
```

启动：

```bash
mvn spring-boot:run
```

或使用 jar 启动：

```bash
java -jar target/freshlife-backend-0.0.1-SNAPSHOT.jar
```

服务默认端口：

```text
8080
```

接口统一前缀：

```text
/api
```

示例：

```text
http://localhost:8080/api/product/list
```

## 接口说明

统一返回格式：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

登录后需要在请求头中携带：

```text
Authorization: Bearer <token>
```

### User

注册：

```http
POST /api/user/register
```

请求示例：

```json
{
  "username": "test",
  "password": "123456",
  "phone": "13800000000"
}
```

登录：

```http
POST /api/user/login
```

请求示例：

```json
{
  "username": "test",
  "password": "123456"
}
```

获取当前用户信息：

```http
GET /api/user/info
```

### Product

商品列表：

```http
GET /api/product/list?page=1&size=10
```

价格升序：

```http
GET /api/product/list?page=1&size=10&priceSort=asc
```

价格降序：

```http
GET /api/product/list?page=1&size=10&priceSort=desc
```

商品详情：

```http
GET /api/product/detail/{id}
```

商品搜索：

```http
GET /api/product/search?name=苹果&page=1&size=10
```

### Cart

加入购物车：

```http
POST /api/cart/add
```

请求示例：

```json
{
  "productId": 1,
  "quantity": 2
}
```

购物车列表：

```http
GET /api/cart/list
```

修改购物车数量：

```http
PUT /api/cart/update
```

请求示例：

```json
{
  "cartId": 1,
  "quantity": 3
}
```

删除购物车项：

```http
DELETE /api/cart/remove/{cartId}
```

### Order

创建订单：

```http
POST /api/order/create
```

订单列表：

```http
GET /api/order/list
```

订单详情：

```http
GET /api/order/detail/{orderId}
```

取消订单：

```http
PUT /api/order/cancel/{orderId}
```

当前订单规则：

- 创建订单会读取当前登录用户购物车
- 创建订单会校验商品状态和库存
- 创建订单会扣减库存并清空购物车
- 只有 `WAIT_PAY` 状态订单可以取消
- 取消订单会恢复库存
- 暂未实现支付、配送、后台管理

## 项目目录结构

```text
FRESH-LIFE
├── backend
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── com
│           │       └── freshlife
│           │           ├── FreshLifeApplication.java
│           │           ├── common
│           │           │   └── Result.java
│           │           ├── config
│           │           │   ├── MybatisPlusConfig.java
│           │           │   └── RedisConfig.java
│           │           ├── controller
│           │           │   ├── UserController.java
│           │           │   ├── ProductController.java
│           │           │   ├── CartController.java
│           │           │   └── OrderController.java
│           │           ├── dto
│           │           ├── entity
│           │           ├── exception
│           │           ├── mapper
│           │           ├── service
│           │           ├── service.impl
│           │           ├── utils
│           │           └── vo
│           └── resources
│               └── application.yml
├── database
│   └── freshlife.sql
├── docs
│   ├── API.md
│   ├── ERD.md
│   └── PRD.md
└── README.md
```

## 常见问题排查

### 1. Maven 命令不可用

现象：

```text
mvn: command not found
```

解决：

- 安装 Maven 3.9+
- 确认 `mvn -v` 可正常输出版本
- 确认 JDK 版本是 17

### 2. MySQL 连接失败

检查：

- MySQL 是否启动
- 数据库 `freshlife` 是否已创建
- `application.yml` 中账号密码是否正确
- MySQL 端口是否为 `3306`

### 3. Redis 连接失败

检查：

```bash
redis-cli ping
```

如果没有返回 `PONG`，说明 Redis 未启动或端口不正确。

### 4. 登录后接口返回 token 无效

检查请求头是否正确：

```text
Authorization: Bearer <token>
```

注意 `Bearer` 后面需要有一个空格。

### 5. 商品详情缓存没有生效

检查：

- Redis 是否启动
- 是否请求了 `GET /api/product/detail/{id}`
- Redis 中是否存在 `product:{id}` key

### 6. 商品列表缓存没有生效

检查 Redis key：

```text
product:list:{page}:{size}:{priceSort}
```

示例：

```text
product:list:1:10:asc
```

如果没有传 `priceSort`，key 末尾为空排序标识。

### 7. 创建订单失败

常见原因：

- 未登录或 token 无效
- 当前用户购物车为空
- 商品已下架
- 商品库存不足

### 8. 取消订单失败

当前只允许取消 `WAIT_PAY` 状态订单。

如果订单已取消、已配送或已完成，会返回状态不允许取消。
