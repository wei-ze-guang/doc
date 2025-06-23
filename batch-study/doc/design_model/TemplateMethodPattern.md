# 模板方法设计模式（Template Method Pattern）
模板方法模式是一种 行为型设计模式，它定义了一个操作的算法框架，允许子类在不改变算法结构的情况下，  
重定义该算法的某些步骤。通过模板方法，父类可以封装不变的部分，将可变的部分留给子类去实现，从而提高代码复用性，减少重复代码。  
## 模板方法的定义
模板方法模式通过定义一个操作中的算法框架（即模板方法），允许子类在不改变算法结构的情况下，  
重定义算法中的某些步骤。  
在模板方法模式中，父类通常会定义一个模板方法（templateMethod），该方法调用一系列步骤方法，  
其中一些方法会被父类实现，而其他方法则由子类实现。  
## 模板方法模式的结构
- 模板方法模式通常包含以下几个角色：
  - AbstractClass（抽象类）：
    - 定义了一个模板方法，包含算法的骨架，定义了操作的步骤。
    - 抽象类中某些步骤可以有默认实现（具体方法），也可以没有实现（抽象方法），需要子类来具体实现。
  - ConcreteClass（具体类）：
    - 实现了抽象类中定义的抽象方法，具体实现每个步骤。
  - Template Method（模板方法）：
    - 通常在抽象类中定义，用来调用算法的各个步骤，保证算法的顺序一致。
  - 该方法是具体方法，无法被子类覆盖。  
## 模板方法模式的类图
```markdown
        ┌────────────────────┐
        │   AbstractClass   │
        └────────────────────┘
                  ▲
       ┌─────────────────────────┐
       │ templateMethod()        │
       │ primitiveOperation1()   │
       │ primitiveOperation2()   │
       └─────────────────────────┘
                  ▲
┌─────────────────────────┐
│ ConcreteClass1          │
│ ConcreteClass2          │
└─────────────────────────┘

```  
- AbstractClass（抽象类）：定义了模板方法（templateMethod()），并实现一些基本操作，其他步骤由子类实现。
- ConcreteClass（具体类）：继承自 AbstractClass，并实现了抽象方法，提供具体步骤。  
## 模板方法模式的代码示例
假设我们有一个 做咖啡 和 做茶 的过程，虽然它们有一些相似的步骤，但也有区别。我们可以通过模板方法模式来抽  
象出公共步骤，并将个别步骤留给子类实现。  
- 1️⃣ 抽象类：Beverage
```java
public abstract class Beverage {

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

```  
- 2️⃣ 具体类：Tea（具体实现）
```java
public class Tea extends Beverage {

    @Override
    protected void brew() {
        System.out.println("Steeping the tea");
    }

    @Override
    protected void addCondiments() {
        System.out.println("Adding lemon");
    }
}

```  
- 3️⃣ 具体类：Coffee（具体实现）
```java
public class Coffee extends Beverage {

    @Override
    protected void brew() {
        System.out.println("Dripping coffee through filter");
    }

    @Override
    protected void addCondiments() {
        System.out.println("Adding sugar and milk");
    }
}

```  
- 4️⃣ 客户端使用  
```java
public class Main {
    public static void main(String[] args) {
        Beverage tea = new Tea();
        tea.prepareRecipe();  // Prepare tea

        System.out.println("\n-----");

        Beverage coffee = new Coffee();
        coffee.prepareRecipe();  // Prepare coffee
    }
}

```
##  总结
- 优点：
  - 代码复用：将相同的代码提取到抽象类中，避免重复代码，提升了代码的复用性。
  - 控制反转：通过模板方法，父类控制了算法的骨架，子类仅需实现个别步骤。
  - 方便扩展：可以通过扩展新的子类，重新定义某些步骤，而不影响算法的整体结构。

- 缺点：
  - 灵活性不足：模板方法模式固定了流程的顺序，灵活性较低，某些步骤不能改变顺序。
  - 继承依赖：子类必须依赖于父类的模板方法，这会增加类之间的依赖性，降低了类的独立性。
  - 无法完全定制：虽然可以定制某些步骤，但流程框架本身不能更改，灵活性较差。

## 适用场景：
- 多个子类共有一个算法骨架，而只有部分步骤不同。
- 希望子类能重新定义某些步骤的行为，而又不改变整体流程。
- 需要控制流程的执行顺序，但其中的某些步骤可以由子类进行定制。  
---  
模板方法模式通过定义一套固定的算法框架，让子类通过继承来定制某些步骤，避免了重复代码，同时保证了流程的一致性。