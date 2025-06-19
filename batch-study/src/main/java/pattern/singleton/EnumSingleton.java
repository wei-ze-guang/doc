package pattern.singleton;

public enum EnumSingleton {

    INSTANCE;

    public void doSomething() {
        System.out.println("执行单例操作");
    }
}

