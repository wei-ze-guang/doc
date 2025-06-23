package pattern.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 我们模拟一个天气发布系统：每次天气数据更新时，订阅者会自动收到通知。
 *
 * 这种模式的话就是自己数据变化的时候使用观察者的接口通知
 */
public class ObserverDemo {

    public static void main(String[] args) {

        WeatherData weatherData = new WeatherData();

        Observer phone = new PhoneDisplay();
        Observer led = new LEDBoardDisplay();

        weatherData.registerObserver(phone);

        weatherData.registerObserver(led);

        weatherData.setWeather("28", "65");  // 通知所有观察者
    }
}

/**
 * 1️⃣ 抽象观察者接口
 */
 interface Observer {
    void update(String temperature, String humidity);
}

/**
 *2️⃣ 抽象被观察者接口
 */
 interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}
/**
 * 3️⃣ 具体被观察者：天气中心
 */


class WeatherData implements Subject {
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

/**
 * 4️⃣ 两个观察者实现
 */
 class PhoneDisplay implements Observer {
    @Override
    public void update(String temperature, String humidity) {
        System.out.println("📱 手机显示：温度=" + temperature + "℃，湿度=" + humidity + "%");
    }
}

 class LEDBoardDisplay implements Observer {
    @Override
    public void update(String temperature, String humidity) {
        System.out.println("💡 LED 显示：温度=" + temperature + "℃，湿度=" + humidity + "%");
    }
}




