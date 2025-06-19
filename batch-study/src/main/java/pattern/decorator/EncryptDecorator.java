package pattern.decorator;

public class EncryptDecorator extends DataSourceDecorator{
    public EncryptDecorator(DataSource source) {
        super(source);
    }

    @Override
    public String write(String data) {
        String encrypted = caesarEncrypt(data, 3);
        return super.write(encrypted);
    }

    private String caesarEncrypt(String data, int shift) {
        StringBuilder sb = new StringBuilder();
        for (char c : data.toCharArray()) {
            sb.append((char)(c + shift));
        }
        return sb.toString();
    }

}
