package vmoption;

/**
 * 请先看这个
 * 下面的注释是我把java的语言的一些功能写出来
 */
public class JvmStandardAndImplCourse {
    /**
     * java定义的是final修饰的字段一旦复制不可以改变
     */
    private final String str = "final修饰的字符串";

    public static void main(String[] args) {
        /**
         * 在控制台输出 Hello World"
         */
        System.out.println("Hello World");//大家都知道这是一个标准输出
    }

    /**
     System.out.println("狗蛋爱JAVA");
     * 这个方法被调用的话输出
     */
    private void jvm(){
        System.out.println("狗蛋爱JAVA");
    }

    /**
     * 加载类阶段初始化阶段自动调用
     */
    static {
        System.out.println("这是静态代码块");
    }
}
