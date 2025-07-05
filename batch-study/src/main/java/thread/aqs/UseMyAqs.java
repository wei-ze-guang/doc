package thread.aqs;


/**
 * 这个是独享模式下的
 */
public class UseMyAqs {

    // 内部成员Sync，使用AQS管理锁状态
    private final Sync sync = new Sync();

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

    private static class Sync extends MyAqs{

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
