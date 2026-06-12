# FreshLife Project Audit

审计日期：2026-06-12

审计范围：Git、项目结构、Spring Boot 后端、HarmonyOS 前端、安全、数据库、文档、可维护性。

审计原则：只分析，不修改业务代码。目标是帮助项目尽快上线并具备商业价值。

## 1. 总体评价

FreshLife 当前已经具备 MVP 项目的基本骨架：后端采用 Spring Boot 单体架构，数据库覆盖用户、商品、购物车、订单、订单明细五张核心表，前端具备 HarmonyOS 页面原型，文档也覆盖了 README、API、测试结果和阶段状态。

整体判断：项目适合作为社区生鲜电商 MVP 原型继续推进，但还不能直接按“可运营产品”上线。

主要原因有三点：

1. 后端订单状态存在代码与数据库不一致问题，会影响支付后订单流转。
2. 前端仍大量使用 Mock 数据，真实前后端闭环没有完全落到商品、购物车、订单页面。
3. 生产配置、安全配置、自动化测试、CI/CD 还不完整。

本次后端执行 `mvn test` 结果为 `BUILD SUCCESS`，但当前无测试用例，不能证明业务链路稳定。HarmonyOS 本机未检测到 `hvigor` 或 `ohpm` 命令，无法通过命令行复验 Build Success；配置文件显示目标 SDK 为 HarmonyOS `6.1.1(24)`。

## 2. 优点

- 架构选择符合 MVP 阶段：Spring Boot 单体、MySQL、Redis、HarmonyOS ArkTS，没有引入微服务、Kafka、Kubernetes 等高成本组件。
- 后端分层清晰：Controller、Service、Mapper、Entity、DTO、VO、Config、Exception、Utils 目录完整。
- 数据库表覆盖下单主链路：`user`、`product`、`cart`、`orders`、`order_item`。
- MyBatis Plus 逻辑删除、分页、基础 CRUD 使用较直接，开发速度快。
- 商品列表和商品详情接入 Redis 缓存，符合生鲜高频浏览场景。
- README、API、PROJECT-STATUS、TEST-RESULT 文档齐全，便于交接和答辩展示。
- `.gitignore` 已覆盖 `target/`、`.hvigor/`、`oh_modules/`、`build/`、`outputs/`、`local.properties` 等常见产物。

## 3. 问题

### 3.1 Git 检查

执行结果：

```text
git status
On branch main
Your branch is up to date with 'origin/main'.
nothing to commit, working tree clean
```

最近 10 次提交：

```text
e1ab3e8 feat: complete FreshLife HarmonyOS frontend and backend integration
1f580c3 Complete backend core modules and documentation
a5dcd76 Add files via upload
aaa4314 Add files via upload
b9ec152 Add files via upload
03b1568 Add files via upload
f548da5 Revise README with project details and updates
3b0f9b5 Revise README for project overview and tech stack
b5b00b7 feat: initialize FreshLife backend docs
```

发现：

- 当前工作区干净，没有未提交业务代码。
- 提交历史里存在多次 `Add files via upload`，不符合项目提交规范，建议后续统一使用 `feat:`、`fix:`、`refactor:`、`docs:`。
- 未发现 Git 已追踪的大文件超过 1MB。
- 本地存在未追踪或被忽略的大文件/产物，例如 `backend/target/freshlife-backend-0.0.1-SNAPSHOT.jar` 约 38MB、前端 `.hvigor` 报告、`entry/build` 产物、`outputs/` PPT 文件。这些已被 `.gitignore` 忽略，不应提交。
- 多个 `.gitkeep` 已被追踪，但对应目录已有实际代码，建议后续清理。

### 3.2 项目结构检查

目录整体符合约定：

```text
backend/
frontend/
database/
docs/
README.md
```

发现：

- 本地存在 `.DS_Store`：根目录、`backend/`、`backend/src/`、`docs/` 等位置。当前未被 Git 追踪，但应清理本地文件。
- 本地存在 IDE 目录：`backend/.idea/`、`frontend/.idea/`。当前未被 Git 追踪，但不应上传。
- 本地存在构建产物：`backend/target/`、`frontend/.hvigor/`、`frontend/entry/build/`、`frontend/oh_modules/`、`outputs/`。当前被忽略，建议定期清理。
- 根目录 `PROJECT-STATUS.md` 与 `docs/PROJECT-STATUS.md` 内容完全重复。
- 根目录 `TEST-RESULT.md` 与 `docs/TEST-RESULT.md` 内容完全重复。
- `backend/src/main/java/com/freshlife/**/.gitkeep` 在目录已有代码后已无意义，可清理。

### 3.3 Spring Boot 检查

配置文件：`backend/src/main/resources/application.yml`

发现：

- 数据库连接配置直接写在配置文件中：`username: freshlife`、`password: freshlife123456`。
- Redis 配置为本地 `localhost:6379`，适合开发，不适合生产直接使用。
- JWT Secret 直接硬编码：`freshlife-jwt-secret-key-change-me-2026`。
- MyBatis Plus 开启 `StdOutImpl` SQL 日志，开发期方便，生产环境会泄露 SQL 和影响日志质量。
- 日志级别为 `debug`，生产建议调整为 `info` 或按模块配置。

