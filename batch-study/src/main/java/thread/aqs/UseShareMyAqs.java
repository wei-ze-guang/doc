package thread.aqs;

/**
 * 这个是共享模式下的
 */
public class UseShareMyAqs {

    public UseShareMyAqs(int states) {
        this.sync = new Sync(states);
    }

    // 内部成员Sync，使用AQS管理锁状态
    private final Sync sync;

    public void lock() {
        sync.lock();  // 调用内部的lock方法，获取锁
    }

    public void unlock() {
        sync.unlock();  // 调用内部的unlock方法，释放锁
    }

    public void printQueue() {
        sync.printQueue();
    }

    public void printAcquireTimes(){
        sync.printAcquireTimes();
    }

    /**
     * 下面是共享模式
     * @param a 获取的资源数量
     */
    public void lockShare(int a){
        sync.acquireShared(a);
    }

    /**
     * @param a 释放的资源个数
     */
    public void unLockShare(int a){
        sync.acquireShared(a);
    }

    /**
     * 打印共享模式下每个线程使用了多少次获取资源
     */
    public void printAcquireShareTimes(){
        sync.printAcquireShareTimes();
    }

    private static class Sync extends MyAqs{

        public Sync(int states) {
            setState(states);
        }

        /**
         * 这个接口只要你返回true他就会对这个接口放行
         * 怎么返回是你的事情
         */
        @Override
        protected boolean tryAcquire(int arg) {
            if(compareAndSetState(0,1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }
        @Override
        protected boolean tryRelease(int arg) {
            if(getState() == 0){
                throw  new IllegalStateException("The lock has been released");
            }
            setExclusiveOwnerThread(null);  //清除独占线程
            setState(0);
            return true;
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            for (;;) {
                int available = getState();
                int remaining = available - acquires;
                if (remaining < 0) return -1;
                if (compareAndSetState( available, remaining)) {
                    return 1;
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(int releases) {
            for (;;) {
                int current = getState();
                int next = current + releases;
                if (compareAndSetState( current, next)) {
                    return true;
                }
            }
        }


        // 共享模式
        public void lockShared(int acquires) {
            acquireShared(acquires);  // 通过AQS的acquire方法来获取锁
        }

        // 共享模式
        public void unlockShared(int releases) {
            releaseShared(releases);  // 通过AQS的release方法来释放锁
        }

        // 阻塞当前线程，直到锁被获取
        public void lock() {
            acquire(1);  // 通过AQS的acquire方法来获取锁
        }

        // 释放锁
        public void unlock() {
            release(1);  // 通过AQS的release方法来释放锁
        }

    }
}