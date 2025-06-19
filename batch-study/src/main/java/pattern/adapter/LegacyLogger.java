package pattern.adapter;

/**
 * 适配器模式的最老的接口
 */
public interface LegacyLogger {
    void logMessage(String msg);  // 普通日志
    void warn(String msg);        // 警告日志
    void debug(String msg);       // 调试日志
}

