## 常量池
```java
public class ConstantPoolExample {

    // 成员变量，不是常量池，只是对象属性
    private int count = 10;

    // 静态变量，不是常量池，存储在方法区的静态区
    private static String staticMessage = "Hello, Static";

    // final 常量，且是编译期常量，会存入常量池
    public static final String GREETING = "Hello, World!";

    // final 常量，但需要运行时计算，不一定存入常量池（取决JVM）
    public static final int COMPUTED = 5 * 3;

    // 构造方法
    public ConstantPoolExample() {
        System.out.println("Constructor called, count = " + count);
    }

    // 普通实例方法
    public void printGreeting() {
        System.out.println(GREETING);  // 引用了常量池的字符串
    }

    // 静态方法，调用静态变量和实例方法（需要先创建对象）
    public static void staticMethod() {
        System.out.println(staticMessage); // 引用常量池字符串
        ConstantPoolExample example = new ConstantPoolExample();
        example.printGreeting();
    }

    // 主函数
    public static void main(String[] args) {
        staticMethod();

        String localStr = "Local String";   // 字面量常量池中有
        System.out.println(localStr);

        String concatStr = GREETING + " " + localStr; // 字符串拼接，涉及字符串常量池及StringBuilder类的符号引用
        System.out.println(concatStr);
    }
}

```  
| 代码部分                             | 是否进入常量池           | 备注                            |
| -------------------------------- | ----------------- | ----------------------------- |
| `"Hello, Static"`                | 是                 | 字符串字面量，存储在字符串常量池              |
| `"Hello, World!"`                | 是                 | final 修饰且是编译期常量，存在类常量池中       |
| `5 * 3`                          | 是（可能）             | 编译期计算后存常量池（int类型常量池）          |
| `"Constructor called, count = "` | 是                 | 字符串字面量，字符串常量池                 |
| `"Local String"`                 | 是                 | 字符串字面量，字符串常量池                 |
| `GREETING + " " + localStr`      | 涉及字符串拼接时会使用字符串常量池 | 编译器生成的 StringBuilder 调用也是符号引用 |
| 方法名、类名、字段名                       | 是符号引用             | 存储在类常量池，后续解析为方法、字段实际地址        |
| `count`（成员变量）                    | 否                 | 对象属性，存储在堆中的实例对象里              |
| `staticMessage`（静态变量）            | 否                 | 存储在方法区的静态区                    |

| 构造方式                             | 编译期进常量池 | 是否可复用引用 | 是否需要 `intern()` |
| -------------------------------- | ------- | ------- | --------------- |
| `"abc"`（字面量）                     | ✅ 是     | ✅ 是     | 不需要             |
| `"a" + "b"`（常量拼接）                | ✅ 是     | ✅ 是     | 不需要             |
| `"a" + variable`（包含变量）           | ❌ 否     | ❌ 否     | 需要              |
| `new String("abc")`              | ❌ 否     | ❌ 否     | 需要              |
| `StringBuilder("a").append("b")` | ❌ 否     | ❌ 否     | 需要              |  
---  
**量池的内容不是“你写在源码里它就进去了”，而是“你运行时第一次用它，它才进去”。**  
## intern()方法  
- 使用intern()后**无论如何返回的都是常量池的引用,这一点非常重要**,但是会出现下面的情况  
  - 如果常量池原先没有这个字符出运行时常量池，如下面
```java
        String s1 = new StringBuilder("计算").append("机").toString();  //这个不是编译常量
        String s2 = s1.intern(); // 常量池中原来没有 "计算机" ，返回的是S1的地址，这时候常量池"计算机的引用就指向"S1
        String s3 = "计算机";     // 之后才加载字面量,他确实在类的常量池里面，但是是第一次使用的时候才会加载进去
        System.out.println(s1 == s2); // true！（s1 的引用放进了常量池）
        System.out.println(s2 == s3); // true  
```  
  - 如果常量池原来已经有了这个字符串的常量池引用  
```java
        String s = "计算机";
        String s1 = new StringBuilder("计算").append("机").toString();
        String s2 = s1.intern(); // 常量池中原来没有 "计算机"
        String s3 = "计算机";     // 之后才加载字面量
        System.out.println(s1 == s2); // true！（s1 的引用放进了常量池）
        System.out.println(s2 == s3); // true
        System.out.println(s2 == s  && s2 == s3); //false

```  

