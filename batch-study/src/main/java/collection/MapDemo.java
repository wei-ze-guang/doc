package collection;

import org.junit.Test;

import java.util.*;

/**
 * HashMap
 * | 方法签名                                                                    | 说明                   |
 * | ----------------------------------------------------------------------- | -------------------- |
 * | `HashMap()`                                                             | 默认构造，默认容量16，加载因子0.75 |
 * | `HashMap(int initialCapacity)`                                          | 指定初始容量               |
 * | `HashMap(int initialCapacity, float loadFactor)`                        | 指定容量和加载因子            |
 * | `V put(K key, V value)`                                                 | 添加或替换键值对             |
 * | `V get(Object key)`                                                     | 根据键获取值               |
 * | `V remove(Object key)`                                                  | 删除指定键及其值             |
 * | `void clear()`                                                          | 清空所有元素               |
 * | `boolean containsKey(Object key)`                                       | 是否包含某键               |
 * | `boolean containsValue(Object value)`                                   | 是否包含某值               |
 * | `Set<K> keySet()`                                                       | 获取所有键的集合             |
 * | `Collection<V> values()`                                                | 获取所有值的集合             |
 * | `Set<Map.Entry<K,V>> entrySet()`                                        | 获取所有键值对的集合           |
 * | `void putAll(Map<? extends K, ? extends V> m)`                          | 添加另一个Map中的所有键值对      |
 * | `V getOrDefault(Object key, V defaultValue)`                            | 如果不存在key返回默认值        |
 * | `V putIfAbsent(K key, V value)`                                         | 只有当key不存在时才放入        |
 * | `boolean remove(Object key, Object value)`                              | 只有当key映射到value时才移除   |
 * | `boolean replace(K key, V oldValue, V newValue)`                        | 替换指定key对应的旧值为新值      |
 * | `V replace(K key, V value)`                                             | 替换指定key的值            |
 * | `void forEach(BiConsumer<? super K,? super V> action)`                  | 遍历所有元素，执行action      |
 * | `void replaceAll(BiFunction<? super K,? super V,? extends V> function)` | 使用函数替换所有元素的值         |
 */

/**
 * | 类/接口                        | 继承关系           | 底层数据结构         | 线程安全 | 线程安全实现方式                         | 是否保证顺序      | 备注                              |
 * | --------------------------- | -------------- | -------------- | ---- | -------------------------------- | ----------- | ------------------------------- |
 * | **Map\<K,V>**               | —              | —              | —    | —                                | —           | 顶层接口                            |
 * | **AbstractMap\<K,V>**       | 实现 Map         | —              | —    | —                                | —           | 部分默认实现                          |
 * | **HashMap\<K,V>**           | 继承 AbstractMap | 数组 + 链表 + 红黑树  | 否    | —                                | 否           | 允许 `null` 键和值                   |
 * | **LinkedHashMap\<K,V>**     | 继承 HashMap     | HashMap + 双向链表 | 否    | —                                | 保证插入顺序或访问顺序 | 维护插入顺序或访问顺序                     |
 * | **TreeMap\<K,V>**           | 继承 AbstractMap | 红黑树            | 否    | —                                | 键的排序顺序      | 键必须实现 Comparable 或传入 Comparator |
 * | **Hashtable\<K,V>**         | 实现 Map         | 数组 + 链表        | 是    | 所有公共方法均加 `synchronized` 关键字      | 否           | 不允许 `null` 键和值                  |
 * | **ConcurrentHashMap\<K,V>** | 实现 Map         | 数组 + 链表 + 红黑树  | 是    | JDK7 分段锁；JDK8 CAS + synchronized | 否           | 高效线程安全实现，不允许 `null` 键和值         |
 */
public class MapDemo implements Comparable<MapDemo> {



    public static void main(String[] args) {

        /**
         * 无序的
         */
        Map<String, String> map1 = new HashMap<String, String>();

        /**
         * 下面和上面几乎一样，但是保持插入游学
         * ├─LinkedHashMap（HashMap + 双向链表，保持插入顺序）
         */
        Map<String,String> map = new LinkedHashMap<String,String>();

        for(int i = 0 ; i <100 ; i++){
            map.put("key" + i, "value" + i);
            String s = map.putIfAbsent("key" + i, "value" + i);
            map.forEach((item,value)->{
                System.out.println(s);
            });
        }

        for (String key : map.keySet()) {
            System.out.println(key + "=" + map.get(key));
        }

        for (String key : map.values()) {
            System.out.println("values=" + key);
        }
    }

    @Test
    public void testLinkHashMap() {
        /**
         * 下面访问是无序的
         */
//        Map<String,String> linkHashMap = new HashMap<>();

        /**
         * 下面是有序的
         */
        Map<String,String> linkHashMap = new LinkedHashMap<>();

        Set<Map.Entry<String, String>> entries = linkHashMap.entrySet();

        Set<String> set = linkHashMap.keySet();
        Collection<String> values = linkHashMap.values();


        for(int i = 0 ; i <100 ; i++){
            linkHashMap.put("key" + i, "value" + i);
        }

        for(String key : linkHashMap.keySet()){
            System.out.println(key + "=" + linkHashMap.get(key));
        }
    }

    /**
     * 只保证键是有序的
     * 不允许 null 键或值	，但是hashMap允许一个 null 键和多个 null 值 这个就是hashMap加了锁
     */
    @Test
    public void testTreeMap() {
        Map<String,String> treeMap = new LinkedHashMap<>();
        for(int i = 0 ; i <100 ; i++){
            treeMap.put("key" + i, "value" + i);
            String s = treeMap.putIfAbsent("key" + i, "value" + i);  //如果存在返回旧的
        }
        for(String value: treeMap.values()){
            System.out.println(value);
        }
    }

    @Test
    public void testHashTable(){
        Map<String,String> hashTable = new Hashtable<>();  //这里不写<>编译器会报警
        Long start = System.currentTimeMillis();
        for(int i = 0 ; i <100 ; i++){
            hashTable.put("key" + i, "value" + i);
        }

        List list = new ArrayList();
        for(int i = 0 ; i <100 ; i++){
            list.add(hashTable.get("key" + i));
        }

        List list2 = new LinkedList(list);

        list2.addAll(list);

        Object[] array = list2.toArray();
        Object[] array1 = list.toArray();



        Long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Override
    public int compareTo(MapDemo o) {
        return 0;
    }
}
