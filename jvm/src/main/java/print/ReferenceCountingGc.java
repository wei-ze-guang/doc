package print;

/**
 * 演示引用计数法的弊端
 */
public class ReferenceCountingGc {
    public Object instance = null;

    private static final int _1MB = 1024 * 1024;

    private byte[] bigSize = new byte[_1MB*2];

    public static void main(String[] args) throws Exception {
        // 下面会出现循环引用
        ReferenceCountingGc gc = new ReferenceCountingGc();
        ReferenceCountingGc gc1 = new ReferenceCountingGc();
        gc.instance = gc1;
        gc1.instance = gc;

        //下面赋值为null 然后开始测试GC
        gc = null; gc1 = null;

        System.gc();
    }
}
