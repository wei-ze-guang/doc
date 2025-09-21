package thread.create;

import pattern.decorator.DataSource;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalDemo {

    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();  //全局唯一地址

    public static void main(String[] args) {

        threadLocal.set(new String("hello"));


        String s = threadLocal.get();
    }

}
