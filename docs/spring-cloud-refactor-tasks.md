## Spring Cloud 改造任务清单（soul-frame）

> 目标：从单体 / 简单 Spring Boot 工程，演进为基于 Spring Cloud & Spring Cloud Alibaba 的规范化微服务架构。

---

### 一、工程结构与依赖管理

- **任务 1：补齐多模块聚合配置**
  - 在根 `pom.xml` 的 `<modules>` 中增加：
    - `soul-frame-api`
    - `soul-frame-modules`
  - 确认 `soul-frame-modules/pom.xml` 中包含 `soul-frame-modules-system` 等子模块。

- **任务 2：统一依赖版本来源**
  - 所有 Spring Boot / Spring Cloud / Spring Cloud Alibaba / sa-token / hutool / fastjson / lombok 等依赖的 `<version>` 只在父 POM 声明。
  - 子模块中只保留 `<groupId>` + `<artifactId>`，不再重复写版本。

- **任务 3：规范内部模块依赖方式**
  - 内部模块（`common-*`、`api-*`、`modules-*`）统一通过父 POM 的 `<dependencyManagement>` 管理版本。
  - 子模块按需引入，例如：
    - 业务服务依赖 `soul-frame-common-core`、`soul-frame-common-log`、`soul-frame-common-security`。
    - 调用系统接口的服务依赖 `soul-frame-api-system`。

---

### 二、配置中心与服务注册（Nacos 相关）

- **任务 4：统一服务名与服务常量**
  - `spring.application.name` 作为唯一服务 ID。
  - 在 `ServiceNameConstants` 中集中管理服务名，例如：
    - `soul-frame-system`
    - `soul-frame-gateway`
    - `soul-frame-file`（预留）
  - 校验所有 `@FeignClient(value = ServiceNameConstants.XXX)` 是否与 `spring.application.name` 保持一致。

- **任务 5：统一 Nacos Config 使用规范**
  - 决定配置命名与加载规则（示例）：
    - DataId：`${spring.application.name}-${spring.profiles.active}.yml`
    - Group：`${spring.profiles.active}` 或 `DEFAULT_GROUP`
    - Namespace：`${spring.profiles.active}`（dev / test / prod）
  - 所有服务 `bootstrap.yml` 采用统一方式引入 Nacos 配置，例如：
    - `spring.config.import = optional:nacos:${spring.application.name}-${spring.profiles.active}.yml`

- **任务 6：统一 Nacos Discovery 配置**
  - 在各服务的 `bootstrap.yml` 中统一 Nacos 注册中心地址与 namespace。
  - 使用同一套 `server-addr` / `namespace` / `group`，避免 profile 切换时配置不一致。

---

### 三、网关（Spring Cloud Gateway）改造任务

- **任务 7：完善全局过滤器（AuthFilter）**
  - 重新启用 `@Component`，将当前仅打印 Header 的逻辑升级为：
    - 从请求中解析 Token（sa-token / JWT），校验登录态。
    - 将用户信息（userId、username、roles 等）透传到下游服务的 Header。
    - 记录基础请求日志（traceId、IP、URI、耗时）。

- **任务 8：路由规则外移到 Nacos**
  - 将路由配置（`/system/**` → `lb://soul-frame-system` 等）从本地配置迁移到 Nacos。
  - 约定统一的路由命名和分组规范，支持后续灰度、蓝绿等发布策略。

- **任务 9：统一错误响应与鉴权异常处理**
  - 在网关层实现统一异常转换：
    - 后端 401 / 403 / 500 等 HTTP 状态统一包装为标准 JSON 响应格式。
  - 对无权限 / 未登录的请求，在网关直接拦截并返回统一错误体。

---

### 四、业务服务（soul-frame-modules-system）Cloud 化改造

- **任务 10：完善 Feign 接口契约**
  - 为 `soul-frame-api-system` 下的 Feign 接口补充：
    - `@PostMapping` / `@GetMapping` 等 HTTP 映射。
    - `@RequestBody` / `@RequestParam` 等参数注解。
  - 在 `soul-frame-modules-system` 中实现与之严格匹配的 Controller + Service。

- **任务 11：统一返回结果标准**
  - 所有对外接口使用 `Result<T>` 作为统一返回模型。
  - 在 `common-core` 中规范：
    - 成功码 / 失败码（`HttpCodeEnum`）。
    - 错误信息与错误码映射。
  - 业务服务 Controller 严格按统一格式返回，便于前端与网关统一处理。

- **任务 12：封装公共 AOP（日志、权限、异常）**
  - 在 `common-log` 中实现操作日志 AOP：
    - 拦截 Controller / Service 方法，收集操作信息，调用 `RemoteLogService` 记日志。
  - 在 `common-security` 中实现权限校验注解与拦截器：
    - 如 `@RequiresPermissions` / `@RequiresRoles` 等。
  - 在公共模块中实现统一异常处理器，避免每个服务重复写。

---

### 五、远程调用、容错与链路追踪

- **任务 13：统一启用 OpenFeign**
  - 在需要调用其它服务的模块（如 system、test、后续 file 服务）添加：
    - `@EnableFeignClients(basePackages = {"com.clm.api"})`
  - 统一配置 Feign 日志级别、超时、重试策略（集中在 Nacos 或公共配置中）。

- **任务 14：集成 Sentinel / 降级保护**
  - 为关键 Feign 调用增加 Sentinel 规则：
    - 限流、熔断、降级兜底返回。
  - 规划并实现统一的 Sentinel 降级处理类（fallback / blockHandler）。

- **任务 15：引入链路追踪（可选，但推荐）**
  - 基于 Spring Cloud 3 推荐方案（Micrometer Tracing + Zipkin / SkyWalking）。
  - 在网关过滤器中生成并透传 TraceId / SpanId。
  - 在日志中打印 TraceId，便于跨服务排查问题。

---

### 六、安全与认证体系（sa-token 为主）

- **任务 16：统一认证登录服务**
  - 在 `soul-frame-modules-system` 中实现登录 / 登出 / 刷新 Token 接口（基于 sa-token）。
  - 将用户会话信息与权限信息统一存储（Redis / DB）。

- **任务 17：网关 + 服务内双层校验**
  - 网关层负责：Token 有效性校验、黑名单过滤、基本权限校验。
  - 服务内部负责：细粒度的数据权限 / 业务权限校验。

- **任务 18：抽象权限模型与菜单/按钮权限**
  - 利用现有 `Role` / `Menu` / `UserRole` / `RoleMenu` 等实体：
    - 建立角色-菜单-按钮-接口的权限映射。
    - 在 Controller 上打权限注解，由 AOP + sa-token 动态判断。

---

### 七、环境与部署规范

- **任务 19：多环境配置与启动脚本**
  - 规划 dev / test / prod 三套配置：
    - Nacos namespace / group / dataId 规范化。
    - 日志级别 / 链路追踪 / Sentinel 等开关按环境区分。
  - 为每个服务提供统一的启动脚本 / Dockerfile（后续容器化时使用）。

- **任务 20：文档与示例更新**
  - 在 `docs` 目录中补充：
    - 各服务职责说明与交互关系图。
    - 常见调用链示例（前端 → 网关 → system → 其他服务）。
    - 本清单中任务的完成状态与变更记录。

---

> 建议实施顺序：  
> 1）工程结构和依赖管理（任务 1–3） → 2）Nacos + 配置规范（任务 4–6） → 3）网关与 Feign 契约（任务 7–12） → 4）容错与链路追踪（任务 13–15） → 5）安全体系与多环境部署（任务 16–20）。


