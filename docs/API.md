# FreshLife 后端 API 文档

本文档根据当前 Spring Boot 后端 Controller、DTO、VO 代码整理，可用于前端开发和 Apifox 接口测试。

## 通用说明

### 基础地址

```text
http://localhost:8080/api
```

### Token 说明

需要登录的接口必须携带请求头：

```http
Authorization: Bearer {token}
```

`{token}` 来自登录接口返回的 `data.token`。

### 通用请求头

JSON 请求体接口使用：

```http
Content-Type: application/json
```

需要登录的接口额外携带：

```http
Authorization: Bearer {token}
```

### 统一返回格式

成功：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

失败：

```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null
}
```

### 常见错误码

| code | 说明 |
| --- | --- |
| 0 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录、Token 无效或密码错误 |
| 404 | 数据不存在 |
| 409 | 状态冲突，例如用户名已存在、库存不足、订单状态不允许操作 |
| 500 | 服务器错误 |

---

## 一、用户模块

### 1. 用户注册

- 接口名称：用户注册
- 请求方式：POST
- 请求路径：`/api/user/register`
- 是否需要 Token：否

#### 请求头

```http
Content-Type: application/json
```

#### 请求参数

无 URL 参数。

#### 请求体示例

```json
{
  "username": "fresh_user",
  "password": "123456",
  "phone": "13800000000"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| username | string | 是 | 用户名，不能为空，不能重复 |
| password | string | 是 | 密码，不能为空 |
| phone | string | 否 | 手机号 |

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "username": "fresh_user",
    "phone": "13800000000",
    "nickname": "fresh_user",
    "avatarUrl": null,
    "status": 1
  }
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | username: username不能为空 | 用户名为空 |
| 400 | password: password不能为空 | 密码为空 |
| 409 | 用户名已存在 | username 已被注册 |

### 2. 用户登录

- 接口名称：用户登录
- 请求方式：POST
- 请求路径：`/api/user/login`
- 是否需要 Token：否

#### 请求头

```http
Content-Type: application/json
```

#### 请求参数

无 URL 参数。

#### 请求体示例

```json
{
  "username": "fresh_user",
  "password": "123456"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "jwt-token",
    "userInfo": {
      "id": 1,
      "username": "fresh_user",
      "phone": "13800000000",
      "nickname": "fresh_user",
      "avatarUrl": null,
      "status": 1
    }
  }
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | username: username不能为空 | 用户名为空 |
| 400 | password: password不能为空 | 密码为空 |
| 404 | 用户不存在 | username 不存在 |
| 401 | 密码错误 | 密码校验失败 |

### 3. 获取用户信息

- 接口名称：获取当前登录用户信息
- 请求方式：GET
- 请求路径：`/api/user/info`
- 是否需要 Token：是

#### 请求头

```http
Authorization: Bearer {token}
```

#### 请求参数

无。

#### 请求体示例

无。

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "username": "fresh_user",
    "phone": "13800000000",
    "nickname": "fresh_user",
    "avatarUrl": null,
    "status": 1
  }
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 401 | token无效 | 未携带 Token、Token 格式错误或 Token 解析失败 |
| 404 | 用户不存在 | Token 中的用户 ID 对应用户不存在 |

---

## 二、商品模块

### 1. 商品分页列表

- 接口名称：商品分页列表
- 请求方式：GET
- 请求路径：`/api/product/list`
- 是否需要 Token：否

#### 请求头

无特殊请求头。

#### 请求参数

Query 参数：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| page | number | 否 | 页码，默认 1 |
| size | number | 否 | 每页数量，默认 10，最大 100 |
| priceSort | string | 否 | 价格排序，支持 `asc`、`desc` |

#### 请求体示例

无。

#### 请求示例

```http
GET /api/product/list?page=1&size=10&priceSort=asc
```

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "西红柿",
        "category": "蔬菜",
        "price": 5.99,
        "stock": 100,
        "imageUrl": "https://example.com/tomato.jpg",
        "description": "新鲜西红柿",
        "status": 1,
        "createTime": "2026-06-08T10:00:00",
        "updateTime": "2026-06-08T10:00:00",
        "deleted": 0
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | 价格排序参数只能是asc或desc | priceSort 不是 `asc` 或 `desc` |
| 500 | 服务器错误 | MySQL 或 Redis 等服务异常 |

### 2. 商品详情

- 接口名称：商品详情
- 请求方式：GET
- 请求路径：`/api/product/detail/{id}`
- 是否需要 Token：否

#### 请求头

无特殊请求头。

#### 请求参数

Path 参数：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | number | 是 | 商品 ID |

#### 请求体示例

无。

#### 请求示例

