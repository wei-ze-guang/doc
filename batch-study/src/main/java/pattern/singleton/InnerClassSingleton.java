package pattern.singleton;


/**
 * 静态内部类
 */
public class InnerClassSingleton {
    private InnerClassSingleton() {}

    private static class Holder {
        private static final InnerClassSingleton INSTANCE = new InnerClassSingleton();
    }

    public static InnerClassSingleton getInstance() {
        return Holder.INSTANCE;
    }

}
