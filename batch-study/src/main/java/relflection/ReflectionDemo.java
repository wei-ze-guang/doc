package relflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionDemo {
    /**
     * | 类名 / 方法                                       | 参数                  | 功能说明                              | 返回值                | 备注/补充说明                                                     |
     * | --------------------------------------------- | ------------------- | --------------------------------- | ------------------ | ----------------------------------------------------------- |
     * | **Class\<?>**                                 |                     | 表示一个类的字节码对象，反射的入口                 | —                  | Java中所有类的`Class`对象代表类的运行时类型                                 |
     * | `Class.forName(String className)`             | `className` (类全限定名) | 根据类的全限定名加载并返回该类的Class对象，触发类加载和初始化 | `Class<?>`         | 抛`ClassNotFoundException`                                   |
     * | `Class.getName()`                             | 无                   | 返回类的全限定名字符串                       | `String`           |                                                             |
     * | `Class.getSimpleName()`                       | 无                   | 返回类的简单名（不含包名）                     | `String`           |                                                             |
     * | `Class.getSuperclass()`                       | 无                   | 返回该类的直接父类的Class对象                 | `Class<?>`         | 若无父类返回null (接口、Object)                                      |
     * | `Class.getInterfaces()`                       | 无                   | 返回该类实现的接口数组                       | `Class<?>[]`       |                                                             |
     * | `Class.getConstructors()`                     | 无                   | 返回该类所有 `public` 构造方法数组            | `Constructor<?>[]` |                                                             |
     * | `Class.getDeclaredConstructors()`             | 无                   | 返回该类所有声明的构造方法数组（包括private）        | `Constructor<?>[]` |                                                             |
     * | `Class.getMethods()`                          | 无                   | 返回该类所有 `public` 方法，包括继承的方法        | `Method[]`         |                                                             |
     * | `Class.getDeclaredMethods()`                  | 无                   | 返回该类声明的所有方法，包括private             | `Method[]`         |                                                             |
     * | `Class.getFields()`                           | 无                   | 返回该类所有 `public` 字段，包括继承字段         | `Field[]`          |                                                             |
     * | `Class.getDeclaredFields()`                   | 无                   | 返回该类声明的所有字段，包括private             | `Field[]`          |                                                             |
     * | `Class.newInstance()`                         | 无                   | 创建该类的实例，调用无参构造器                   | `Object`           | 已过时，推荐用`Constructor.newInstance()`                          |
     * | **Constructor<T>**                            |                     | 构造方法对象，代表类的构造器                    | —                  |                                                             |
     * | `Constructor.newInstance(Object... initargs)` | 构造参数                | 调用构造方法创建实例                        | `T`                | 可能抛异常，如`InstantiationException`、`InvocationTargetException` |
     * | `Constructor.getParameterTypes()`             | 无                   | 返回构造方法参数类型数组                      | `Class<?>[]`       |                                                             |
     * | `Constructor.setAccessible(boolean)`          | `true`或`false`      | 设置是否绕过Java访问权限检查                  | `void`             | 用于访问private构造器                                              |
     * | **Method**                                    |                     | 代表类的方法                            | —                  |                                                             |
     * | `Method.invoke(Object obj, Object... args)`   | 调用对象、方法参数           | 通过反射调用该方法                         | `Object`           | 可能抛异常，如`InvocationTargetException`                          |
     * | `Method.getName()`                            | 无                   | 获取方法名                             | `String`           |                                                             |
     * | `Method.getReturnType()`                      | 无                   | 获取方法返回类型                          | `Class<?>`         |                                                             |
     * | `Method.getParameterTypes()`                  | 无                   | 获取方法参数类型数组                        | `Class<?>[]`       |                                                             |
     * | `Method.setAccessible(boolean)`               | `true`或`false`      | 绕过访问检查                            | `void`             | 用于访问private方法                                               |
     * | **Field**                                     |                     | 代表类的成员变量                          | —                  |                                                             |
     * | `Field.get(Object obj)`                       | 对象实例                | 获取指定对象该字段的值                       | `Object`           | 可能抛`IllegalAccessException`                                 |
     * | `Field.set(Object obj, Object value)`         | 对象实例、新值             | 设置指定对象该字段的新值                      | `void`             |                                                             |
     * | `Field.getName()`                             | 无                   | 获取字段名                             | `String`           |                                                             |
     * | `Field.getType()`                             | 无                   | 获取字段类型                            | `Class<?>`         |                                                             |
     * | `Field.setAccessible(boolean)`                | `true`或`false`      | 绕过访问检查                            | `void`             | 用于访问private字段                                               |
     * @param args
     * @throws ClassNotFoundException
     */

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        ReflectionExample reflectionExample = new ReflectionExample();

        Class<?> clazz = ReflectionExample.class;

        Class<?> clazz2 = ReflectionExample.class;


        /**
         * 设置一下加载器
         */
        Class<?> clazz3 = Class.forName("relflection.ReflectionExample",true,Thread.currentThread().getContextClassLoader());

        String name = clazz.getName();
        System.out.println("全类名："+name);
        System.out.println("简名："+clazz.getSimpleName());
        System.out.println("父类名字"+clazz.getSuperclass().getSimpleName());

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        constructors[0].setAccessible(true);

//        Object o = constructors[0].newInstance(boolean.class);


        Object obj  = clazz.newInstance();// 构造实例

        Method[] methods = clazz.getDeclaredMethods();

        Method sayHello = clazz.getDeclaredMethod("sayHello");

        sayHello.setAccessible(true);

        sayHello.invoke(obj);

        methods[0].setAccessible(true);

        methods[0].invoke(obj);

    }
}
