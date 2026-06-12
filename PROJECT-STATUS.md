# FreshLife 项目状态文档

## 1. 当前项目总览

- 项目名称：FreshLife 社区生鲜购物系统
- 当前阶段：前后端联调完成，核心业务闭环已跑通
- 当前状态：可运行、可演示、已上传 GitHub

FreshLife 当前已经完成 Spring Boot 后端核心接口、HarmonyOS NEXT 前端页面骨架、登录接口真实联调、页面路由跳转和基础演示流程。项目适合进行课程项目汇报、答辩演示和后续功能完善。

说明：当前登录接口已接入真实后端；商品列表、商品详情、购物车、订单等前端页面已完成展示和跳转，部分页面数据仍以模拟数据为主，后续可继续接入真实后端接口。

## 2. 已完成内容

### 后端

- Spring Boot 后端服务启动成功
- Tomcat 运行在 8080 端口
- context-path 为 `/api`
- 登录接口 curl 测试成功
- JWT Token 返回成功
- 商品、购物车、订单相关接口已实现
- MySQL 数据库脚本已完成
- Redis 商品缓存配置已完成
- 统一返回对象、业务异常、全局异常处理已完成

### 前端

- HarmonyOS NEXT 前端运行成功
- DevEco Studio 6.1.1 配置完成
- HarmonyOS API 24 模拟器运行成功
- 登录页已完成
- 商品列表页已完成
- 商品详情页已完成
- 购物车页已完成
- 订单页已完成
- 已添加 `ohos.permission.INTERNET` 网络权限
- `HttpService` baseURL 已配置为当前后端地址
- 登录、商品浏览、购物车、订单页面跳转成功
- Token 本地保存能力已完成
- 后续请求统一携带 `Authorization: Bearer {token}` 的基础能力已完成

### 联调

- curl 登录接口测试通过
- HarmonyOS 前端登录接口已接入
- 前后端网络连接问题已修复
- GitHub 同步状态已恢复正常
- 核心演示流程已跑通：

```text
登录 -> 商品列表 -> 商品详情 -> 加入购物车 -> 购物车 -> 创建订单 -> 订单展示
```

当前说明：

- 登录步骤调用真实后端接口。
- 商品、购物车、订单页面可完成演示跳转。
- 商品、购物车、订单页面的真实接口联调仍可继续完善。

## 3. 关键问题与解决记录

### DevEco Studio SDK 配置问题

- 问题现象：DevEco Studio 打开前端项目后 Build Init 失败，命令行 hvigor 提示 SDK 路径无效或 SDK component missing。
- 原因：`DEVECO_SDK_HOME` 指向的 SDK 根目录不正确；DevEco 工具期望使用有效的 SDK 根路径。
- 解决方案：确认 DevEco Studio 自带 SDK 路径，并在构建命令中使用正确 SDK 根目录。
- 当前状态：已解决，项目可同步、可构建。

### targetSdkVersion 缺失问题

- 问题现象：DevEco Studio 提示当前项目没有配置 `targetSdkVersion`。
- 原因：HarmonyOS 项目级 `build-profile.json5` 中缺少 DevEco 能识别的目标 SDK 配置。
- 解决方案：在 `frontend/build-profile.json5` 的产品配置中补充 `targetSdkVersion`，并适配 HarmonyOS 6.1.1 / API 24。
- 当前状态：已解决。

### hvigor 构建失败问题

- 问题现象：`hvigor assembleHap` 构建失败，曾出现项目级和模块级 hvigor 配置不完整、ArkTS 类型检查错误等问题。
- 原因：HarmonyOS 工程需要项目级 `hvigorfile.ts` 和模块级 `entry/hvigorfile.ts`；ArkTS 严格模式也要求对象类型、异常类型等更明确。
- 解决方案：补充项目级和模块级 hvigor 配置，修复 ArkTS 类型错误，使用 `AxiosHeaders` 构造请求头，避免未声明对象字面量问题。
- 当前状态：已解决，`assembleHap` 构建成功。

### 模拟器创建与运行问题

- 问题现象：HarmonyOS 模拟器创建和运行阶段需要匹配 DevEco Studio、SDK、API 版本。
- 原因：前端项目目标环境为 DevEco Studio 6.1.1 和 HarmonyOS API 24，模拟器环境需要一致。
- 解决方案：使用 DevEco Studio 6.1.1，创建 HarmonyOS API 24 模拟器，并在该模拟器中运行前端工程。
- 当前状态：已解决，模拟器运行成功。

### GitHub SSH Key 推送问题

