package pattern.adapter;


/**
 * 注入方法是 对象适配器，把被适配当作心事配的一个成员变量
 *
 * 这种方法是对象适配器，关键点是组合+委托，灵活性高，最常用
 */
public class LegacyToNewAdapter implements NewLogger{

    /**
     *这个老接口是被适配，适配新接口NewLogger
     * 这是一种方法，就是把被适配的类当作适配类的一个成员变量，使用
     */

    private final LegacyLogger legacyLogger;

    public LegacyToNewAdapter(LegacyLogger legacyLogger) {
        this.legacyLogger = legacyLogger;
    }

    /**
     * 不一定的是
     * @param msg
     */
    @Override
    public void info(String msg) {
        // 把 info 统一映射为 log + debug
        legacyLogger.debug(msg);
        legacyLogger.logMessage(msg);
    }

    @Override
    public void error(String msg) {
        // 把 error 映射为 warn + log
        legacyLogger.warn(msg);
        legacyLogger.logMessage(msg);

    }
}
