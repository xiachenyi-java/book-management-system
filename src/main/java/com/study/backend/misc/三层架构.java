package com.study.backend.misc;

/**
 * 夏辰义
 * 2026/8/1020:59
 */
public class 三层架构 {
    /*
    Spring Boot 的三层架构是一种经典的分层设计模式，
    将应用按职责划分为三个独立层次，实现关注点分离和解耦。
    具体结构如下：1. 表现层（Controller 层）
职责：接收和响应 HTTP 请求，处理用户交互。
使用 @RestController / @Controller 定义接口
负责参数校验、数据转换（DTO ↔ Entity）
调用 Service 层处理业务，不直接操作数据库
返回统一的响应格式（如 JSON）
java
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
2. 业务层（Service 层）
职责：封装核心业务逻辑，协调各组件完成业务流程。
使用 @Service 标注
处理事务控制（@Transactional）
执行业务规则校验、复杂计算、流程编排
调用 Mapper/Repository 进行数据持久化
不直接处理 HTTP 请求，也不直接操作数据库连接
java
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO findById(Long id) {
        User user = userMapper.selectById(id);
        // 业务处理、转换...
        return convertToDTO(user);
    }
}
3. 数据访问层（DAO / Mapper / Repository 层）
职责：负责与数据库交互，执行 CRUD 操作。
使用 @Mapper（MyBatis）或继承 JpaRepository（JPA）或 @Repository
只关注 SQL/查询逻辑，不包含业务判断
返回原始数据实体（Entity）
java
@Mapper
public interface UserMapper {
    User selectById(Long id);
    int insert(User user);
}
三层之间的调用关系
plain
用户请求
    ↓
Controller（表现层）← 接收请求、返回结果
    ↓
Service（业务层）   ← 处理业务逻辑、事务
    ↓
Mapper/DAO（数据层） ← 执行 SQL、操作数据库
    ↓
数据库
分层的好处
表格
优势	说明
职责清晰	每层只关心自己的任务，代码可读性高
易于维护	修改数据库实现不影响上层业务
方便测试	可单独对 Service 做单元测试，Mock 数据层
可替换性	例如从 MyBatis 换为 JPA，只需改动数据层
团队协作	前后端、各层可并行开发
补充：有时提到的"四层"
在实际项目中，有时会扩展为四层，增加 DTO/Entity 层（模型层）：
Controller：接收/返回 DTO/VO
Service：处理业务，操作 Entity
Mapper/DAO：持久化 Entity
Model/Entity：数据实体定义
但核心思想不变：上层依赖下层，下层不依赖上层，通过接口和依赖注入实现解耦。
     */
}
