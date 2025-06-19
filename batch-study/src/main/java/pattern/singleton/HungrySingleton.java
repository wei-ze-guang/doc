package pattern.singleton;

/**
 * 饿汉式（类加载时创建实例，线程安全）, 不能延迟加载（类加载就创建了）
 */
public class HungrySingleton {

    private static HungrySingleton instance = new HungrySingleton();

    private HungrySingleton() {}  ; //私有构造器

    public static HungrySingleton getInstance() {
        return instance;
    }
}
