package pattern.adapter;


/**
 * 这种类型的接口适配器
 * 关键是抽象类空实现 不使用组合，灵活性高
 * 常用于监听器
 * 为什么只重写一个接口了，这是因为抽象实现类已经重写了接口，我们要的接口并不是全部的接口方法
 * 我们只需要重写我们的就扣方法就可以了
 */
public class PartialLegacyAdapter extends AbstractLoggerAdapter{
    @Override
    public void logMessage(String msg) {
        System.out.println("[Adapted LOG] " + msg);
    }

    // 不需要适配 warn/debug 的情况下，不实现也没关系
}
