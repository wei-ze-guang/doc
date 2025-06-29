## BigDecimal；类  不是原子性的，有线程安全
### 1. 构造方法
- BigDecimal(String val)
用于通过字符串创建 BigDecimal 实例，避免浮点数精度丢失问题。推荐使用此构造方法。  
```java
BigDecimal num = new BigDecimal("0.1");  // 推荐

```  
- BigDecimal(double val)
```java
BigDecimal num = new BigDecimal(0.1);  // 不推荐，可能会丢失精度

```