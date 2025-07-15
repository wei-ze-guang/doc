# 装饰器模式
**装饰器模式就是对数据的一层一层加工，比如加密，解密的话数据反过来**  

装饰器模式是一种结构型设计模式，它允许在不改变原有类结构的前提下，动态地给对象添加额外功能。  
相比继承扩展功能，它更灵活、更符合开闭原则  
📦 二、项目场景设计（中等偏上难度）
🎯 场景：一个数据加密组件系统
我们要实现一个数据加密系统，用户可以按需选择不同的功能：
```text
- 原始功能：输出原始数据
- 可选功能：
    - 压缩
    - 加密（对称加密：Caesar）
    - Base64 编码
    - 用户可以组合使用，比如：压缩 → 加密 → Base64 编码  
      ┌────────────┐
      │ DataSource │ <───接口（定义核心方法）
      └────┬───────┘
      │
      ┌──────▼──────────┐
      │   FileDataSource│  ← 被装饰者（具体实现）
      └─────────────────┘
      ▲
      │（组合）
      ┌────────┴───────────┐
      │   DataSourceDecorator│ ← 抽象装饰器
      └────────┬───────────┘
      ▲
      ┌──────┴──────┬─────────┐
      │             │         │
      EncryptDecorator CompressDecorator Base64Decorator
```
---  
## 🧱 四、代码实现
- 接口
```java
public interface DataSource {
    String write(String data);
    String read();
}

```  
- 具体实现 FileDataSource
```java
public class FileDataSource implements DataSource {
    private String data;

    @Override
    public String write(String data) {
        this.data = data;
        return "原始写入：" + data;
    }

    @Override
    public String read() {
        return data;
    }
}

```  
- 抽象装饰器类 DataSourceDecorator
```java
public abstract class DataSourceDecorator implements DataSource {
    protected DataSource wrappee;

    public DataSourceDecorator(DataSource source) {
        this.wrappee = source;
    }

    @Override
    public String write(String data) {
        return wrappee.write(data);
    }

    @Override
    public String read() {
        return wrappee.read();
    }
}

```  
- 压缩装饰器 CompressDecorator
```java
public class CompressDecorator extends DataSourceDecorator {
    public CompressDecorator(DataSource source) {
        super(source);
    }

    @Override
    public String write(String data) {
        String compressed = "【压缩后】" + data;
        return super.write(compressed);
    }
}

```  
- 5️⃣ 加密装饰器 EncryptDecorator
```java
public class EncryptDecorator extends DataSourceDecorator {
    public EncryptDecorator(DataSource source) {
        super(source);
    }

    @Override
    public String write(String data) {
        String encrypted = caesarEncrypt(data, 3);
        return super.write(encrypted);
    }

    private String caesarEncrypt(String data, int shift) {
        StringBuilder sb = new StringBuilder();
        for (char c : data.toCharArray()) {
            sb.append((char)(c + shift));
        }
        return sb.toString();
    }
}

```  
- 6️⃣ Base64 装饰器
```java
import java.util.Base64;

public class Base64Decorator extends DataSourceDecorator {
    public Base64Decorator(DataSource source) {
        super(source);
    }

    @Override
    public String write(String data) {
        String encoded = Base64.getEncoder().encodeToString(data.getBytes());
        return super.write(encoded);
    }
}

```  
- 7️⃣ 测试入口类
```java
public class Main {
    public static void main(String[] args) {
        // 原始数据源
        DataSource source = new FileDataSource();

        // 装饰器链：压缩 → 加密 → Base64
        DataSource decorated = new Base64Decorator(
                                    new EncryptDecorator(
                                        new CompressDecorator(source)));

        String result = decorated.write("hello world");
        System.out.println(result);  // 输出被多重装饰后的写入

        System.out.println("最终读取结果：" + source.read());  // 可验证读取链
    }
}

```  
## 、关键点总结
| 关键点                 | 说明          |
| ------------------- | ----------- |
| 装饰器和原始对象实现相同接口      | 保证“可以互换”    |
| 装饰器内部组合了原对象（或其他装饰器） | 实现链式增强      |
| 可以任意组合多个装饰器         | 保持开放扩展、关闭修改 |
| 每个装饰器只关注一件事         | 遵守单一职责原则    |
## 优缺点分析
- ✅ 优点
- 比继承更灵活（运行时组合）
    - 满足开闭原则（添加新功能不用改旧类）
    - 可以动态控制功能增强顺序
    - 可以无限叠加，组合出多种行为

- ❌ 缺点
    - 结构较复杂，调试时可能难以追踪
    - 多层嵌套可能影响性能与可读性
    - 对顺序敏感（先压缩还是先加密会影响结果）
###  补充一点Base64知识
- Base64 是一种编码（Encoding）方式，主要用于在文本中安全地传输二进制数据。
- Base64 是一种把二进制数据转换为 ASCII 字符串的方式，常用于网络传输、存储等场景。
- 举个实际例子
    - 你有一张图片、一个文件、或者一段加密后的二进制数据——这类东西不能直接放进 HTTP 请求、HTML、XML 这些“文本格式”中传输。
      怎么办？
        - 就用 Base64 把它“变成文本”。
```java
import java.util.Base64;

public class Demo {
    public static void main(String[] args) {
        //编码
        String text = "狗蛋";
        String encoded = Base64.getEncoder().encodeToString(text.getBytes());
        System.out.println("编码后: " + encoded); // 5aSN5a2m
    }
}

// 解码回来：
byte[] decodedBytes = Base64.getDecoder().decode(encoded);
String decoded = new String(decodedBytes);
System.out.println("解码后: " + decoded); // 狗蛋


```  
| 应用场景          | 说明                           |
| ------------- | ---------------------------- |
| 📡 网络传输       | 邮件、HTTP 表单中传递文件或图片           |
| 🧾 数据持久化      | 把图片、音频编码成字符串保存到数据库           |
| 🔐 加密辅助       | 加密结果通常是乱码，用 Base64 编码才能打印或存储 |
| 🧱 JSON / XML | 二进制文件不能直接放进 JSON，先 Base64 一下 |
---  

