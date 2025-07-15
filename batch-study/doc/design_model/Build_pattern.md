## Build 构建者模式
- 私有构造器
- Lombok的 @Builder
```java
class Product {
    private xxx;

    static class Builder {
        private xxx;

        Builder setXxx(...) { return this; }

        Product build() { ... }
    }
}

```
```java
public class Product {
    // 一些私有属性
    private String partA;
    private String partB;
    private int partC;

    // 私有构造器，不允许外部直接 new
    private Product() {}

    // 静态内部类：Builder
    public static class Builder {
        // 和 Product 一样的字段
        private String partA;
        private String partB;
        private int partC;

        // 每个字段对应一个构建方法
        public Builder partA(String partA) {
            this.partA = partA;
            return this;
        }

        public Builder partB(String partB) {
            this.partB = partB;
            return this;
        }

        public Builder partC(int partC) {
            this.partC = partC;
            return this;
        }

        // 最后一步：构建目标对象
        public Product build() {
            Product product = new Product();
            product.partA = this.partA;
            product.partB = this.partB;
            product.partC = this.partC;
            return product;
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "partA='" + partA + '\'' +
                ", partB='" + partB + '\'' +
                ", partC=" + partC +
                '}';
    }
}

```  
```java
Product p = new Product.Builder()
    .partA("CPU")
    .partB("内存")
    .partC(123)
    .build();

```