- 问题现象：执行 `git push origin main` 时曾出现 `Permission denied (publickey)`。
- 原因：本机 GitHub SSH Key 未配置或未被 GitHub 账号认可。
- 解决方案：配置 GitHub SSH Key 或改用 HTTPS 凭据；后续确认本地分支与 `origin/main` 同步。
- 当前状态：已解决，当前项目已上传 GitHub。

### HarmonyOS 模拟器访问 localhost 失败问题

- 问题现象：后端本机 curl 成功，但 HarmonyOS 模拟器内请求 `127.0.0.1` 或 `localhost` 失败。
- 原因：模拟器内的 `localhost` 指向模拟器自身，不是开发电脑的后端服务。
- 解决方案：将前端 `HttpService` baseURL 从 `127.0.0.1` 改为开发电脑局域网 IP。
- 当前状态：已解决，当前后端访问地址为 `http://10.47.0.106:8080/api`。

### 缺少 ohos.permission.INTERNET 网络权限问题

- 问题现象：curl 登录成功，但 HarmonyOS 前端登录请求仍失败。
- 原因：`frontend/entry/src/main/module.json5` 缺少网络权限配置。
- 解决方案：在 `module.json5` 中添加：

```json
"requestPermissions": [
  {
    "name": "ohos.permission.INTERNET"
  }
]
```

- 当前状态：已解决，前端可发起真实网络请求。

## 4. 当前测试账号

```text
username: tony
password: 123456
```

## 5. 当前后端访问地址

```text
http://10.47.0.106:8080/api
```

如果电脑更换网络，需要重新确认本机 IP，并更新以下文件中的 baseURL：

```text
frontend/entry/src/main/ets/service/HttpService.ets
```

建议后续将 baseURL 抽取到统一环境配置中，减少手动修改成本。

## 6. 当前项目风险

- 前端 baseURL 仍为本机局域网 IP，换网络后需要修改。
- 后端配置中可能存在数据库密码和 JWT Secret 硬编码，正式环境需要改为环境变量或配置中心。
- 单元测试不足，当前主要依赖接口手动测试和前端运行验证。
- 部分页面仍需优化 UI，尤其是商品列表、购物车和订单展示细节。
- 商品、购物车、订单前端真实接口联调仍可继续完善。
- 订单支付流程目前偏演示性质，可继续完善真实支付、支付回调和状态流转。

## 7. 下一步计划

优先级从高到低：

1. 固化 README 和答辩文档
2. 补充测试报告
3. 准备答辩 PPT
4. 优化前端 UI
5. 增加商品搜索
6. 增加收藏功能
7. 完善订单状态流转
8. 增加单元测试
9. 整理部署说明

## 8. 项目完成度评分

| 维度 | 评分 | 说明 |
| --- | --- | --- |
| 环境搭建 | 9/10 | DevEco Studio、HarmonyOS API 24、Spring Boot、MySQL 等环境已跑通。 |
| 后端功能 | 8/10 | 用户、商品、购物车、订单核心接口已完成，支付和配送仍可继续扩展。 |
| 前端功能 | 7/10 | 页面骨架、路由、登录联调已完成，商品/购物车/订单真实接口接入仍可完善。 |
| 前后端联调 | 8/10 | 登录接口真实联调成功，核心演示流程可运行，后续需继续扩大接口联调范围。 |
| 文档完整度 | 8/10 | README、API、测试结果、项目状态文档已具备，答辩材料可继续加强。 |
| GitHub 管理 | 8/10 | 项目已同步 GitHub，基础忽略规则已完善，后续可增加规范分支和发布标签。 |
| 答辩准备度 | 7/10 | 项目可演示，仍需补充 PPT、截图、测试报告和讲解稿。 |

综合评价：

FreshLife 已经达到课程项目中“可运行、可演示、具备核心业务链路”的阶段。后端核心能力较完整，前端已经完成 HarmonyOS NEXT 项目搭建、页面路由和登录接口联调。当前最适合投入精力的方向不是继续扩大后端范围，而是固化演示材料、补充测试报告、优化前端 UI，并逐步把商品、购物车和订单页面从模拟数据切换到真实接口。

## 9. 汇报结论

FreshLife 当前具备以下课程汇报价值：

- 有明确业务场景：社区生鲜购物。
- 有完整技术栈：HarmonyOS ArkTS + Spring Boot + MySQL + JWT。
- 有可运行前端：HarmonyOS 模拟器运行成功。
- 有可运行后端：Spring Boot 服务运行成功。
- 有真实联调接口：用户登录接口已完成前后端联调。
- 有数据库设计和接口文档：便于展示工程完整度。
- 有问题解决记录：能体现调试和工程实践过程。

后续工作重点应放在答辩材料、测试报告、前端 UI 优化和剩余接口联调上。
