# 适配器模式
**适配器模式就是都需要实现一样的功能，但是接口不同，利用别人实现来是实现自己的**  

| 做法    | 关键点          | 是否使用继承 | 是否使用组合 | 灵活性  | 实际使用频率   |
| ----- | ------------ | ------ | ------ | ---- | -------- |
| 类适配器  | `extends` 适配 | ✅ 是    | ❌ 否    | ❌ 较低 | ❌ 很少     |
| 对象适配器 | `组合` + 委托    | ❌ 否    | ✅ 是    | ✅ 高  | ✅ 最常用    |
| 接口适配器 | 抽象类空实现       | ✅ 是    | ❌ 否    | ✅ 高  | ✅ 常用于监听器 |

---  
## 接口适配器,抽象类
- 如果你直接实现它，你就必须实现所有方法：接口适配器有点在于降低代码数量，只需要关心我们要实现的接口就可以了
LegacyLogger（接口，多个方法）
|
└── AbstractLoggerAdapter（抽象类，提供空实现）
|
└── PartialLegacyAdapter（只适配部分方法）  
```java
public interface LegacyInterface {
    void a();
    void b();
    void c();
}
public abstract class AbstractAdapter implements LegacyInterface {
    public void a() {}
    public void b() {}
    public void c() {}
}
public class ConcreteAdapter extends AbstractAdapter {
    @Override
    public void b() {
        System.out.println("我只适配了 b 方法");
    }
}

```


## 类适配器，使用继承
- 这个写法最简单，但是只能单继承，不灵活
     Target（目标接口）
         ▲
         │
     Adapter（适配器）
         ▲
         │
     Adaptee（被适配者）
  
```java
interface Target {
    void request();
}

class Adaptee {
    void specificRequest() {
        System.out.println("老方法");
    }
}

class Adapter extends Adaptee implements Target {
    public void request() {
        specificRequest(); // 调用老方法
    }
}

```  
## 对象适配器，使用组合
- 这个使用最多，灵活度很高

     Target（目标接口）
         ▲
         │
     Adapter（适配器）
         │
         ▼
     Adaptee（被适配者）  
```java
interface Target {
    void request();
}

class Adaptee {
    void specificRequest() {
        System.out.println("老方法");
    }
}

class Adapter implements Target {
    private Adaptee adaptee;

    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    public void request() {
        adaptee.specificRequest(); // 委托调用
    }
}

```



