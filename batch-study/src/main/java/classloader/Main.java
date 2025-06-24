package classloader;

public class Main {
    public static void main(String[] args) {

        String path = ClassLoader.getSystemClassLoader().getName();
        System.out.println(path);

        String parentPath = ClassLoader.getSystemClassLoader().getParent().getName();
        System.out.println(parentPath);

        System.out.println(ClassLoader.getSystemClassLoader().getParent().getParent().getName());
    }
}
