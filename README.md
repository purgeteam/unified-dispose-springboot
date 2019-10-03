# unified-dispose-springboot-starter
## 简介:
- 基础全局统一异常，统一返回包装

## 使用:

> 添加依赖

```
<dependency>
  <groupId>com.purgeteam</groupId>
  <artifactId>unified-dispose-springboot-starter</artifactId>
  <version>0.1.1.RELEASE</version>
</dependency>
```

### 1. @EnableGlobalDispose

简介: 开启这个特性注解，才可以具备统一异常、统一返回功能。

### 2. 统一异常处理

如遇到自定义的`throw`业务异常、运行时异常，会统一被`GlobalDefaultExceptionHandler`拦截处理，返回值如下：

```json
{
  "succ": false,        // 是否成功
  "ts": 1566467628851,  // 时间戳
  "data": null,         // 数据
  "code": "CLOUD800",   // 错误类型
  "msg": "业务异常",    // 错误描述
  "fail": true
}
```
> 基础`CommonErrorCode`错误定义

```java
@Getter
public enum CommonErrorCode {
    /**
     * 系统异常
     */
    EXCEPTION("CLOUD500","服务器开小差，请稍后再试"),
    /**
     * 参数错误
     */
    PARAM_ERROR("CLOUD100", "参数错误"),
    /**
     * 业务异常
     */
    BUSINESS_ERROR("CLOUD400","业务异常"),

    /**
     * rpc调用异常
     */
    RPC_ERROR("CLOUD510","呀，网络出问题啦！")
    ;

    private String code;

    private String message;

    CommonErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

> 基础`BusinessErrorCode`错误定义

```
@Getter
public enum BusinessErrorCode {

    /**
     * 通用业务异常
     */
    BUSINESS_ERROR("CLOUD800","业务异常"),
    ;

    private String code;

    private String message;

    BusinessErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

> 通用业务`BusinessException`

程序主动抛出异常可以通过下面方式:

```
throw new BusinessException(BusinessErrorCode.BUSINESS_ERROR);
// 或者
throw new BusinessException("CLOUD800","没有多余的库存");
```

通常不建议直接抛出通用的`BusinessException`异常，应当在对应的模块里添加对应的领域的异常处理类以及对应的枚举错误类型。

> 自定义异常处理

如会员模块：
创建`UserException`异常类、`UserErrorCode`枚举、以及`UserExceptionHandler`统一拦截类。

UserException:

```java
@Data
public class UserException extends RuntimeException {

  private String code;
  private boolean isShowMsg = true;

  /**
   * 使用枚举传参
   *
   * @param errorCode 异常枚举
   */
  public UserException(UserErrorCode errorCode) {
    super(errorCode.getMessage());
    this.setCode(errorCode.getCode());
  }

}
```

UserErrorCode:

```java
@Getter
public enum UserErrorCode {
    /**
     * 权限异常
     */
    NOT_PERMISSIONS("CLOUD401","您没有操作权限"),
    ;

    private String code;

    private String message;

    CommonErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

UserExceptionHandler:
```java
@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

  /**
   * UserException 类捕获
   */
  @ExceptionHandler(value = UserException.class)
  public Result handler(UserException e) {
    log.error(e.getMessage(), e);
    return Result.ofFail(e.getCode(), e.getMessage());
  }

}
```

最后业务使用如下：

```java
// 判断是否有权限抛出异常
throw new UserException(UserErrorCode.NOT_PERMISSIONS);
```

### 3.统一返回包装

所有返回都将通过`CommonResponseDataAdvice`处理后返回，减少业务代码封装工作。

> 1. 统一返回包装默认处理

接口：

```java
@GetMapping("test")
public String test(){
  return "test";
}
```

返回

```json
{
  "succ": true,             // 是否成功
  "ts": 1566386951005,      // 时间戳
  "data": "test",           // 数据
  "code": null,             // 错误类型
  "msg": null,              // 错误描述
  "fail": false             
}
```
> 2. 忽略封装注解:@IgnorReponseAdvice

`@IgnorReponseAdvice`允许范围为：类 + 方法，标识在类上这个类下的说有方法的返回都将忽略返回封装。

接口：

```java
@IgnorReponseAdvice // 忽略数据包装 可添加到类、方法上
@GetMapping("test")
public String test(){
  return "test";
}
```

返回 `test`
