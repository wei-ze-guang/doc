## 类型判断和比较
- 1. instanceof —— 判断是否是某个类或接口的实例  ,最常用
```java
Object obj = "hello";
if (obj instanceof String) {
    System.out.println("是字符串！");
}

```  
| 语法                      | 说明                         |
| ----------------------- | -------------------------- |
| `a instanceof B`        | `a` 是 `B` 或其子类/实现类，则为 true |
| `null instanceof X`     | 永远返回 false，不抛异常            |
| `if (x instanceof T t)` | Java 16+，自动强转变量            |
| instanceof 只能用于引用类型     | 基本类型（如 int）不能用             | 

- 2. getClass() —— 获取运行时“真实类型”  
```java
Object obj = "hello";
System.out.println(obj.getClass());          // class java.lang.String
System.out.println(obj.getClass() == String.class); // true

```  
| 方法                          | 判断逻辑            | 是否考虑继承     |
| --------------------------- | --------------- | ---------- |
| `obj instanceof T`          | 是 T 或其子类都为 true | ✅ 是        |
| `obj.getClass() == T.class` | 只匹配完全相同的类       | ❌ 否（不包含子类） |  
---  

- 3 Object.getClass().isArray() —— 判断是不是数组类型  
```java
int[] arr = {1, 2, 3};
System.out.println(arr.getClass().isArray());  // true

@Test
public void useIsArray(){
    int[] t = new int[]{1,2,1};
    System.out.println(t.getClass() == int[].class);  //true
    System.out.println(t.getClass().isArray());  //true
}

```  
### 当然还有其他的
