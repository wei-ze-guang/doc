package thread.aqs;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 熟悉一下  tryAcquire tryRelease 独占锁
 */
public class MutexLock {

    // 内部成员Sync，使用AQS管理锁状态
    private final Sync sync = new Sync();

    public void lock() {
        sync.lock();  // 调用内部的lock方法，获取锁
    }

    public void unlock() {
        sync.unlock();  // 调用内部的unlock方法，释放锁
    }

    private static class Sync extends AbstractQueuedSynchronizer{

        /**
         * 这个接口只要你返回true他就会对这个接口放行
         * 怎么返回是你的事情
         */
        @Override
        protected boolean tryAcquire(int arg) {
//            if(compareAndSetState(0,1)){
//                setExclusiveOwnerThread(Thread.currentThread());
//                return true;
//            }
//            return false;
            return true;
        }
        @Override
        protected boolean tryRelease(int arg) {
            if(getState() == 0){
                /**
                 * 检查一下，万一他已经是0了，说明没有人拥有锁，再释放的话就问题了
                 */
                return false;
//                throw  new IllegalStateException("The lock has been released");
            }
                setExclusiveOwnerThread(null);  //清除独占线程
                setState(0);
                return false;
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
