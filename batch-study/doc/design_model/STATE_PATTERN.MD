# 状态模式的定义
状态模式允许一个对象在其内部状态发生改变时，改变其行为。看起来像是对象改变了类。  
当对象的状态发生变化时，它会表现出不同的行为，而这些行为是根据当前的状态来决定的。状态模式通过将不同的行为封装 
到不同的状态对象中，使得这些行为与对象的状态关联。  
## 2. 状态模式的结构
- 状态模式主要包括以下几个角色：
  - Context（环境角色）：维护一个 State 类型的引用，客户端通过此对象来触发状态的转移。
  - Context 对象并不直接处理具体的行为，而是委托给当前的 State 对象来执行。
- State（抽象状态角色）：定义所有具体状态类需要实现的接口或抽象类。
- ConcreteState（具体状态角色）：每个具体状态类负责实现某一具体状态下的行为。  
## 状态模式的类图
```markdown
     ┌────────────┐
     │   Context  │
     └─────┬──────┘
           │
     ┌─────▼──────┐
     │   State    │
     └─────┬──────┘
           │
┌─────────┴────────┐
│                 │
▼                 ▼
ConcreteState1    ConcreteState2

```  
- Context：环境角色，持有当前状态，并通过状态对象来完成行为。
- State：状态角色，定义每个状态的行为。
- ConcreteState：具体状态角色，实现具体状态下的行为。  
## 状态模式代码示例：电梯控制
我们以 电梯控制 为例来说明状态模式。电梯的工作有多个状态，如：开门、关门、上下楼层 等。  
- 1️⃣ 抽象状态：LiftState  
```java
public abstract class LiftState {
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

```  
- 2️⃣ 具体状态：OpeningState（开门状态）  
```java
public class OpeningState extends LiftState {

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

```  
- 3️⃣ 具体状态：ClosingState（关门状态）  
```java
public class ClosingState extends LiftState {

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

```  
- 4️⃣ 具体状态：RunningState（运行状态）  
```java
public class RunningState extends LiftState {

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

```  
- 5️⃣ 具体状态：StoppedState（停止状态）  
```java
public class StoppedState extends LiftState {

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

```  
- 6️⃣ 环境类：Lift  
```java
public class Lift {
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

```  
- 7️⃣ 客户端
```java
public class Main {
    public static void main(String[] args) {
        Lift lift = new Lift();

        lift.open(); // 打开门
        lift.close(); // 关门
        lift.run(); // 启动电梯
        lift.stop(); // 停止电梯
    }
}

```  
##  总结
🔑 5. 总结
- ✅ 优点：
  - 封装状态转换的复杂性：状态模式将与状态相关的行为放入不同的状态类中，避免了在代码中进行复杂的条件判断。
  - 扩展性强：添加新的状态非常简单，只需新增状态类即可。
  - 清晰的结构：通过将每个状态作为独立的类，代码更加清晰易懂。
- ❌ 缺点：
  - 类的数量增加：每增加一个状态，就需要创建一个新的类，可能导致类的数量暴增。
  - 状态管理：如果状态之间的转换逻辑过于复杂，可能会导致状态类之间的相互依赖和管理变得困难。
- ✅ 适用场景：
  - 多个状态的对象：对象的行为依赖于其内部状态，并且这些状态之间有明确的转换关系。
  - 复杂的状态机：如GUI组件、游戏状态、工作流系统等。