package instanceOf;

import org.junit.Test;

/**
 * 用来做一下java类型判断的
 */
public class Main {


    public static void main(String[] args) {

    }

    @Test
    public void useInstanceOf(){
        String s = "555";
        String ss = new String("555");
        System.out.println(s instanceof String);
        System.out.println(ss instanceof String);
        System.out.println(null instanceof String);
        System.out.println(new intSImpl() instanceof intS);
        System.out.println(null instanceof  String);
    }

    @Test
    public void useGetClass(){
        /**
         * 只匹配完全相等的类，不包括子类
         */
        String s = "555";
        String ss = new String("555");
        System.out.println(s.getClass());
        System.out.println(ss.getClass());

        Character t = 'v';
        System.out.println(t.getClass() == Character.class);
    }

    @Test
    public void useClassInstance(){
        String s = "555";
        System.out.println(String.class.isInstance(s));
    }


}

interface intS{
    int a = 0; //这里默认是final static

    /**
     * 新版JDK支持默认方法，而且是实现
     */
    default void has(){
        System.out.println(a);
    }

    void t();
}

class intSImpl implements intS{

    @Override
    public void has() {
        intS.super.has();
    }

    @Override
    public void t() {

    }
}
