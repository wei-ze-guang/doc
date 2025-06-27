package thread.interrupt;


/**
 * isInterrupted(）方法，他是对象的方法，这个方法唯一作用就是查询这个对象线程的interrupted的值，只查询，不做任何操作
 *
 *    public boolean isInterrupted() {
 *         return interrupted;
 *     }
 *
 */
public class isInterruptedDemo {
    /**
     * Thread的变量interrupted，这个变量是由JVM自己操作的，用户无法自己操作  ,当不同的方法调用的时候这个变量会发生变化
     * @param args
     */
    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch(InterruptedException e) {

                    boolean s = Thread.currentThread().isInterrupted();
                    System.out.println("isInterrupted: " + s);  //被打断后JVM会清除这里的标志
                     //说明我改推出了
                    Thread.currentThread().interrupt(); // 自己打断自己，重置表质量
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        System.out.println("isInterrupted: " + ex);
                    }
                    if(s){
                        /**
                         * 重置一下信号量，但是啥也没做
                         */
                        Thread.interrupted();
                    }
                }
                System.out.println("变量 interrupted的值是:"+Thread.currentThread().isInterrupted());
                if(!Thread.currentThread().isInterrupted()){
                    System.out.println("这个变量被改变了");
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    thread.interrupt();
                } catch (InterruptedException e) {

                }

            }
        });

        thread.start();
        thread2.start();
    }
}
