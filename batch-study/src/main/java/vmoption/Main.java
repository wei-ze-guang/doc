package vmoption;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试OOP
 */
public class Main {
    public static void main(String[] args) {
        int times = 4000000;
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < times; i++) {
            list.add(String.valueOf(i));
        }
        System.out.println(list);
    }
}
