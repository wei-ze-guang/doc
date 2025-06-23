package pattern.proxy;


/**
 * 代理设计模式
 */
public class Proxy {

    public static void main(String[] args) {

        AccessControl adminAccess = new AccessControlProxy("Admin");
        AccessControl guestAccess = new AccessControlProxy("Guest");

        adminAccess.enter("Alice");  // Admin access granted
        guestAccess.enter("Bob");    // Guest access denied

        /**🎯 一、责任链模式是什么？
         责任链模式的核心思想是：

         将多个处理对象连接成一条链条，每个对象在链条中处理自己的任务或请求，然后将请求传递给下一个对象，直到请求得到处理。

         简单来说，责任链模式将一个请求的处理过程分发到多个处理者（对象）上，每个对象处理自己负责的部分，未处理的部分会传递给下一个对象，直到整个请求完成。
         * 通过继承
         */
        AccessControlProxyByExtends accessControlProxyByExtends = new AccessControlProxyByExtends();
        accessControlProxyByExtends.enter("Alice");

    }
}

/**
 * 1️⃣ 抽象接口：Subject（目标对象）
 */

 interface AccessControl {
    void enter(String personName);
}

/**
 * 2️⃣ 真实对象：RealAccessControl（门禁系统的核心功能）
 */
 class RealAccessControl implements AccessControl {
    @Override
    public void enter(String personName) {
        System.out.println(personName + " has access to the building.");
    }
}
/**
 * 3️⃣ 代理类：AccessControlProxy（控制访问）
 */
class AccessControlProxy implements AccessControl {

    private RealAccessControl realAccessControl;

    private String role;

    public AccessControlProxy(String role) {
        this.role = role;
    }

    @Override
    public void enter(String personName) {
        if (role.equals("Admin")) {
            if (realAccessControl == null) {
                realAccessControl = new RealAccessControl();
            }
            realAccessControl.enter(personName);
        } else {
            System.out.println(personName + " does not have access rights.");
        }
    }
}
/**
 * 通过继承也可以
 * 代理类通过继承 RealAccessControl，可以直接访问目标类的功能，并且可以在访问时加入额外的逻辑。
 */
 class AccessControlProxyByExtends extends RealAccessControl {


    @Override
    public void enter(String personName) {
        // 增加权限检查逻辑
        if (true) {
            super.enter(personName); // 调用真实对象的方法
        } else {
            System.out.println(personName + " does not have access rights.");
        }
    }
}







