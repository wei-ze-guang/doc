package pattern.singleton;

/**
 * 双重检查锁 （DCL）
 */
public class DCLSingleton {
    private static volatile DCLSingleton instance;

    private DCLSingleton() {}

    public static DCLSingleton getInstance() {
        if (instance == null) {
            synchronized (DCLSingleton.class) {
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
