### 如果一个变量没有使用volatile  
就算 main线程或者TwoThreadWord 线程对flag修改，但是OneThreadWord 永远在循环状态
```java
package multithread;

/**
 * 用来测试volatile关键字
 */
public class TestVolatileWord {

    private static  boolean flag = false;

    public static void main(String[] args) {

        new Thread(new OneThreadWord()).start();
        new Thread(new TwoThreadWord()).start();

        try {
            Thread.sleep(10);
            flag = true;
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    static class  OneThreadWord implements Runnable {

        @Override
        public void run() {
//            System.out.println("OneThreadWord");
            while (!flag) {
                //这里调用IO的话可能就会出现强制刷新缓存
//                System.out.println("flag一直是false");
            }
            System.out.println("看到flag变为true");
        }
    }

    static  class TwoThreadWord implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);  //这里sleep是为了让OneThread确保进去while循环
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            flag = true;
            System.out.println("已经设置flag为true");
        }
    }
}

```