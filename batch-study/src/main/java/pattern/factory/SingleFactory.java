package pattern.factory;


/**
 * ✅ 优点：简单易懂
 * ❌ 缺点：增加新产品要修改工厂类，不符合开闭原则
 *
 * 使用静态方法
 * 工厂只有一个
 */
public class SingleFactory {

    static Shape getShape(String type) {
        if(type.equalsIgnoreCase("Rectangle")) {
            return new Rectangle();
        }else if(type.equalsIgnoreCase("Circle")) {
            return new Circle();
        }
        throw new IllegalArgumentException("Unsupported shape: " + type);
    }

    public static void main(String[] args) {
        Shape shape = SingleFactory.getShape("circle");
        shape.draw(); // 输出：绘制一个圆

        Shape shape2 = SingleFactory.getShape("rectangle");
        shape2.draw();
    }

}

/**
 * 当然开发过程中一般写在外面
 */
 interface Shape {
    void draw();
}

 class Circle implements Shape {
    public void draw() {
        System.out.println("绘制一个圆");
    }
}

class Rectangle implements Shape {
    public void draw() {
        System.out.println("绘制一个矩形");
    }
}


