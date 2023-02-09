# pedroAuth

轻量级的权限管理工具，支持极简配置进行web基础权限管理，避免传统权限管理工具的复杂代码配置。

### 1. 功能特性

- 通过配置文件或注解进行权限管理。
- 基于浏览器SESSION、COOKIE的登陆状态维护。
- 调用权限主体获取当前登录权限信息，进行手动的权限验证。
- 密码加密服务。

### 2. 权限等级说明

```java
// 可供选择的权限等级
NO_AUTH("noAuth"), // 1.无需认证（无需登录）
NEED_AUTH("needAuth"), // 2.需要认证（需要登录）
NEED_ROLE("needRole"); // 3.要求指定权限

// 权限等级为needRole时，可供选择的两种模式
NEED_ALL("needAll"), // 1.需要用户拥有配置的所有角色
NEED_ONE("needOne"); // 2.需要用户拥有配置的角色之一即可
```

### 3. 快速开始

#### 3.1 依赖导入

在 maven 配置文件`pom.xml`中进行依赖导入。

```xml
<repositories>
    <repository>
        <id>pedro7k</id>
        <url>https://raw.github.com/pedro7k/pedroAuth/main/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>

<!--pedroAuth-->
<dependency>
    <groupId>com.pedro</groupId>
    <artifactId>pedro-auth-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 3.2 权限配置

##### 3.2.1 配置文件

在 spring 的 yaml 配置文件中进行配置。

```yaml
pedro-auth:
  auth-level:
    default: needAuth # 默认权限等级，只支持noAuth和needAuth
    rule-list: rule1,rule2,rule3 # 规则列表
    rule1:
      path: /noAuth
      level: noAuth # 无需认证
    rule2:
      path: /needAuth
      level: needAuth # 需要认证
    rule3:
      path: /needRole
      level: needRole # 需要权限
      roles: user,manager # 所需的权限列表，NEED_ROLE时不允许为空
      roleRule: needAll # 权限处理方式，默认为needOne
  no-auth-path: /jumpToLoginPage # 未认证时的跳转目录
  no-role-path: /roleDenied # 无权限时的跳转目录
```

> 跳转目录在跳转 ajax 请求时可能无法正常跳转，需前端配合实现。

##### 3.2.2 注解

在 SpringBoot Controller 层方法上可以使用注解`@MethodAuth`。三个参数`level`、`roles`、`roleRule`

```java
@MethodAuth(level = RuleLevelEnum.NEED_AUTH)
@GetMapping("/test")
public void test() {
    System.out.println("test");
}

@MethodAuth(level = RuleLevelEnum.NEED_ROLE, // 权限等级
            roles = "user,manager", // 所需的权限列表，NEED_ROLE时不允许为空
            roleRule = RoleRuleEnum.NEED_ALL) // 权限处理方式，默认为needOne
@GetMapping("/needRole")
public void needRole() {
    System.out.println("needRole");
}
```

#### 3.3 权限主体使用

`PedroAuth`的使用依赖权限主体`AuthSubject`进行，使用比较简单。

##### 3.3.1 获取权限主体

利用提供的工具类`PedroAuthUtil`

```java
// 获取当前subject
AuthSubject subject = PedroAuthUtil.getAuthSubject();
```

##### 3.3.2 登录

如果要通过`needAuth`及以上的权限，必须先在本次请求中登录。下面以一个登录接口的简单实现为例：

```java
@PostMapping("/login")
public CommonResult login(@RequestParam("username") String username, @RequestParam("password") String passWord) {

    // 1.pedroAuth 获取当前subject,封装登陆数据
    AuthSubject subject = PedroAuthUtil.getAuthSubject();

    // 2.登录
    try {
        subject.login(username, passWord, EncryptionEnum.MD5_ENCRYPTION, this::getUserInfo);
        return CommonResult.success(null, "登陆成功！");
    } catch (PedroAuthException e) {
        // 登陆过程中出现异常
        logger.warn("登陆失败", e);
        return CommonResult.error(null, e.getMessage());
    } catch (Throwable e) {
        logger.error("登陆时出现异常", e);
        e.printStackTrace();
        throw new ServiceException(ServiceExceptionEnum.SYS_ERROR);
    }
}

