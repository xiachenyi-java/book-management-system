package com.study.backend.infra;

/**
 * 夏辰义
 * 2026/8/1514:48
 */
public class A0版本清单 {
}
/*
□ 第1步：Service层抽离
□ 第2步：统一返回格式
□ 第3步：全局异常处理
□ 第4步：Lombok引入
□ 第5步：参数校验
□ 第6步：DTO分离
□ 第7步：后端搜索
□ 第8步：分页查询
□ 第9步：事务控制
□ 第10步：日志规范
□ 第11步：单元测试
□ 第12步：API文档
□ 第13步：多环境配置
□ 第14步：Docker打包
 */
/*
第 1 步：Service 层抽离
餐厅逻辑：服务员（Controller）不能直接炒菜，必须交给厨师（Service）。
Controller 只负责"接单和传菜"（接收请求、返回 Result）
Service 负责"决定怎么做菜"（业务逻辑：算序号、判断存在性、重排序）
铁律：Controller 不碰数据库，Service 不感知 HTTP
第 2 步：统一返回格式（Result）
餐厅逻辑：不管菜做得怎么样，给顾客的盘子（JSON）格式永远一样。
java
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
前端只需要解析一种结构，成功失败都统一
用泛型 <T> 保证 data 可以是任意类型
坑：不要直接返回 Entity，不要裸抛异常给前端
第 3 步：全局异常处理
餐厅逻辑：厨房着火了，不能让顾客冲进后厨看热闹。服务员统一说"稍等，正在处理"。
@RestControllerAdvice 拦截所有 Controller 异常
按异常类型分流：BusinessException → 业务错，RuntimeException → 兜底
坑：不要用裸 RuntimeException 做业务异常，要自定义
坑：log.error("xxx", e) 必须带异常对象，否则线上查不到堆栈
第 4 步：Lombok 引入
餐厅逻辑：自动生成菜单模板，不用手写每一道菜的做法。
@Data = getter + setter + toString + equals + hashCode
@Slf4j = 自动生成 log 对象
@RequiredArgsConstructor = 为 final 字段生成构造器，实现注入
坑：Entity 上慎用 @Data（可能循环引用），推荐 @Getter + @Setter + @EqualsAndHashCode(of = "id")
第 5 步：参数校验
餐厅逻辑：顾客点菜时，服务员先检查"菜名有没有写、格式对不对"，而不是直接送进厨房。
@NotBlank（仅 String，非 null 非空串非纯空格）
@NotNull（任意类型，不能为 null）
@NotEmpty（String/集合/数组，长度 > 0）
@Valid 触发校验，@Validated 支持分组校验
坑：@NotBlank 不能用在 Integer 上
坑：嵌套对象校验，内部字段也要加 @Valid
第 6 步：DTO 分离
餐厅逻辑：顾客看到的菜单（DTO）和厨房冰箱里的食材清单（Entity）不是一回事。
DTO：接收前端参数，带校验注解，字段按需定义
Entity：映射数据库表，带 JPA 注解
VO：返回给前端，可隐藏敏感字段（密码、逻辑删除标记）
原则：分层传输，不要直接拿 Entity 给前端
第 7 步：后端搜索
餐厅逻辑：顾客说"我要带'辣'字的菜"，厨师去仓库找所有包含"辣"的食材。
findByNameContaining(keyword) → 生成 LIKE %keyword%
坑：大数据量时 %xx% 不走索引，生产环境要用 Elasticsearch 或全文检索
第 8 步：分页查询
餐厅逻辑：顾客不要一次性上 1000 道菜，要一页一页上。
PageRequest.of(page, size) 创建分页参数
返回 Page<T>，自带总页数、总条数
坑：Spring Data 页码从 0 开始，前端通常从 1 开始，Service 层要做 page - 1
坑：要限制 size 最大值（如 100），防止前端传 99999 拖垮数据库
第 9 步：事务控制
餐厅逻辑：一桌菜要么全上，要么全不上。不能上了汤发现主材没了，让顾客干等着。
@Transactional 把多步数据库操作打包成原子操作
默认回滚 RuntimeException，不回滚受检异常
坑：同类方法内部调用 @Transactional 不生效（AOP 代理限制）
坑：查询方法加 readOnly = true 提升性能
第 10 步：日志规范
餐厅逻辑：厨房要有监控，记录谁点了什么、哪道菜做失败了。
log.info("用户{}登录", username) 占位符性能优于字符串拼接
业务异常 → warn，系统异常 → error（必须带堆栈）
坑：只配 logging.file.name 不配 max-file-size，日志会无限膨胀占满磁盘
第 11 步：单元测试
餐厅逻辑：正式营业前，厨师先试做一遍，确保味道对。
@SpringBootTest + @Transactional = 集成测试，跑完自动回滚不污染数据库
assertThrows = JUnit 5 测异常的利器
坑：测试方法要自给自足，不要依赖其他测试产生的数据
进阶：Service 层用 Mockito 做纯单元测试，毫秒级，不启动 Spring
第 12 步：API 文档（Knife4j）
餐厅逻辑：给顾客一本菜单，上面写了每道菜是什么、怎么做。
@Tag + @Operation = 接口分类和描述
访问 /doc.html 查看增强版 Swagger
坑：生产环境要关掉（knife4j.enable=false），不能暴露给外人
第 13 步：多环境配置
餐厅逻辑：家厨房、试营业店、正式门店，三套操作手册。
表格
环境	数据库	DDL-auto	日志	密码
dev	localhost	update	DEBUG	明文/简单
prod	阿里云 RDS	validate	WARN	${DB_PASSWORD} 环境变量
spring.profiles.active=dev 激活环境
命令行覆盖：java -jar app.jar --spring.profiles.active=prod
铁律：生产密码绝不写死在配置文件里提交 Git
铁律：生产绝不用 ddl-auto=update，用 validate + Flyway

 */