代码风险：

| 位置 | 问题 | 风险等级 |
| --- | --- | --- |
| `OrderServiceImpl` | 代码使用 `PAID` 作为支付后状态，但数据库 `orders.status` 枚举没有 `PAID`，只有 `WAIT_DELIVERY`、`DELIVERING` 等 | 高 |
| `OrderServiceImpl` | 取消订单会恢复库存，但没有限制只恢复当前用户订单明细，依赖前一步更新成功，整体还可接受 | 中 |
| `ProductServiceImpl` | 商品列表缓存后，如果商品库存或上下架变化，没有缓存失效逻辑 | 中 |
| `CartServiceImpl` | 加入购物车只校验商品上架，不校验库存上限，可能出现购物车数量超过库存 | 中 |
| `UserServiceImpl` | 注册只校验 username，不主动校验 phone 唯一性，数据库唯一索引会抛异常 | 低 |
| 全局异常 | 业务异常统一返回 HTTP 200，前端需要始终读取业务 code；对真实客户端不够标准 | 低 |

后端构建：

```text
mvn test
BUILD SUCCESS
No tests to run.
```

结论：后端可编译，但测试覆盖为空。订单状态不一致是上线前必须修复的问题。

### 3.4 HarmonyOS 检查

关键配置：

- `frontend/build-profile.json5`：`compatibleSdkVersion` 和 `targetSdkVersion` 均为 `6.1.1(24)`。
- `frontend/hvigorfile.ts`：使用 `@ohos/hvigor-ohos-plugin` 的 `appTasks`。
- `frontend/entry/hvigorfile.ts`：使用 `hapTasks`。
- `frontend/entry/src/main/module.json5`：Stage Model、entry module、phone device 配置正常。
- `frontend/entry/src/main/resources/base/profile/main_pages.json`：入口页只有 `pages/LoginPage`。

发现：

- 本机未检测到 `hvigor`、`ohpm` 命令，也无 `hvigorw` wrapper，因此本次无法命令行确认 HarmonyOS Build Success。
- 页面路由集中写在 `LoginPage.ets` 的 `NavigationDestination`，能跑原型，但长期维护不够清晰。
- `ProductListPage`、`ProductDetailPage`、`CartPage`、`OrderPage` 都通过 `LoginPage` 动态路由引用，没有发现完全未引用页面。
- `ProductDetailPage` 没有读取列表传入的商品 id，始终展示 `MockDataService.getProductDetail()` 的第一个商品。
- 商品列表、商品详情、购物车、订单页面仍使用 `MockDataService`，只有登录调用真实接口。
- `HttpService.login()` 硬编码 `tony / 123456`，没有使用登录页输入框里的 username/password。
- `HttpService` baseURL 固定为 `http://127.0.0.1:8080/api`，真机或生产环境不可用。
- Token 存储使用 `AppStorage`，适合原型，不适合长期安全存储。

结论：HarmonyOS 配置看起来兼容 6.1.1，但当前前端更偏演示原型，不能认为已经完成真实用户下单闭环。

### 3.5 安全检查

风险：

| 风险项 | 位置 | 风险等级 | 说明 |
| --- | --- | --- | --- |
| JWT Secret 硬编码 | `application.yml` | 高 | 上线后 Secret 泄露会导致 Token 可被伪造 |
| 数据库密码提交 | `application.yml`、`README.md` | 高 | 即使是开发密码，也会形成坏习惯 |
| 前端测试账号密码硬编码 | `LoginPage.ets`、`HttpService.ets` | 中 | 影响真实登录流程，泄露测试账号 |
| Token 存储过弱 | `AuthTokenStore.ets` | 中 | `AppStorage` 不是安全凭据存储方案 |
| HTTP 明文接口 | `HttpService.ets` | 中 | 生产必须使用 HTTPS |
| SQL Debug 日志 | `application.yml` | 中 | 生产可能输出敏感 SQL 或参数 |

未发现真实云厂商 AccessKey、OSS Key、GitHub Token 等明显泄露。

### 3.6 数据库检查

核心表检查：

- `user`：字段简单，包含 username、password、phone、nickname、avatar、status、逻辑删除字段。索引基本合理。
- `product`：字段覆盖商品浏览和库存，索引覆盖 category/status/deleted、name、status/stock。
- `cart`：缺少 `(user_id, product_id, deleted)` 唯一约束，可能出现同一用户同一商品多条有效购物车记录。
- `orders`：字段覆盖订单金额、状态、收货信息、时间节点。当前最大问题是状态枚举与代码不一致。
- `order_item`：保留商品下单快照字段，设计合理，不属于冗余。

建议：

- 将 `orders.status` 与代码统一。MVP 阶段建议使用 `WAIT_PAY -> WAIT_DELIVERY -> DELIVERING -> FINISHED/CANCELLED`，不要再使用未定义的 `PAID`。
- `cart` 增加唯一约束：`uk_cart_user_product_deleted(user_id, product_id, deleted)`，避免重复购物车项。
- 高频查询可补充 `orders(user_id, deleted, create_time)` 或调整现有索引。
- 商品名称搜索目前 `LIKE %keyword%` 对普通索引帮助有限，MVP 可以接受，数据量上来后再考虑搜索方案。

