package proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.FileOutputStream;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ByteBuddyFromJdkProxy {

    //  这里不要乱执行 他会生成一堆代码

    interface Service { void doSomething(); }

    public static void main(String[] args) throws Exception {

        // ✅ JDK 17 用这个属性名
        System.setProperty("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
        // 1️⃣ 定义 InvocationHandler
        InvocationHandler handler = (proxy, method, methodArgs) -> {
            System.out.println("Before " + method.getName());
            Object res = null; // 可以执行真实逻辑
            System.out.println("After " + method.getName());
            return res;
        };

        // 2️⃣ JDK 动态代理
        Service jdkProxy = (Service) Proxy.newProxyInstance(
                Service.class.getClassLoader(),
                new Class[]{Service.class},
                handler
        );

//        // 3️⃣ 保存 JDK 代理 class 文件
//        byte[] bytes = ProxyGenerator.generateProxyClass("$Proxy0", new Class[]{Service.class});
//        Path path = Path.of("./$Proxy0.class");
//        Files.write(path, bytes);
//        System.out.println("JDK proxy class saved to: " + path.toAbsolutePath());

        // 4️⃣ 用 ByteBuddy 生成等价代理类
        Class<? extends Service> byteBuddyProxyClass = (Class<? extends Service>) new ByteBuddy()
                .subclass(Object.class)            // 可以选择 Object 或其他类
                .implement(Service.class)          // 实现接口
                .method(ElementMatchers.any())      // 拦截所有方法
                .intercept(InvocationHandlerAdapter.of(handler)) // 用 JDK 的 handler
                .name("MyServiceProxy")             // 自定义类名
                .make()
                .load(ByteBuddyFromJdkProxy.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        // 5️⃣ 实例化 ByteBuddy 代理
        Service proxy = byteBuddyProxyClass.getDeclaredConstructor().newInstance();
        proxy.doSomething();

        // 6️⃣ 保存 ByteBuddy 生成的 class 文件
        try (FileOutputStream fos = new FileOutputStream("./MyServiceProxy.class")) {
            byteBuddyProxyClass.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .openStream()
                    .transferTo(fos);
        }
        System.out.println("ByteBuddy proxy class saved to: ./MyServiceProxy.class");
    }
}

