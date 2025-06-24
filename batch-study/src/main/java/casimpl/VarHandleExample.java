package casimpl;

import org.junit.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class VarHandleExample {
    public int a = 4;
    public String b = "sss";

    static  MethodHandles.Lookup lookup ;  //不赋值的话默认是努力了
    static VarHandle handle;

    public static void main(String[] args) {
        VarHandleExample varHandleExample = new VarHandleExample();
        varHandleExample.inc();
    }

    private void inc(){
        int expect = (Integer) handle.get(this);
        Object oldValue = handle.compareAndExchange(this, expect, 5);
        boolean success = ((Integer) oldValue == expect);  // 表示 CAS 是否成功
        System.out.println("返回的结果是:"+oldValue+"状态成功与否:"+success);
    }
    public VarHandleExample(){
        System.out.println("构造方法被执行");
    }



    /**
     * 静态代码块在 类加载过程中的“初始化阶段”执行
     */

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        System.out.println("static block");
        try {
            handle = lookup.findVarHandle(VarHandleExample.class,"a",int.class);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test(){
        VarHandleExample varHandleExample = new VarHandleExample();

    }

}
