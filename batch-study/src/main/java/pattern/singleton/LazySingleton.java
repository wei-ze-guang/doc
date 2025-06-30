package pattern.singleton;

/**
 * | 特性              | 加锁版懒汉式（synchronized 方法） | 双重检查锁（DCL）          |
 * | --------------- | ----------------------- | ------------------- |
 * | 线程安全            | 是                       | 是                   |
 * | 性能              | 每次调用都加锁，开销较大            | 只有第一次初始化时加锁，性能优     |
 * | 代码复杂度           | 简单                      | 复杂一点，需要加 `volatile` |
 * | 是否需要 `volatile` | 不需要                     | 需要，防止指令重排序          |
 * | 适用场景            | 线程不频繁调用时可以用             | 推荐使用，线程频繁调用首选       |
 */
public class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {}

    /**
     * 在下面方法加一个synchronized的时候就变为线程安全的了
     * public static synchronized LazySingleton getInstance()
     * @return
     */
    public synchronized static  LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }

}
