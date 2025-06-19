package pattern.decorator;

public class Main {
    public static void main(String[] args) {
        DataSource source = new FileDataSource();

        System.out.println(source.write("原始的没装饰过测"));

        //压缩一下
        DataSource compressDecorator = new CompressDecorator(source);
        System.out.println(compressDecorator.write("压缩后的数据"));

        // 加密一下
        DataSource encryptDecorator = new EncryptDecorator(compressDecorator);
        System.out.println(encryptDecorator.write("加密后的数据"));

        //Base 64一下
        DataSource base64Decorator = new Base64Decorator(encryptDecorator);
        System.out.println(base64Decorator.write("对加密后的的数据进行Base64"));

    }
}
