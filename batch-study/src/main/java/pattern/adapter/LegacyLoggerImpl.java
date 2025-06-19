package pattern.adapter;

/**
 * 新的接口的实现
 */
public class LegacyLoggerImpl implements LegacyLogger {
    @Override
    public void logMessage(String msg) {
        System.out.println("Legacy_Log: " + msg);
    }

    @Override
    public void warn(String msg) {
        System.out.println("Legacy_Warn: " + msg);
    }

    @Override
    public void debug(String msg) {
        System.out.println("Legacy_Debug: " + msg);
    }
}
