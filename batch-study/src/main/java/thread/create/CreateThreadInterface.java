package thread.create;

/**
 * 这个接口是每一种策略需要实现的功能，策略模式要求的是实现方式不同，但是结果要一样
 *
 * 下面的Thread的原始  public class Thread implements Runnable 他实现了Runnable接口，是一个类
 * 下面是Thread 实现Runnable接口的具体
 * @Override
 *     public void run() {
 *         if (target != null) {
 *             target.run();
 *         }
 *     }
 *     也就是说直接new 一个 Thread类就可以运行，但是target为null  什么都不执行，我们通过继承Thread重写他的方法就可以了
 */
public interface CreateThreadInterface {

    Thread createThread();
}
