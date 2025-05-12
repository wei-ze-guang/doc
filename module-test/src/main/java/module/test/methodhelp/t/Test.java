package module.test.methodhelp.t;

import me.doudan.doc.annotation.ControllerLayer;
import me.doudan.doc.annotation.DataLayer;
import me.doudan.doc.annotation.ManagementLayer;
import me.doudan.doc.annotation.ServiceLayer;

import java.util.List;

public class Test {

    @ControllerLayer(value = "这是控制层方法",module = "登录")
    public void test() {

    }

    @ManagementLayer(value = "这是服务层管理方法",module = "登录")
    public void test11() {

    }
    @DataLayer(value = "这是数据层方法",module = "登录")
    public void test22() {

    }
    @ServiceLayer(value = "这是服务层方法",module = "注册")
    public List<Integer> test444() {
        return null;

    }
    @ServiceLayer(value = "这是服务层方法",module = "注册")
    public String test4(String string,Integer integer) {
            return null;
    }
    @ServiceLayer(value = "这是服务层方法",module = "注册")
    public void test44() {

    }
    @ServiceLayer(value = "这是服务层方法",module = "注册")
    public void testp4fsdfffffffffffffsdfffffffffffffffffffffffffffff() {

    }
    @ServiceLayer(value = "这是服务层方法",module = "注册")
    public void test4p4() {

    }
}