### 3.7 文档检查

检查文件：

- `README.md`
- `docs/API.md`
- `docs/TEST-RESULT.md`
- `docs/PROJECT-STATUS.md`

发现：

- README 声称“前后端联调成功”“用户端核心购物流程已跑通”，但当前前端除登录外仍主要使用 Mock 数据，描述偏乐观。
- `docs/PROJECT-STATUS.md` 的“当前未完成内容”仍写着 HarmonyOS 页面未完成，但代码中已经存在前端页面，文档未同步。
- `docs/API.md` 的订单完成接口写明完成状态依赖 `PAID`，这与数据库枚举不一致。
- 根目录和 `docs/` 下存在重复的 `PROJECT-STATUS.md`、`TEST-RESULT.md`。
- `docs/PRD.md`、`docs/ERD.md` 中部分字段名为 `password_hash`，当前数据库实际是 `password`，存在命名不一致。

### 3.8 可维护性检查

主要问题：

- 自动化测试缺失，后端虽然能编译，但无法保障注册、登录、购物车、订单状态流转。
- 前端 Mock 与真实接口混用，容易让“演示成功”和“真实联调成功”混淆。
- 环境配置没有分层，开发、测试、生产共用一个 `application.yml` 风险高。
- 没有 CI/CD 配置，无法自动发现构建失败和测试回归。
- 订单状态建议抽成枚举或常量集中管理，并与数据库脚本保持同源。

## 4. 风险

### 高风险

1. 订单支付状态不一致：代码写入 `PAID`，数据库枚举无 `PAID`，支付接口可能失败，订单无法进入完成流程。
2. JWT Secret 和数据库密码硬编码：不能直接用于生产或公开仓库。
3. 前端真实下单闭环未完成：页面仍读 Mock 数据，当前不具备真实用户购物体验。

### 中风险

1. 无自动化测试：无法保障每次改动后核心链路可用。
2. Redis 缓存无失效策略：商品变化后用户可能看到旧数据。
3. 生产日志配置过宽：SQL 和 debug 日志可能影响性能与安全。
4. `cart` 缺少唯一约束：并发或异常情况下可能出现重复购物车项。

### 低风险

1. `.DS_Store`、`.idea`、构建产物存在于本地目录，虽未被追踪，但建议清理。
2. 重复文档增加维护成本。
3. 多余 `.gitkeep` 已无实际作用。

## 5. 优化建议

### 上线前必须做

1. 统一订单状态：建议把支付后状态改为 `WAIT_DELIVERY`，完成订单从 `WAIT_DELIVERY` 或 `DELIVERING` 流转到 `FINISHED`。
2. 移除配置硬编码：数据库密码、JWT Secret、Redis 地址改为环境变量或不同 profile。
3. 前端去 Mock：商品列表、商品详情、购物车、创建订单、订单列表全部接真实接口。
4. 增加最小自动化测试：至少覆盖登录、加入购物车、创建订单、支付订单、完成订单。
5. 生产环境关闭 SQL StdOut 和 debug 日志。

### MVP 阶段建议做

1. 清理重复文档：保留 `docs/PROJECT-STATUS.md`、`docs/TEST-RESULT.md`，根目录只保留 README。
2. 清理 `.gitkeep`、`.DS_Store`、本地构建产物。
3. 为前端增加环境配置：开发地址、测试地址、生产地址分开。
4. 补充管理后台的最小商品管理和订单管理能力。
5. 增加简单 CI：后端执行 `mvn test`，前端至少做 HarmonyOS 构建检查。

### MVP 后优化

1. 商品搜索增强、AI 商品文案生成、AI 客服、运营文案生成。
2. 真实支付、支付回调、退款和售后。
3. 商家接单、配送员接单、配送状态流转。
4. 数据分析：热销商品、滞销商品、复购率和运营建议。

## 6. 项目评分

| 维度 | 分数 | 说明 |
| --- | ---: | --- |
| 项目结构 | 82 | 目录清晰，MVP 分层明确；本地冗余产物和重复文档需清理 |
| 代码质量 | 70 | 后端可编译，分层可读；订单状态不一致和测试缺失拉低评分 |
| 文档质量 | 72 | 文档齐全，但部分内容与当前代码、前端真实状态不一致 |
| 前后端联调 | 58 | 登录接口真实联调，其余核心页面仍 Mock，不能算完整闭环 |
| 可维护性 | 66 | 单体架构适合快速上线，但配置分层、测试、CI、状态枚举需要补齐 |
| 课程项目完成度 | 88 | 作为课程/答辩项目完成度高，结构和展示材料较完整 |

综合评分：73 / 100

结论：适合作为 MVP 原型继续推进；若目标是实际运营上线，建议先修复订单状态、安全配置和前端真实接口接入，再进入小范围试运营。
