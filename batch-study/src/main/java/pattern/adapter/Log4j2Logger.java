package pattern.adapter;


/**
 * 这个算是初次适配NewLogger
 *
 */
public class Log4j2Logger implements NewLogger{
    public void info(String msg) {
        System.out.println("[Log4j2 INFO] " + msg);
    }

    public void error(String msg) {
        System.out.println("[Log4j2 ERROR] " + msg);
    }
}
