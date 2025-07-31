package proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 被代理类不能是final
 */
public class CglibDemo {
    public static void main(String[] args) {

        TargetCglib targetCglib = new TargetCglib();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetCglib.getClass());

        //或者enhancer.setSuperclass(TargetCglib.class);
        enhancer.setCallback(new MethodInterceptor() {

            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("前置处理：" + method.getName());
                //// 调用原始类的方法（注意是调用父类）：
                Object result = methodProxy.invokeSuper(obj, args); // 调用目标方法
                System.out.println("后置处理：" + method.getName());
                return result;
            }
        } );

        TargetCglib o = (TargetCglib)enhancer.create();
        o.tCglib();

    }
}
/**
 * JDK9 以后引入了模块系统（JPMS），默认不允许反射访问某些内部类和方法。
 * CGLIB 动态生成字节码时，需要调用 ClassLoader.defineClass 这种受限制的方法。
 * JVM 抛出 InaccessibleObjectException，阻止访问。
 * 方案1：给 JVM 添加启动参数，打开对 java.lang 模块的访问权限   --add-opens java.base/java.lang=ALL-UNNAMED
 * CGLIB 需要调用无参构造函数，确保你的 TargetCglib 有个无参构造函数。
 * | 参数名           | 含义说明                                      |
 * | ------------- | ----------------------------------------- |
 * | `obj`         | 被代理对象的实例（增强后的代理对象）                        |
 * | `method`      | 被调用的原始方法的反射对象（`java.lang.reflect.Method`） |
 * | `args`        | 调用方法时传入的参数数组                              |
 * | `methodProxy` | CGLIB 生成的用于调用父类（即原始类）方法的代理对象              |
 */
