package pattern.decorator;

import java.util.Base64;

public class Base64Decorator extends  DataSourceDecorator{

    public Base64Decorator(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String write(String data) {
        String encoded = Base64.getEncoder().encodeToString(data.getBytes());
        return super.write(encoded);
    }
}
