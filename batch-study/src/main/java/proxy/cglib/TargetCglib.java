package proxy.cglib;

/**
 *CGLIB的目标类，没实现接口 CGLIB代理的话会继承这个类生成一个新的类
 */
public class TargetCglib {

    public void tCglib(){
        System.out.println("tCglib");
    }
}
