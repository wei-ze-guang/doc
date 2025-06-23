package pattern.template;

/**
 * 假设我们有一个 做咖啡 和 做茶 的过程，
 * 虽然它们有一些相似的步骤，但也有区别。我们可以通过模板方法模式来抽象出公共步骤，
 * 并将个别步骤留给子类实现。
 */
public class Template {
    public static void main(String[] args) {
        Beverage tea = new Tea();

        tea.prepareRecipe();  // Prepare tea

        System.out.println("\n-----");

        Beverage coffee = new Coffee();

        coffee.prepareRecipe();  // Prepare coffee
    }
}

/**
 * 1️⃣ 抽象类：Beverage
 */
 abstract class Beverage {

    // 模板方法，定义了整个算法的框架
    public final void prepareRecipe() {
        boilWater();
        brew();
        pourInCup();
        addCondiments();
    }

    // 这些步骤是固定的
    private void boilWater() {
        System.out.println("Boiling water");
    }

    private void pourInCup() {
        System.out.println("Pouring into cup");
    }

    // 以下步骤由子类决定
    protected abstract void brew();  // 冲泡
    protected abstract void addCondiments();  // 加配料
}

/**
 * 2️⃣ 具体类：Tea（具体实现）
 */
 class Tea extends Beverage {

    @Override
    protected void brew() {
        System.out.println("Steeping the tea");
    }

    @Override
    protected void addCondiments() {
        System.out.println("Adding lemon");
    }
}
/**
 * 3️⃣ 具体类：Coffee（具体实现）
 */
 class Coffee extends Beverage {

    @Override
    protected void brew() {
        System.out.println("Dripping coffee through filter");
    }

    @Override
    protected void addCondiments() {
        System.out.println("Adding sugar and milk");
    }
}
/**
 * 4️⃣ 客户端使用
 */



