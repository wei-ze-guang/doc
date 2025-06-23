package pattern.responsibilitychain;



/**
 * 📦 三、代码示例：责任链模式（请假申请处理）
 * 我们以一个简单的 请假申请 的例子来实现责任链模式，
 * 假设我们有三个审批层级：经理、部门主管、人力资源部，不同的请假天数会由不同的审批人处理。
 */
public class ResponsibilityChain {

    public static void main(String[] args) {

        /**
         * 创建具体处理者
         */
        Handler manager = new Manager();
        Handler departmentHead = new DepartmentHead();
        Handler hr = new HR();

        /**
         * 设置责任链
         */
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

/**
 * 1️⃣ 抽象处理者：Handler
 */
 abstract class Handler {
    protected Handler nextHandler;  // 下一个处理者

    // 设置下一个处理者
    public void setNextHandler(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    // 处理请求的抽象方法
    public abstract void handleRequest(int leaveDays);
}
/**
 * 2️⃣ 具体处理者：Manager（经理）
 */
class Manager extends Handler {
    @Override
    public void handleRequest(int leaveDays) {
        if (leaveDays <= 2) {
            System.out.println("Manager approves " + leaveDays + " days of leave.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(leaveDays);
        }
    }
}
/**
 * 3️⃣ 具体处理者：DepartmentHead（部门主管）
 */
 class DepartmentHead extends Handler {
    @Override
    public void handleRequest(int leaveDays) {
        if (leaveDays <= 5) {
            System.out.println("Department Head approves " + leaveDays + " days of leave.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(leaveDays);
        }
    }
}

/**
 * 4️⃣ 具体处理者：HR（人力资源部）
 */
 class HR extends Handler {
    @Override
    public void handleRequest(int leaveDays) {
        if (leaveDays > 5) {
            System.out.println("HR approves " + leaveDays + " days of leave.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(leaveDays);
        }
    }
}




