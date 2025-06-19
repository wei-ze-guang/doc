package pattern.decorator;

public class CompressDecorator extends DataSourceDecorator{

    public CompressDecorator(DataSource source) {
        super(source);
    }

    @Override
    public String write(String data) {
        String compressed = "【压缩后】" + data;
        return super.write(compressed);
    }
}
