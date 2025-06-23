# 观察者模式是什么？
观察者模式的核心思想是：
- 当一个对象状态发生改变时，它会自动通知依赖它的所有对象，让它们自动更新，实现对象之间的低耦合联动机制。
- 也被称为：发布-订阅模式（Publish-Subscribe）  
## 二、举个最通俗的例子：
你关注了某个公众号（你是观察者，公众号是被观察者），每次公众号发文（状态变化），你就能收到通知。这就是观察者模式。
### 三、UML 类图结构
```scss
┌────────────────────┐
│    Subject（被观察者） │
│────────────────────│
│ +register(Observer) │
│ +remove(Observer)   │
│ +notifyAll()        │
└────────────────────┘
          ▲
          │
┌────────────────────┐
│ ConcreteSubject     │
│（具体被观察者）     │
└────────────────────┘

       ▲
       │
┌────────────────────┐
│   Observer（观察者） │
│ +update()           │
└────────────────────┘
       ▲
       │
┌────────────────────┐
│ ConcreteObserver    │
│（具体观察者）       │
└────────────────────┘

```  
### 四、代码实战示例（天气预报系统）
我们模拟一个天气发布系统：每次天气数据更新时，订阅者会自动收到通知。  
- 1️⃣ 抽象观察者接口
````java
public interface Observer {
    void update(String temperature, String humidity);
}

````  
---  
- 2️⃣ 抽象被观察者接口  
```java
public interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}

```  
---  
- 3️⃣ 具体被观察者：天气中心  
```java
import java.util.ArrayList;
import java.util.List;

public class WeatherData implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private String temperature;
    private String humidity;

    public void setWeather(String temperature, String humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
        notifyObservers(); // 通知所有观察者
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(temperature, humidity);
        }
    }
}

```  
- 4️⃣ 两个观察者实现  
```java
public class PhoneDisplay implements Observer {
    @Override
    public void update(String temperature, String humidity) {
        System.out.println("📱 手机显示：温度=" + temperature + "℃，湿度=" + humidity + "%");
    }
}

public class LEDBoardDisplay implements Observer {
    @Override
    public void update(String temperature, String humidity) {
        System.out.println("💡 LED 显示：温度=" + temperature + "℃，湿度=" + humidity + "%");
    }
}

```  
---  
- 5️⃣ 客户端测试
```java
public class PhoneDisplay implements Observer {
    @Override
    public void update(String temperature, String humidity) {
        System.out.println("📱 手机显示：温度=" + temperature + "℃，湿度=" + humidity + "%");
    }
}

public class LEDBoardDisplay implements Observer {
    @Override
    public void update(String temperature, String humidity) {
        System.out.println("💡 LED 显示：温度=" + temperature + "℃，湿度=" + humidity + "%");
    }
}

```  
- 五、优缺点总结
- 优点：
  - 解耦合：被观察者不需要知道具体观察者是谁
  - 灵活扩展：添加/移除观察者很方便
  - 实现事件驱动机制
- 缺点：
  - 观察者多时，通知成本高
  - 如果观察者逻辑复杂，一个观察者出错可能影响整个通知链  
## 观察者模式常见应用场景
| 场景      | 举例                    |
| ------- | --------------------- |
| GUI事件监听 | 按钮点击、文本框输入            |
| 消息订阅系统  | MQ、通知系统               |
| 数据绑定    | Vue / React 响应式系统     |
| 日志/监控系统 | 日志中心监听服务事件            |
| 业务解耦    | 用户注册后通知多个服务（积分、邮件、推荐） |

### 关键点总结
| 关键点             | 内容                 |
| --------------- | ------------------ |
| **核心接口**        | Subject + Observer |
| **状态变化触发通知**    | notifyObservers()  |
| **可动态添加/移除监听者** | register/remove    |
| **低耦合**         | 被观察者不关心观察者逻辑       |
### 补充一句通俗总结
观察者模式让“我变了”这件事能被别人自动知道，而且我不用管是谁听到了。