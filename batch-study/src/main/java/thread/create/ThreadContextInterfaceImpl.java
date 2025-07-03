package thread.create;

public class ThreadContextInterfaceImpl implements ThreadContextInterface {
    private CreateThreadInterface createThread;

    public ThreadContextInterfaceImpl(CreateThreadInterface createThread) {
        this.createThread = createThread;
    }


    @Override
    public Thread getThreadContext() {
        return createThread.createThread();
    }
}
