### 1.11

* TODO: 用方法句柄替换反射
* 对多IP地址使用直接代理而非SSL工厂
* 移除多IP地址的本地地址检查
* 修复本地IP地址解析问题
* 修复Mojang API轮询地址的轮换机制
* 优化issue模板
* 使用Instant类处理时间戳
* 迁移至SLF4J日志框架（修复#177）
* 使用Gson的TypeAdapter提升类型安全性
* 新增IPv6代理支持
* 共享配置实现以便于代码维护
* 使用Gson进行JSON解析（全平台兼容并消除重复代码）
* 项目代码清理
* 弃用旧的AuthMe API支持
* 移除遗留数据库迁移代码
* 停止支持RoyalAuth（项目已无维护迹象）
* 精简客户端-服务端加密流程（单连接单加密器，代码简化）

### 1.10

* 防范认证代理滥用
* 移除数据库导入工具
* 默认增强日志记录
* 新增HTTP代理支持
* 将离线UUID伪造设为最低优先级（最早执行）
* 移除Bukkit的Bungee聊天颜色支持以兼容KCauldron
* 代码检查优化 + 使用HTTPS
* 延长钩子延迟以确保ProtocolLib注入监听器
* 弃用旧版AuthMe API + 支持新版AuthMe API
* 移除eBean工具依赖以兼容1.12
* 若通过FastLogin API已设置验证插件钩子，则不再重复挂钩
* 当玩家在验证插件无记录但FastLogin数据库存在时自动注册
* 升级BungeeAuth依赖并使用新API（请更新旧版插件）
* 移除上个版本标记为过时的API方法
* 最终实现每次登录时更新IP字段
* 防止重复会话登录
* 修复新版SQLite时间戳解析问题
* 修复Spigot控制台命令执行结果误发玩家的问题

### 1.9

* 新增二次登录尝试机制（离线登录）
* 新增离线白名单（在线模式切换支持）
* 新增配置项禁用双因素认证玩家的自动登录
* 补充缺失的`add-premium-other`语言条目
* 升级至Java 8以缩减文件体积
* 大规模代码重构/清理
* [API] 弃用平台特定验证插件（请使用`AuthPlugin<平台玩家类型>`）
* [API] 弃用Bukkit密码生成器（请使用`PasswordGenerator<平台玩家类型>`）
* 修复ProtocolSupport自动注册
* 修复改名后FastLogin数据库用户名更新
* 修复加密启用时的异常日志
* 兼容旧版ProtocolLib（1.7版本缺少`getMethodAccessorOrNull`方法）
* 修复Bukkit离线权限配置
* 尝试修复SQLite时间戳解析
* 停止支持LoginSecurity 1.X（2.X已稳定）
* 通过新API方法移除UltraAuth假人补丁（建议更新UltraAuth）

### 1.8

* 新增autoIn数据库导入工具
* 新增BFA数据库导入工具
* 新增ElDziAuth数据库导入工具
* 修复非正版玩家的第三方检测
* 修复ProtocolSupport的BungeeCord兼容性
* 修复BungeeAuth用户重复登录问题

### 1.7.1

* 修复BungeeCord自动注册（修复#46）
* 修复ProtocolSupport自动注册

### 1.7

* 新增多IPv4地址轮询Mojang功能
* 新增us.mcapi.com作为备用API（规避速率限制）
* 修复BungeeCord离线会话的空指针异常
* 修复禁用正版UUID时皮肤同步问题
* 修复启用用户名检查时玩家数据保存失败
* 修复第三方插件的皮肤同步
* 切换至mcapi.ca进行UUID查询
* 修复BungeeCord未设置正版UUID
* 修复Cauldron服务器皮肤设置
* 修复改名后数据保存

### 1.6.2

* 修复新版LoginSecurity兼容性

### 1.6.1

* 修复BungeeCord提示信息拼写错误导致的空指针异常（当启用正版警告时）

### 1.6

* 新增/premium命令使用警告提示
* 补充服务器未完全启动时的缺失翻译
* ProtocolLib改为可选依赖（可使用ProtocolSupport或BungeeCord替代）
* ProtocolLib工作线程数从5减至3
* ProtocolLib异步/非阻塞处理数据包（性能提升）
* 修复命令提示缺失翻译
* 修复BungeeCord的/cracked命令失效
* 修复禁用皮肤转发时的错误

