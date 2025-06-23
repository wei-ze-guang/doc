package pattern.singleton;

public class Main {
    public static void main(String[] args) {
        DCLSingleton singleton = DCLSingleton.getInstance();
        EnumSingleton enumSingleton = EnumSingleton.INSTANCE;
        HungrySingleton hungrySingleton = HungrySingleton.getInstance();
        InnerClassSingleton innerClassSingleton = InnerClassSingleton.getInstance();

        LazySingleton lazySingleton = LazySingleton.getInstance();
    }
}
