package pattern.factory;

/**
 * 3️⃣ 抽象工厂模式（Abstract Factory Pattern）
 * 一次性生产 一整套产品（多个维度）
 * 🎯 假设我们有两套 UI 风格（Windows 和 MacOS），每套包含 Button 和 Checkbox：
 */
public class AbstractFactoryPattern {

    public static void main(String[] args) {
        GUIFactory factory = new MacFactory();

        Button btn = factory.createButton();
        Checkbox chk = factory.createCheckbox();

        btn.render(); // Mac 风格按钮
        chk.check();  // Mac 风格复选框

        GUIFactory factory1 = new WindowsFactory();
        Checkbox chk1 = factory1.createCheckbox();

        Button button = factory1.createButton();
        button.render();
        chk1.check();
    }
}

/**
 * 开发的时候一般写在外面
 */

/**
 * 抽象产品：
 */
interface Button {
    void render();
}
interface Checkbox {
    void check();
}

/**
 * 具体产品：
 */
 class WindowsButton implements Button {
    public void render() {
        System.out.println("Windows 风格按钮");
    }
}

 class MacButton implements Button {
    public void render() {
        System.out.println("Mac 风格按钮");
    }
}

 class WindowsCheckbox implements Checkbox {
    public void check() {
        System.out.println("Windows 风格复选框");
    }
}

 class MacCheckbox implements Checkbox {
    public void check() {
        System.out.println("Mac 风格复选框");
    }
}

/**
 * 抽象工厂：
 */
interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

/**
 * 具体工厂
 */

class WindowsFactory implements GUIFactory {
    public Button createButton() {
        return new WindowsButton();
    }
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

class MacFactory implements GUIFactory {
    public Button createButton() {
        return new MacButton();
    }
    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}