/**
 * 获取User信息，用于登陆验证
 */
private User getUserInfo(String username) {

    // 1.通过查询DB等方式，构造一个User，至少需填充username和password
    User user = ...;

    // 2.返回
    return user;
}
```

**提供用户信息获取方法**

在调用`login()`方法时，需要提供一个类型为`UserAccessFunction`的函数式接口实现。实则为提供一个由`username`查询用户信息的方法。通过这个方法，`pedroAuth`获取当前用户的密码、盐值（可选）、权限等信息。

这个方法也是`pedroAuth`实际上唯一需要做的代码配置。

**login 重载**

通过调用`subject.login()`来进行登录，本方法有几个重载可供选择：

```java
boolean login(String username, String password, UserAccessFunction userAccessFunction);

boolean login(String username, String password, EncryptionEnum encryptionType, UserAccessFunction userAccessFunction);

boolean login(String username, String password, boolean rememberMe, UserAccessFunction userAccessFunction);

/**
 * @param username           用户名
 * @param password           密码
 * @param encryptionType     加密方式
 * @param rememberMe         是否记住我
 * @param userAccessFunction 用户信息获取方法
 * @return 登陆是否成功
 */
boolean login(String username, String password, EncryptionEnum encryptionType, boolean rememberMe, UserAccessFunction userAccessFunction);
```

**抛出异常**

登陆时抛出异常中的`e。message`字段包含了登陆失败的原因，如用户名错误、密码错误等等。

```java
/**
 * 错误状态和信息枚举
 */
LOGIN_ERROR("1", "[pedroAuth]登录过程中出现异常"),
USERNAME_ERROR("2", "[pedroAuth]登录用户名错误"),
PASSWORD_ERROR("3", "[pedroAuth]登录密码错误"),
GET_REQUEST_INFO_ERROR("4", "[pedroAuth]获取请求request/response信息失败");
```

通过传入`EncryptionEnum`和`rememberMe`可以配置加密方式和是否记住我，加密方式在下一节说明。

##### 3.2.3 手动获取User信息和权限

`PedroAuth`可以围绕权限主体手动获取权限进行验证，下面是一个例子：

```java
/**
 * 权限不足返回错误
 */
@GetMapping("/roleCheck")
public CommonResult roleCheck(@RequestParam int role) {

    // 1.获取当前权限
    User currentUser = PedroAuthUtil.getAuthSubject().getUser();
    List<String> roleList = currentUser.getRoleList();

    // 2.权限校验
    ...

    // 3.成功返回
    return CommonResult.success(null,"鉴权通过");

}
```

第 8 和第 9 行分别可以获取当前的 `User`和权限列表，通过权限列表我们可以手动进行校验，并根据业务需要进行逻辑处理。

#### 3.4 加密

上面提到，通过在登录时传入`EncryptionEnum`可以自动进行密码加密比对，那么我们在将密码存入数据库的时候，如何进行加密呢，`PedroAuth`提供了一个加密工具。

```java
public class EncryptionUtil {

    /**
     * 加密工具
     *
     * @param username       用户名
     * @param password       密码
     * @param salt           盐值，可为空
     * @param encryptionEnum 加密方式
     */
    public static String encode(String username, String password, @Nullable String salt, EncryptionEnum encryptionEnum) throws Throwable {
        EncryptionFacade encryptionTool = EncryptionContext.encryptionToolMap.get(encryptionEnum);
        return encryptionTool.encode(username, password, salt);
    }
}
```

可调用`EncryptionUtil.encode()`方法，来通过用户名和密码进行加密，其中`salt`是可选参数。目前提供了三种加密方式：

```java
public enum EncryptionEnum {

    /**
     * BASE64加密
     */
    BASE64_ENCRYPTION,
    /**
     * SHA加密
     */
    SHA_ENCRYPTION,
    /**
     * MD5加密
     */
    MD5_ENCRYPTION
}
```

### 4. 架构大图

**不准确，懒得画，有空再画**

![image-20230210001216147](http://pedro-imgsubmit.oss-cn-beijing.aliyuncs.com/img/image-20230210001216147.png)

