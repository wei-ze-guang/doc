package j.util.map;

public class TestMyHashMap {

    public static void main(String[] args) {
        HashMap map = new HashMap();
//        map.put("key1", "value1");
//        map.put("key2", "value2");
//        map.put("key3", "value3");
//        map.put("key4", "value4");
//        for (Object key : map.keySet()) {
//            System.out.println("key=" + key + ", value=" + map.get(key));
//        }
        for (int j = 0; j < 10000; j++) {
            map.put("key" + j, "value" + j);
        }
        System.out.println("扩容次数"+map.getResizeCount());

        map.printSize();
        map.printTableSize();
        map.printLinked();


    }

    static void log(String s) {
        System.out.println(s);
    }
}
