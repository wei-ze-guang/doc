package pattern.state;

/**
 * 📦 4. 状态模式代码示例：电梯控制
 * 我们以 电梯控制 为例来说明状态模式。电梯的工作有多个状态，如：开门、关门、上下楼层 等。
 */
public class State {

    public static void main(String[] args) {
        Lift lift = new Lift();

        lift.open();
        lift.close();
        lift.run();
        lift.stop();
    }
}

/**
 * 1️⃣ 抽象状态：LiftState
 */
 abstract class LiftState {
    protected Lift lift;

    public LiftState(Lift lift) {
        this.lift = lift;
    }

    // 定义不同状态的行为
    public abstract void open();
    public abstract void close();
    public abstract void run();
    public abstract void stop();
}

/**
 * 2️⃣ 具体状态：OpeningState（开门状态）
 */
 class OpeningState extends LiftState {

    public OpeningState(Lift lift) {
        super(lift);
    }

    @Override
    public void open() {
        System.out.println("The lift door is already open.");
    }

    @Override
    public void close() {
        System.out.println("The lift door is closing.");
        lift.setLiftState(lift.getClosingState()); // 切换到关门状态
    }

    @Override
    public void run() {
        System.out.println("The lift is opening the door. Cannot run now.");
    }

    @Override
    public void stop() {
        System.out.println("The lift is opening the door. Cannot stop now.");
    }
}

/**
 * 3️⃣ 具体状态：ClosingState（关门状态）
 */
 class ClosingState extends LiftState {

    public ClosingState(Lift lift) {
        super(lift);
    }

    @Override
    public void open() {
        System.out.println("The lift door is opening.");
        lift.setLiftState(lift.getOpeningState()); // 切换到开门状态
    }

    @Override
    public void close() {
        System.out.println("The lift door is already closed.");
    }

    @Override
    public void run() {
        System.out.println("The lift is now running.");
        lift.setLiftState(lift.getRunningState()); // 切换到运行状态
    }

    @Override
    public void stop() {
        System.out.println("The lift is stopped.");
        lift.setLiftState(lift.getStoppedState()); // 切换到停止状态
    }
}

/**
 * 4️⃣ 具体状态：RunningState（运行状态）
 */
class RunningState extends LiftState {

    public RunningState(Lift lift) {
        super(lift);
    }

    @Override
    public void open() {
        System.out.println("Cannot open the door while the lift is running.");
    }

    @Override
    public void close() {
        System.out.println("The lift door is already closed.");
    }

    @Override
    public void run() {
        System.out.println("The lift is already running.");
    }

    @Override
    public void stop() {
        System.out.println("The lift is stopping.");
        lift.setLiftState(lift.getStoppedState()); // 切换到停止状态
    }
}

/**
 * 5️⃣ 具体状态：StoppedState（停止状态）
 */

class StoppedState extends LiftState {

    public StoppedState(Lift lift) {
        super(lift);
    }

    @Override
    public void open() {
        System.out.println("The lift door is opening.");
        lift.setLiftState(lift.getOpeningState()); // 切换到开门状态
    }

    @Override
    public void close() {
        System.out.println("The lift door is closing.");
        lift.setLiftState(lift.getClosingState()); // 切换到关门状态
    }

    @Override
    public void run() {
        System.out.println("The lift is running now.");
        lift.setLiftState(lift.getRunningState()); // 切换到运行状态
    }

    @Override
    public void stop() {
        System.out.println("The lift is already stopped.");
    }
}

/**
 * 6️⃣ 环境类：Lift
 */
class Lift {
    private LiftState openingState;
    private LiftState closingState;
    private LiftState runningState;
    private LiftState stoppedState;

    private LiftState currentState;

    public Lift() {
        openingState = new OpeningState(this);
        closingState = new ClosingState(this);
        runningState = new RunningState(this);
        stoppedState = new StoppedState(this);

        currentState = stoppedState; // 初始状态为停止状态
    }

    // 切换状态
    public void setLiftState(LiftState liftState) {
        this.currentState = liftState;
    }

    public LiftState getOpeningState() {
        return openingState;
    }

    public LiftState getClosingState() {
        return closingState;
    }

    public LiftState getRunningState() {
        return runningState;
    }

    public LiftState getStoppedState() {
        return stoppedState;
    }

    // 委托方法，调用当前状态的操作
    public void open() {
        currentState.open();
    }

    public void close() {
        currentState.close();
    }

    public void run() {
        currentState.run();
    }

    public void stop() {
        currentState.stop();
    }
}