```http
GET /api/product/detail/1
```

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "name": "西红柿",
    "category": "蔬菜",
    "price": 5.99,
    "stock": 100,
    "imageUrl": "https://example.com/tomato.jpg",
    "description": "新鲜西红柿",
    "status": 1,
    "createTime": "2026-06-08T10:00:00",
    "updateTime": "2026-06-08T10:00:00",
    "deleted": 0
  }
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | 商品ID不能为空 | id 为空或小于等于 0 |
| 404 | 商品不存在 | 商品不存在或非上架状态 |
| 500 | 服务器错误 | MySQL 或 Redis 等服务异常 |

### 3. 商品搜索

- 接口名称：商品搜索
- 请求方式：GET
- 请求路径：`/api/product/search`
- 是否需要 Token：否

#### 请求头

无特殊请求头。

#### 请求参数

Query 参数：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| name | string | 是 | 商品名称关键词，模糊查询 |
| page | number | 否 | 页码，默认 1 |
| size | number | 否 | 每页数量，默认 10，最大 100 |
| priceSort | string | 否 | 价格排序，支持 `asc`、`desc` |

#### 请求体示例

无。

#### 请求示例

```http
GET /api/product/search?name=西红柿&page=1&size=10&priceSort=desc
```

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "西红柿",
        "category": "蔬菜",
        "price": 5.99,
        "stock": 100,
        "imageUrl": "https://example.com/tomato.jpg",
        "description": "新鲜西红柿",
        "status": 1,
        "createTime": "2026-06-08T10:00:00",
        "updateTime": "2026-06-08T10:00:00",
        "deleted": 0
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | 搜索关键词不能为空 | name 为空 |
| 400 | 价格排序参数只能是asc或desc | priceSort 不是 `asc` 或 `desc` |

---

## 三、购物车模块

### 1. 加入购物车

- 接口名称：加入购物车
- 请求方式：POST
- 请求路径：`/api/cart/add`
- 是否需要 Token：是

#### 请求头

```http
Content-Type: application/json
Authorization: Bearer {token}
```

#### 请求参数

无 URL 参数。

#### 请求体示例

```json
{
  "productId": 1,
  "quantity": 2
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| productId | number | 是 | 商品 ID |
| quantity | number | 是 | 加入数量，必须大于 0 |

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | productId: productId不能为空 | productId 为空 |
| 400 | quantity: quantity不能为空 | quantity 为空 |
| 400 | quantity: quantity必须大于0 | quantity 小于 1 |
| 401 | token无效 | 未登录或 Token 无效 |
| 404 | 商品不存在 | 商品不存在 |
| 409 | 商品已下架 | 商品 status 不是 1 |

### 2. 查询购物车

- 接口名称：查询购物车
- 请求方式：GET
- 请求路径：`/api/cart/list`
- 是否需要 Token：是

#### 请求头

```http
Authorization: Bearer {token}
```

#### 请求参数

无。

#### 请求体示例

无。

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "cartId": 1,
      "productId": 1,
      "productName": "西红柿",
      "productImageUrl": "https://example.com/tomato.jpg",
      "productPrice": 5.99,
      "quantity": 2,
      "subtotal": 11.98
    }
  ]
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 401 | token无效 | 未登录或 Token 无效 |

### 3. 修改购物车数量

- 接口名称：修改购物车数量
- 请求方式：PUT
- 请求路径：`/api/cart/update`
- 是否需要 Token：是

#### 请求头

```http
Content-Type: application/json
Authorization: Bearer {token}
```

#### 请求参数

无 URL 参数。

#### 请求体示例

```json
{
  "cartId": 1,
  "quantity": 3
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| cartId | number | 是 | 购物车项 ID |
| quantity | number | 是 | 修改后的数量，必须大于 0 |

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | cartId: cartId不能为空 | cartId 为空 |
| 400 | quantity: quantity不能为空 | quantity 为空 |
| 400 | quantity: quantity必须大于0 | quantity 小于 1 |
| 400 | cartId不能为空 | cartId 小于等于 0 |
| 401 | token无效 | 未登录或 Token 无效 |
| 404 | 购物车项不存在 | 购物车项不存在，或不属于当前用户 |

### 4. 删除购物车项

- 接口名称：删除购物车项
- 请求方式：DELETE
- 请求路径：`/api/cart/remove/{cartId}`
- 是否需要 Token：是

#### 请求头

```http
Authorization: Bearer {token}
```

#### 请求参数

Path 参数：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| cartId | number | 是 | 购物车项 ID |

#### 请求体示例

无。

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | cartId不能为空 | cartId 为空或小于等于 0 |
| 401 | token无效 | 未登录或 Token 无效 |
| 404 | 购物车项不存在 | 购物车项不存在，或不属于当前用户 |

---

## 四、订单模块

### 1. 创建订单

- 接口名称：创建订单
- 请求方式：POST
- 请求路径：`/api/order/create`
- 是否需要 Token：是

#### 请求头

```http
Authorization: Bearer {token}
```

#### 请求参数

无。

#### 请求体示例

无。

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "orderId": 1,
    "orderNo": "FL20260608103000123456",
    "totalAmount": 11.98,
    "payAmount": 11.98,
    "status": "WAIT_PAY",
    "createTime": "2026-06-08T10:30:00"
  }
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | 购物车不能为空 | 当前用户购物车为空 |
| 401 | token无效 | 未登录或 Token 无效 |
| 404 | 商品不存在 | 购物车中的商品不存在 |
| 409 | 商品已下架 | 商品 status 不是 1 |
| 409 | 商品库存不足 | 商品库存不足或扣减库存失败 |

