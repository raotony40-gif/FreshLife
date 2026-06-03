# 乐鲜生活 API

## 1. 目标

本文档定义乐鲜生活 MVP 阶段接口规范，支撑以下业务闭环：

用户下单 -> 商家接单 -> 配送员配送 -> 用户签收

接口设计原则：

- 优先跑通核心交易流程。
- 参数简单，便于 HarmonyOS ArkTS 前端接入。
- 后端采用 Node.js 单体服务。
- 第一阶段不引入复杂网关、微服务和过度权限系统。

## 2. 功能说明

MVP API 覆盖：

- 用户注册登录
- 用户地址管理
- 商品分类、商品列表、商品详情、搜索
- 购物车
- 创建订单、支付、取消、确认收货
- 商家商品和订单管理
- 配送员接单、配送、完成订单
- 管理后台用户、商品、订单管理
- AI 商品文案和运营文案生成

## 3. 通用规范

### 3.1 基础地址

```text
/api
```

示例：

```text
GET /api/products
```

### 3.2 请求格式

除文件上传外，统一使用 JSON：

```http
Content-Type: application/json
```

### 3.3 鉴权方式

登录成功后返回 `token`，后续请求放入请求头：

```http
Authorization: Bearer <token>
```

第一阶段 token 可使用 JWT，避免额外维护复杂会话表。

### 3.4 通用返回格式

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

### 3.5 分页返回格式

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [],
    "page": 1,
    "pageSize": 20,
    "total": 0
  }
}
```

### 3.6 通用错误码

| code | 说明 |
| --- | --- |
| 0 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或 token 失效 |
| 403 | 无权限 |
| 404 | 数据不存在 |
| 409 | 状态冲突，如库存不足、订单状态不允许 |
| 500 | 服务器错误 |

## 4. 数据结构

### 4.1 User

```json
{
  "id": 1,
  "phone": "13800000000",
  "nickname": "张三",
  "avatar": "",
  "status": 1
}
```

### 4.2 Address

```json
{
  "id": 1,
  "receiverName": "张三",
  "receiverPhone": "13800000000",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "detailAddress": "科技园 1 号楼",
  "isDefault": 1
}
```

### 4.3 Product

```json
{
  "id": 1,
  "categoryId": 1,
  "name": "山东红富士苹果",
  "title": "脆甜多汁红富士苹果",
  "description": "适合家庭日常水果补充。",
  "imageUrl": "https://example.com/apple.jpg",
  "price": "9.90",
  "unit": "斤",
  "stock": 100,
  "tags": ["水果", "新鲜", "热销"],
  "status": 1
}
```

### 4.4 CartItem

```json
{
  "id": 1,
  "productId": 1,
  "productName": "山东红富士苹果",
  "productImage": "https://example.com/apple.jpg",
  "price": "9.90",
  "unit": "斤",
  "quantity": 2,
  "subtotal": "19.80"
}
```

### 4.5 Order

```json
{
  "id": 1,
  "orderNo": "202606031620000001",
  "status": "pending_accept",
  "payStatus": "paid",
  "totalAmount": "19.80",
  "deliveryFee": "3.00",
  "payAmount": "22.80",
  "items": []
}
```

## 5. API 设计

### 5.1 用户认证

#### POST /auth/send-code

发送手机验证码。

请求参数：

```json
{
  "phone": "13800000000"
}
```

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "expireSeconds": 300
  }
}
```

说明：

- 开发阶段可固定验证码为 `123456`。
- 正式上线前接入短信服务。

#### POST /auth/login

手机号验证码登录。用户不存在时自动注册。

请求参数：

```json
{
  "phone": "13800000000",
  "code": "123456"
}
```

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "jwt-token",
    "user": {
      "id": 1,
      "phone": "13800000000",
      "nickname": "用户0000",
      "avatar": "",
      "status": 1
    }
  }
}
```

### 5.2 用户信息

#### GET /user/profile

获取当前用户信息。

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "phone": "13800000000",
    "nickname": "张三",
    "avatar": "",
    "status": 1
  }
}
```

#### PUT /user/profile

更新当前用户信息。

请求参数：

```json
{
  "nickname": "张三",
  "avatar": "https://example.com/avatar.jpg"
}
```

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 5.3 收货地址

#### GET /addresses

获取当前用户地址列表。

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "receiverName": "张三",
      "receiverPhone": "13800000000",
      "province": "广东省",
      "city": "深圳市",
      "district": "南山区",
      "detailAddress": "科技园 1 号楼",
      "isDefault": 1
    }
  ]
}
```

#### POST /addresses

新增收货地址。

请求参数：

```json
{
  "receiverName": "张三",
  "receiverPhone": "13800000000",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "detailAddress": "科技园 1 号楼",
  "isDefault": 1
}
```

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1
  }
}
```

