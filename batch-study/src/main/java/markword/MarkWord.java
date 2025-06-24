package markword;

import org.openjdk.jol.info.ClassLayout;

public class MarkWord {

    static class MyObject {
        int x = 1;
        boolean flag = true;
    }

    public static void main(String[] args) {
        MyObject obj = new MyObject();
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());
    }
}