### 2. 查询订单列表

- 接口名称：查询订单列表
- 请求方式：GET
- 请求路径：`/api/order/list`
- 是否需要 Token：是

#### 请求头

```http
Authorization: Bearer {token}
```

#### 请求参数

无。

#### 请求体示例

无。

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "orderId": 1,
      "orderNo": "FL20260608103000123456",
      "totalAmount": 11.98,
      "payAmount": 11.98,
      "status": "WAIT_PAY",
      "createTime": "2026-06-08T10:30:00"
    }
  ]
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 401 | token无效 | 未登录或 Token 无效 |

### 3. 查询订单详情

- 接口名称：查询订单详情
- 请求方式：GET
- 请求路径：`/api/order/detail/{orderId}`
- 是否需要 Token：是

#### 请求头

```http
Authorization: Bearer {token}
```

#### 请求参数

Path 参数：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| orderId | number | 是 | 订单 ID |

#### 请求体示例

无。

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "orderId": 1,
    "orderNo": "FL20260608103000123456",
    "totalAmount": 11.98,
    "deliveryFee": 0,
    "payAmount": 11.98,
    "status": "WAIT_PAY",
    "createTime": "2026-06-08T10:30:00",
    "items": [
      {
        "productId": 1,
        "productName": "西红柿",
        "productImageUrl": "https://example.com/tomato.jpg",
        "productPrice": 5.99,
        "quantity": 2,
        "subtotal": 11.98
      }
    ]
  }
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | 订单ID不能为空 | orderId 为空或小于等于 0 |
| 401 | token无效 | 未登录或 Token 无效 |
| 404 | 订单不存在 | 订单不存在，或不属于当前用户 |

### 4. 取消订单

- 接口名称：取消订单
- 请求方式：PUT
- 请求路径：`/api/order/cancel/{orderId}`
- 是否需要 Token：是

#### 请求头

```http
Authorization: Bearer {token}
```

#### 请求参数

Path 参数：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| orderId | number | 是 | 订单 ID |

#### 请求体示例

无。

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | 订单ID不能为空 | orderId 为空或小于等于 0 |
| 401 | token无效 | 未登录或 Token 无效 |
| 409 | 订单不存在或当前状态不允许取消 | 订单不存在、不属于当前用户，或状态不是 `WAIT_PAY` |

### 5. 支付订单

- 接口名称：支付订单
- 请求方式：PUT
- 请求路径：`/api/order/pay/{orderId}`
- 是否需要 Token：是

#### 请求头

```http
Authorization: Bearer {token}
```

#### 请求参数

Path 参数：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| orderId | number | 是 | 订单 ID |

#### 请求体示例

无。

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | 订单ID不能为空 | orderId 为空或小于等于 0 |
| 401 | token无效 | 未登录或 Token 无效 |
| 409 | 订单不存在或当前状态不允许支付 | 订单不存在、不属于当前用户，或状态不是 `WAIT_PAY` |

### 6. 完成订单

- 接口名称：完成订单
- 请求方式：PUT
- 请求路径：`/api/order/finish/{orderId}`
- 是否需要 Token：是

#### 请求头

```http
Authorization: Bearer {token}
```

#### 请求参数

Path 参数：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| orderId | number | 是 | 订单 ID |

#### 请求体示例

无。

#### 返回结果示例

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

#### 失败情况说明

| code | message | 说明 |
| --- | --- | --- |
| 400 | 订单ID不能为空 | orderId 为空或小于等于 0 |
| 401 | token无效 | 未登录或 Token 无效 |
| 409 | 订单不存在或当前状态不允许完成 | 订单不存在、不属于当前用户，或状态不是 `PAID` |
