package pattern.adapter;


/**
 * 这个是接口适配的储抽象实现类，优点在于如果别的类继承这个类的话，重写他们自己的接口，不需要重写所有
 * 的接口，代码简洁
 */
public abstract class AbstractLoggerAdapter implements LegacyLogger {
    @Override
    public void logMessage(String msg) {}

    @Override
    public void warn(String msg) {}

    @Override
    public void debug(String msg) {}
}
