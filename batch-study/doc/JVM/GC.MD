| GC 类型             | 主要处理哪块内存                   |
| ----------------- | -------------------------- |
| Minor GC          | 新生代（Heap 的一部分）             |
| Major GC / Old GC | 老年代（Heap 的另一部分）            |
| Full GC           | 新生代 + 老年代 + 元空间（Metaspace） |  

总结：针对 HotSpot VM 的实现，它里面的 GC 其实准确分类只有两大种：
- 部分收集 (Partial GC)：新生代收集（Minor GC / Young GC）：只对新生代进行垃圾收集；
- 老年代收集（Major GC / Old GC）：只对老年代进行垃圾收集。需要注意的是 Major GC 在有的语境中也用于指代整堆收集；
- 混合收集（Mixed GC）：对整个新生代和部分老年代进行垃圾收集。整堆收集 (Full GC)：收集整个 Java 堆和方法区。空间分配担保
---  
| 触发场景                             | 触发的 GC 类型          | GC 后变化的区域                  | 可能出现的情况                                                              | 示例代码片段                                      |
| -------------------------------- | ------------------ | -------------------------- | -------------------------------------------------------------------- | ------------------------------------------- |
| Eden 区满，需要创建新对象                  | Minor GC           | Eden 清空；部分对象晋升或移入 Survivor | - 短暂 STW<br>- Survivor 不够时可能晋升老年代                                    | `byte[] data = new byte[10 * 1024 * 1024];` |
| Survivor 区空间不足，复制失败              | Minor GC + 对象晋升    | Survivor 清空；老年代对象增多        | - 老年代压力上升<br>- 晋升失败可能导致 Full GC                                      | 大对象生命周期长 + 小 Survivor 区                     |
| 老年代空间不足                          | Major GC（或 CMS GC） | 老年代整理（标记-整理/清除）            | - 长时间 STW（尤其是 Serial GC）<br>- 如果失败可能 Full GC<br>- OOM if GC 不够用      | 多次 `list.add(new Object())` 保持引用            |
| 手动调用 `System.gc()`               | Full GC            | 整个堆（新生代、老年代、元空间）           | - 强制 STW<br>- 性能抖动大<br>- 元空间也被清理                                     | `System.gc();`                              |
| 元空间空间不足                          | Full GC            | 元空间（类元数据）                  | - Full GC 仍无法释放 → OOM<br>- 报 `java.lang.OutOfMemoryError: Metaspace` | 类加载器反复加载类（如热加载场景）                           |
| G1 GC 判断回收阈值达到（如老年代 Region 使用率高） | G1 Mixed GC        | 多个 Region（年轻 + 老）          | - 停顿时间较可控<br>- 部分 Region 被合并回收                                       | 大量中长期对象混杂在 Young 和 Old Region               |
| 多线程同时创建大量新对象                     | Minor GC（频繁）       | Eden 被反复清理                 | - GC 频率过高<br>- STW 间歇性抖动                                             | 多线程同时执行 `new byte[512*1024]`                |

| 特性         | PermGen（JDK 7 及以前） | Metaspace（JDK 8+）   |
| ---------- | ------------------ | ------------------- |
| 内存分配位置     | JVM 堆内             | 本地内存（native）        |
| 是否在 GC 范围内 | ✅ 是                | ✅ 部分是（依赖类卸载）        |
| 回收方式       | Full GC 时清理        | Full GC + 类卸载后释放    |
| 会不会 OOM    | 会（`PermGen OOM`）   | 也会（`Metaspace OOM`） |

- ✅ 为什么说 Metaspace 不完全受 GC 管理？
  - 它在**堆外内存（Native Memory）**中分配，不属于 -Xmx 所控制的堆空间。
  - 只有当类被卸载时，Metaspace 中的空间才能被释放。
  - 而类的卸载需要满足条件，依赖于类加载器没有强引用存在。  

- 假设你不断创建新的类（模拟热部署）  
```java
while (true) {
    ClassLoader loader = new URLClassLoader(new URL[]{new URL("file:/some/path/")});
    Class<?> clazz = loader.loadClass("MyClass");
    // 不再引用 loader 的话，GC 有可能卸载这个类
}

```  
- 如果你不释放 loader：
  - 类加载器就不会被 GC 回收
  - 加载的类也不能卸载
  - Metaspace 不会释放
- 最终你可能得到这个：
```java
java.lang.OutOfMemoryError: Metaspace

```  
| 参数                          | 说明                        |
| --------------------------- | ------------------------- |
| `-XX:MetaspaceSize=64m`     | 初始大小，超过才会触发 Full GC 尝试卸载类 |
| `-XX:MaxMetaspaceSize=128m` | 最大大小，超过会 OOM              |
| `-XX:+PrintGCDetails`       | 可以看到 Metaspace 的变化        |

- 总结一张嘴：
  - Metaspace 在本地内存，但还是受 GC 的“间接影响”
  - 只有当类被卸载时，它的空间才会被释放
  - 如果类加载器不被释放，Metaspace 会撑爆 → OOM 
  - 
JDK1.7 之前运行时常量池逻辑包含字符串常量池存放在方法区, 此时 hotspot 虚拟机对方法区的实现为永久代JDK1.7 字符串常量池被从方法区拿到了堆中,   
这里没有提到运行时常量池,也就是说字符串常量池被单独拿到堆,运行时常量池剩下的东西还在方法区, 也就是 hotspot 中的永久代 。  
JDK1.8 hotspot 移除了永久代用元空间(Metaspace)取而代之, 这时候字符串常量池还在堆, 运行时常量池还在方法区, 只不过方法区的实现从永久代变成了元空间(Metaspace)


