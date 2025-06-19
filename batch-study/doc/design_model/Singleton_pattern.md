# 单例模式
一、什么是单例模式？  
- 单例模式保证一个类在整个程序运行期间只有一个实例，并提供全局访问点。
- 使用场景  

  | 场景   | 说明             |
  | ---- | -------------- |
  | 配置中心 | 全局唯一配置对象       |
  | 缓存   | Redis、线程池、连接池等 |
  | 工具类  | 日志、打印、构建器      |
  | 控制器  | 游戏主控器、状态控制器等   |

---  
二、经典结构图  
+----------------+  
|   Singleton    |    <== 只允许存在一个对象  
+----------------+  
| - instance     |    <-- 私有静态变量保存唯一实例  
| + getInstance()|    <-- 提供静态方法获取实例  
+----------------+    
三、常见实现方式（含中高难度优化）  
- 饿汉式（类加载时创建实例，线程安全）在代码块执行私有构造方法  
  - ✅ 线程安全
  - ❌ 不能延迟加载（类加载就创建了） 
```java
public class HungrySingleton {
    private static final HungrySingleton instance = new HungrySingleton();

    private HungrySingleton() {}

    public static HungrySingleton getInstance() {
        return instance;
    }
}

```  
---  
 - 懒汉式（需要时才创建，线程不安全 ❌）  
```java
public class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {}

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton(); // 多线程可能会出问题！
        }
        return instance;
    }
}

```  
- 懒汉式 + synchronized（线程安全，但效率低）  
```java
public class SyncLazySingleton {
    private static SyncLazySingleton instance;

    private SyncLazySingleton() {}

    public static synchronized SyncLazySingleton getInstance() {
        if (instance == null) {
            instance = new SyncLazySingleton();
        }
        return instance;
    }
}

```  
---  
- 双重检查锁（DCL，推荐 ✅  
  - ✅ 延迟加载
  - ✅ 线程安全
  - ✅ 性能好
  - ⚠️ volatile 防止指令重排是关键 
```java
public class DCLSingleton {
    private static volatile DCLSingleton instance;

    private DCLSingleton() {}

    public static DCLSingleton getInstance() {
        if (instance == null) {
            synchronized (DCLSingleton.class) {
                if (instance == null) {
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }
}

```  
---  
- 静态内部类（最推荐 ✅✅✅）  
  - ✅ 线程安全
  - ✅ 延迟加载
  - ✅ 性能高
  - ✅ 写法简单
  - ⭐ 推荐使用 
```java
public class InnerClassSingleton {
    private InnerClassSingleton() {}

    private static class Holder {
        private static final InnerClassSingleton INSTANCE = new InnerClassSingleton();
    }

    public static InnerClassSingleton getInstance() {
        return Holder.INSTANCE;
    }
}

```  
---  
-  枚举实现（最强大，防反射 + 反序列化）  
  - ✅ 天生线程安全
  - ✅ 防止反射破解
  - ✅ 防止反序列化破坏单例
  - ✅ 推荐用于功能型单例（不需要继承） 

## 优缺点
- ✅ 优点
  - 全局唯一，节省资源
  - 避免重复实例化
  - 提供统一的访问方式

- ❌ 缺点
  - 不易扩展（继承、依赖注入困难）
  - 隐式状态可能造成并发问题
  - 写错容易踩坑（尤其多线程懒加载）
---  
## 六、关键点总结
| 关键点   | 说明                             |
| ----- | ------------------------------ |
| 构造器私有 | `private` 构造器防止外部 new          |
| 静态持有  | 使用 static 成员存储实例               |
| 线程安全  | 多线程下保证只创建一次（推荐 DCL / 内部类）      |
| 防反射   | 枚举天然防止反射                       |
| 防序列化  | 枚举天然防止破坏，也可以实现 `readResolve()` |
## 七、真实使用建议
| 情况         | 推荐实现方式              |
| ---------- | ------------------- |
| 普通应用       | 静态内部类 ✅             |
| 性能要求高      | DCL（+ `volatile`） ✅ |
| 安全最重要（防反射） | 枚举实现 ✅✅✅            |
