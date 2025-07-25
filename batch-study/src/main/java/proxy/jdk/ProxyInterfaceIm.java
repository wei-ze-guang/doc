package proxy.jdk;

/**
 * 实现接口的类和需要被代理的类
 */

class ProxyInterfaceIm implements ProxyInterface {

    @Override
    public void t(Object o, int a) {
        System.out.println("ProxyInterfaceIm的t方法(被代理实现类)");
    }

    @Override
    public String s(String s) {
        System.out.println("实现类的s方法，有返回值，返回值是字符串类型（被代理实现类）");
        return "返回值";
    }
}
