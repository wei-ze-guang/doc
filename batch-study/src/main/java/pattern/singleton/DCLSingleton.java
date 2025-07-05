package pattern.singleton;

/**
 * 双重检查锁 （DCL）
 */
public class DCLSingleton {
    private static volatile DCLSingleton instance;

    private DCLSingleton() {}

    public static DCLSingleton getInstance() {
        /**
         * 第一次判断是又能出线this逃逸，出现半初始化
         */
        if (instance == null) {
            synchronized (DCLSingleton.class) {
                /**
                 * 这里需要再判断一次是因为外层第一次判断的是时候很多线程看到都是null，就都会还进来
                 */
                if (instance == null) {
                    //如果，没有vilatile关键字，这里会发生this逃逸，其他线程会
                    // 看到instance不是null ，会直接返回一个null的instance
                    //通过禁止指令重排
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }
}
