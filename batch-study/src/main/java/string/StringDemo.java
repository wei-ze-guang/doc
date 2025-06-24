package string;

/**
 *     public native String intern();  String的源码，返回的是字符串
 */
public class StringDemo {
    static String string = "1";

    public static void main(String[] args) {
//        String s1 = new String("1");
//
//        String s2 = new String("1");
//
//        System.out.println(s1 == string); //false
//
//        System.out.println(s1 == s2); //false
//
//        System.out.println((""+"1") == "1");  //true

        /**
         * 在使用intern的时候常量池中就已经在常量区
         */

//        String ss1 = new String("abc");  //这个是对象地址
//        String ss2 = "abc"; // 字面量，JVM 启动时就放入常量池了，这个在类加载的时候就已经加载进常量池
//        System.out.println(ss1.intern() == ss2); // true（intern 拿到的是 s2 的引用）
//        System.out.println(ss1 == ss2);          // false（s1 是 new 出来的） new出来的永远不相等

        /**
         * 下面是常量池中原先没有的
         */
        String s1 = new StringBuilder("计算").append("机").toString();
        String s2 = s1.intern(); // 常量池中原来没有 "计算机"
        String s3 = "计算机";     // 之后才加载字面量
        System.out.println(s1 == s2); // true！（s1 的引用放进了常量池）
        System.out.println(s2 == s3); // true

    }

}
