package classloader;

/**
 * 这个类用来而是一个对象初始化的执行相属顺序
 */
public class ProcessOrder {
    public static void main(String[] args) {

        /**
         * 这个是第一次加载
         */
        ProcessOrder p = new ProcessOrder();

        /**
         * 下面在new 一个，因为已经记载过
         */
        System.out.println("再new一个");

        ProcessOrder p2 = new ProcessOrder();

    }

    /**
     * 这里是需要注意的
     * 只会只会被new的时候发生， new ProcessOrder()的时候，执行下面的操作
     * 对象内存分配 -> 实例变量默认值赋值（a=0） -> 显示初始化赋值（a=5） -> 构造方法体（可以修改a）
     */
    /**
     * 发生在 对象创建（new）时，即调用构造器过程中。
     * JVM会先给实例变量赋默认值（0、false、null 等）。
     * 然后执行代码里显式赋的初始值（如 private int a = 5;）。
     * 最后执行构造器体内的代码。
     */
    private int a = 5 ;

    private static final int c ;
    private static int b = 5  ;  //默认是0

    /**
     * 今天代码块之后再类加载的时候执行一次，而是是类初始化阶段
     */
    static {
        new String("fff");

        System.out.println("执行static之前b的值:b="+b);
        c = 500;//在这里写的话可以不用构造器，
        b = 100;
        System.out.println("在代码块中设置b的值是,"+"b="+b);
    }

    public ProcessOrder() {
        System.out.println("如果成员变量一开始就给他赋值的话a的值是:a="+this.a);  //也就是说这个是在构造方法之前执行
        b = 200;
        System.out.println("在构造器给b设置的值是200,"+"b="+b);
    }

}
/**
 *     /**
 *      * 阶段名	说明
 *      * 1. 加载（Loading）	找到 .class 文件并加载到内存，生成 Class 对象
 *      * 2. 验证（Verification）	验证 class 文件格式是否符合规范
 *      * 3. 准备（Preparation）	给静态变量分配内存并赋默认值 不包括成员变量，就是上面的a是不分配内存和值的
 *      * 4. 解析（Resolution）	把常量池里的符号引用替换为真实引用
 *      * 5. 初始化（Initialization）	执行类的 <clinit> 方法，包括静态变量赋值和 static {} 代码块
 *      */
/**
 * | 名称         | 作用          | 触发时机        | 对应 Java 代码          |
 * | ---------- | ----------- | ----------- | ------------------- |
 * | `<clinit>` | **类的初始化方法** | 第一次使用类时     | `static` 变量赋值、静态代码块 |
 * | `<init>`   | **对象的构造方法** | `new` 一个对象时 | 构造方法 `Person()`     |
 */

