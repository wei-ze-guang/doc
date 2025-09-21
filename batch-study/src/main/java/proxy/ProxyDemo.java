package proxy;


import java.lang.reflect.Proxy;

public class ProxyDemo {

    interface Service { void doSomething(); }

    static class ServiceImpl implements Service {
        public void doSomething() { System.out.println("Doing"); }
    }

    public static void main(String[] args) {
        // ✅ JDK 17 用这个属性名
        System.setProperty("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
        // （可选，兼容旧 JDK）
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

        Service proxy = (Service) Proxy.newProxyInstance(
                Service.class.getClassLoader(),
                new Class[]{Service.class},
                (p, m, a) -> {
                    System.out.println("Before " + m.getName());
                    Object res = m.invoke(new ServiceImpl(), a);
                    System.out.println("After " + m.getName());
                    return res;
                }
        );

        proxy.doSomething();

        System.out.println("user.dir = " + System.getProperty("user.dir"));
        // 生成的位置通常是：
        // {user.dir}/jdk/proxy2/$Proxy0.class  (或 jdk/proxy1)
    }
}


