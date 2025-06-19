```text
java.util.Collection
       ▲
       │
java.util.Queue （接口） ←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←
       ▲                                                       ▲
       │                                                       │
java.util.concurrent.BlockingQueue<E>              java.util.Deque<E>
       ▲                                                       ▲
       │                                                       │
       ├─────────────────────────────────────┐       java.util.concurrent.BlockingDeque<E>
       │                                     │                   ▲
       │                                     │                   │
       ▼                                     ▼                   ▼
ArrayBlockingQueue<E>        LinkedBlockingQueue<E>     LinkedBlockingDeque<E>
DelayQueue<E>                PriorityBlockingQueue<E>
SynchronousQueue<E>          LinkedTransferQueue<E>
LinkedBlockingDeque<E>
```

## queue接口
| 方法名          | 说明                  | 阻塞性   |
| ------------ | ------------------- | ----- |
| `add(E e)`   | 插入元素，失败抛异常          | ❌ 非阻塞 |
| `offer(E e)` | 插入元素，失败返回 false     | ❌ 非阻塞 |
| `remove()`   | 获取并删除头元素，失败抛异常      | ❌ 非阻塞 |
| `poll()`     | 获取并删除头元素，失败返回 null  | ❌ 非阻塞 |
| `element()`  | 获取头元素但不移除，失败抛异常     | ❌ 非阻塞 |
| `peek()`     | 获取头元素但不移除，失败返回 null | ❌ 非阻塞 |

# Java 集合接口、实现类及特性一览表

| 接口           | 主要实现类                                               | 核心方法（简要）                             | 线程安全 | 典型使用场景                       |
|----------------|----------------------------------------------------------|----------------------------------------------|----------|----------------------------------|
| **Collection** | `ArrayList`, `HashSet`, `LinkedList`                     | `add()`, `remove()`, `contains()`, `iterator()` | 否       | 存储对象的通用容器                 |
| **List**       | `ArrayList`, `LinkedList`, `Vector`                      | `get()`, `set()`, `add(index, e)`, `remove(index)` | `Vector`线程安全，其他非线程安全 | 有序集合，支持索引访问             |
| **Set**        | `HashSet`, `LinkedHashSet`, `TreeSet`                    | `add()`, `remove()`, `contains()`             | 否       | 不允许重复元素的集合               |
| **Queue**      | `LinkedList`, `PriorityQueue`, `ArrayDeque`, `ConcurrentLinkedQueue` | `offer()`, `poll()`, `peek()`                   | `ConcurrentLinkedQueue`线程安全，其它非线程安全 | 先进先出队列，任务调度、消息传递 |
| **Deque**      | `ArrayDeque`, `LinkedList`, `ConcurrentLinkedDeque`      | `addFirst()`, `addLast()`, `pollFirst()`, `pollLast()` | `ConcurrentLinkedDeque`线程安全，其它非线程安全 | 双端队列，支持两端插入和删除     |
| **BlockingQueue** | `LinkedBlockingQueue`, `ArrayBlockingQueue`, `DelayQueue`, `SynchronousQueue` | `put()`, `take()`, `offer(e, timeout)`, `poll(timeout)` | 是       | 线程间安全的阻塞队列，用于线程通信 |
| **Map**        | `HashMap`, `LinkedHashMap`, `TreeMap`, `ConcurrentHashMap` | `put()`, `get()`, `remove()`, `containsKey()` | `ConcurrentHashMap`线程安全，其它非线程安全 | 键值对存储                       |

---

# 接口核心方法简介

| 接口          | 主要方法（含阻塞信息）                               |
|---------------|-----------------------------------------------------|
| **Collection**| `add(E e)`, `remove(Object o)`, `contains(Object o)`, `size()`, `iterator()` |
| **List**      | 继承Collection，增加：`get(int index)`, `set(int index, E element)`, `add(int index, E element)`, `remove(int index)` |
| **Set**       | 继承Collection，无序且无重复元素                      |
| **Queue**     | `offer(E e)`(非阻塞插入), `poll()`(非阻塞移除), `peek()`(查看头部) |
| **Deque**     | 继承Queue，增加：`addFirst()`, `addLast()`, `pollFirst()`, `pollLast()` |
| **BlockingQueue** | 阻塞方法：`put(E e)`(阻塞插入), `take()`(阻塞移除)，带超时的 `offer()`, `poll()` |
| **Map**       | `put(K key, V value)`, `get(Object key)`, `remove(Object key)` |

---

# 线程安全说明

- 绝大多数基础集合实现（`ArrayList`, `HashSet`, `LinkedList`等） **非线程安全**，多线程并发时需额外同步。
- **Vector**, **Hashtable** 是线程安全的老版本实现，已不推荐。
- 并发包（`java.util.concurrent`）中的集合如 `ConcurrentHashMap`, `ConcurrentLinkedQueue`, `LinkedBlockingQueue` 等为线程安全设计，适合多线程使用。

---

# 一般使用场景总结

| 场景           | 推荐集合类型                             |
|----------------|---------------------------------------|
| 顺序访问和修改 | `ArrayList`, `LinkedList`              |
| 唯一元素集合   | `HashSet`, `TreeSet`                   |
| 先进先出队列   | `LinkedList`, `ArrayDeque`, `BlockingQueue` |
| 需要阻塞和线程通信 | `LinkedBlockingQueue`, `ArrayBlockingQueue`   |
| 键值存储       | `HashMap`, `ConcurrentHashMap`         |
| 多线程安全访问 | 并发集合（`ConcurrentHashMap`, `ConcurrentLinkedQueue`） |

