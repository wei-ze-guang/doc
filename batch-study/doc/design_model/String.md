## String 类
**String 不存在自动装箱/拆箱机制，因为它本身就是一个引用类型，不是基本数据类型。**  

intern()方法  
- 返回字符串对象在字符串常量池中对应的“规范化”的字符串引用。
- 换句话说：
  - 如果字符串常量池中已经有一个内容相同的字符串，intern() 会返回那个字符串的引用。
  - 如果字符串常量池中没有，就会把当前字符串对象加入常量池，然后返回这个字符串的引用。 
```java
s1 = new String("hello");
String s2 = s1.intern();
String s3 = "hello";
System.out.println(s2 == s3);  // true
```  
解释：
- s1 是堆中新创建的字符串对象，地址不同于常量池中的字面量 "hello"。
- 调用 s1.intern() 返回的是常量池中 "hello" 的引用。
- 所以 s2 == s3 是 true。    
---  

- 什么时候用 intern()？
  - 想利用字符串常量池减少重复字符串对象的内存开销时使用。
  - 需要快速判断字符串是否相等时，使用 intern() 后可以用 == 比较引用。  
- 小结
  - 方法	作用描述
    - String.intern()	返回字符串常量池中对应的字符串引用，保证同内容字符串共享一个实例  
---  
### 情况分析：如果常量池中没有对应的字符串
```java
String s1 = new String("abc");
String s2 = s1.intern(); // 假设常量池中还没有 "abc"
/**
 * JVM 会把 s1 指向的字符串的内容添加到字符串常量池中。
 然后返回这个常量池中字符串的引用（注意，不一定是 s1 自身）
 */
```  
但注意一个细节（重点）  
- s1.intern() 返回的字符串对象，不一定是 s1 本身，而是堆里复制进去的“常量池”版本。
- 只有在你手动构造一个新的字符串，而常量池还没这个内容时，才会“首次加入”到常量池。  
---  
### 常量池中已有字面量
```java
String s1 = new String("abc");
String s2 = "abc"; // 字面量，JVM 启动时就放入常量池了
System.out.println(s1.intern() == s2); // true（intern 拿到的是 s2 的引用）
System.out.println(s1 == s2);          // false（s1 是 new 出来的）

```  
### 常量池中没有，intern 会放进去
```java
String s1 = new StringBuilder("计算").append("机").toString();
String s2 = s1.intern(); // 常量池中原来没有 "计算机"
String s3 = "计算机";     // 之后才加载字面量

System.out.println(s1 == s2); // true！（s1 的引用放进了常量池）
System.out.println(s2 == s3); // true

```  
⚠️ 在 Java 8 以后，intern 更倾向于将已有的堆对象直接放进常量池，不再复制（优化行为），所以 s1 == s2 为 true。  