#### PUT /addresses/:id

编辑收货地址。

请求参数同新增地址。

#### DELETE /addresses/:id

删除收货地址。

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 5.4 商品

#### GET /categories

获取商品分类。

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "新鲜水果",
      "sort": 1
    }
  ]
}
```

#### GET /products

获取商品列表。

查询参数：

| 参数 | 必填 | 说明 |
| --- | --- | --- |
| categoryId | 否 | 分类 ID |
| keyword | 否 | 搜索关键词 |
| page | 否 | 页码，默认 1 |
| pageSize | 否 | 每页数量，默认 20 |

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "categoryId": 1,
        "name": "山东红富士苹果",
        "title": "脆甜多汁红富士苹果",
        "imageUrl": "https://example.com/apple.jpg",
        "price": "9.90",
        "unit": "斤",
        "stock": 100,
        "tags": ["水果", "新鲜"],
        "status": 1
      }
    ],
    "page": 1,
    "pageSize": 20,
    "total": 1
  }
}
```

#### GET /products/:id

获取商品详情。

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "categoryId": 1,
    "name": "山东红富士苹果",
    "title": "脆甜多汁红富士苹果",
    "description": "果肉清脆，适合家庭日常食用。",
    "imageUrl": "https://example.com/apple.jpg",
    "price": "9.90",
    "unit": "斤",
    "stock": 100,
    "tags": ["水果", "新鲜"],
    "status": 1
  }
}
```

#### GET /products/search

搜索商品。

查询参数：

| 参数 | 必填 | 说明 |
| --- | --- | --- |
| keyword | 是 | 搜索关键词 |
| page | 否 | 页码 |
| pageSize | 否 | 每页数量 |

说明：

- 第一阶段使用 MySQL LIKE。
- 只返回上架商品。

### 5.5 购物车

#### GET /cart

获取购物车列表。

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "山东红富士苹果",
        "productImage": "https://example.com/apple.jpg",
        "price": "9.90",
        "unit": "斤",
        "quantity": 2,
        "subtotal": "19.80"
      }
    ],
    "totalAmount": "19.80"
  }
}
```

#### POST /cart/items

加入购物车。

请求参数：

```json
{
  "productId": 1,
  "quantity": 2
}
```

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

#### PUT /cart/items/:id

修改购物车数量。

请求参数：

```json
{
  "quantity": 3
}
```

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

#### DELETE /cart/items/:id

删除购物车商品。

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 5.6 订单

#### POST /orders

创建订单。

请求参数：

```json
{
  "addressId": 1,
  "cartItemIds": [1, 2],
  "remark": "请尽快配送"
}
```

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "orderId": 1,
    "orderNo": "202606031620000001",
    "payAmount": "22.80",
    "status": "pending_payment",
    "payStatus": "unpaid"
  }
}
```

业务规则：

- 创建订单时校验商品是否上架。
- 创建订单时校验库存是否充足。
- 创建订单时扣减库存。
- 创建成功后清理已下单的购物车项。

#### POST /orders/:id/pay

发起支付。

请求参数：

```json
{
  "payType": "mock"
}
```

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "orderId": 1,
    "payStatus": "paid",
    "status": "pending_accept"
  }
}
```

说明：

- 开发阶段 `payType` 使用 `mock`。
- 正式上线后接入微信支付。

#### GET /orders

获取当前用户订单列表。

查询参数：

| 参数 | 必填 | 说明 |
| --- | --- | --- |
| status | 否 | 订单状态 |
| page | 否 | 页码 |
| pageSize | 否 | 每页数量 |

#### GET /orders/:id

获取订单详情。

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "orderNo": "202606031620000001",
    "status": "pending_accept",
    "payStatus": "paid",
    "totalAmount": "19.80",
    "deliveryFee": "3.00",
    "payAmount": "22.80",
    "address": {
      "receiverName": "张三",
      "receiverPhone": "13800000000",
      "detailAddress": "广东省深圳市南山区科技园 1 号楼"
    },
    "items": [
      {
        "productId": 1,
        "productName": "山东红富士苹果",
        "productImage": "https://example.com/apple.jpg",
        "price": "9.90",
        "quantity": 2,
        "subtotal": "19.80"
      }
    ]
  }
}
```

#### POST /orders/:id/cancel

取消订单。

请求参数：

```json
{
  "reason": "不想买了"
}
```

业务规则：

- 待支付订单可以取消。
- 待接单订单可以取消。
- 已配送订单第一阶段不允许用户自助取消。
- 取消订单需要释放库存。

#### POST /orders/:id/confirm

确认收货。

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "orderId": 1,
    "status": "completed"
  }
}
```

