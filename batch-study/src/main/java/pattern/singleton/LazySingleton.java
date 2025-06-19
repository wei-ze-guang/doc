package pattern.singleton;

public class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {}

    /**
     * 在下面方法加一个synchronized的时候就变为线程安全的了
     * public static synchronized LazySingleton getInstance()
     * @return
     */
    public static  LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }

}
