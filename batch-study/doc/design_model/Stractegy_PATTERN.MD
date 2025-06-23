## 策略模式
### 策略模式是什么？
- 策略模式的核心思想是：
  - 把一组算法（策略）分别封装起来，使它们可以互相替换，让算法的变化独立于使用算法的客户端。  
- 简而言之： “我不写死规则，我用策略！”
  - 比如你可以在运行时选择：
    - 不同的排序方法
    - 不同的优惠计算规则
    - 不同的登录方式（密码、验证码、扫码）  
```css
┌────────────────┐
│    Context     │———→ 使用哪个策略
│───────────────│
│ - strategy     │
│ + execute()    │
└────────────────┘
         ▲
         │
         ▼
┌────────────────┐
│   Strategy 接口 │←────────────┐
└────────────────┘             │
     ▲            ▲            │
     │            │            │
┌───────────┐ ┌────────────┐ ┌────────────┐
│策略A实现类│ │策略B实现类 │ │策略C实现类 │
└───────────┘ └────────────┘ └────────────┘

```  
---  
#### 代码示例（以「支付方式」为例）
- 我们实现：
  - 支付策略接口：PayStrategy
  - 三种支付实现类：AliPay、WeChatPay、CreditCardPay
  - 一个上下文类：PayContext

- 策略接口  
```java
public interface PayStrategy {
    void pay(double amount);
}

```  
---  
- 具体策略实现  
```css
public class AliPayStrategy implements PayStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("使用支付宝支付：" + amount + " 元");
    }
}

public class WeChatPayStrategy implements PayStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("使用微信支付：" + amount + " 元");
    }
}

public class CreditCardPayStrategy implements PayStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("使用信用卡支付：" + amount + " 元");
    }
}

```  
- 上下文类（根据传入策略执行）  
```java
public class PayContext {
    private PayStrategy strategy;

    public PayContext(PayStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePay(double amount) {
        strategy.pay(amount);
    }
}

```  
- 使用方式  
```java
public class Main {
    public static void main(String[] args) {
        PayContext context1 = new PayContext(new AliPayStrategy());
        context1.executePay(200); // 支付宝支付

        PayContext context2 = new PayContext(new WeChatPayStrategy());
        context2.executePay(150); // 微信支付
    }
}

```  
### 策略模式的优缺点

- ✅ 优点
  - 算法可自由切换（运行时可变）
  - 扩展性强（新增策略不改旧代码，符合开闭原则）
  - 避免大量 if-else / switch
- ❌ 缺点
  - 策略类过多（每个策略一个类）
  - 如果策略太简单，可能显得“重”  
---  
### 策略模式适合的场景
| 场景         | 示例           |
| ---------- | ------------ |
| 多种算法可互换    | 排序（快排/冒泡）    |
| 多种行为可定制    | 支付/促销策略/登录方式 |
| 避免 if-else | 替代条件判断       |
---  
### 常见用法进阶
- ✅ 用枚举+策略：
```java
public enum PayType {
    ALI(new AliPayStrategy()),
    WECHAT(new WeChatPayStrategy());

    private final PayStrategy strategy;

    PayType(PayStrategy strategy) {
        this.strategy = strategy;
    }

    public void pay(double amount) {
        strategy.pay(amount);
    }
}

```  
```java
PayType.ALI.pay(100); // 支付宝支付

```  
- ✅ 用 Map 做策略注册中心：  
```java
Map<String, PayStrategy> strategyMap = new HashMap<>();
strategyMap.put("ali", new AliPayStrategy());
strategyMap.put("wechat", new WeChatPayStrategy());

String type = "wechat";
strategyMap.get(type).pay(99.0);

```  
- ✅ 在 Spring 中使用策略模式  
  - Spring 中你可以使用：
    - @Component + @Autowired Map<String, Strategy>
    - 利用 BeanName 自动注入策略
    - 动态注册策略（甚至热插拔）  
### 策略模式 vs 状态模式（容易混淆）
| 对比项   | 策略模式      | 状态模式     |
| ----- | --------- | -------- |
| 意图    | 封装算法      | 封装状态和行为  |
| 行为是否变 | 不会影响上下文状态 | 会改变上下文状态 |
| 使用方式  | 手动选择策略    | 状态类自动跳转  |
---  
### 总结
“我不在类里写死流程，我把规则抽出来，用的时候选策略。”
- 如果你项目中有「可配置、可扩展的处理逻辑」，如：
   - 不同会员等级的折扣策略
   - 不同渠道的消息推送
   - 不同条件下的日志格式化器  
用策略模式都很合适。