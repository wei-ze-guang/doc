package base;

/**
 * | 操作符   | 名称          | 说明                      |             |
 * | ----- | ----------- | ----------------------- | ----------- |
 * | `&`   | 按位与（AND）    | 同为 1 才为 1               |             |
 * | \`    | \`          | 按位或（OR）                 | 有一个为 1 就为 1 |
 * | `^`   | 按位异或（XOR）   | 不同为 1，相同为 0             |             |
 * | `~`   | 按位取反（NOT）   | 0 变 1，1 变 0             |             |
 * | `<<`  | 左移          | 向左移动指定位数（*乘以 2ⁿ*）       |             |
 * | `>>`  | 右移（带符号）     | 向右移动指定位数，保留符号位（正补0，负补1） |             |
 * | `>>>` | 无符号右移（逻辑右移） | 高位补0，忽略符号位              |             |
 */
public class SymbolDemo {

    static void base8Type(){
        /**
         * Java的八种基本类型
         */
        char ch = 5;  //第一种，字符
        char chb = '5';  //单引号

        //char ch = "5";  // 错误，类型不匹配

        byte b = 127;  //第二种 byte 1个字节
        byte bb = '8'; //这样也是可以的

        short s = 32767;  //第三种 short 2两字节

        int i = 5555;  //第五种 默认类型  ，4个字节

        long t = 44444L;  //第四种

        //对于整数你不写后缀的话默认都是int类类型  符号条件下会给你自动转换，

        long l2 = 10;    // 正确，int自动提升为long
        //long l3 = 3000000000; // 错误，3000000000 超过了 int 范围，编译报错 ，除非你写成  long l3 = 3000000000L;

        boolean bbb = false; //第五种

        float f = 444.5f ;   //第六种

        double d = 444.5d;  //第七种

        double ddd = 3.14;     // 正确，3.14 默认是 double
        //float fff = 3.14;      // 错误，编译器报错：possible lossy conversion from double to float
        float f2 = 3.14f;    // 正确，加 f 后缀表示 float



    }

    public static void main(String[] args) {
        leftMoveOrRightMove();
    }

    static void leftMoveOrRightMove(){
        /**
         * 左移或者右移。 左移<<   右移 >>
         */
        long l = 444L;
        System.out.println("原始数据为:"+Long.toBinaryString(l));
        System.out.println("左移两位:"+Long.toBinaryString(l << 2));
        System.out.println(l << 2);
        System.out.println("右移两位:"+Long.toBinaryString(l >>2));

        int a  =  200;

        System.out.println(444&a);
        System.out.println(444^a);
        System.out.println(~a);
    }
}
