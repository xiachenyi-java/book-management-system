package com.study.backend.auth;

/**
 * 夏辰义
 * 2026/8/2114:44
 */
public class A3A4第三四版 {
}
/*
登录认证逻辑总结
用"银行柜台取钱"的比喻来理解这段代码：
核心问题：登录时系统怎么验证"你真的是你"？
想象你去银行柜台办理业务：
❌ 明文比对 = 银行柜员电脑上直接显示你的密码，你报一个他看一个 → 旁边人偷看一眼就完蛋
✅ BCrypt 比对 = 银行只存密码的"指纹档案"，你在密码器上输入后，后台自动比对指纹是否匹配，柜员自己也看不到密码
登录流程：四步验身份
第一步：查档案（账号是否存在）
java
if (userRepository.findByUsername(dto.getUsername()).isEmpty()) {
    throw new RuntimeException("用户名或密码错误");
}
银行场景：你报上卡号，柜员先查系统里有没有这张卡。
没有 → 柜员只说"卡号或密码错误"，不会告诉你"这卡不存在"
安全细节：不区分"用户名不存在"和"密码错误"，避免黑客通过报错信息枚举有效账号。
第二步：调取档案
java
User user = userRepository.findByUsername(dto.getUsername()).orElse(null);
银行场景：卡号存在，柜员从档案柜里调出你的开户资料。
第三步：核对密码（核心）
java
if (bCryptPasswordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
银行场景：你在密码器上输入密码，银行后台用"指纹比对机"验证：
把你输入的密码 → 用同样的模具压出指纹
和档案里存的指纹比对
一致 → 通过
matches(rawPassword, encodedPassword) 就是"指纹比对机"：不需要还原原密码，只需要验证"新输入的密码加密后是否和存档一致"。
第四步：擦除敏感信息，发放凭证
java
user.setPasswordHash(null);
return user;
银行场景：验证通过后，柜员给你办理回单，但回单上绝对不会打印银行内部保存的密码哈希。只给你看"你能看的"（用户名、ID 等），敏感字段内部抹掉。
Controller 层：接待窗口
java
@PostMapping("/login")
public Result<User> login(@RequestBody @Valid LoginDTO dto) {
    return Result.success(userService.login(dto));
}
银行场景：这是银行的业务受理窗口：
@RequestBody = 客户递进来一张填好的申请表（JSON）
@Valid = 窗口先检查表格必填项有没有空着
Result.success() = 统一信封包装，成功盖"受理成功"章
一句话总结
表格
环节	代码对应	银行柜台理解
接收请求	Controller.login()	客户到窗口递交申请表
参数校验	@Valid LoginDTO	窗口检查表格是否填完整
查账号	findByUsername().isEmpty()	柜员查系统有没有这张卡
防枚举	统一抛"用户名或密码错误"	不告诉你是卡号错还是密码错
验密码	bCryptPasswordEncoder.matches()	后台指纹比对机验证
脱敏返回	setPasswordHash(null)	回单上不打印内部密码档案
统一包装	Result.success()	统一信封，成功盖章
安全核心：登录认证就像银行取钱——只验身份，不暴露细节；比对指纹，不还原密码；返回凭证，不留敏感档案。
 */