## 6. 商家端 API

### POST /merchant/login

商家登录。

请求参数：

```json
{
  "username": "merchant",
  "password": "123456"
}
```

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "jwt-token",
    "role": "merchant"
  }
}
```

### GET /merchant/products

商家商品列表。

查询参数：

| 参数 | 必填 | 说明 |
| --- | --- | --- |
| keyword | 否 | 商品关键词 |
| status | 否 | 商品状态 |
| page | 否 | 页码 |
| pageSize | 否 | 每页数量 |

### POST /merchant/products

新增商品。

请求参数：

```json
{
  "categoryId": 1,
  "name": "山东红富士苹果",
  "title": "脆甜多汁红富士苹果",
  "description": "果肉清脆，适合家庭日常食用。",
  "imageUrl": "https://example.com/apple.jpg",
  "price": "9.90",
  "unit": "斤",
  "stock": 100,
  "tags": ["水果", "新鲜"],
  "status": 1
}
```

### PUT /merchant/products/:id

编辑商品。请求参数同新增商品。

### PUT /merchant/products/:id/status

上架或下架商品。

请求参数：

```json
{
  "status": 1
}
```

### PUT /merchant/products/:id/stock

修改库存。

请求参数：

```json
{
  "stock": 100
}
```

### GET /merchant/orders

商家订单列表。

查询参数：

| 参数 | 必填 | 说明 |
| --- | --- | --- |
| status | 否 | 订单状态 |
| page | 否 | 页码 |
| pageSize | 否 | 每页数量 |

### GET /merchant/orders/:id

商家订单详情。

### POST /merchant/orders/:id/accept

商家接单。

业务规则：

- 只允许 `pending_accept` 状态订单接单。
- 接单后订单状态变为 `pending_delivery`。
- 系统创建配送单，配送单状态为 `pending`。

### POST /merchant/orders/:id/reject

商家拒单。

请求参数：

```json
{
  "reason": "商品缺货"
}
```

业务规则：

- 订单状态变为 `cancelled`。
- 需要释放库存。
- 已支付订单后续接入退款流程，MVP 阶段可由运营人工处理退款。

### POST /merchant/orders/:id/ready

标记订单待配送。

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

## 7. 配送端 API

### POST /rider/login

配送员登录。

请求参数：

```json
{
  "username": "rider",
  "password": "123456"
}
```

### GET /rider/orders/available

获取可接配送单。

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "deliveryOrderId": 1,
      "orderId": 1,
      "orderNo": "202606031620000001",
      "receiverName": "张三",
      "receiverPhone": "13800000000",
      "address": "广东省深圳市南山区科技园 1 号楼",
      "status": "pending"
    }
  ]
}
```

### POST /rider/orders/:id/accept

配送员接单。

业务规则：

- `:id` 为配送单 ID。
- 只允许 `pending` 状态配送单接单。
- 接单后配送单状态变为 `accepted`。

### POST /rider/orders/:id/start

开始配送。

业务规则：

- 配送单状态变为 `delivering`。
- 订单状态变为 `delivering`。

### POST /rider/orders/:id/finish

完成配送。

业务规则：

- 配送单状态变为 `finished`。
- 订单可进入 `completed`，也可等待用户确认收货。
- MVP 为减少用户操作成本，配送员完成后订单直接标记 `completed`。

### GET /rider/orders

我的配送单列表。

查询参数：

| 参数 | 必填 | 说明 |
| --- | --- | --- |
| status | 否 | 配送状态 |
| page | 否 | 页码 |
| pageSize | 否 | 每页数量 |

## 8. 管理后台 API

### POST /admin/login

后台管理员登录。

请求参数：

```json
{
  "username": "admin",
  "password": "123456"
}
```

### GET /admin/users

用户列表。

查询参数：

| 参数 | 必填 | 说明 |
| --- | --- | --- |
| keyword | 否 | 手机号或昵称 |
| status | 否 | 用户状态 |
| page | 否 | 页码 |
| pageSize | 否 | 每页数量 |

### PUT /admin/users/:id/status

启用或禁用用户。

请求参数：

```json
{
  "status": 0
}
```

### GET /admin/products

后台商品列表。

### PUT /admin/products/:id/status

后台上架或下架商品。

请求参数：

```json
{
  "status": 1
}
```

### GET /admin/orders

后台订单列表。

查询参数：

