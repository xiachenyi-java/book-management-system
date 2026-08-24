package com.study.backend.misc;

/**
 * 夏辰义
 * 2026/8/915:35
 */
public class 实体类和web的注解 {
}
/*
1. 实体类（JPA 层）—— 5 个
注解	作用	一句话记忆
@Entity	告诉 JPA：这是数据库表对应的类	"Entity = 实体 = 一张表"
@Table(name="xxx")	指定表名（不写默认用类名）	"表名和类名不一样时才用"
@Id	声明主键字段	"Id = 主键"
@GeneratedValue(strategy = GenerationType.IDENTITY)	主键自增（MySQL 用）	"IDENTITY = 数据库自增"
@Column(nullable=false, length=50)	限制字段属性	"nullable=false 就是 NOT NULL"
2. 控制器（Web 层）—— 5 个
注解	作用	一句话记忆
@RestController	声明"返回 JSON 数据"的接口类	"REST 风格的控制器"
@RequestMapping("/路径")	给整个类加基础 URL 前缀	"公共路径写这里"
@GetMapping / @PostMapping / @PutMapping / @DeleteMapping	分别对应 查/增/改/删	"GET 查、POST 增、PUT 改、DELETE 删"
@PathVariable	从 URL 路径里取参数 /books/{id}	"Path = 路径"
@RequestBody	把请求体的 JSON 转成对象
@Autowired	自动注入对象（你的代码里用了）
@Repository	标记数据访问层接口（继承 JpaRepository 的接口）
@Transactional	多个数据库操作要"要么全成功、要么全失败"时用
@Query("SQL语句")	需要写自定义 SQL 或 JPQL 时
 */