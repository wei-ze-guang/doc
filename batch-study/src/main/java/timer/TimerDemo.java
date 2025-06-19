package timer;

import java.util.Timer;
import java.util.TimerTask;

public class TimerDemo {

    static final Timer timer = new Timer();

    public static void main(String[] args) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("TimerTask executed");
            }
        },1000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("每隔 3 秒执行一次");
            }
        }, 1000, 3000); // 延迟 1 秒后开始，每隔 3 秒执行一次
    }
}