| 参数 | 必填 | 说明 |
| --- | --- | --- |
| orderNo | 否 | 订单编号 |
| phone | 否 | 用户手机号 |
| status | 否 | 订单状态 |
| page | 否 | 页码 |
| pageSize | 否 | 每页数量 |

### GET /admin/orders/:id

后台订单详情。

### POST /admin/orders/:id/cancel

后台取消异常订单。

请求参数：

```json
{
  "reason": "运营人工取消"
}
```

## 9. AI 接口

### POST /admin/ai/product-copy

AI 生成商品文案。

请求参数：

```json
{
  "name": "山东红富士苹果",
  "categoryName": "新鲜水果",
  "price": "9.90",
  "unit": "斤",
  "keywords": ["脆甜", "多汁", "家庭装"]
}
```

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "title": "脆甜多汁山东红富士苹果",
    "description": "果肉清脆，汁水充足，适合家庭日常水果补充。",
    "tags": ["水果", "新鲜", "脆甜"]
  }
}
```

### POST /admin/ai/promotion-copy

AI 生成社群运营文案。

请求参数：

```json
{
  "theme": "周末生鲜促销",
  "products": ["红富士苹果", "本地青菜", "鲜鸡蛋"],
  "channel": "微信群"
}
```

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "copy": "周末补货不用跑远，乐鲜生活把新鲜送到家。红富士苹果、本地青菜、鲜鸡蛋今日上新，社区内快速配送。"
  }
}
```

### GET /admin/analysis/products

商品销售分析。

查询参数：

| 参数 | 必填 | 说明 |
| --- | --- | --- |
| startDate | 否 | 开始日期 |
| endDate | 否 | 结束日期 |

返回示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "hotProducts": [
      {
        "productId": 1,
        "productName": "山东红富士苹果",
        "salesQuantity": 120,
        "salesAmount": "1188.00"
      }
    ],
    "slowProducts": [],
    "suggestions": [
      "红富士苹果销量稳定，可放在首页推荐位。",
      "低销量商品建议检查图片、价格和库存。"
    ]
  }
}
```

## 10. 订单状态流转

```text
pending_payment -> pending_accept -> pending_delivery -> delivering -> completed
pending_payment -> cancelled
pending_accept -> cancelled
```

说明：

- 用户创建订单后进入 `pending_payment`。
- 支付成功后进入 `pending_accept`。
- 商家接单后进入 `pending_delivery`。
- 配送员开始配送后进入 `delivering`。
- 配送完成后进入 `completed`。

## 11. 开发步骤

1. 先开发用户认证、商品列表、商品详情。
2. 开发购物车接口。
3. 开发创建订单和模拟支付接口。
4. 开发订单列表和订单详情。
5. 开发商家订单接单接口。
6. 开发配送员接单和完成配送接口。
7. 开发管理后台查询接口。
8. 最后接入 AI 文案接口，不影响主交易链路。

## 12. 风险分析

### 12.1 接口范围过大风险

风险：

- 一次性开发过多接口会拖慢上线。

方案：

- 第一优先级只开发用户下单、商家接单、配送完成相关接口。
- AI 和管理分析接口可以排在主流程之后。

### 12.2 订单状态错误风险

风险：

- 状态更新不受控会导致重复接单、重复配送或错误完成。

方案：

- 每个状态变更接口必须校验当前状态。
- 状态不允许时返回 `409`。

### 12.3 库存不足风险

风险：

- 用户支付后才发现库存不足，影响体验。

方案：

- 创建订单时校验并扣减库存。
- 取消订单或商家拒单时释放库存。

### 12.4 支付联调风险

风险：

- 微信支付申请和回调联调耗时较长。

方案：

- MVP 开发阶段使用 `mock` 支付。
- 正式上线前再接入微信支付。

## 13. 上线方案

### 13.1 测试环境验收

必须跑通以下流程：

1. 用户登录。
2. 浏览商品。
3. 加入购物车。
4. 创建订单。
5. 模拟支付。
6. 商家接单。
7. 配送员接单。
8. 完成配送。
9. 用户查看订单状态。

### 13.2 生产环境上线

- API 统一使用 HTTPS。
- 后台接口必须鉴权。
- 管理员、商家、配送员使用不同角色。
- 生产环境关闭固定验证码。
- 正式交易前关闭模拟支付。
- 保留接口日志，方便排查订单问题。

### 13.3 后续优化

后续根据真实运营数据增加：

- 支付回调接口
- 退款接口
- 售后接口
- 优惠券接口
- 商品多图上传接口
- 订单状态日志接口
- AI 客服接口
