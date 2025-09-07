# 金融时序数据预测系统 - 详细文档

## 目录
1. [项目概述](#项目概述)
2. [项目结构](#项目结构)
3. [核心配置文件详解](#核心配置文件详解)
4. [Java源代码文件详解](#java源代码文件详解)
5. [数据库相关文件](#数据库相关文件)
6. [Java基础语法说明](#java基础语法说明)

---

## 项目概述

这是一个基于 **Spring Boot** 框架开发的金融时序数据预测和异常检测系统。系统采用前后端分离架构，提供 RESTful API 接口，支持用户认证、金融数据管理、预测算法调用等功能。

### 技术栈
- **后端框架**: Spring Boot 2.7.0
- **数据库**: MySQL 8.0
- **ORM框架**: Spring Data JPA (Hibernate)
- **认证**: JWT (JSON Web Token)
- **构建工具**: Maven
- **编程语言**: Java 11

---

## 项目结构

```
FinancialForecastingSystem/
├── pom.xml                              # Maven项目配置文件
├── src/
│   ├── main/
│   │   ├── java/com/financial/         # Java源代码目录
│   │   │   ├── FinancialForecastingApplication.java  # 主启动类
│   │   │   ├── Hello.java              # 测试控制器
│   │   │   ├── config/                 # 配置类目录
│   │   │   │   ├── AuthInterceptor.java
│   │   │   │   ├── RestTemplateConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/             # 控制器层（处理HTTP请求）
│   │   │   │   ├── AlgorithmController.java
│   │   │   │   ├── FinancialDataController.java
│   │   │   │   └── UserController.java
│   │   │   ├── entity/                 # 实体类（数据库表映射）
│   │   │   │   ├── FinancialData.java
│   │   │   │   ├── PredictionResult.java
│   │   │   │   └── User.java
│   │   │   ├── repository/             # 数据访问层（数据库操作）
│   │   │   │   ├── FinancialDataRepository.java
│   │   │   │   ├── PredictionResultRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/                # 业务逻辑层
│   │   │   │   ├── AlgorithmService.java
│   │   │   │   ├── FinancialDataService.java
│   │   │   │   ├── PredictionResultService.java
│   │   │   │   └── UserService.java
│   │   │   └── util/                   # 工具类
│   │   │       ├── DataUtil.java
│   │   │       └── JwtUtil.java
│   │   └── resources/                  # 资源文件目录
│   │       ├── application.yml         # Spring Boot配置文件
│   │       └── sql/
│   │           └── init.sql           # 数据库初始化SQL
└── target/                             # 编译输出目录
```

---

## 核心配置文件详解

### 1. pom.xml - Maven项目配置文件

**作用**: 定义项目的依赖、构建配置和项目信息

**关键内容解释**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <!-- Maven模型版本，固定为4.0.0 -->
    <modelVersion>4.0.0</modelVersion>
    
    <!-- 项目标识：组织ID.项目ID.版本号 -->
    <groupId>com.financial</groupId>  <!-- 组织/公司标识 -->
    <artifactId>financial-forecasting-system</artifactId>  <!-- 项目唯一标识 -->
    <version>1.0.0</version>  <!-- 项目版本 -->
    <packaging>jar</packaging>  <!-- 打包方式：jar包 -->
    
    <!-- 项目属性配置 -->
    <properties>
        <maven.compiler.source>11</maven.compiler.source>  <!-- Java源代码版本 -->
        <maven.compiler.target>11</maven.compiler.target>  <!-- 编译目标版本 -->
        <spring.boot.version>2.7.0</spring.boot.version>  <!-- Spring Boot版本 -->
    </properties>
    
    <!-- 依赖管理：管理所有依赖的版本 -->
    <dependencyManagement>...</dependencyManagement>
    
    <!-- 项目依赖列表 -->
    <dependencies>
        <!-- JWT认证相关依赖 -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        
        <!-- Spring Boot核心依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <!-- 包含Web开发所需的所有基础组件 -->
        </dependency>
        
        <!-- JPA数据库ORM框架 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <!-- MySQL数据库驱动 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
    </dependencies>
    
    <!-- 构建配置 -->
    <build>
        <plugins>
            <!-- Spring Boot打包插件 -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <!-- 指定主类 -->
                    <mainClass>com.financial.FinancialForecastingApplication</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 2. application.yml - Spring Boot配置文件

**作用**: 配置应用程序的各种参数

**语法说明**: YAML格式，使用缩进表示层级关系

```yaml
# 服务器配置
server:
  port: 8080  # 应用监听端口

# Spring框架配置
spring:
  application:
    name: financial-forecasting-system  # 应用名称
  
  # 数据库连接配置
  datasource:
    url: jdbc:mysql://localhost:3306/financial_forecasting  # 数据库连接URL
    username: fin  # 数据库用户名
    password: fin123  # 数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver  # MySQL驱动类
  
  # JPA/Hibernate配置
  jpa:
    hibernate:
      ddl-auto: update  # 自动更新数据库表结构
    show-sql: true  # 控制台显示SQL语句
    properties:
      hibernate:
        format_sql: true  # 格式化SQL输出
        dialect: org.hibernate.dialect.MySQL8Dialect  # MySQL方言

# 自定义算法配置
financial:
  algorithm:
    local-model-path: models/local_model.py  # Python模型路径
    python-exec: python  # Python执行命令
    prediction-window: 30  # 预测窗口大小
    anomaly-threshold: 0.95  # 异常检测阈值

# 日志配置
logging:
  level:
    com.financial: DEBUG  # 包日志级别
  file:
    name: logs/financial-forecasting.log  # 日志文件路径
```

---

## Java源代码文件详解

### 核心概念说明

#### Java注解（Annotations）
注解是以`@`开头的特殊标记，用于提供元数据：
- `@SpringBootApplication`: 标记主启动类
- `@RestController`: 标记REST API控制器
- `@Service`: 标记业务服务类
- `@Repository`: 标记数据访问类
- `@Entity`: 标记数据库实体类
- `@Autowired`: 自动注入依赖

### 1. 主启动类 - FinancialForecastingApplication.java

**作用**: Spring Boot应用程序的入口点

```java
package com.financial;  // 包声明：定义类所在的包路径

import org.springframework.boot.SpringApplication;  // 导入Spring启动类
import org.springframework.boot.autoconfigure.SpringBootApplication;  // 导入注解

@SpringBootApplication  // 核心注解：标记这是Spring Boot应用主类
public class FinancialForecastingApplication {  // 类声明：public表示公开访问
    
    // main方法：Java程序入口
    public static void main(String[] args) {
        // 启动Spring Boot应用
        SpringApplication.run(FinancialForecastingApplication.class, args);
    }
}
```

**语法解释**:
- `package`: 定义包名，用于组织代码
- `import`: 导入其他包中的类
- `public class`: 定义公开类
- `public static void main(String[] args)`: Java程序标准入口方法
  - `public`: 公开访问权限
  - `static`: 静态方法，不需要创建对象即可调用
  - `void`: 无返回值
  - `String[] args`: 命令行参数数组

### 2. 实体类 - User.java

**作用**: 映射数据库中的users表

```java
package com.financial.entity;

import javax.persistence.*;  // JPA注解包
import java.time.LocalDateTime;  // 日期时间类

@Entity  // 标记为JPA实体类
@Table(name = "users")  // 指定对应的数据库表名
public class User {
    
    @Id  // 标记为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增主键
    private Long id;  // 用户ID，Long是长整型
    
    @Column(nullable=false, unique=true, length=64)  // 列定义：非空、唯一、长度64
    private String username;  // 用户名，String是字符串类型
    
    @Column(nullable=false, length=128)
    private String password;  // 密码
    
    @Column(name="created_time", nullable=false)  // 数据库列名映射
    private LocalDateTime createdTime;  // 创建时间
    
    @PrePersist  // 在持久化之前执行
    public void prePersist() {
        if (createdTime == null) createdTime = LocalDateTime.now();
    }
    
    // Getter和Setter方法：用于访问私有属性
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    // ... 其他getter/setter方法
}
```

**语法解释**:
- `private`: 私有访问权限，只能在类内部访问
- `Long`: 包装类型，可以为null
- `@Column`: 定义数据库列属性
  - `nullable`: 是否可为空
  - `unique`: 是否唯一
  - `length`: 字段长度
- Getter/Setter: Java Bean规范，用于封装属性访问

### 3. 实体类 - FinancialData.java

**作用**: 映射金融数据表

```java
@Entity
@Table(name = "financial_data")
public class FinancialData {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable=false, length=32)
    private String symbol;  // 股票/期货代码
    
    @Column(name="date_time", nullable=false)
    private LocalDateTime dateTime;  // 数据时间
    
    @Column(name="bid_price", precision=18, scale=6)  // 精度18位，小数6位
    private BigDecimal bidPrice;  // 买入价，BigDecimal用于精确的金额计算
    
    @Column(name="bid_order_qty")
    private Long bidOrderQty;  // 买入订单量
    
    @Column(name="bid_executed_qty")
    private Long bidExecutedQty;  // 买入成交量
    
    // ... getter/setter方法
}
```

**数据类型说明**:
- `BigDecimal`: 用于精确的十进制数计算，避免浮点数精度问题
- `LocalDateTime`: Java 8的日期时间类型
- `Long`: 长整型，用于存储大数值

### 4. 控制器类 - UserController.java

**作用**: 处理用户相关的HTTP请求

```java
@RestController  // 标记为REST控制器，返回JSON数据
@RequestMapping("/users")  // 基础路径映射
@CrossOrigin(origins="*")  // 允许跨域访问
public class UserController {
    
    private final UserService userService;  // 业务服务依赖
    private final JwtUtil jwtUtil;  // JWT工具依赖
    
    // 构造函数注入（推荐方式）
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * 登录接口
     * POST /users/login
     */
    @PostMapping("/login")  // 处理POST请求
    public ResponseEntity<?> login(@RequestBody Map<String,String> req) {
        // @RequestBody: 将请求体JSON转换为Map对象
        try {
            // 调用服务层进行登录验证
            User u = userService.login(req.get("username"), req.get("password"));
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(u.getUsername(), u.getId());
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("user", u);
            response.put("token", token);
            
            // 返回HTTP 200 OK响应
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // 异常处理：返回400错误
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 注册接口
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> req) {
        User u = userService.register(req.get("username"), req.get("password"));
        // Map.of(): Java 9+的静态工厂方法，创建不可变Map
        return ResponseEntity.ok(Map.of("success", true, "message", "注册成功", "user", u));
    }
}
```

**语法解释**:
- `@RestController`: 组合注解 = `@Controller` + `@ResponseBody`
- `@RequestMapping`: URL路径映射
- `@PostMapping`: 处理POST请求
- `@RequestBody`: 请求体数据绑定
- `ResponseEntity<?>`: Spring的HTTP响应包装类，`?`表示泛型通配符
- `Map<String, Object>`: 键值对集合，用于构建JSON响应
- `try-catch`: 异常处理机制

### 5. 控制器类 - AlgorithmController.java

**作用**: 处理算法预测相关请求

```java
@RestController
@RequestMapping("/algorithms")
@CrossOrigin(origins = "*")
public class AlgorithmController {
    
    @Autowired  // 自动注入依赖
    private AlgorithmService algorithmService;
    
    /**
     * 获取可用算法列表
     * GET /algorithms/available
     */
    @GetMapping("/available")  // 处理GET请求
    public ResponseEntity<List<String>> getAvailableAlgorithms() {
        List<String> algorithms = algorithmService.getAvailableAlgorithms();
        return ResponseEntity.ok(algorithms);
    }
    
    /**
     * 执行本地预测
     * POST /algorithms/predict/local
     */
    @PostMapping("/predict/local")
    public ResponseEntity<Map<String, Object>> executeLocalPrediction(
            @RequestBody Map<String, Object> request) {
        try {
            // 从请求中提取参数
            String symbol = (String) request.get("symbol");  // 类型转换
            String algorithmName = (String) request.get("algorithmName");
            String feature = (String) request.getOrDefault("feature", null);  // 默认值
            
            // 调用服务执行预测
            Map<String, Object> result = algorithmService.executeLocalPrediction(
                symbol, algorithmName, feature);
            
            // 构建成功响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "本地预测执行成功");
            response.put("result", result);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // 构建错误响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "本地预测执行失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);  // 500错误
        }
    }
}
```

### 6. 服务类 - UserService.java

**作用**: 用户业务逻辑处理

```java
@Service  // 标记为服务组件
public class UserService {
    
    private final UserRepository userRepository;  // 数据访问层依赖
    
    // 构造函数注入
    public UserService(UserRepository userRepository) { 
        this.userRepository = userRepository; 
    }
    
    /**
     * 用户注册
     */
    public User register(String username, String password) {
        // 检查用户名是否存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");  // 抛出运行时异常
        }
        
        // 创建新用户
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);  // 注意：实际应用应加密密码
        
        // 保存到数据库
        return userRepository.save(u);
    }
    
    /**
     * 用户登录
     */
    public User login(String username, String password) {
        // 查找用户，返回Optional类型
        Optional<User> opt = userRepository.findByUsername(username);
        
        // 验证用户存在且密码正确
        if (opt.isPresent() && opt.get().getPassword().equals(password)) {
            return opt.get();
        }
        
        throw new RuntimeException("用户名或密码错误");
    }
    
    // 其他业务方法
    public Optional<User> byId(Long id) { 
        return userRepository.findById(id); 
    }
}
```

**语法解释**:
- `Optional<User>`: Java 8引入的容器类，用于避免null引用
- `throw new RuntimeException()`: 抛出异常
- `opt.isPresent()`: 检查Optional是否有值
- `opt.get()`: 获取Optional中的值

### 7. 服务类 - AlgorithmService.java

**作用**: 算法集成和调用服务

```java
@Service
public class AlgorithmService {
    
    private static final Logger log = LoggerFactory.getLogger(AlgorithmService.class);
    
    @Autowired
    private PredictionResultService predictionResultService;
    
    @Autowired
    private RestTemplate restTemplate;  // HTTP客户端
    
    // 从配置文件注入值
    @Value("${financial.algorithm.local-model-path}")
    private String localModelPath;
    
    @Value("${financial.algorithm.prediction-window}")
    private int predictionWindow;
    
    /**
     * 执行本地预测算法
     */
    public Map<String, Object> executeLocalPrediction(String symbol, String algorithmName) {
        try {
            // 调用本地Python模型
            Map<String, Object> result = callLocalModel(symbol, algorithmName, null);
            
            // 创建预测结果实体
            PredictionResult predictionResult = new PredictionResult();
            predictionResult.setSymbol(symbol);
            predictionResult.setAlgorithmName(algorithmName);
            predictionResult.setPredictionTime(LocalDateTime.now());
            
            // 解析预测值
            Object predictedValueObj = result.get("predicted_value");
            if (predictedValueObj != null) {
                // 安全的类型转换
                if (predictedValueObj instanceof Number) {
                    predictionResult.setPredictedValue(
                        new BigDecimal(predictedValueObj.toString()));
                }
            }
            
            // 保存到数据库
            predictionResultService.save(predictionResult);
            
            return result;
            
        } catch (Exception e) {
            log.error("本地预测执行失败: {}", e.getMessage(), e);  // 记录日志
            throw new RuntimeException("本地预测执行失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 调用本地Python模型
     */
    private Map<String, Object> callLocalModel(String symbol, String algorithmName, String feature) {
        try {
            // 构建路径
            Path scriptPath = Paths.get(this.localModelPath).normalize();
            
            // 创建进程构建器，执行Python脚本
            ProcessBuilder pb = new ProcessBuilder(
                "python",
                scriptPath.toString(),
                "--data", dataPath.toString(),
                "--window", String.valueOf(this.predictionWindow),
                "--algorithm", algorithmName,
                "--feature", feature
            );
            
            // 启动进程
            Process p = pb.start();
            
            // 读取输出
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                
                // 等待进程结束
                int exit = p.waitFor();
                if (exit != 0) {
                    throw new RuntimeException("模型执行失败，退出码: " + exit);
                }
                
                // 解析JSON响应
                Map<String, Object> parsed = JSON.parseObject(sb.toString(), Map.class);
                return parsed;
            }
            
        } catch (Exception ex) {
            throw new RuntimeException("调用本地模型失败: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * 获取可用算法列表
     */
    public List<String> getAvailableAlgorithms() {
        return List.of(  // Java 9+ 不可变列表
            "SINGLE_LSTM",
            "SINGLE_TRANSFORMER",
            "SERIAL_LSTM_TRANSFORMER",
            "FUSION_LSTM_TRANSFORMER"
        );
    }
}
```

**高级语法解释**:
- `Logger`: SLF4J日志框架
- `@Value`: 从配置文件注入值
- `ProcessBuilder`: 创建操作系统进程
- `try-with-resources`: 自动资源管理，自动关闭流
- `Path/Paths`: Java NIO路径操作
- `List.of()`: Java 9+创建不可变列表

### 8. 数据访问层 - UserRepository.java

**作用**: 定义数据库操作接口

```java
package com.financial.repository;

import com.financial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// 继承JpaRepository，自动获得CRUD操作
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository<实体类型, 主键类型>
    
    // Spring Data JPA自动实现这些方法
    Optional<User> findByUsername(String username);  // 根据用户名查找
    boolean existsByUsername(String username);  // 检查用户名是否存在
}
```

**Spring Data JPA方法命名规则**:
- `findBy` + 属性名: 查询
- `existsBy` + 属性名: 检查存在性
- `deleteBy` + 属性名: 删除
- `countBy` + 属性名: 计数

### 9. 数据访问层 - FinancialDataRepository.java

```java
public interface FinancialDataRepository extends JpaRepository<FinancialData, Long> {
    
    // 分页查询指定代码的数据
    Page<FinancialData> findBySymbol(String symbol, Pageable pageable);
    
    // 查询时间范围内的数据，按时间升序
    List<FinancialData> findBySymbolAndDateTimeBetweenOrderByDateTimeAsc(
        String symbol, LocalDateTime start, LocalDateTime end);
    
    // 查询最新的一条记录
    FinancialData findTop1BySymbolOrderByDateTimeDesc(String symbol);
}
```

**方法命名解析**:
- `findBy`: 查询前缀
- `Symbol`: 按symbol字段
- `And`: 条件连接
- `DateTimeBetween`: 时间范围查询
- `OrderByDateTimeAsc`: 按dateTime升序
- `Top1`: 限制返回1条记录

### 10. 配置类 - AuthInterceptor.java

**作用**: JWT认证拦截器

```java
@Component  // 标记为Spring组件
public class AuthInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    
    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override  // 重写接口方法
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) {
        
        // 定义公开路径（不需要认证）
        String[] publicPaths = {
            "/api/users/login",
            "/api/users/register",
            "/error"
        };
        
        // 检查是否是公开路径
        String requestPath = request.getRequestURI();
        for (String publicPath : publicPaths) {  // for-each循环
            if (requestPath.startsWith(publicPath)) {
                return true;  // 允许访问
            }
        }
        
        // 获取Authorization请求头
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401状态码
            return false;  // 拒绝访问
        }
        
        // 验证token
        String token = authHeader.substring(7);  // 去掉"Bearer "前缀
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        
        return true;  // 验证通过
    }
}
```

### 11. 配置类 - WebConfig.java

**作用**: Web MVC配置

```java
@Configuration  // 标记为配置类
public class WebConfig implements WebMvcConfigurer {
    
    private final AuthInterceptor authInterceptor;
    
    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")  // 拦截/api下的所有路径
                .excludePathPatterns("/api/users/login", "/api/users/register");  // 排除路径
    }
}
```

### 12. 工具类 - JwtUtil.java

**作用**: JWT令牌生成和验证

```java
@Component
public class JwtUtil {
    
    private static final String SECRET_KEY = "your-secret-key-must-be-at-least-256-bits-long";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24小时（毫秒）
    
    // 使用密钥创建签名密钥
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    
    /**
     * 生成JWT令牌
     */
    public String generateToken(String username, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        
        return Jwts.builder()
                .setSubject(username)  // 设置主题（用户名）
                .claim("userId", userId)  // 添加自定义声明
                .setIssuedAt(now)  // 签发时间
                .setExpiration(expiryDate)  // 过期时间
                .signWith(key)  // 使用密钥签名
                .compact();  // 生成令牌字符串
    }
    
    /**
     * 解析JWT令牌
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }
    
    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
```

### 13. 工具类 - DataUtil.java

**作用**: 数据处理工具类

```java
public class DataUtil {
    
    // 日期格式化器（线程安全）
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 格式化日期时间
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_FORMATTER);
    }
    
    /**
     * 计算移动平均线
     * @param dataList 数据列表
     * @param period 周期
     * @return 移动平均值
     */
    public static BigDecimal calculateMA(List<FinancialData> dataList, int period) {
        if (dataList == null || dataList.size() < period) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            FinancialData data = dataList.get(i);
            if (data.getBidPrice() != null) {
                sum = sum.add(data.getBidPrice());  // BigDecimal加法
            }
        }
        
        // BigDecimal除法，指定精度和舍入模式
        return sum.divide(new BigDecimal(period), 4, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 验证期货代码格式
     */
    public static boolean isValidSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return false;
        }
        
        String trimmedSymbol = symbol.trim();
        return trimmedSymbol.length() >= 2 && trimmedSymbol.length() <= 20;
    }
}
```

---

## 数据库相关文件

### init.sql - 数据库初始化脚本

```sql
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS financial_forecasting 
    DEFAULT CHARACTER SET utf8mb4  -- 支持中文和emoji
    COLLATE utf8mb4_unicode_ci;    -- 排序规则

USE financial_forecasting;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- 主键，自增
  username VARCHAR(64) NOT NULL UNIQUE,   -- 用户名，唯一索引
  password VARCHAR(128) NOT NULL,         -- 密码（应加密存储）
  created_time DATETIME NOT NULL          -- 创建时间
);

-- 金融数据表
CREATE TABLE IF NOT EXISTS financial_data (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  symbol VARCHAR(32) NOT NULL,            -- 股票/期货代码
  date_time DATETIME NOT NULL,            -- 数据时间
  bid_price DECIMAL(18,6),               -- 买入价（18位数字，6位小数）
  bid_order_qty BIGINT,                   -- 买入订单量
  bid_executed_qty BIGINT,                -- 买入成交量
  ask_order_qty BIGINT,                   -- 卖出订单量
  ask_executed_qty BIGINT,                -- 卖出成交量
  INDEX idx_fd_symbol_time (symbol, date_time)  -- 复合索引，加速查询
);

-- 预测结果表
CREATE TABLE IF NOT EXISTS prediction_results (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  symbol VARCHAR(32) NOT NULL,
  algorithm_name VARCHAR(64) NOT NULL,    -- 算法名称
  prediction_type VARCHAR(32),            -- 预测类型
  prediction_time DATETIME NOT NULL,      -- 预测时间
  predicted_value DECIMAL(18,6),          -- 预测值
  confidence_score DECIMAL(18,6),         -- 置信度分数
  is_anomaly TINYINT(1),                 -- 是否异常（布尔值）
  created_time DATETIME NOT NULL,
  INDEX idx_pr_symbol_time (symbol, prediction_time)
);
```

---

## Java基础语法说明

### 1. 数据类型

**基本类型**:
- `int`: 整数（32位）
- `long`: 长整数（64位）
- `double`: 双精度浮点数
- `boolean`: 布尔值（true/false）
- `char`: 单个字符

**引用类型**:
- `String`: 字符串
- `Integer`, `Long`: 基本类型的包装类（可为null）
- `BigDecimal`: 精确的十进制数
- `LocalDateTime`: 日期时间
- `List<T>`: 列表集合
- `Map<K,V>`: 键值对映射

### 2. 访问修饰符

- `public`: 公开访问，任何地方都可访问
- `private`: 私有访问，只能在本类中访问
- `protected`: 受保护访问，同包或子类可访问
- （默认）: 包访问，同包内可访问

### 3. 类和接口

```java
// 类定义
public class MyClass {
    // 属性
    private String name;
    
    // 构造函数
    public MyClass(String name) {
        this.name = name;
    }
    
    // 方法
    public String getName() {
        return name;
    }
}

// 接口定义
public interface MyInterface {
    void doSomething();  // 抽象方法
}
```

### 4. 异常处理

```java
try {
    // 可能抛出异常的代码
    doSomething();
} catch (SpecificException e) {
    // 处理特定异常
    log.error("错误: ", e);
} catch (Exception e) {
    // 处理其他异常
    throw new RuntimeException("处理失败", e);
} finally {
    // 总是执行的代码
    cleanup();
}
```

### 5. 泛型

```java
// 泛型类
public class Box<T> {
    private T content;
    
    public void set(T content) {
        this.content = content;
    }
    
    public T get() {
        return content;
    }
}

// 使用
Box<String> stringBox = new Box<>();
stringBox.set("Hello");

// 泛型方法
public <T> List<T> toList(T... elements) {
    return Arrays.asList(elements);
}
```

### 6. Lambda表达式和流

```java
// Lambda表达式
List<String> names = List.of("Alice", "Bob", "Charlie");

// 过滤和映射
List<String> filtered = names.stream()
    .filter(name -> name.startsWith("A"))  // Lambda: 参数 -> 表达式
    .map(String::toUpperCase)              // 方法引用
    .collect(Collectors.toList());

// forEach循环
names.forEach(name -> System.out.println(name));
```

### 7. Optional类

```java
Optional<User> userOpt = userRepository.findById(1L);

// 检查是否有值
if (userOpt.isPresent()) {
    User user = userOpt.get();
}

// 或者使用函数式风格
userOpt.ifPresent(user -> {
    System.out.println(user.getName());
});

// 提供默认值
User user = userOpt.orElse(new User());

// 抛出异常
User user = userOpt.orElseThrow(() -> 
    new RuntimeException("用户不存在"));
```

---

## 项目运行说明

### 1. 环境要求

- **Java**: JDK 11或更高版本
- **MySQL**: 8.0或更高版本
- **Maven**: 3.6或更高版本
- **Python**: 3.7或更高版本（用于运行预测模型）

### 2. 配置步骤

1. **创建数据库**:
   ```sql
   mysql -u root -p
   source src/main/resources/sql/init.sql
   ```

2. **修改配置文件**:
   编辑 `application.yml`，修改数据库连接信息

3. **安装依赖**:
   ```bash
   mvn clean install
   ```

### 3. 运行项目

```bash
# 使用Maven运行
mvn spring-boot:run

# 或者运行打包后的JAR
java -jar target/financial-forecasting-system-1.0.0.jar
```

### 4. API测试

项目启动后，可以访问：
- 主页: http://localhost:8080/
- 用户注册: POST http://localhost:8080/users/register
- 用户登录: POST http://localhost:8080/users/login
- 金融数据: GET http://localhost:8080/financial-data
- 执行预测: POST http://localhost:8080/algorithms/predict/local

---

## 常见问题解答

### Q1: 什么是Spring Boot？
**A**: Spring Boot是一个基于Spring框架的快速开发框架，它简化了Spring应用的配置和部署。通过"约定优于配置"的理念，大大减少了配置文件的编写。

### Q2: 什么是JPA？
**A**: JPA (Java Persistence API) 是Java的ORM（对象关系映射）标准。它允许你用面向对象的方式操作数据库，而不需要写SQL语句。

### Q3: 什么是REST API？
**A**: REST (Representational State Transfer) 是一种软件架构风格。RESTful API使用HTTP方法（GET、POST、PUT、DELETE）来操作资源，返回JSON格式的数据。

### Q4: 什么是JWT？
**A**: JWT (JSON Web Token) 是一种用于身份认证的令牌格式。它包含用户信息和签名，可以在客户端和服务器之间安全地传递身份信息。

### Q5: Maven是什么？
**A**: Maven是Java项目的构建和依赖管理工具。它通过pom.xml文件管理项目依赖，自动下载所需的库文件。

### Q6: @Autowired是如何工作的？
**A**: @Autowired是Spring的依赖注入注解。Spring容器会自动查找匹配类型的Bean（组件），并注入到标记的字段或方法参数中。

### Q7: 为什么使用BigDecimal而不是double？
**A**: 金融计算需要精确的十进制运算。double类型存在精度问题（如0.1+0.2≠0.3），而BigDecimal可以精确表示和计算十进制数。

---

## 总结

这个金融预测系统是一个典型的Spring Boot企业级应用，展示了：

1. **分层架构**: Controller → Service → Repository → Entity
2. **RESTful API设计**: 使用HTTP方法和JSON数据交互
3. **数据持久化**: 使用JPA/Hibernate操作MySQL数据库
4. **安全认证**: JWT令牌认证机制
5. **算法集成**: 调用Python模型进行预测
6. **配置管理**: 使用YAML文件集中管理配置

对于Java初学者，建议：
1. 先理解基本的Java语法（类、方法、数据类型）
2. 学习Spring Boot的核心概念（IoC、AOP）
3. 理解MVC模式和REST API设计
4. 掌握数据库基础和JPA使用
5. 了解项目构建工具（Maven）的使用

通过这个项目，你可以学习到完整的Java Web开发流程和最佳实践。
