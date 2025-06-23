package pattern.factory;

/**
 * ✅ 满足开闭原则（新增产品时不修改旧代码）
 * ❌ 类太多，扩展复杂
 * 抽象工厂：定义创建对象的接口
 * 具体工厂：实现创建某个具体对象
 * 这个就是使用接口，每一种实现类自己实现接口，使用哪个就new 哪个
 *
 * 一般不需要提供静态方法
 * 一个实现类代表一个工厂
 */
public class FactoryMethodPattern {

    public static void main(String[] args) {
        ShapeFactory factory = new CircleFactory();
        Shape shape = factory.createShape();
        shape.draw(); // 输出：绘制一个圆

        ShapeFactory factory2 = new RectangleFactory();
        Shape shape2 = factory2.createShape();
        shape2.draw();
    }
}

/**
 * 实际中我们一般写在外面
 */
interface ShapeFactory {
    Shape createShape();
}
 class CircleFactory implements ShapeFactory {
    public Shape createShape() {
        return new Circle();
    }
}

 class RectangleFactory implements ShapeFactory {
    public Shape createShape() {
        return new Rectangle();
    }
}


