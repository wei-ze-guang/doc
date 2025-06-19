## volatile关键字
 - 保证的是对这个变量的写操作的时候，写完立刻刷新回内存，他只保证这个,但是你写的时候,你什么时候能拿到CPU是线程自己的事情
 - 刷新回主存之后别的线程读不读，那是别的线程的事情，不要求你立刻读，保证你是你读取的时候读取到的是最新值
 - 它只是插入内存屏障，防止缓存不一致和指令重排，但不能阻止多个线程对同一变量的竞争修改（如 ++、count += 1 这种复合操作）  
 - 如果不使用这个关键字，线程还是一样会把线程内内容写回，但是一般是线程结束的时候写，不会马上写  
 - 换句话说：
  - volatile写操作：先把之前所有写操作都同步出去（刷新缓存），再写volatile变量。   
  - volatile读操作：读volatile变量时，强制从主存拉最新值，保证之后读
## 练习这个的时候需要注意使用IO，比如打印或者文件的时候可能会触发内存同步  

为什么打印语句会影响可见性？
1. 这是 JVM 的一个“副作用”机制。System.out.println() 做了很多底层的操作，例如:
- 涉及系统调用（I/O 操作）
- synchronized 内部锁的使用（打印流是线程安全的）
- 编译器/CPU 的 指令重排序会被内存屏障限制
2. 这些副作用有可能导致：
- 线程工作内存和主内存之间发生同步
- 导致其他线程写入的值被当前线程刷新并可见
## 如果一个变量没有使用volatile  
下面得案例，就算 main线程或者TwoThreadWord 线程对flag修改，但是OneThreadWord 永远在循环状态，  
无论是使用布尔值还是实例对象还是数组都不可见，下面案例只会输出 "数据已经改变" 

```java
public class TestVolatileWord {

    //使用基本类型
    private static  boolean flag = false;

    //使用数组等等
    private static   int[] arr = new int[]{1,2,3,4};
    
    //使用数组等等
    private static  final Obj obj = new Obj();

    public static void main(String[] args) {

        new Thread(new OneThreadWord()).start();
        new Thread(new TwoThreadWord()).start();

        try {
            Thread.sleep(10);
            arr[0] = 2;
            flag = true;
            obj.num = 2;
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    static class  OneThreadWord implements Runnable {

        @Override
        public void run() {
//            System.out.println("OneThreadWord");
            while (arr[0] == 1 && !flag && obj.num == 1 ) {
                //这里调用IO的话可能就会出现强制刷新缓存
//                System.out.println("flag一直是false");
            }
            System.out.println("arr[0]:"+arr[0]+",flag:"+flag+",obj.num:"+obj.num);
            System.out.println("现在是可见性的了");
        }
    }

    static  class TwoThreadWord implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);  //这里sleep是为了让OneThread确保进去while循环
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            arr[0] = 2;
            flag = true;
            obj.num = 2;
            System.out.println("数据已经改变");
        }
    }

    static class Obj {
        public int num = 1;
    }

}


```
1. 只修改flag为volatile
```java
package multithread;

/**
 * 用来测试volatile关键字
 */
public class TestVolatileWord {

    //修改为使用 volatile关键字修饰
    private static volatile   boolean flag = false;

    //FIXME 上面修改flg为volatile输出为  现在是可见性的了

    private static   int[] arr = new int[]{1,2,3,4};

    private static  final Obj obj = new Obj();

    public static void main(String[] args) {

        new Thread(new OneThreadWord()).start();
        new Thread(new TwoThreadWord()).start();

        try {
            Thread.sleep(10);
            arr[0] = 2;
            flag = true;
            obj.num = 2;
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    static class  OneThreadWord implements Runnable {

        @Override
        public void run() {
            while (arr[0] == 1 && !flag && obj.num == 1 ) {

            }
            System.out.println("arr[0]:"+arr[0]+",flag:"+flag+",obj.num:"+obj.num);
            System.out.println("现在是可见性的了");
        }
    }

    static  class TwoThreadWord implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);  //这里sleep是为了让OneThread确保进去while循环
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            arr[0] = 2;
//            flag = true;
//            obj.num = 2;
            System.out.println("已经设置flag为true");
        }
    }

    static class Obj {
        public int num = 1;
    }

}



```
 - 输出为 arr[0]:2,flag:true,obj.num:2  ->  现在是可见性的了 ->  已经设置flag为true

再来一个经典案例，就是连续操作,不使用volatile
 - 下面结果就是OneThread永远循环 ，OneThread按理说a = 5  的时候肯定会退出，但是他没退出  
 - 下面如果写为 private static volatile int a = 1; OneThread会退出，就说明是可见性的，最后他读取到了a = 5
