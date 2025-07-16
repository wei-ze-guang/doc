package io.input;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Java 的字符串JDK17  是     @Stable
 *     private final byte[] value;
 * 以前是 private  final char[] value是
 */
public class Unicode {
    @Test
    public void test() {
        char a = '中';

        byte[] bytes = new byte[]{11,22,33,55,66,55,44,66,5,44,55,44,55,0,11,2,-4,-5,2,2,2,2,2,2,2};


        String str = new String(bytes,StandardCharsets.UTF_16LE);

        String str1 = new String(bytes,StandardCharsets.UTF_8);

        System.out.println(str);//

        System.out.println(str1);


        byte[] bytes1 = str.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes1) {
            System.out.print((char) b);
        }
        System.out.println("=========");
        byte[] bytes2 = str.getBytes(StandardCharsets.UTF_16);
        for (byte b : bytes2) {
            String binaryString = Integer.toBinaryString(b);
            System.out.println(binaryString);
            System.out.print((char) b);
        }

        Map<String,String> map = new HashMap<String,String>(5);
        Map<String,String> map1 = new LinkedHashMap<String,String>();
        Map<String,String> map2 = new TreeMap<String,String>();
        Map<String,String> map3 = new Hashtable<>();
        Map<String,String> map4 = new ConcurrentHashMap<String,String>();

    }
}
