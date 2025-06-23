package pattern.strategy;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 策略模式就是同一个接口的不同的实现，但是要要求每个实现的功能和结果是相同或的
 */
public class StrategyPattern {
    public static void main(String[] args) {
        /**
         * 支付宝策略
         */
        PayContext payContext = new PayContext(new AliPayStrategy());
        payContext.executePay(100);

        /**
         * 微信支付策略
         */

        PayContext payContext2 = new PayContext(new WeChatPayStrategy());
        payContext2.executePay(100);

    }

    /**
     * 进阶版,枚举
     */
    enum PayType {
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

    @Test
    public void testEnumPay(){
        PayType payType = PayType.ALI;
        payType.pay(100);

        PayType payType2 = PayType.WECHAT;
        payType2.pay(100);
    }

    /**
     * 进阶版 用 Map 做策略注册中心：
     */
    @Test
    public void testMapStrategy(){
        Map<String,PayStrategy> strategyMap = new HashMap<String,PayStrategy>();
        strategyMap.put("ali",new AliPayStrategy());
        strategyMap.put("weChat",new WeChatPayStrategy());
        String type = "weChat";
        String payType = "ali";

        strategyMap.get(type).pay(100);
        strategyMap.get(payType).pay(100);

    }

}

/**
 * 开发的时候一般写在外面
 */

/**
 * 1️⃣ 策略接口
 */
 interface PayStrategy {
    void pay(double amount);
}
/**
 * 2️⃣ 具体策略实现
 */
 class AliPayStrategy implements PayStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("使用支付宝支付：" + amount + " 元");
    }
}

 class WeChatPayStrategy implements PayStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("使用微信支付：" + amount + " 元");
    }
}

 class CreditCardPayStrategy implements PayStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("使用信用卡支付：" + amount + " 元");
    }
}

/**
 * 上下文类（根据传入策略执行）
 */

 class PayContext {
    private PayStrategy strategy;

    public PayContext(PayStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePay(double amount) {
        strategy.pay(amount);
    }


    /**
     *
     */
}







