package pattern.adapter;

public class SLF4JToLog4j2Adapter implements NewLogger{
    private final Log4j2Logger log4j2Logger;

    public SLF4JToLog4j2Adapter(Log4j2Logger log4j2Logger) {
        this.log4j2Logger = log4j2Logger;
    }

    public void info(String msg) {
        log4j2Logger.info("[Adapted from SLF4J] " + msg);
    }

    public void error(String msg) {
        log4j2Logger.error("[Adapted from SLF4J] " + msg);
    }
}
