package pattern.adapter;

/**
 * 这是通过继承方法，缺点是只能单继承
 *
 * 灵活零低 很少使用
 */
public class LegacyToNewAdapterByExtends extends LegacyLoggerImpl implements NewLogger{
    @Override
    public void info(String msg) {
        logMessage(msg);
    }

    @Override
    public void error(String msg) {
        error(msg);
        logMessage(msg);
    }
}
