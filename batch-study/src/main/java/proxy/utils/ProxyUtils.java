package proxy.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyUtils{

    public  static <T>  T getProxyInstance(T target){
        ClassLoader classLoader = target.getClass().getClassLoader();
        Class<?>[] interfaces = target.getClass().getInterfaces();

        int length = interfaces.length;
        System.out.println(length);

        T o = (T)Proxy.newProxyInstance(
                classLoader,
                interfaces,
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("代理类先执行");
                        Object invoke = method.invoke(target, args);
                        System.out.println("代理类后执行执行");

                        return invoke;
                    }
                }
        );

        return o;
    }
}