```java
public class TestVolatileWord {

    private static int a = 1;

    public static void main(String[] args) {

        new Thread(new OneThreadWord()).start();
        new Thread(new TwoThreadWord()).start();

    }

    static class  OneThreadWord implements Runnable {

        @Override
        public void run() {

            while (a == 1){
//                System.out.println(a);   //FIXME 注意这里不要触发IO不然效果完全不同，一旦触发IO 他自动回去同步数据，练习结果就达不到了
            }


            while (a == 2){
//                System.out.println(a);
            }

            while (a == 3){
//                System.out.println(a);
            }

            while (a == 4){
//                System.out.println(a);
            }

        }
    }

    static  class TwoThreadWord implements Runnable {

        @Override
        public void run() {
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 2;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 3;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 4;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            a = 5;   //注意这里最后操作结果是5

            System.out.println("操作线程结束");
        }
    }

    static class Obj {
        public volatile int num = 1;
    }

}
```

基于上面的循环操作，严重一个线程多次操作一个volatile修饰的宾变量，没操作一次都会触发一次
 - 不使用volatile 下面的oneThread线程会死循环
```java
public class TestVolatileWord {
    
    private static int a = 1;

    public static void main(String[] args) {

        new Thread(new OneThreadWord()).start();
        new Thread(new TwoThreadWord()).start();

    }

    static class OneThreadWord implements Runnable {
        @Override
        public void run() {
            int count = 0;
            while (a == 1) {
                count++;
            }
            System.out.println("a != 1，退出了 while1，循环了 " + count + " 次");

            count = 0;
            while (a == 2) {
                count++;
            }
            System.out.println("a != 2，退出了 while2，循环了 " + count + " 次");

            count = 0;
            while (a == 3) {
                count++;
            }
            System.out.println("a != 3，退出了 while3，循环了 " + count + " 次");

            count = 0;
            while (a == 4) {
                count++;
            }
            System.out.println("a != 4，退出了 while4，循环了 " + count + " 次");
        }
    }


    static  class TwoThreadWord implements Runnable {

        @Override
        public void run() {
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 2;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 3;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 4;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            a = 5;   //注意这里最后操作结果是5

            System.out.println("操作线程结束");
        }
    }

}

``` 
 - 如果上面的a使用了 volatile关键字修饰 
输出为 
 - "a != 1，退出了 while1，循环了 -1293649071 次  
   a != 2，退出了 while2，循环了 -1476317786 次  
   a != 3，退出了 while3，循环了 -1601420900 次  
   操作线程结束  
   a != 4，退出了 while4，循环了 -1369753089 次
   "  
**注意**  
 - 这个案例的测试至少说明，如果一个被volatile修饰的变量，在任何地方被子**修改**，他都会刷新内存，就算一个线程内多次修改，就会多次刷新
```java
public class TestVolatileWord {

    //  加了volatile
    private static volatile int a = 1;

    public static void main(String[] args) {

        new Thread(new OneThreadWord()).start();
        new Thread(new TwoThreadWord()).start();

    }

    static class OneThreadWord implements Runnable {
        @Override
        public void run() {
            int count = 0;
            while (a == 1) {
                count++;
            }
            System.out.println("a != 1，退出了 while1，循环了 " + count + " 次");

            count = 0;
            while (a == 2) {
                count++;
            }
            System.out.println("a != 2，退出了 while2，循环了 " + count + " 次");

            count = 0;
            while (a == 3) {
                count++;
            }
            System.out.println("a != 3，退出了 while3，循环了 " + count + " 次");

            count = 0;
            while (a == 4) {
                count++;
            }
            System.out.println("a != 4，退出了 while4，循环了 " + count + " 次");
        }
    }


    static  class TwoThreadWord implements Runnable {

        @Override
        public void run() {
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 2;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 3;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 4;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            a = 5;   //注意这里最后操作结果是5

            System.out.println("操作线程结束");
        }
    }

}
```

## volatile不保证原子性  需要使用CAS实现原子性
 - 防止缓存不一致和指令重排，但不能阻止多个线程对同一变量的竞争修改（如 ++、count += 1 这种复合操作）。  
---  

总结：
- volatile 关键字 是建立在真实硬件缓存一致性协议基础上的。
- 它通过内存屏障和缓存一致性协议确保多线程环境下的可见性和顺序性。
- 如果没有底层硬件的缓存一致性机制，volatile 的语义是无法实现的。  
线程和缓存的关系：
- 线程不是物理上的“缓存个数”的限制因素。
- 线程是操作系统调度的“逻辑单位”，一个核心可以快速切换多个线程（通过时间片轮转）
- 一个核心的缓存内容随线程切换而变化：
- 当线程A运行时，核心缓存中可能存放线程A的数据和指令。
- 当切换到线程B时，缓存会逐步被线程B的数据覆盖或刷新（称为“缓存抖动”）
         
         