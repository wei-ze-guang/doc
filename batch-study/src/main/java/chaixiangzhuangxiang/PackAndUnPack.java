package chaixiangzhuangxiang;

import org.junit.Test;

/**
 * 自动装拆箱
 */
public class PackAndUnPack {

    public static void main(String[] args) {

//        int a = 444;
//        int at = new Integer(444);
//        a.equals;
//        at.equals;
//
//
//        Integer b = 5555;
//        Integer bt = new Integer(555);
//        b.equals(bt);  //他是对象，他是对象，他能使用这个方法
//        bt.equals(a);  //他是对象，他是对象，他能使用这个方法，但是这里注意看他的参数 equals的参数是没有要求的，要看类怎么重写他的


        String s="这是值";
        String ss = new String("这是值");
        String sss = new String(s);

        System.out.println(s.equals(ss));  //true
        System.out.println(s.equals(sss));  //true
        System.out.println(ss.equals(ss));  // true




    }

    @Test
    /**
     * 值得比较
     */
    public void test() {
        Integer i = new Integer(888);
        Integer ii = 888;  //注意这里返回的是对象
        Integer iii = new Integer(887);
        System.out.println(i.equals(ii));  //true
        System.out.println(i.equals(iii));  //false
        System.out.println(ii.equals(iii));  //false
    }

    @Test
    /**
     * 只要有任何一个变量是基本类型
     */
    public void denggdenghao(){
        int a = 555;
        int b = 555;
        int c = new Integer(555);
        int d = new Integer(555);
        String s = "555";
        String ss = new String("555");

        /**
         * 下面三个比较的时候都涉及到了 基本类型a ，所以比较的时候只能比较值，因为我是基本类型，我只有值啊！
         */
        System.out.println(a == b);  //true
        System.out.println(a == c);  //true
        System.out.println(a == d);  //true
    }

    @Test
    public void dengdengAdderess(){

        Integer a = 555;  //这个a是一个对象类型引用，不是基本类型，不是基本类型

//        Integer a = Integer.valueOf(555);


        Integer b = 554;  //这个b是一个对象类型引用，不是基本类型，不是基本类型
        Integer c = new Integer(555);  //这个c是一个对象类型引用，不是基本类型，不是基本类型

        System.out.println(a == b);  //false
        System.out.println(a == c);  //false
        System.out.println(c == b);  //false

        System.out.println(System.identityHashCode(a));  //1289479439
        System.out.println(System.identityHashCode(b));  //6738746
        System.out.println(System.identityHashCode(c));  //2096171631

    }

    @Test
    public void t(){
        /**
         * 自己new的对象 ，只要使用new 他肯定是唯一的，肯定是唯一的
         */

        Integer a = 100;
        Integer b = 100;
        System.out.println(a == b);  //true ，这个等下讲话
        Integer big = 500;
        Integer bi = 500;
        System.out.println(big == bi);  //false

        Integer c = new Integer(100);
        Integer cc = new Integer(100);

        System.out.println(a == c); //false，这两个一定不相等，先不说他如何装箱，这个c是我自己new的，无论你这个c从哪里来的，但是肯定不是我自己new这个

        System.out.println(c == cc); //false  这两个一定不相等

        Integer d = c;
        System.out.println(d == c);  //true  这两个一定相等，就是地址引用！！！  记住这里，等一下讲装箱的时候重点讲这个地址引用

    }

    @Test
    public void test1(){
        //  先讲一个事实，是事实，java程序一开始做了一件事，对于Integer来说他把数字 -128 到正数127 都各new对象在内存
        Integer a = new Integer(-128);
        Integer b = new Integer(-127);
        //中间还有很多
        Integer f = new Integer(100);
        Integer c = new Integer(126);
        Integer d = new Integer(127);
        //  解释上面的 就是java自己存在内存里面的,这里我们需要假装看不见，但是他是实际存在的

        Integer aa = new Integer(-128);//这个是我们自己new的，只是-128和上面相同，除了这个几乎没有任何关系了

        Integer aaa = 100;  //如果我们这样子，假设这个数字是-128 到127 结果是拿到上面的，java给你返回的就是 Integer a = new Integer(-128);
        Integer aaaa = 100 ; //看到还是还是在-128到127中间，给你返回的还是 Integer a = new Integer(-128); 这个

        System.out.println(aaa == aaaa);  //true

        //当你的数组不在-128 到127 的时候
        //  他会new一个给你
        Integer t = 500;
        Integer tt = 500;
        System.out.println(t == tt); //false

    }
}
