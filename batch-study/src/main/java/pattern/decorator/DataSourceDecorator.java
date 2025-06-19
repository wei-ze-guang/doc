package pattern.decorator;

/**
 * 3️⃣ 抽象装饰器类 DataSourceDecorator
 */
public class DataSourceDecorator implements DataSource {
    protected DataSource wrappee;
    public DataSourceDecorator(DataSource dataSource) {
        this.wrappee = dataSource;
    }

    @Override
    public String write(String data) {
        return wrappee.write(data);
    }

    @Override
    public String read() {
        return wrappee.read();
    }
}
