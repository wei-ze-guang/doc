package pattern.adapter;

public class AdapterPatternDemo {
    public static void main(String[] args) {
        System.out.println("== 使用 LegacyLogger 的适配器 ==");

        NewLogger legacyAdapter = new LegacyToNewAdapter(new LegacyLoggerImpl());
        legacyAdapter.info("系统启动成功");

        legacyAdapter.error("无法连接数据库");

        System.out.println("\n== 使用原生 SLF4J ==");
        NewLogger slf4jLogger = new SLF4JLogger();
        slf4jLogger.info("处理请求");
        slf4jLogger.error("内部错误");


        System.out.println("\n== 使用 SLF4J 到 Log4j2 的适配器 ==");
        NewLogger slf4jToLog4j2 = new SLF4JToLog4j2Adapter(new Log4j2Logger());
        slf4jToLog4j2.info("已完成任务");
        slf4jToLog4j2.error("超时异常");

    }
}