### 1.5.2

* 修复存在大厅服务器时BungeeCord强制登录
* 移除BungeeCord缓存过期机制
* 提前应用皮肤以便其他插件监听登录事件

### 1.5.1

* 通过正确保存代理ID修复BungeeCord支持

### 1.5

* 新增多语言支持
* 修复纯离线玩家正版名称检查的NPE
* 修复BungeeCord现有玩家离线登录的NPE
* 修复现有离线玩家数据保存

### 1.4

* 新增Bungee的setAuthPlugin方法
* 新增用户名变更检查
* 增强多BungeeCord支持

### 1.3.1

* 防止BungeeCord线程创建冲突

### 1.3

* 新增AuthMe 3.X支持
* 修复服务器未完全启动时的正版登录
* 为/premium和/cracked命令添加其他玩家参数
* 新增LogIt插件支持
* 移除Guava 11+特性以修复1.7兼容性（支持Cauldron）
* 修复Cauldron的BungeeCord支持

### 1.2.1

* 修复BungeeCord正版状态变更通知

### 1.2

* 修复BungeeCord竞态条件
* 修复xAuth死锁
* 新增API供插件自定义密码生成器
* 新增API供插件自定义验证插件钩子
=> 新增AdvancedLogin支持

### 1.1

* 使配置选项在BungeeCord生效（premiumUUID, forwardSkin）
* 捕获非Spigot构建的配置加载异常
* 修复旧版Spigot配置加载

### 1.0

* 重大重构以安全处理强制操作错误
* 强制方法现异步执行
* 强制方法返回布尔值反映操作状态
* isRegistered方法在查询失败时抛出异常

### 0.8

* 修复Bukkit模块的BungeeCord支持
* 新增数据库存储正版状态
* 修复/premium命令逻辑错误（感谢@NorbiPeti）
* 修复hosts文件解析问题（感谢@NorbiPeti）
* 移除引发系统错误的手写握手监听器

### 0.7

* 新增BungeeAuth支持
* 新增/premium [玩家]命令（可选参数）
* 新增正版名单重复检查
* 新增forwardSkin配置项
* 新增正版UUID支持
* 适配Spigot最新变更
* BungeeCord环境下无需Bukkit验证插件
* 优化性能与线程安全
* 修复BungeeCord支持
* 将auto-login配置项重命名为auto-register以明确用途

### 0.6

* 修复1.9版本兼容性问题
* 新增UltraAuth支持

### 0.5

* 新增/cracked命令
* 新增自动登录功能（见配置）
* 新增配置文件
* 新增isRegistered API方法
* 新增forceRegister API方法
* 修复CrazyLogin玩家数据恢复（解决内存泄漏）
* 修复ProtocolSupport正版名称检查
* 改进权限管理

### 0.4

* 新增正版皮肤转发
* 新增ProtocolSupport插件支持

### 0.3.2

* 数据包处理移至独立线程（与Netty I/O线程分离）→ 性能提升
* 修复服务器误开在线模式时的插件禁用

### 0.3.1

* 增强BungeeCord安全性

### 0.3

* 新增BungeeCord支持
* 缩短超时检测以快速响应连接问题
* 代码风格优化

### 0.2.4

* 修复无效会话的空指针异常
* 通过随机化serverId增强安全性
* 移除/premium [玩家]命令（防止未注册正版玩家盗号）

### 0.2.3

* 移除冗余的AuthMe强制登录代码
* 向客户端发送具体踢出信息（替代"Disconnect"）
* 代码格式化
* 修复假启动包线程安全问题（Bukkit.getOfflinePlayer非线程安全）
* 补充文档

### 0.2.2

* 改用Java 7编译项目

### 0.2.1

* 多项安全修复（正版玩家无法盗取离线账号）
* 新增/premium命令标记正版状态

### 0.2

* 新增CrazyLogin和LoginSecurity支持
* 实现Minecraft版本无关性
* 新增调试日志
* 代码清理
* 强化状态校验
* 改进错误处理

### 0.1

* 首次发布
