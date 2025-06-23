# 代理模式是什么？
- 代理模式的核心思想是：
  - 为其他对象提供一种代理，以控制对这个对象的访问。
- 简单来说：
  - 代理模式是为某个对象提供一个替身对象，通过代理对象来间接地控制对目标对象的访问。
常见的代理模式可以在访问控制、性能优化、懒加载等场景中使用。  
## 结构图
```scss
┌────────────────┐
│     Client     │
└────────────────┘
         ▲
         │
  ┌──────────────┐
  │   Subject    │ ←──────────────┐
  │ (目标对象)   │                │
  └──────────────┘                │
         ▲                        │
         │                        │
┌────────────────┐         ┌────────────────┐
│ RealSubject   │         │ Proxy          │
│ (真实对象)    │         │ (代理对象)     │
└────────────────┘         └────────────────┘
         ▲                        ▲
         │                        │
┌────────────────┐         ┌────────────────┐
│ ConcreteProxy │         │ RealSubject   │
└────────────────┘         └────────────────┘

```  
---  
### 代码示例：代理模式实战（门禁系
- 1️⃣ 抽象接口：Subject（目标对象）  
```java
public interface AccessControl {
    void enter(String personName);
}

```  
- 2️⃣ 真实对象：RealAccessControl（门禁系统的核心功能）
```java
public class RealAccessControl implements AccessControl {
    @Override
    public void enter(String personName) {
        System.out.println(personName + " has access to the building.");
    }
}

```  
---  
-  3️⃣ 代理类：AccessControlProxy（控制访问）  
```java
public class AccessControlProxy implements AccessControl {
    private RealAccessControl realAccessControl;
    private String role;

    public AccessControlProxy(String role) {
        this.role = role;
    }

    @Override
    public void enter(String personName) {
        if (role.equals("Admin")) {
            if (realAccessControl == null) {
                realAccessControl = new RealAccessControl();
            }
            realAccessControl.enter(personName);
        } else {
            System.out.println(personName + " does not have access rights.");
        }
    }
}

```  
- 4️⃣ 客户端测试：模拟访问控制
```java
public class Main {
    public static void main(String[] args) {
        AccessControl adminAccess = new AccessControlProxy("Admin");
        AccessControl guestAccess = new AccessControlProxy("Guest");

        adminAccess.enter("Alice");  // Admin access granted
        guestAccess.enter("Bob");    // Guest access denied
    }
}

```  
---  
### 优缺点总结
- 优点：
  - 控制访问：可以在代理中加入额外的控制逻辑，比如权限验证、缓存、日志记录等。
  - 减少性能开销：通过懒加载、延迟初始化等策略来提高性能。
  - 解耦：客户端不直接操作目标对象，而是通过代理来间接访问。
  - 增强功能：通过代理，可以为目标对象增加附加功能，如日志、性能监控等。
-  缺点：
  - 复杂性增加：如果代理层过多，可能导致代码变得复杂，维护困难。
  - 性能问题：由于代理对象对目标对象进行封装，可能会引入额外的性能开销，尤其是当代理层很深时。
  - 难以调试：通过代理调用目标对象时，可能会影响调试和跟踪代码的流向。  
### 代理模式适合的场景
| 场景       | 举例                       |
| -------- | ------------------------ |
| **访问控制** | 防火墙、权限检查（如访问数据库、敏感操作）    |
| **懒加载**  | 延迟实例化，减少不必要的对象创建（如缓存代理）  |
| **远程代理** | 分布式应用中的远程方法调用（如 RMI、RPC） |
| **性能优化** | 对象缓存（如数据库缓存、文件缓存）        |
| **日志记录** | 方法调用日志（AOP 也是基于代理实现的）    |  
---  

### 代理模式常见的变种
- 1. 静态代理  
代理类在编译时就已经创建好，代理类和目标类必须实现相同的接口。  
```java
public class StaticProxy implements AccessControl {
    private RealAccessControl realAccessControl;

    public StaticProxy() {
        this.realAccessControl = new RealAccessControl();
    }

    @Override
    public void enter(String personName) {
        realAccessControl.enter(personName);
    }
}

```  
- 2. 动态代理  
代理类和目标类在运行时动态生成，Java 反射机制可以用来实现动态代理。  
java.lang.reflect.Proxy 类及其 InvocationHandler 接口可以用来动态生成代理类。  
```java
import java.lang.reflect.*;

public class DynamicProxyExample {
    public static void main(String[] args) {
        AccessControl realAccessControl = new RealAccessControl();
        AccessControl proxy = (AccessControl) Proxy.newProxyInstance(
            realAccessControl.getClass().getClassLoader(),
            new Class[]{AccessControl.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("Logging: " + method.getName() + " method called");
                    return method.invoke(realAccessControl, args);
                }
            });

        proxy.enter("Alice");
    }
}

```  
### 代理模式 vs 装饰器模式
| 对比项     | 代理模式            | 装饰器模式                   |
| ------- | --------------- | ----------------------- |
| **目的**  | 控制访问            | 增强功能                    |
| **功能**  | 主要是控制对真实对象的访问   | 在不修改对象代码的情况下，动态地增加额外的功能 |
| **扩展性** | 可以限制代理功能（如权限控制） | 通过装饰器链扩展功能              |
### 总结
代理模式让我们通过代理对象来访问目标对象，从而可以在访问过程中增加额外的控制逻辑，
比如访问权限检查、性能优化等。代理模式非常适合访问控制、懒加载、缓存等场景。