package classloader;

public class StringDemo {
    static String string = "1";

    public static void main(String[] args) {
        String s1 = new String("1");

        String s2 = new String("1");

        System.out.println(s1 == string); //false

        System.out.println(s1 == s2); //false

        System.out.println((""+"1") == "1");  //true
    }

}
