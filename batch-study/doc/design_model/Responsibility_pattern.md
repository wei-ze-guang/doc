# 责任链模式是什么？
- 责任链模式的核心思想是：
  - 将多个处理对象连接成一条链条，每个对象在链条中处理自己的任务或请求，然后将请求传递给下一个对象，直到请求得到处理。
  - 简单来说，责任链模式将一个请求的处理过程分发到多个处理者（对象）上，每个对象处理自己负责的部分，未处理的部分会传递给下一个对象，直到整个请求完成。  
## 结构图UML
```scss
 ┌────────────────┐
│     Client     │
└────────────────┘
│
▼
┌────────────────┐    ┌────────────────┐   ┌────────────────┐
│ Handler (抽象类) │──▶│ ConcreteHandler │──▶│ ConcreteHandler │
                   └────────────────┘    └────────────────┘   └────────────────┘
                   ▲                      ▲                      ▲
                   │                      │                      │
                   ┌────────────────┐    ┌────────────────┐   ┌────────────────┐
                   │ ConcreteHandler │    │ ConcreteHandler │   │ ConcreteHandler │
                   └────────────────┘    └────────────────┘   └────────────────┘

```  
- Handler（抽象处理者）：定义一个处理请求的接口，并且包含一个指向下一个处理者的引用。
- ConcreteHandler（具体处理者）：实现具体的请求处理逻辑，并决定是否将请求传递给下一个处理者。  
---  
## 三、代码示例：责任链模式（请假申请处理）
我们以一个简单的 请假申请 的例子来实现责任链模式，  
假设我们有三个审批层级：经理、部门主管、人力资源部，不同的请假天数会由不同的审批人处理。  
---  
- 抽象处理者：Handler  
```java
public abstract class Handler {
    protected Handler nextHandler;  // 下一个处理者

    // 设置下一个处理者
    public void setNextHandler(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    // 处理请求的抽象方法
    public abstract void handleRequest(int leaveDays);
}

```  
- 具体处理者：Manager（经理）  
```java
public class Manager extends Handler {
    @Override
    public void handleRequest(int leaveDays) {
        if (leaveDays <= 2) {
            System.out.println("Manager approves " + leaveDays + " days of leave.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(leaveDays);
        }
    }
}

```  
- 3️⃣ 具体处理者：DepartmentHead（部门主管） 
```java
public class DepartmentHead extends Handler {
    @Override
    public void handleRequest(int leaveDays) {
        if (leaveDays <= 5) {
            System.out.println("Department Head approves " + leaveDays + " days of leave.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(leaveDays);
        }
    }
}

```  
- 4️⃣ 具体处理者：HR（人力资源部）  
```java
public class HR extends Handler {
    @Override
    public void handleRequest(int leaveDays) {
        if (leaveDays > 5) {
            System.out.println("HR approves " + leaveDays + " days of leave.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(leaveDays);
        }
    }
}

```  
-  客户端：模拟请假流程  
```java
public class Main {
    public static void main(String[] args) {
        // 创建具体处理者
        Handler manager = new Manager();
        Handler departmentHead = new DepartmentHead();
        Handler hr = new HR();

        // 设置责任链
        manager.setNextHandler(departmentHead);
        departmentHead.setNextHandler(hr);

        // 模拟不同的请假请求
        System.out.println("Request for 1 day leave:");
        manager.handleRequest(1);

        System.out.println("\nRequest for 4 days leave:");
        manager.handleRequest(4);

        System.out.println("\nRequest for 7 days leave:");
        manager.handleRequest(7);
    }
}

```  
##  四、优缺点总结
- ✅ 优点：
  - 降低耦合性：责任链模式通过将请求的处理分布到多个处理者中，每个处理者只关心自己处理的部分，降低了类与类之间的耦合性。
  - 增加灵活性：可以动态地修改处理流程，只需调整责任链的顺序或替换处理者，而不需要修改客户端代码。
  - 简化客户端代码：客户端无需知道具体由哪个处理者处理请求，只需发送请求即可，责任链内部自动处理。
- ❌ 缺点：
  - 处理顺序不明确：如果责任链过长或者链条中的处理者过多，可能导致请求没有被处理，或者处理的顺序不符合预期。
  - 增加系统复杂性：责任链模式增加了系统的复杂度，尤其是在有多个链条的情况下，可能会引入更多的管理和维护成本。
  - 调试困难：由于请求可能传递给多个处理者，调试时不容易追踪请求的实际处理过程。  
## 五、适用场景
- 责任链模式适用于以下场景：
  - 多个对象可以处理请求：当一个请求需要多个对象来处理时，使用责任链模式可以将处理分担给多个处理者。
  - 请求的处理者不确定：处理请求的对象可以动态选择，而不是固定在某个对象上。
  - 请求的处理顺序需要灵活控制：在处理请求时，我们可以根据实际需要决定由哪个对象来处理。
- 例子：
  - 日志处理：多个日志处理对象（如日志输出到控制台、文件、数据库等），可以根据配置链式处理。
  - 事件处理：在事件驱动系统中，可以用责任链模式处理不同类型的事件。
  - 权限验证：多个不同的权限验证模块可以串联在一起，处理用户请求的权限校验。  
## 总结
责任链模式通过链式结构将请求传递给多个处理对象，每个处理对象只负责处理自己能处理的部分，其余部分交给下一个对象处理。  
它可以解耦客户端与请求处理的过程，让多个对象可以顺序地处理请求。常见的应用场景包括事件处理、日志记录、权限验证等。