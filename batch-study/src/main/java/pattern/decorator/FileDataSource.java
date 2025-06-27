package pattern.decorator;

public class FileDataSource implements DataSource {

    private String data;

    @Override
    public String write(String data) {
        this.data = data;
        return "原始写入：" + data;
    }

    @Override
    public String read() {
        return data;
    }
}
