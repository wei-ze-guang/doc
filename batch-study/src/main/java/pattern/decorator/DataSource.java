package pattern.decorator;

/**
 * 接口
 */
public interface DataSource {
    String write(String data);
    String read();
}

