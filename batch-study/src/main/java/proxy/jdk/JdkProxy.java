package proxy.jdk;

import org.junit.Test;
import proxy.utils.ProxyUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK 方式生成代理，目标类需要实现接口 ,在目标类实现接口的时候spring AOP 会使用这种，目标类没实现接口是一个类时候会使用CGLIB
 */
public class JdkProxy {
    public static void main(String[] args) {
        ProxyInterface proxyInterface = new ProxyInterfaceIm();

        ProxyInterface o = (ProxyInterface)Proxy.newProxyInstance(
                proxyInterface.getClass().getClassLoader(),
                new Class[]{ProxyInterface.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = null;
                        try {
                            System.out.println("这是代理类的前输出");
                            result = method.invoke(proxyInterface, args);
                            System.out.println("这是代理类的后输出");
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                        return result;
                    }
                }
        );

        o.s("参数");
        o.t(1,444);


    }

    @Test
    public void testProxyUtils(){
        ProxyInterface proxyInstance = (ProxyInterface) ProxyUtils.getProxyInstance(new ProxyInterfaceIm());
        proxyInstance.s("字符串");
        proxyInstance.t(1,4);
    }
}


