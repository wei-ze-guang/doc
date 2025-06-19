package pattern.adapter;


/**
 * 这个算是初次适配NewLogger
 *
 */
public class SLF4JLogger implements NewLogger {
    public void info(String msg) {
        System.out.println("[SLF4J INFO] " + msg);
    }

    public void error(String msg) {
        System.out.println("[SLF4J ERROR] " + msg);
    }
}
