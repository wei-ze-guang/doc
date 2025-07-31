package proxy.byt.buddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteBuddyDemo {

    public static void main(String[] args) throws Exception {
        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello ByteBuddy!"))
                .make()
                .load(ByteBuddyDemo.class.getClassLoader())
                .getLoaded();

        System.out.println(dynamicType.newInstance().toString());  // 输出 Hello ByteBuddy!
    }
}
/**
 * 为什么很多现代框架推荐 ByteBuddy？
 * 兼容 Java 模块系统，不容易出现 InaccessibleObjectException。 cglib容易出现这个问题
 * API 设计现代，使用更灵活
 * 社区活跃，维护及时。
 * 性能和生成的字节码质量优于传统 CGLIB。
 */

/**
 * | 特性        | CGLIB                | ByteBuddy                  |
 * | --------- | -------------------- | -------------------------- |
 * | 支持 JDK 版本 | 支持较老，但对 JDK 9+ 模块支持差 | 设计兼容 JDK 8+，对模块支持好         |
 * | 生成方式      | 继承方式代理               | 继承或实现接口，多样化生成方式            |
 * | 使用难度      | API 复杂               | Fluent 风格，易用               |
 * | 依赖        | ASM（底层字节码操作库）        | 也依赖 ASM，但封装更好              |
 * | 框架应用      | Spring AOP（老版本）      | Spring AOP（新版默认）、Mockito 等 ，可能还没使用|
 */
