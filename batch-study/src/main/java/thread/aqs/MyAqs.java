package thread.aqs;/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */


import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;


public abstract class MyAqs
        extends AbstractOwnableSynchronizer
        implements java.io.Serializable {

    private static final long serialVersionUID = 7373984972572414691L;

    /**
     * Creates a new {@code AbstractQueuedSynchronizer} instance
     * with initial synchronization state of zero.
     */
    protected MyAqs() { }

    /*
     * Overview.
     *
     * The wait queue is a variant of a "CLH" (Craig, Landin, and
     * Hagersten) lock queue. CLH locks are normally used for
     * spinlocks.  We instead use them for blocking synchronizers by
     * including explicit ("prev" and "next") links plus a "status"
     * field that allow nodes to signal successors when releasing
     * locks, and handle cancellation due to interrupts and timeouts.
     * The status field includes bits that track whether a thread
     * needs a signal (using LockSupport.unpark). Despite these
     * additions, we maintain most CLH locality properties.
     *
     * To enqueue into a CLH lock, you atomically splice it in as new
     * tail. To dequeue, you set the head field, so the next eligible
     * waiter becomes first.
     *
     *  +------+  prev +-------+       +------+
     *  | head | <---- | first | <---- | tail |
     *  +------+       +-------+       +------+
     *
     * Insertion into a CLH queue requires only a single atomic
     * operation on "tail", so there is a simple point of demarcation
     * from unqueued to queued. The "next" link of the predecessor is
     * set by the enqueuing thread after successful CAS. Even though
     * non-atomic, this suffices to ensure that any blocked thread is
     * signalled by a predecessor when eligible (although in the case
     * of cancellation, possibly with the assistance of a signal in
     * method cleanQueue). Signalling is based in part on a
     * Dekker-like scheme in which the to-be waiting thread indicates
     * WAITING status, then retries acquiring, and then rechecks
     * status before blocking. The signaller atomically clears WAITING
     * status when unparking.
     *
     * Dequeuing on acquire involves detaching (nulling) a node's
     * "prev" node and then updating the "head". Other threads check
     * if a node is or was dequeued by checking "prev" rather than
     * head. We enforce the nulling then setting order by spin-waiting
     * if necessary. Because of this, the lock algorithm is not itself
     * strictly "lock-free" because an acquiring thread may need to
     * wait for a previous acquire to make progress. When used with
     * exclusive locks, such progress is required anyway. However
     * Shared mode may (uncommonly) require a spin-wait before
     * setting head field to ensure proper propagation. (Historical
     * note: This allows some simplifications and efficiencies
     * compared to previous versions of this class.)
     *
     * A node's predecessor can change due to cancellation while it is
     * waiting, until the node is first in queue, at which point it
     * cannot change. The acquire methods cope with this by rechecking
     * "prev" before waiting. The prev and next fields are modified
     * only via CAS by cancelled nodes in method cleanQueue. The
     * unsplice strategy is reminiscent of Michael-Scott queues in
     * that after a successful CAS to prev field, other threads help
     * fix next fields.  Because cancellation often occurs in bunches
     * that complicate decisions about necessary signals, each call to
     * cleanQueue traverses the queue until a clean sweep. Nodes that
     * become relinked as first are unconditionally unparked
     * (sometimes unnecessarily, but those cases are not worth
     * avoiding).
     *
     * A thread may try to acquire if it is first (frontmost) in the
     * queue, and sometimes before.  Being first does not guarantee
     * success; it only gives the right to contend. We balance
     * throughput, overhead, and fairness by allowing incoming threads
     * to "barge" and acquire the synchronizer while in the process of
     * enqueuing, in which case an awakened first thread may need to
     * rewait.  To counteract possible repeated unlucky rewaits, we
     * exponentially increase retries (up to 256) to acquire each time
     * a thread is unparked. Except in this case, AQS locks do not
     * spin; they instead interleave attempts to acquire with
     * bookkeeping steps. (Users who want spinlocks can use
     * tryAcquire.)
     *
     * To improve garbage collectibility, fields of nodes not yet on
     * list are null. (It is not rare to create and then throw away a
     * node without using it.) Fields of nodes coming off the list are
     * nulled out as soon as possible. This accentuates the challenge
     * of externally determining the first waiting thread (as in
     * method getFirstQueuedThread). This sometimes requires the
     * fallback of traversing backwards from the atomically updated
     * "tail" when fields appear null. (This is never needed in the
     * process of signalling though.)
     *
     * CLH queues need a dummy header node to get started. But
     * we don't create them on construction, because it would be wasted
     * effort if there is never contention. Instead, the node
     * is constructed and head and tail pointers are set upon first
     * contention.
     *
     * Shared mode operations differ from Exclusive in that an acquire
     * signals the next waiter to try to acquire if it is also
     * Shared. The tryAcquireShared API allows users to indicate the
     * degree of propagation, but in most applications, it is more
     * efficient to ignore this, allowing the successor to try
     * acquiring in any case.
     *
     * Threads waiting on Conditions use nodes with an additional
     * link to maintain the (FIFO) list of conditions. Conditions only
     * need to link nodes in simple (non-concurrent) linked queues
     * because they are only accessed when exclusively held.  Upon
     * await, a node is inserted into a condition queue.  Upon signal,
     * the node is enqueued on the main queue.  A special status field
     * value is used to track and atomically trigger this.
     *
     * Accesses to fields head, tail, and state use full Volatile
     * mode, along with CAS. Node fields status, prev and next also do
     * so while threads may be signallable, but sometimes use weaker
     * modes otherwise. Accesses to field "waiter" (the thread to be
     * signalled) are always sandwiched between other atomic accesses
     * so are used in Plain mode. We use jdk.internal Unsafe versions
     * of atomic access methods rather than VarHandles to avoid
     * potential VM bootstrap issues.
     *
     * Most of the above is performed by primary internal method
     * acquire, that is invoked in some way by all exported acquire
     * methods.  (It is usually easy for compilers to optimize
     * call-site specializations when heavily used.)
     *
     * There are several arbitrary decisions about when and how to
     * check interrupts in both acquire and await before and/or after
     * blocking. The decisions are less arbitrary in implementation
     * updates because some users appear to rely on original behaviors
     * in ways that are racy and so (rarely) wrong in general but hard
     * to justify changing.
     *
     * Thanks go to Dave Dice, Mark Moir, Victor Luchangco, Bill
     * Scherer and Michael Scott, along with members of JSR-166
     * expert group, for helpful ideas, discussions, and critiques
     * on the design of this class.
     */

    // Node status bits, also used as argument and return values
    static final int WAITING   = 1;          // must be 1
    static final int CANCELLED = 0x80000000; // must be negative
    static final int COND      = 2;          // in a condition wait

    /** CLH Nodes */
    abstract static class Node {
        volatile Node prev;
        volatile Node next;
        Thread waiter;
        volatile int status;

        private static final VarHandle PREV;
        private static final VarHandle NEXT;
        private static final VarHandle STATUS;

        static {
            try {
                MethodHandles.Lookup l = MethodHandles.lookup();
                PREV = l.findVarHandle(Node.class, "prev", Node.class);
                NEXT = l.findVarHandle(Node.class, "next", Node.class);
                STATUS = l.findVarHandle(Node.class, "status", int.class);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }

        final boolean casPrev(Node c, Node v) {
            return PREV.weakCompareAndSet(this, c, v);
        }

        final boolean casNext(Node c, Node v) {
            return NEXT.weakCompareAndSet(this, c, v);
        }

        final int getAndUnsetStatus(int v) {
            return (int) STATUS.getAndBitwiseAnd(this, ~v);
        }

        final void setPrevRelaxed(Node p) {
            PREV.set(this, p); // 或 PREV.setOpaque(this, p);
        }

        final void setStatusRelaxed(int s) {
            STATUS.set(this, s); // 或 STATUS.setOpaque(this, s);
        }

        final void clearStatus() {
            STATUS.setOpaque(this, 0);
        }
    }

    // Concrete classes tagged by type
    static final class ExclusiveNode extends Node { }
    static final class SharedNode extends Node { }

    static final class ConditionNode extends Node
            implements ForkJoinPool.ManagedBlocker {
        ConditionNode nextWaiter;            // link to next waiting node

        /**
         * Allows Conditions to be used in ForkJoinPools without
         * risking fixed pool exhaustion. This is usable only for
         * untimed Condition waits, not timed versions.
         */
        public final boolean isReleasable() {
            return status <= 1 || Thread.currentThread().isInterrupted();
        }

        public final boolean block() {
            while (!isReleasable()) LockSupport.park();
            return true;
        }
    }

    /**
     * 打印
     */
    public void printQueue() {
        Node p = head;
        System.out.println("AQS 等待队列：");
        while (p != null) {
            System.out.println("  Node: " +
                    (p.waiter == null ? "null" : p.waiter.getName()) +
                    "  status: " + p.status);
            p = p.next;
        }
    }

    /**
     * Head of the wait queue, lazily initialized.
     */
    private transient volatile Node head;

    /**
     * Tail of the wait queue. After initialization, modified only via casTail.
     */
    private transient volatile Node tail;

    /**
     * The synchronization state.
     */
    private volatile int state;

    // ✅ 等价于 U.getIntVolatile(this, STATE)
    protected final int getState() {
        return (int) STATE.getVolatile(this);
    }

    // ✅ 等价于 U.putIntVolatile(this, STATE, newState)
    protected final void setState(int newState) {
        STATE.setVolatile(this, newState);
    }

    /**
     * Atomically sets synchronization state to the given updated
     * value if the current state value equals the expected value.
     * This operation has memory semantics of a {@code volatile} read
     * and write.
     *
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful. False return indicates that the actual
     *         value was not equal to the expected value.
     */
    // ✅ 等价于 U.compareAndSetInt(this, STATE, expect, update)
    protected final boolean compareAndSetState(int expect, int update) {
        return STATE.compareAndSet(this, expect, update);
    }

    /** 替代 compareAndSetReference(this, TAIL, c, v) */
    private boolean casTail(Node c, Node v) {
        return TAIL.compareAndSet(this, c, v);

    }
    /** 替代 compareAndSetReference(this, HEAD, null, h) */
    private void tryInitializeHead() {
        Node h = new ExclusiveNode();
        if (HEAD.compareAndSet(this, null, h))
            tail = h;  // 如果你希望这个也变成原子写，可以加 TAIL.setVolatile(this, h)
    }

    /**
     * Enqueues the node unless null. (Currently used only for
     * ConditionNodes; other cases are interleaved with acquires.)
     */
    final void enqueue(Node node) {
        if (node != null) {
            for (;;) {
                Node t = tail;
                node.setPrevRelaxed(t);        // avoid unnecessary fence
                if (t == null)                 // initialize
                    tryInitializeHead();
                else if (casTail(t, node)) {
                    t.next = node;
                    if (t.status < 0)          // wake up to clean link
                        LockSupport.unpark(node.waiter);
                    break;
                }
            }
        }
    }

    /** Returns true if node is found in traversal from tail */
    final boolean isEnqueued(Node node) {
        for (Node t = tail; t != null; t = t.prev)
            if (t == node)
                return true;
        return false;
    }

    /**
     * Wakes up the successor of given node, if one exists, and unsets its
     * WAITING status to avoid park race. This may fail to wake up an
     * eligible thread when one or more have been cancelled, but
     * cancelAcquire ensures liveness.
     */
    /**
     * 如果当前节点后面有等待的节点（successor），就尝试唤醒它。
     * 同时把它的等待状态（WAITING）去掉，防止出现“唤醒和挂起打架”的情况（也就是竞态问题）。
     * 不过，如果后面的线程已经取消了（cancel），可能唤醒失败，但别担心，cancelAcquire() 方法会兜底，确保不会卡死，系统还是能继续运行的。
     * @param h
     */
    /**
     * 🧠 更通俗地说：
     * 就像排队：
     * 如果你走了，应该拍拍后面那个人说“该你啦！”
     * 如果他已经放弃了（比如取消、崩了），你拍不醒他也没关系
     * 系统会自动找下一个能动的人继续队伍，不至于整个队列停摆
     * @param h
     */
    private static void signalNext(Node h) {
        Node s;
        if (h != null && (s = h.next) != null && s.status != 0) {
            s.getAndUnsetStatus(WAITING);
            log("准备把线程"+s.waiter.getName()+"唤醒");
            System.out.println(s.waiter.getName()+"准备被唤醒");
            LockSupport.unpark(s.waiter);
        }
    }

    /** Wakes up the given node if in shared mode */
    /**
     * 如果头节点后面是个共享锁线程，并且还在等待中，就把它的等待状态清除，然后唤醒它。
     */
    private static void signalNextIfShared(Node h) {
        Node s;
        if (h != null && (s = h.next) != null &&
                (s instanceof SharedNode) && s.status != 0) {
            s.getAndUnsetStatus(WAITING);
            log("现在在共享实模式下，准备唤醒线程"+s.waiter.getName());
            LockSupport.unpark(s.waiter);
        }
    }

    private static void log(String msg) {
        System.out.println(Thread.currentThread().getName() + " - " + msg);
    }


    /**
     * Main acquire method, invoked by all exported acquire methods.
     *
     * @param node null unless a reacquiring Condition
     * @param arg the acquire argument
     * @param shared true if shared mode else exclusive
     * @param interruptible if abort and return negative on interrupt
     * @param timed if true use timed waits
     * @param time if timed, the System.nanoTime value to timeout
     * @return positive if acquired, 0 if timed out, negative if interrupted
     */


    final int acquire(Node node, int arg, boolean shared,
                      boolean interruptible, boolean timed, long time) {
        Thread current = Thread.currentThread();
        byte spins = 0, postSpins = 0;   // retries upon unpark of first thread
        boolean interrupted = false, first = false;
        Node pred = null;                // predecessor of node when enqueued

        /*
         * Repeatedly:
         *  Check if node now first
         *    if so, ensure head stable, else ensure valid predecessor
         *  if node is first or not yet enqueued, try acquiring
         *  else if node not yet created, create it
         *  else if not yet enqueued, try once to enqueue
         *  else if woken from park, retry (up to postSpins times)
         *  else if WAITING status not set, set and retry
         *  else park and clear WAITING status, and check cancellation
         */

        for (;;) {
            /**
             * 我不是第一个等待线程？
             * ├── 我的前驱是不是 head？
             * │   ├── 是：我是 next，你可以尝试竞争锁了
             * │   └── 否：
             * │       ├── 前驱取消了？→ 清理队列，继续等
             * │       └── 前驱还没连好？→ 自旋等等，别抢
             */
            if (!first && (pred = (node == null) ? null : node.prev) != null &&
                    !(first = (head == pred))) {
                if (pred.status < 0) {
                    cleanQueue();           // predecessor cancelled
                    continue;
                } else if (pred.prev == null) {
                    log("前驱是null");
                    Thread.onSpinWait();    // ensure serialization
                    continue;
                }
            }
            if (first || pred == null) {
                /**
                 * ✅ 含义：
                 * first：当前线程是不是队首（排在 head 后面）
                 * pred == null：可能是还没入队
                 * ✅ 如果你是队首，或者还没排队，那就可以试一试抢锁。
                 */
                log("我是在head后面尝试获取锁");
                boolean acquired;
                try {
                    if (shared){
                        acquired = (tryAcquireShared(arg) >= 0);
                    }
                    else
                        acquired = tryAcquire(arg);
                } catch (Throwable ex) {
                    cancelAcquire(node, interrupted, false);
                    throw ex;
                }
                if (acquired) {
                    if (first) {
                        node.prev = null;
                        head = node;
                        pred.next = null;
                        node.waiter = null;
                        if (shared)
                        {  log("在共享模式下获取到资源");
                            signalNextIfShared(node);}

                        if (interrupted)
                            current.interrupt();
                    }
                    return 1;
                }
            }
            if (node == null) {                 // allocate; retry before enqueue
                if (shared)
                {node = new SharedNode();
                log("创建共享节点");}
                else
                {node = new ExclusiveNode();
                log("创建独占节点");}
            } else if (pred == null) {          // try to enqueue
                node.waiter = current;
                Node t = tail;
                node.setPrevRelaxed(t);         // avoid unnecessary fence
                if (t == null)
                    tryInitializeHead();
                else if (!casTail(t, node))
                    node.setPrevRelaxed(null);  // back out
                else
                {t.next = node;
                log("插入队尾成功");}
            } else if (first && spins != 0) {
                /**
                 * 它告诉 JVM + CPU：
                 * “这个线程现在处于自旋等待中，不用优化、不用抢执行资源、帮我省点功耗。”
                 * 也就是说，它是给 JIT 编译器 + CPU 打了一个 “我在自旋”等待提示。
                 */
                --spins;                        // reduce unfairness on rewaits
                log("自旋");
                Thread.onSpinWait();
            } else if (node.status == 0) {
                node.status = WAITING;          // enable signal and recheck
            } else {
                long nanos;
                spins = postSpins = (byte)((postSpins << 1) | 1);
                if (!timed){
                    log("准备进入阻塞状态（park）");
                    LockSupport.park(this);
                   }
                else if ((nanos = time - System.nanoTime()) > 0L)
                {
                    log("准备进入定时阻塞状态（parkNanos）");
                    LockSupport.parkNanos(this, nanos);
                }
                else
                    break;
                node.clearStatus();
                if ((interrupted |= Thread.interrupted()) && interruptible)
                    break;
            }
        }
        return cancelAcquire(node, interrupted, interruptible);
    }

    /**
     * Possibly repeatedly traverses from tail, unsplicing cancelled
     * nodes until none are found. Unparks nodes that may have been
     * relinked to be next eligible acquirer.
     */
    private void cleanQueue() {
        for (;;) {                               // restart point
            for (Node q = tail, s = null, p, n;;) { // (p, q, s) triples
                if (q == null || (p = q.prev) == null)
                    return;                      // end of list
                if (s == null ? tail != q : (s.prev != q || s.status < 0))
                    break;                       // inconsistent
                if (q.status < 0) {              // cancelled
                    if ((s == null ? casTail(q, p) : s.casPrev(q, p)) &&
                            q.prev == p) {
                        p.casNext(q, s);         // OK if fails
                        if (p.prev == null)
                            signalNext(p);
                    }
                    break;
                }
                if ((n = p.next) != q) {         // help finish
                    if (n != null && q.prev == p) {
                        p.casNext(n, q);
                        if (p.prev == null)
                            signalNext(p);
                    }
                    break;
                }
                s = q;
                q = q.prev;
            }
        }
    }

    /**
     * Cancels an ongoing attempt to acquire.
     *
     * @param node the node (may be null if cancelled before enqueuing)
     * @param interrupted true if thread interrupted
     * @param interruptible if should report interruption vs reset
     */
    private int cancelAcquire(Node node, boolean interrupted,
                              boolean interruptible) {
        if (node != null) {
            node.waiter = null;
            node.status = CANCELLED;
            if (node.prev != null)
                cleanQueue();
        }
        if (interrupted) {
            if (interruptible)
                return CANCELLED;
            else
                Thread.currentThread().interrupt();
        }
        return 0;
    }

    // Main exported methods

    /**
     * Attempts to acquire in exclusive mode. This method should query
     * if the state of the object permits it to be acquired in the
     * exclusive mode, and if so to acquire it.
     *
     * <p>This method is always invoked by the thread performing
     * acquire.  If this method reports failure, the acquire method
     * may queue the thread, if it is not already queued, until it is
     * signalled by a release from some other thread. This can be used
     *
     * <p>The default
     * implementation throws {@link UnsupportedOperationException}.
     *
     * @param arg the acquire argument. This value is always the one
     *        passed to an acquire method, or is the value saved on entry
     *        to a condition wait.  The value is otherwise uninterpreted
     *        and can represent anything you like.
     * @return {@code true} if successful. Upon success, this object has
     *         been acquired.
     * @throws IllegalMonitorStateException if acquiring would place this
     *         synchronizer in an illegal state. This exception must be
     *         thrown in a consistent fashion for synchronization to work
     *         correctly.
     * @throws UnsupportedOperationException if exclusive mode is not supported
     */
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to set the state to reflect a release in exclusive
     * mode.
     *
     * <p>This method is always invoked by the thread performing release.
     *
     * <p>The default implementation throws
     * {@link UnsupportedOperationException}.
     *
     * @param arg the release argument. This value is always the one
     *        passed to a release method, or the current state value upon
     *        entry to a condition wait.  The value is otherwise
     *        uninterpreted and can represent anything you like.
     * @return {@code true} if this object is now in a fully released
     *         state, so that any waiting threads may attempt to acquire;
     *         and {@code false} otherwise.
     * @throws IllegalMonitorStateException if releasing would place this
     *         synchronizer in an illegal state. This exception must be
     *         thrown in a consistent fashion for synchronization to work
     *         correctly.
     * @throws UnsupportedOperationException if exclusive mode is not supported
     */
    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to acquire in shared mode. This method should query if
     * the state of the object permits it to be acquired in the shared
     * mode, and if so to acquire it.
     *
     * <p>This method is always invoked by the thread performing
     * acquire.  If this method reports failure, the acquire method
     * may queue the thread, if it is not already queued, until it is
     * signalled by a release from some other thread.
     *
     * <p>The default implementation throws {@link
     * UnsupportedOperationException}.
     *
     * @param arg the acquire argument. This value is always the one
     *        passed to an acquire method, or is the value saved on entry
     *        to a condition wait.  The value is otherwise uninterpreted
     *        and can represent anything you like.
     * @return a negative value on failure; zero if acquisition in shared
     *         mode succeeded but no subsequent shared-mode acquire can
     *         succeed; and a positive value if acquisition in shared
     *         mode succeeded and subsequent shared-mode acquires might
     *         also succeed, in which case a subsequent waiting thread
     *         must check availability. (Support for three different
     *         return values enables this method to be used in contexts
     *         where acquires only sometimes act exclusively.)  Upon
     *         success, this object has been acquired.
     * @throws IllegalMonitorStateException if acquiring would place this
     *         synchronizer in an illegal state. This exception must be
     *         thrown in a consistent fashion for synchronization to work
     *         correctly.
     * @throws UnsupportedOperationException if shared mode is not supported
     */
    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to set the state to reflect a release in shared mode.
     *
     * <p>This method is always invoked by the thread performing release.
     *
     * <p>The default implementation throws
     * {@link UnsupportedOperationException}.
     *
     * @param arg the release argument. This value is always the one
     *        passed to a release method, or the current state value upon
     *        entry to a condition wait.  The value is otherwise
     *        uninterpreted and can represent anything you like.
     * @return {@code true} if this release of shared mode may permit a
     *         waiting acquire (shared or exclusive) to succeed; and
     *         {@code false} otherwise
     * @throws IllegalMonitorStateException if releasing would place this
     *         synchronizer in an illegal state. This exception must be
     *         thrown in a consistent fashion for synchronization to work
     *         correctly.
     * @throws UnsupportedOperationException if shared mode is not supported
     */
    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if synchronization is held exclusively with
     * respect to the current (calling) thread.  This method is invoked
     * upon each call to a {@link ConditionObject} method.
     *
     * <p>The default implementation throws {@link
     * UnsupportedOperationException}. This method is invoked
     * internally only within {@link ConditionObject} methods, so need
     * not be defined if conditions are not used.
     *
     * @return {@code true} if synchronization is held exclusively;
     *         {@code false} otherwise
     * @throws UnsupportedOperationException if conditions are not supported
     */
    protected boolean isHeldExclusively() {
        throw new UnsupportedOperationException();
    }

    /**
     * Acquires in exclusive mode, ignoring interrupts.  Implemented
     * by invoking at least once {@link #tryAcquire},
     * returning on success.  Otherwise the thread is queued, possibly
     * repeatedly blocking and unblocking, invoking {@link

     *
     * @param arg the acquire argument.  This value is conveyed to
     *        {@link #tryAcquire} but is otherwise uninterpreted and
     *        can represent anything you like.
     */
    public final void acquire(int arg) {
        countMethod();
        if (!tryAcquire(arg))
            acquire(null, arg, false, false, false, 0L);
    }

    /**
     * 用来记录每个线程独享 使用了多少次acquire
     */
    public final static ConcurrentHashMap<String, AtomicInteger> count = new ConcurrentHashMap<>();
    private final void countMethod(){
        count.computeIfAbsent(Thread.currentThread().getName(), k -> new AtomicInteger(1)).incrementAndGet();
    }
    public void printAcquireTimes(){
        for (String key : count.keySet()) {
            System.out.println(key + ":" + count.get(key).get());
        }
    }

    /**
     * 用来记录每个线程在共享模式下使用了多少次acquireShare
     */
    public final static ConcurrentHashMap<String, AtomicInteger> countShare = new ConcurrentHashMap<>();
    private final void countShareMethod(){
        count.computeIfAbsent(Thread.currentThread().getName(), k -> new AtomicInteger(1)).incrementAndGet();
    }
    public void printAcquireShareTimes(){
        for (String key : countShare.keySet()) {
            System.out.println(key + ":" + count.get(key).get());
        }
    }

    /**
     * Acquires in exclusive mode, aborting if interrupted.
     * Implemented by first checking interrupt status, then invoking
     * at least once {@link #tryAcquire}, returning on
     * success.  Otherwise the thread is queued, possibly repeatedly
     * blocking and unblocking, invoking {@link #tryAcquire}
     * until success or the thread is interrupted.  This method can be
     *
     * @param arg the acquire argument.  This value is conveyed to
     *        {@link #tryAcquire} but is otherwise uninterpreted and
     *        can represent anything you like.
     * @throws InterruptedException if the current thread is interrupted
     */
    public final void acquireInterruptibly(int arg)
            throws InterruptedException {
        if (Thread.interrupted() ||
                (!tryAcquire(arg) && acquire(null, arg, false, true, false, 0L) < 0))
            throw new InterruptedException();
    }

    /**
     * Attempts to acquire in exclusive mode, aborting if interrupted,
     * and failing if the given timeout elapses.  Implemented by first
     * checking interrupt status, then invoking at least once {@link
     * #tryAcquire}, returning on success.  Otherwise, the thread is
     * queued, possibly repeatedly blocking and unblocking, invoking
     * {@link #tryAcquire} until success or the thread is interrupted
     * or the timeout elapses.  This method can be used to implement
     *
     * @param arg the acquire argument.  This value is conveyed to
     *        {@link #tryAcquire} but is otherwise uninterpreted and
     *        can represent anything you like.
     * @param nanosTimeout the maximum number of nanoseconds to wait
     * @return {@code true} if acquired; {@code false} if timed out
     * @throws InterruptedException if the current thread is interrupted
     */
    public final boolean tryAcquireNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (!Thread.interrupted()) {
            if (tryAcquire(arg))
                return true;
            if (nanosTimeout <= 0L)
                return false;
            int stat = acquire(null, arg, false, true, true,
                    System.nanoTime() + nanosTimeout);
            if (stat > 0)
                return true;
            if (stat == 0)
                return false;
        }
        throw new InterruptedException();
    }

    /**
     * Releases in exclusive mode.  Implemented by unblocking one or
     *
     * @param arg the release argument.  This value is conveyed to
     *        {@link #tryRelease} but is otherwise uninterpreted and
     *        can represent anything you like.
     * @return the value returned from {@link #tryRelease}
     */
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            signalNext(head);
            return true;
        }
        return false;
    }

    /**
     * Acquires in shared mode, ignoring interrupts.  Implemented by
     * first invoking at least once {@link #tryAcquireShared},
     * returning on success.  Otherwise the thread is queued, possibly
     * repeatedly blocking and unblocking, invoking {@link
     * #tryAcquireShared} until success.
     *
     * @param arg the acquire argument.  This value is conveyed to
     *        {@link #tryAcquireShared} but is otherwise uninterpreted
     *        and can represent anything you like.
     */
    public final void acquireShared(int arg) {
        countShareMethod();
        if (tryAcquireShared(arg) < 0)
            acquire(null, arg, true, false, false, 0L);
    }

    /**
     * Acquires in shared mode, aborting if interrupted.  Implemented
     * by first checking interrupt status, then invoking at least once
     * {@link #tryAcquireShared}, returning on success.  Otherwise the
     * thread is queued, possibly repeatedly blocking and unblocking,
     * invoking {@link #tryAcquireShared} until success or the thread
     * is interrupted.
     * @param arg the acquire argument.
     * This value is conveyed to {@link #tryAcquireShared} but is
     * otherwise uninterpreted and can represent anything
     * you like.
     * @throws InterruptedException if the current thread is interrupted
     */
    public final void acquireSharedInterruptibly(int arg)
            throws InterruptedException {
        if (Thread.interrupted() ||
                (tryAcquireShared(arg) < 0 &&
                        acquire(null, arg, true, true, false, 0L) < 0))
            throw new InterruptedException();
    }

    /**
     * Attempts to acquire in shared mode, aborting if interrupted, and
     * failing if the given timeout elapses.  Implemented by first
     * checking interrupt status, then invoking at least once {@link
     * #tryAcquireShared}, returning on success.  Otherwise, the
     * thread is queued, possibly repeatedly blocking and unblocking,
     * invoking {@link #tryAcquireShared} until success or the thread
     * is interrupted or the timeout elapses.
     *
     * @param arg the acquire argument.  This value is conveyed to
     *        {@link #tryAcquireShared} but is otherwise uninterpreted
     *        and can represent anything you like.
     * @param nanosTimeout the maximum number of nanoseconds to wait
     * @return {@code true} if acquired; {@code false} if timed out
     * @throws InterruptedException if the current thread is interrupted
     */
    public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (!Thread.interrupted()) {
            if (tryAcquireShared(arg) >= 0)
                return true;
            if (nanosTimeout <= 0L)
                return false;
            int stat = acquire(null, arg, true, true, true,
                    System.nanoTime() + nanosTimeout);
            if (stat > 0)
                return true;
            if (stat == 0)
                return false;
        }
        throw new InterruptedException();
    }

    /**
     * Releases in shared mode.  Implemented by unblocking one or more
     * threads if {@link #tryReleaseShared} returns true.
     *
     * @param arg the release argument.  This value is conveyed to
     *        {@link #tryReleaseShared} but is otherwise uninterpreted
     *        and can represent anything you like.
     * @return the value returned from {@link #tryReleaseShared}
     */
    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            signalNext(head);
            return true;
        }
        return false;
    }

    // Queue inspection methods

    /**
     * Queries whether any threads are waiting to acquire. Note that
     * because cancellations due to interrupts and timeouts may occur
     * at any time, a {@code true} return does not guarantee that any
     * other thread will ever acquire.
     *
     * @return {@code true} if there may be other threads waiting to acquire
     */
    public final boolean hasQueuedThreads() {
        for (Node p = tail, h = head; p != h && p != null; p = p.prev)
            if (p.status >= 0)
                return true;
        return false;
    }

    /**
     * Queries whether any threads have ever contended to acquire this
     * synchronizer; that is, if an acquire method has ever blocked.
     *
     * <p>In this implementation, this operation returns in
     * constant time.
     *
     * @return {@code true} if there has ever been contention
     */
    public final boolean hasContended() {
        return head != null;
    }

    /**
     * Returns the first (longest-waiting) thread in the queue, or
     * {@code null} if no threads are currently queued.
     *
     * <p>In this implementation, this operation normally returns in
     * constant time, but may iterate upon contention if other threads are
     * concurrently modifying the queue.
     *
     * @return the first (longest-waiting) thread in the queue, or
     *         {@code null} if no threads are currently queued
     */
    public final Thread getFirstQueuedThread() {
        Thread first = null, w; Node h, s;
        if ((h = head) != null && ((s = h.next) == null ||
                (first = s.waiter) == null ||
                s.prev == null)) {
            // traverse from tail on stale reads
            for (Node p = tail, q; p != null && (q = p.prev) != null; p = q)
                if ((w = p.waiter) != null)
                    first = w;
        }
        return first;
    }

    /**
     * Returns true if the given thread is currently queued.
     *
     * <p>This implementation traverses the queue to determine
     * presence of the given thread.
     *
     * @param thread the thread
     * @return {@code true} if the given thread is on the queue
     * @throws NullPointerException if the thread is null
     */
    public final boolean isQueued(Thread thread) {
        if (thread == null)
            throw new NullPointerException();
        for (Node p = tail; p != null; p = p.prev)
            if (p.waiter == thread)
                return true;
        return false;
    }

    /**
     * Returns {@code true} if the apparent first queued thread, if one
     * exists, is waiting in exclusive mode.  If this method returns
     * {@code true}, and the current thread is attempting to acquire in
     * shared mode (that is, this method is invoked from {@link
     * #tryAcquireShared}) then it is guaranteed that the current thread
     * is not the first queued thread.  Used only as a heuristic in
     * ReentrantReadWriteLock.
     */
    final boolean apparentlyFirstQueuedIsExclusive() {
        Node h, s;
        return (h = head) != null && (s = h.next)  != null &&
                !(s instanceof SharedNode) && s.waiter != null;
    }

    /**
     * Queries whether any threads have been waiting to acquire longer
     * than the current thread.
     *
     * <p>An invocation of this method is equivalent to (but may be
     * more efficient than):
     * <pre> {@code
     * getFirstQueuedThread() != Thread.currentThread()
     *   && hasQueuedThreads()}</pre>
     *
     * <p>Note that because cancellations due to interrupts and
     * timeouts may occur at any time, a {@code true} return does not
     * guarantee that some other thread will acquire before the current
     * thread.  Likewise, it is possible for another thread to win a
     * race to enqueue after this method has returned {@code false},
     * due to the queue being empty.
     *
     * <p>This method is designed to be used by a fair synchronizer to
     * avoid <a href="AbstractQueuedSynchronizer.html#barging">barging</a>.
     * Such a synchronizer's {@link #tryAcquire} method should return
     * {@code false}, and its {@link #tryAcquireShared} method should
     * return a negative value, if this method returns {@code true}
     * (unless this is a reentrant acquire).  For example, the {@code
     * tryAcquire} method for a fair, reentrant, exclusive mode
     * synchronizer might look like this:
     *
     * <pre> {@code
     * protected boolean tryAcquire(int arg) {
     *   if (isHeldExclusively()) {
     *     // A reentrant acquire; increment hold count
     *     return true;
     *   } else if (hasQueuedPredecessors()) {
     *     return false;
     *   } else {
     *     // try to acquire normally
     *   }
     * }}</pre>
     *
     * @return {@code true} if there is a queued thread preceding the
     *         current thread, and {@code false} if the current thread
     *         is at the head of the queue or the queue is empty
     * @since 1.7
     */
    public final boolean hasQueuedPredecessors() {
        Thread first = null; Node h, s;
        if ((h = head) != null && ((s = h.next) == null ||
                (first = s.waiter) == null ||
                s.prev == null))
            first = getFirstQueuedThread(); // retry via getFirstQueuedThread
        return first != null && first != Thread.currentThread();
    }

    // Instrumentation and monitoring methods

    /**
     * Returns an estimate of the number of threads waiting to
     * acquire.  The value is only an estimate because the number of
     * threads may change dynamically while this method traverses
     * internal data structures.  This method is designed for use in
     * monitoring system state, not for synchronization control.
     *
     * @return the estimated number of threads waiting to acquire
     */
    public final int getQueueLength() {
        int n = 0;
        for (Node p = tail; p != null; p = p.prev) {
            if (p.waiter != null)
                ++n;
        }
        return n;
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire.  Because the actual set of threads may change
     * dynamically while constructing this result, the returned
     * collection is only a best-effort estimate.  The elements of the
     * returned collection are in no particular order.  This method is
     * designed to facilitate construction of subclasses that provide
     * more extensive monitoring facilities.
     *
     * @return the collection of threads
     */
    public final Collection<Thread> getQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<>();
        for (Node p = tail; p != null; p = p.prev) {
            Thread t = p.waiter;
            if (t != null)
                list.add(t);
        }
        return list;
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire in exclusive mode. This has the same properties
     * as {@link #getQueuedThreads} except that it only returns
     * those threads waiting due to an exclusive acquire.
     *
     * @return the collection of threads
     */
    public final Collection<Thread> getExclusiveQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<>();
        for (Node p = tail; p != null; p = p.prev) {
            if (!(p instanceof SharedNode)) {
                Thread t = p.waiter;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire in shared mode. This has the same properties
     * as {@link #getQueuedThreads} except that it only returns
     * those threads waiting due to a shared acquire.
     *
     * @return the collection of threads
     */
    public final Collection<Thread> getSharedQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<>();
        for (Node p = tail; p != null; p = p.prev) {
            if (p instanceof SharedNode) {
                Thread t = p.waiter;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

    /**
     * Returns a string identifying this synchronizer, as well as its state.
     * The state, in brackets, includes the String {@code "State ="}
     * followed by the current value of {@link #getState}, and either
     * {@code "nonempty"} or {@code "empty"} depending on whether the
     * queue is empty.
     *
     * @return a string identifying this synchronizer, as well as its state
     */
    public String toString() {
        return super.toString()
                + "[State = " + getState() + ", "
                + (hasQueuedThreads() ? "non" : "") + "empty queue]";
    }

    // Instrumentation methods for conditions

    /**
     * Queries whether the given ConditionObject
     * uses this synchronizer as its lock.
     *
     * @param condition the condition
     * @return {@code true} if owned
     * @throws NullPointerException if the condition is null
     */
    public final boolean owns(ConditionObject condition) {
        return condition.isOwnedBy(this);
    }

    /**
     * Queries whether any threads are waiting on the given condition
     * associated with this synchronizer. Note that because timeouts
     * and interrupts may occur at any time, a {@code true} return
     * does not guarantee that a future {@code signal} will awaken
     * any threads.  This method is designed primarily for use in
     * monitoring of the system state.
     *
     * @param condition the condition
     * @return {@code true} if there are any waiting threads
     * @throws IllegalMonitorStateException if exclusive synchronization
     *         is not held
     * @throws IllegalArgumentException if the given condition is
     *         not associated with this synchronizer
     * @throws NullPointerException if the condition is null
     */
    public final boolean hasWaiters(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.hasWaiters();
    }

    /**
     * Returns an estimate of the number of threads waiting on the
     * given condition associated with this synchronizer. Note that
     * because timeouts and interrupts may occur at any time, the
     * estimate serves only as an upper bound on the actual number of
     * waiters.  This method is designed for use in monitoring system
     * state, not for synchronization control.
     *
     * @param condition the condition
     * @return the estimated number of waiting threads
     * @throws IllegalMonitorStateException if exclusive synchronization
     *         is not held
     * @throws IllegalArgumentException if the given condition is
     *         not associated with this synchronizer
     * @throws NullPointerException if the condition is null
     */
    public final int getWaitQueueLength(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitQueueLength();
    }

    /**
     * Returns a collection containing those threads that may be
     * waiting on the given condition associated with this
     * synchronizer.  Because the actual set of threads may change
     * dynamically while constructing this result, the returned
     * collection is only a best-effort estimate. The elements of the
     * returned collection are in no particular order.
     *
     * @param condition the condition
     * @return the collection of threads
     * @throws IllegalMonitorStateException if exclusive synchronization
     *         is not held
     * @throws IllegalArgumentException if the given condition is
     *         not associated with this synchronizer
     * @throws NullPointerException if the condition is null
     */
    public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitingThreads();
    }


    public class ConditionObject implements Condition, java.io.Serializable {
        private static final long serialVersionUID = 1173984872572414699L;
        /** First node of condition queue. */
        private transient ConditionNode firstWaiter;
        /** Last node of condition queue. */
        private transient ConditionNode lastWaiter;

        /**
         * Creates a new {@code ConditionObject} instance.
         */
        public ConditionObject() { }

        // Signalling methods

        /**
         * Removes and transfers one or all waiters to sync queue.
         */
        private void doSignal(ConditionNode first, boolean all) {
            while (first != null) {
                ConditionNode next = first.nextWaiter;
                if ((firstWaiter = next) == null)
                    lastWaiter = null;
                if ((first.getAndUnsetStatus(COND) & COND) != 0) {
                    enqueue(first);
                    if (!all)
                        break;
                }
                first = next;
            }
        }

        /**
         * Moves the longest-waiting thread, if one exists, from the
         * wait queue for this condition to the wait queue for the
         * owning lock.
         *
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        public final void signal() {
            ConditionNode first = firstWaiter;
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            if (first != null)
                doSignal(first, false);
        }

        /**
         * Moves all threads from the wait queue for this condition to
         * the wait queue for the owning lock.
         *
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        public final void signalAll() {
            ConditionNode first = firstWaiter;
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            if (first != null)
                doSignal(first, true);
        }

        // Waiting methods

        /**
         * Adds node to condition list and releases lock.
         *
         * @param node the node
         * @return savedState to reacquire after wait
         */
        private int enableWait(ConditionNode node) {
            if (isHeldExclusively()) {
                node.waiter = Thread.currentThread();
                node.setStatusRelaxed(COND | WAITING);
                ConditionNode last = lastWaiter;
                if (last == null)
                    firstWaiter = node;
                else
                    last.nextWaiter = node;
                lastWaiter = node;
                int savedState = getState();
                if (release(savedState))
                    return savedState;
            }
            node.status = CANCELLED; // lock not held or inconsistent
            throw new IllegalMonitorStateException();
        }

        /**
         * Returns true if a node that was initially placed on a condition
         * queue is now ready to reacquire on sync queue.
         * @param node the node
         * @return true if is reacquiring
         */
        private boolean canReacquire(ConditionNode node) {
            // check links, not status to avoid enqueue race
            return node != null && node.prev != null && isEnqueued(node);
        }

        /**
         * Unlinks the given node and other non-waiting nodes from
         * condition queue unless already unlinked.
         */
        private void unlinkCancelledWaiters(ConditionNode node) {
            if (node == null || node.nextWaiter != null || node == lastWaiter) {
                ConditionNode w = firstWaiter, trail = null;
                while (w != null) {
                    ConditionNode next = w.nextWaiter;
                    if ((w.status & COND) == 0) {
                        w.nextWaiter = null;
                        if (trail == null)
                            firstWaiter = next;
                        else
                            trail.nextWaiter = next;
                        if (next == null)
                            lastWaiter = trail;
                    } else
                        trail = w;
                    w = next;
                }
            }
        }

        /**
         * Implements uninterruptible condition wait.
         * <ol>
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument,
         *     throwing IllegalMonitorStateException if it fails.
         * <li>Block until signalled.
         * <li>Reacquire by invoking specialized version of
         *     {@link #acquire} with saved state as argument.
         * </ol>
         */
        public final void awaitUninterruptibly() {
            ConditionNode node = new ConditionNode();
            int savedState = enableWait(node);
            LockSupport.setCurrentBlocker(this); // for back-compatibility
            boolean interrupted = false, rejected = false;
            while (!canReacquire(node)) {
                if (Thread.interrupted())
                    interrupted = true;
                else if ((node.status & COND) != 0) {
                    try {
                        if (rejected)
                            node.block();
                        else
                            ForkJoinPool.managedBlock(node);
                    } catch (RejectedExecutionException ex) {
                        rejected = true;
                    } catch (InterruptedException ie) {
                        interrupted = true;
                    }
                } else
                    Thread.onSpinWait();    // awoke while enqueuing
            }
            LockSupport.setCurrentBlocker(null);
            node.clearStatus();
            acquire(node, savedState, false, false, false, 0L);
            if (interrupted)
                Thread.currentThread().interrupt();
        }

        /**
         * Implements interruptible condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument,
         *     throwing IllegalMonitorStateException if it fails.
         * <li>Block until signalled or interrupted.
         * <li>Reacquire by invoking specialized version of
         *     {@link #acquire} with saved state as argument.
         * <li>If interrupted while blocked in step 4, throw InterruptedException.
         * </ol>
         */
        public final void await() throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            ConditionNode node = new ConditionNode();
            int savedState = enableWait(node);
            LockSupport.setCurrentBlocker(this); // for back-compatibility
            boolean interrupted = false, cancelled = false, rejected = false;
            while (!canReacquire(node)) {
                if (interrupted |= Thread.interrupted()) {
                    if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                        break;              // else interrupted after signal
                } else if ((node.status & COND) != 0) {
                    try {
                        if (rejected)
                            node.block();
                        else
                            ForkJoinPool.managedBlock(node);
                    } catch (RejectedExecutionException ex) {
                        rejected = true;
                    } catch (InterruptedException ie) {
                        interrupted = true;
                    }
                } else
                    Thread.onSpinWait();    // awoke while enqueuing
            }
            LockSupport.setCurrentBlocker(null);
            node.clearStatus();
            acquire(node, savedState, false, false, false, 0L);
            if (interrupted) {
                if (cancelled) {
                    unlinkCancelledWaiters(node);
                    throw new InterruptedException();
                }
                Thread.currentThread().interrupt();
            }
        }

        /**
         * Implements timed condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument,
         *     throwing IllegalMonitorStateException if it fails.
         * <li>Block until signalled, interrupted, or timed out.
         * <li>Reacquire by invoking specialized version of
         *     {@link #acquire} with saved state as argument.
         * <li>If interrupted while blocked in step 4, throw InterruptedException.
         * </ol>
         */
        public final long awaitNanos(long nanosTimeout)
                throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            ConditionNode node = new ConditionNode();
            int savedState = enableWait(node);
            long nanos = (nanosTimeout < 0L) ? 0L : nanosTimeout;
            long deadline = System.nanoTime() + nanos;
            boolean cancelled = false, interrupted = false;
            while (!canReacquire(node)) {
                if ((interrupted |= Thread.interrupted()) ||
                        (nanos = deadline - System.nanoTime()) <= 0L) {
                    if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                        break;
                } else
                    LockSupport.parkNanos(this, nanos);
            }
            node.clearStatus();
            acquire(node, savedState, false, false, false, 0L);
            if (cancelled) {
                unlinkCancelledWaiters(node);
                if (interrupted)
                    throw new InterruptedException();
            } else if (interrupted)
                Thread.currentThread().interrupt();
            long remaining = deadline - System.nanoTime(); // avoid overflow
            return (remaining <= nanosTimeout) ? remaining : Long.MIN_VALUE;
        }

        /**
         * Implements absolute timed condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument,
         *     throwing IllegalMonitorStateException if it fails.
         * <li>Block until signalled, interrupted, or timed out.
         * <li>Reacquire by invoking specialized version of
         *     {@link #acquire} with saved state as argument.
         * <li>If interrupted while blocked in step 4, throw InterruptedException.
         * <li>If timed out while blocked in step 4, return false, else true.
         * </ol>
         */
        public final boolean awaitUntil(Date deadline)
                throws InterruptedException {
            long abstime = deadline.getTime();
            if (Thread.interrupted())
                throw new InterruptedException();
            ConditionNode node = new ConditionNode();
            int savedState = enableWait(node);
            boolean cancelled = false, interrupted = false;
            while (!canReacquire(node)) {
                if ((interrupted |= Thread.interrupted()) ||
                        System.currentTimeMillis() >= abstime) {
                    if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                        break;
                } else
                    LockSupport.parkUntil(this, abstime);
            }
            node.clearStatus();
            acquire(node, savedState, false, false, false, 0L);
            if (cancelled) {
                unlinkCancelledWaiters(node);
                if (interrupted)
                    throw new InterruptedException();
            } else if (interrupted)
                Thread.currentThread().interrupt();
            return !cancelled;
        }

        /**
         * Implements timed condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument,
         *     throwing IllegalMonitorStateException if it fails.
         * <li>Block until signalled, interrupted, or timed out.
         * <li>Reacquire by invoking specialized version of
         *     {@link #acquire} with saved state as argument.
         * <li>If interrupted while blocked in step 4, throw InterruptedException.
         * <li>If timed out while blocked in step 4, return false, else true.
         * </ol>
         */
        public final boolean await(long time, TimeUnit unit)
                throws InterruptedException {
            long nanosTimeout = unit.toNanos(time);
            if (Thread.interrupted())
                throw new InterruptedException();
            ConditionNode node = new ConditionNode();
            int savedState = enableWait(node);
            long nanos = (nanosTimeout < 0L) ? 0L : nanosTimeout;
            long deadline = System.nanoTime() + nanos;
            boolean cancelled = false, interrupted = false;
            while (!canReacquire(node)) {
                if ((interrupted |= Thread.interrupted()) ||
                        (nanos = deadline - System.nanoTime()) <= 0L) {
                    if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                        break;
                } else
                    LockSupport.parkNanos(this, nanos);
            }
            node.clearStatus();
            acquire(node, savedState, false, false, false, 0L);
            if (cancelled) {
                unlinkCancelledWaiters(node);
                if (interrupted)
                    throw new InterruptedException();
            } else if (interrupted)
                Thread.currentThread().interrupt();
            return !cancelled;
        }

        //  support for instrumentation

        /**
         * Returns true if this condition was created by the given
         * synchronization object.
         *
         * @return {@code true} if owned
         */
        final boolean isOwnedBy(MyAqs sync) {
            return sync == MyAqs.this;
        }

        /**
         * Queries whether any threads are waiting on this condition.
         *
         * @return {@code true} if there are any waiting threads
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        protected final boolean hasWaiters() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            for (ConditionNode w = firstWaiter; w != null; w = w.nextWaiter) {
                if ((w.status & COND) != 0)
                    return true;
            }
            return false;
        }

        /**
         * Returns an estimate of the number of threads waiting on
         * this condition.
         *
         * @return the estimated number of waiting threads
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        protected final int getWaitQueueLength() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            int n = 0;
            for (ConditionNode w = firstWaiter; w != null; w = w.nextWaiter) {
                if ((w.status & COND) != 0)
                    ++n;
            }
            return n;
        }

        /**
         * Returns a collection containing those threads that may be
         * waiting on this Condition.
         *
         * @return the collection of threads
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        protected final Collection<Thread> getWaitingThreads() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            ArrayList<Thread> list = new ArrayList<>();
            for (ConditionNode w = firstWaiter; w != null; w = w.nextWaiter) {
                if ((w.status & COND) != 0) {
                    Thread t = w.waiter;
                    if (t != null)
                        list.add(t);
                }
            }
            return list;
        }
    }

    // Unsafe
//    private static final Unsafe U = Unsafe.getUnsafe();
//    private static final long STATE
//            = U.objectFieldOffset(AbstractQueuedSynchronizer.class, "state");
//    private static final long HEAD
//            = U.objectFieldOffset(AbstractQueuedSynchronizer.class, "head");
//    private static final long TAIL
//            = U.objectFieldOffset(AbstractQueuedSynchronizer.class, "tail");

    // 对应的 VarHandle
    private static final VarHandle STATE;
    private static final VarHandle HEAD;
    private static final VarHandle TAIL;

    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            STATE = l.findVarHandle(MyAqs.class, "state", int.class);
            HEAD = l.findVarHandle(MyAqs.class, "head", Node.class);
            TAIL = l.findVarHandle(MyAqs.class, "tail", Node.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    static {
        Class<?> ensureLoaded = LockSupport.class;
    }
}
