## Comparable 接口和 Comparator 接口都是 Java 中用于排序的接口，它们在实现类对象之间比较大小、排序等方面发挥了重要作  
- Comparable 接口实际上是出自java.lang包 它有一个 compareTo(Object obj)方法用来排序Comparator接口实际上是出自 java.util 包它有一个compare(Object obj1, Object obj2)方法用来排序
```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```
- 如果一个对象是实现了Comparable接口。默认采用上面排序  
---  
